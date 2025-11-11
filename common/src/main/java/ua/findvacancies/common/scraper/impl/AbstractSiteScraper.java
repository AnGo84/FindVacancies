package ua.findvacancies.common.scraper.impl;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.scraper.JsoupDocumentFetcher;
import ua.findvacancies.common.service.JobFilterService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractSiteScraper extends AbstractScraper {

    protected final JsoupDocumentFetcher documentFetcher;
    protected final JobFilterService jobFilterService;
    protected final JobSite jobSite;

    protected AbstractSiteScraper(final JsoupDocumentFetcher documentFetcher,
                                  final JobFilterService jobFilterService,
                                  final JobSite jobSite) {
        super(jobFilterService, jobSite);
        this.documentFetcher = documentFetcher;
        this.jobFilterService = jobFilterService;
        this.jobSite = jobSite;
    }

    /**
     * Scrapes job vacancies from Site and applies filtering based on criteria.
     * Iterates through multiple pages until no more jobs are found or MAX_PAGES_TO_SCRAPE is reached.
     *
     * @param criteria The {@link JobSearchCriteria} to apply for filtering.
     * @return A list of JobVacancy objects found on Site that match the criteria.
     */
    @Override
    public List<JobVacancy> scrapeJobs(final JobSearchCriteria criteria) {
        log.info("Starting to scrape jobs from {}. Criteria: {}", getJobSite(), criteria);
        List<JobVacancy> vacancies = new ArrayList<>();
        int currentPage = 1;
        boolean hasMorePages = true;

        final int effectiveMaxPages = criteria.getMaxPagesToScrapeOrDefault();

        while (hasMorePages && currentPage <= effectiveMaxPages) {
            final var url = buildSearchUrl(criteria, currentPage);
            log.info("Scraping page {} from {}: {}", currentPage, getJobSite(), url);

            try {
                Document doc = documentFetcher.fetchDocument(url);

                Elements jobElements = getJobElements(doc);

                if (jobElements.isEmpty()) {
                    log.info("No job elements found on page {} of {}. Assuming end of results.",
                            currentPage, getJobSite());
                    hasMorePages = false;
                }

                int finalCurrentPage = currentPage;
                var scrappedVacancies = jobElements.stream()
                        .map(el -> getJobVacancy(el, finalCurrentPage))
                        .flatMap(Optional::stream)
                        .toList();

                if (hasRecentNonHotVacancy(scrappedVacancies, criteria.getLastNDays())) {
                    log.debug("At least one non-hot vacancy was posted after the allowed timeframe (>{} days).",
                            criteria.getLastNDays());
                    hasMorePages = false;
                }

                scrappedVacancies.stream().filter(jobVacancy -> matchCriteria(jobVacancy, criteria))
                        .forEach(vacancies::add);

                currentPage++;
            } catch (IOException e) {
                log.error("Error connecting to or reading from {} (page {}): {}",
                        getJobSite(), currentPage, e.getMessage(), e);
                hasMorePages = false;
            }
        }

        log.debug("Finished scraping {}. Found {} vacancies matching criteria across {} pages.",
                getJobSite(), vacancies.size(), currentPage - 1);

        return vacancies;
    }

}
