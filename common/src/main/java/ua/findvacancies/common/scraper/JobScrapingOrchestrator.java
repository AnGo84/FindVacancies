package ua.findvacancies.common.scraper;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
public class JobScrapingOrchestrator {

    private static final int MAX_CURRENT_SCRAPPER = JobSite.values().length;

    private final JobScraperRegistry scraperRegistry;
    private final ExecutorService executorService;

    /**
     * Constructor for JobScrapingOrchestrator.
     *
     * @param scraperRegistry The registry containing all discovered JobScraper implementations.
     */
    public JobScrapingOrchestrator(final JobScraperRegistry scraperRegistry) {
        this.scraperRegistry = scraperRegistry;

        // Create a fixed thread pool. The number of threads can be configured,
        // e.g., based on the number of CPU cores or desired concurrency level.
        // Using a small pool for web scraping to avoid overwhelming target sites.
        this.executorService = Executors.newFixedThreadPool(
                Math.min(scraperRegistry.getAllScrapers().size(), MAX_CURRENT_SCRAPPER));
    }

    /**
     * Constructor for testing, allowing the ExecutorService to be injected.
     * @param jobScraperRegistry The registry of available job scrapers.
     * @param executorService The thread pool to be used for concurrent scraping.
     */
    public JobScrapingOrchestrator(final JobScraperRegistry jobScraperRegistry, final ExecutorService executorService) {
        this.scraperRegistry = jobScraperRegistry;
        this.executorService = executorService;
    }

    /**
     * Initiates the scraping process across a selection of job sites.
     * Each selected site is scraped concurrently.
     *
     * @param criteria    The {@link JobSearchCriteria} to apply to all scrapers.
     * @param targetSites An optional set of {@link JobSite} enums
     *                    to specifically scrape. If null or empty, all available scrapers will be used.
     * @return A consolidated list of {@link JobVacancy} objects from the selected sites.
     */
    public List<JobVacancy> scrapeJobs(final JobSearchCriteria criteria, final Set<JobSite> targetSites) {
        log.info("Initiating scraping across selected job sites with criteria: {} and target sites: {}",
                criteria, targetSites);
        List<JobVacancy> allVacancies = new ArrayList<>();

        Set<JobScraper> scrapersToRun = scraperRegistry.getScrapers(targetSites);

        if (scrapersToRun.isEmpty()) {
            log.warn("No scrapers found matching the target sites: {}. Returning empty list.", targetSites);
            return allVacancies;
        }

        var sitesToScrap = scrapersToRun.stream().map(JobScraper::getJobSite).collect(Collectors.toUnmodifiableSet());
        log.info("Running scrapers for sites: {}", JobSite.joinSiteNames(sitesToScrap));

        // Use CompletableFuture to run scrapers concurrently
        List<CompletableFuture<List<JobVacancy>>> futures = scrapersToRun.stream()
                .map(scraper ->
                        CompletableFuture.supplyAsync(() -> getJobVacancies(criteria, scraper), executorService))
                .toList();

        // Wait for all futures to complete and collect results
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        // Aggregate results from all completed futures
        for (CompletableFuture<List<JobVacancy>> future : futures) {
            try {
                allVacancies.addAll(future.get());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Interrupted", e);
            } catch (Exception e) {
                log.error("Error retrieving results from a scraper future: {}", e.getMessage(), e);
            }
        }

        log.info("Completed scraping all selected sites. Total vacancies found: {}", allVacancies.size());
        return allVacancies;
    }

    private List<JobVacancy> getJobVacancies(JobSearchCriteria criteria, JobScraper scraper) {
        final var siteName = scraper.getJobSite().getSiteName();
        try {
            log.info("Starting scraping for site: {}", siteName);
            List<JobVacancy> siteVacancies = scraper.scrapeJobs(criteria);
            log.info("Finished scraping for site: {}. Found {} vacancies.", siteName, siteVacancies.size());
            return siteVacancies;
        } catch (Exception e) {
            log.error("Error scraping jobs from site {}: {}", siteName, e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * @return A list of available site names.
     */
    public Set<JobSite> getAvailableJobSites() {
        return scraperRegistry.getAvailableJobSites();
    }

    /**
     * Method should shut down the executor service gracefully.
     */
    @PreDestroy
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
            log.info("JobScrapingOrchestrator executor service shut down.");
        }
    }

}
