package ua.findvacancies.common.scraper.impl;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.scraper.JobScraper;
import ua.findvacancies.common.service.JobFilterService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
//TODO: move Elements parsing to Util class
public abstract class AbstractScraper implements JobScraper {

    public static final String NOT_AVAILABLE = "N/A";

    protected final JobFilterService jobFilterService;
    protected final JobSite jobSite;

    protected AbstractScraper(final JobFilterService jobFilterService,
                              final JobSite jobSite) {

        this.jobFilterService = jobFilterService;
        this.jobSite = jobSite;
    }

    /**
     * Replaces non-breaking spaces and trims the input text.
     *
     * @param text the input string possibly containing web-specific symbols
     * @return cleaned string with regular spaces and trimmed edges
     */
    protected static String normalizeWebText(final String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.replace('\u00A0', ' ').trim();
    }

    /**
     * Checks if there is any non-hot vacancy posted within the last N days.
     *
     * @param vacancies the collection of scraped job vacancies
     * @param daysBack  the number of days to look back from today
     * @return true if at least one non-hot vacancy was posted within the time frame
     */
    protected static boolean hasRecentNonHotVacancy(final Collection<JobVacancy> vacancies,
                                                    final int daysBack) {
        if (vacancies == null || vacancies.isEmpty()) {
            return false;
        }

        LocalDate thresholdDate = LocalDate.now().minusDays(daysBack);
        return vacancies.stream()
                .filter(v -> !v.isHot())
                .map(JobVacancy::getDatePosted)
                .filter(Objects::nonNull)
                .anyMatch(date -> date.isBefore(thresholdDate));
    }

    /**
     * Extracts the value of a specified attribute from a Jsoup Element,
     * returning a default value if the element is null or the attribute is missing.
     *
     * @param element      the Jsoup Element to extract the attribute from
     * @param attr         the name of the attribute to retrieve
     * @param defaultValue the fallback value if the element is null or lacks the attribute
     * @return the attribute value or the default value
     */
    protected static String getAttributeOrDefault(final Element element,
                                                  final String attr,
                                                  final String defaultValue) {
        return (element != null && element.hasAttr(attr)) ? element.attr(attr).trim() : defaultValue;
    }

    /**
     * Overload with a predefined fallback value.
     *
     * @param element the Jsoup Element to extract the attribute from
     * @param attr    the name of the attribute to retrieve
     * @return the attribute value or a predefined fallback
     */
    protected static String getAttributeOrDefault(final Element element, final String attr) {
        return getAttributeOrDefault(element, attr, NOT_AVAILABLE);
    }

    @Override
    public JobSite getJobSite() {
        return jobSite;
    }

    public String getSiteUrl() {
        return jobSite.getUrl();
    }

    protected Optional<JobVacancy> getJobVacancy(final Element jobElement, final int currentPage) {
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

    protected boolean matchCriteria(final JobVacancy vacancy, final JobSearchCriteria criteria) {
        var result = jobFilterService.matches(vacancy, criteria);
        log.debug("Vacancy '{}' from {} matches criteria: {}", vacancy.getTitle(), vacancy.getJobSite().name(), result);
        return result;
    }

    /**
     * Safely extracts text from an Element, returning a default value if the element is null.
     *
     * @param element      the Jsoup Element to extract text from
     * @param defaultValue the fallback value if the element is null
     * @return the element's text or the default value
     */
    protected String getTextOrDefault(final Element element, final String defaultValue) {
        return (element != null && element.hasText()) ? normalizeWebText(element.text()) : defaultValue;
    }

    /**
     * Overload with default fallback value.
     *
     * @param element the Jsoup Element to extract text from
     * @return the element's text or a predefined fallback
     */
    protected String getTextOrDefault(final Element element) {
        return getTextOrDefault(element, NOT_AVAILABLE);
    }

    /**
     * Select all job listing elements.
     * NOTE: You MUST inspect the Site's current HTML to find the exact CSS selector for individual job listings.
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
    private JobVacancy parseSingleJobElement(final Element jobElement) {
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
