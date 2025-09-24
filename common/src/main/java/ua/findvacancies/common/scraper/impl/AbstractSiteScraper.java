package ua.findvacancies.common.scraper.impl;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.scraper.JobScraper;
import ua.findvacancies.common.scraper.JsoupDocumentFetcher;
import ua.findvacancies.common.service.JobFilterService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class AbstractSiteScraper implements JobScraper {

    public static final String NOT_AVAILABLE = "N/A";

    protected final JsoupDocumentFetcher documentFetcher;
    protected final JobFilterService jobFilterService;
    protected final JobSite jobSite;

    protected AbstractSiteScraper(final JsoupDocumentFetcher documentFetcher,
                                  final JobFilterService jobFilterService,
                                  final JobSite jobSite) {
        this.documentFetcher = documentFetcher;
        this.jobFilterService = jobFilterService;
        this.jobSite = jobSite;
    }


    @Override
    public JobSite getJobSite() {
        return jobSite;
    }

    public String getSiteUrl() {
        return jobSite.getUrl();
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
        List<JobVacancy> vacancies = new ArrayList<>();
        int currentPage = 1;
        boolean hasMorePages = true;

        final int effectiveMaxPages = criteria.getMaxPagesToScrapeOrDefault();

        log.info("Starting to scrape jobs from {}. Criteria: {}", getJobSite(), criteria);
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

                if (hasNonHotVacancyOutSideTimeFrame(scrappedVacancies, criteria.getLastNDays())) {
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

        log.info("Finished scraping {}. Found {} vacancies matching criteria across {} pages.", getJobSite(),
                vacancies.size(), currentPage - 1);

        return vacancies;
    }

    private boolean hasNonHotVacancyOutSideTimeFrame(List<JobVacancy> scrappedVacancies, int lastNDays) {
        return scrappedVacancies.stream().anyMatch(vacancy -> !vacancy.isHot() &&
                vacancy.getDatePosted().isAfter(LocalDate.now().minusDays(lastNDays)));
    }

    private Optional<JobVacancy> getJobVacancy(Element jobElement, int currentPage) {
        try {
            var vacancy = parseSingleJobElement(jobElement);
            log.debug("Parsed vacancy: {}", vacancy);
            return Optional.of(vacancy);
        } catch (Exception e) {
            log.error("Error parsing a job element on {} (page {}): {}", getJobSite(), currentPage,
                    e.getMessage(), e);
        }
        return Optional.empty();
    }

    private boolean matchCriteria(JobVacancy vacancy, JobSearchCriteria criteria) {
        var result = jobFilterService.matches(vacancy, criteria);
        log.debug("Vacancy '{}' from {} matches criteria: {}", vacancy.getTitle(), vacancy.getJobSite(), result);
        return result;
    }

    /**
     * Select all job listing elements.
     * NOTE: You MUST inspect Site's current HTML to find the exact CSS selector for individual job listings.
     */
    protected abstract Elements getJobElements(Document doc);

    /**
     * Constructs the URL for Site jobs based on search criteria and page number.
     *
     * @param criteria The search criteria.
     * @param page     The page number.
     * @return The constructed URL.
     */
    protected abstract String buildSearchUrl(JobSearchCriteria criteria, int page);

    protected abstract String getTitle(Element jobElement);

    protected abstract String getJobUrl(Element jobElement);

    protected abstract String getCompanyName(Element jobElement);

    protected abstract String getCity(Element jobElement);

    protected abstract String getSalary(Element jobElement);

    protected abstract LocalDate getDatePosted(Element jobElement, String title);

    protected abstract boolean isHot(Element jobElement);

    protected abstract String getDescription(Element jobElement);

    /**
     * Helper method to parse the date string from element.
     *
     * @param dateString The date string from the element.
     * @return A LocalDate object, or null if parsing fails.
     */
    protected abstract LocalDate parseDateElementValue(String dateString);

    /**
     * Parses a single job element from the Djinni.co HTML and extracts job details.
     * This method now performs "deep scraping" by visiting the individual job URL
     * to extract the full description and a more precise date posted.
     *
     * @param jobElement The Jsoup Element representing a single job listing from the search results page.
     * @return A JobVacancy object populated with extracted data.
     */
    private JobVacancy parseSingleJobElement(Element jobElement) {
        String title = getTitle(jobElement);

        String jobUrl = getJobUrl(jobElement);

        String companyName = getCompanyName(jobElement);

        String city = getCity(jobElement);

        String salary = getSalary(jobElement);

        boolean isHot = isHot(jobElement);

        LocalDate datePosted = getDatePosted(jobElement, title);

        String description = getDescription(jobElement);

        // A placeholder for now
        List<String> skills = new ArrayList<>();

        return JobVacancy.builder()
                .title(title)
                .salary(salary)
                .city(city)
                .companyName(companyName)
                .jobSite(getJobSite())
                .url(jobUrl)
                .datePosted(datePosted)
                .isHot(isHot)
                .description(description)
                .skills(skills)
                .build();
    }

}
