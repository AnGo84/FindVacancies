package ua.findvacancies.common.scraper.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.scraper.JsoupDocumentFetcher;
import ua.findvacancies.common.service.JobFilterService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service responsible for scraping job vacancies from work.ua.
 * This class implements the JobScraper interface and uses JsoupDocumentFetcher
 * to obtain the HTML document, then parses its content to extract job details.
 * It uses JobFilterService to apply filtering on individual vacancies
 * before adding them to the result list.
 */
@Slf4j
public class WorkUaJobScraper extends AbstractSiteScraper {

    public static final DateTimeFormatter JOB_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public WorkUaJobScraper(final JsoupDocumentFetcher documentFetcher, final JobFilterService jobFilterService) {
        super(documentFetcher, jobFilterService, JobSite.WORK_UA);
    }

    @Override
    protected Elements getJobElements(Document doc) {
        return doc.select("div.card.card-hover.card-visited.wordwrap.job-link");
    }

    /**
     * Constructs the URL for work.ua jobs based on search criteria and page number.
     * Format: <a href="https://www.work.ua/jobs-keyword1+keyword2/?page=N"></a>
     *
     * @param criteria The search criteria.
     * @param page     The page number.
     * @return The constructed URL.
     */
    @Override
    protected String buildSearchUrl(JobSearchCriteria criteria, int page) {
        StringBuilder urlBuilder = new StringBuilder(getSiteUrl()).append("/jobs-");

        if (criteria != null && criteria.hasKeywords()) {
            String keywordsPath = criteria.getKeywords().stream()
                    .map(s ->
                            URLEncoder.encode(s.trim(), StandardCharsets.UTF_8).replace("+", "%20"))
                    .collect(Collectors.joining("+"));
            urlBuilder.append(keywordsPath).append("/");
        }

        if (page > 1) {
            urlBuilder.append("?page=").append(page);
        }

        return urlBuilder.toString();
    }

    @Override
    protected String getTitle(Element jobElement) {
        return Optional.ofNullable(jobElement.selectFirst("h2 > a"))
                .map(Element::text)
                .orElse("N/A");
    }

    @Override
    protected String getJobUrl(Element jobElement) {
        return Optional.ofNullable(jobElement.selectFirst("h2 > a"))
                .map(link -> jobSite.getUrl() + link.attr("href"))
                .orElse("N/A");
    }

    @Override
    protected String getCompanyName(Element jobElement) {
        return Optional.ofNullable(jobElement.selectFirst("span.mr-xs > span.strong-600"))
                .map(Element::text)
                .orElse("N/A");
    }

    @Override
    protected String getCity(Element jobElement) {
        List<Element> cityElements = jobElement.select("div.mt-xs > span").stream()
                .filter(spanEl -> !spanEl.hasClass("distance-block"))
                .toList();
        return cityElements.isEmpty() ? "N/A" : cityElements.getLast().text();
    }

    @Override
    protected String getSalary(Element jobElement) {
        return Optional.ofNullable(jobElement.selectFirst("div > span.strong-600"))
                .map(Element::text)
                .map(this::cleanWorkUaSalary)
                .orElse("N/A");
    }

    @Override
    protected LocalDate getDatePosted(Element jobElement, String title) {
        String dateTimeAttribute = Optional.ofNullable(jobElement.selectFirst("time"))
                .map(element -> element.attr("datetime"))
                .orElse(null);

        if (dateTimeAttribute != null) {
            return parseDateElementValue(dateTimeAttribute); // Use a specific parser for this format
        } else {
            log.warn("Could not find <time> tag with datetime attribute for vacancy: {}", title);
            return null;
        }
    }

    @Override
    protected boolean isHot(Element jobElement) {
        return false;
    }

    @Override
    protected String getDescription(Element jobElement) {
        return Optional.ofNullable(jobElement.selectFirst("p.ellipsis-line-3"))
                .map(Element::text)
                .orElse("N/A");
    }

    /**
     * Helper method to clean the raw salary string from Work.ua.
     * Removes HTML entities like &#8239;, &thinsp;, &nbsp;, and currency symbols like "грн".
     *
     * @param rawSalary The raw salary string (e.g., "40&#8239;000&thinsp;–&thinsp;60&#8239;000&nbsp;грн").
     * @return A cleaned salary string (e.g., "40000 – 60000").
     */
    private String cleanWorkUaSalary(String rawSalary) {
        if (rawSalary == null || rawSalary.isEmpty()) {
            return "N/A";
        }
        // Remove HTML entities for thin space, hair space, and non-breaking space
        String cleaned = rawSalary.replace("&#8239;", "")
                .replace("&thinsp;", "")
                .replace("&nbsp;", " ");
        // Remove "грн" and trim whitespace
        //cleaned = cleaned.replace("грн", "").trim();
        cleaned = cleaned.trim();
        return cleaned;
    }

    /**
     * Helper method to parse the date string from Work.ua's <time> tag's datetime attribute.
     * Expected format: "yyyy-MM-dd HH:mm:ss" (e.g., "2025-07-16 00:16:19")
     *
     * @param dateString The date string from the datetime attribute.
     * @return A LocalDate object, or null if parsing fails.
     */
    @Override
    public LocalDate parseDateElementValue(String dateString) {
        if (StringUtils.isBlank(dateString)) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateString, JOB_DATETIME_FORMATTER).toLocalDate();
        } catch (DateTimeParseException e) {
            log.warn("Could not parse Work.ua datetime attribute string '{}'. Error: {}", dateString, e.getMessage());
            return null;
        }
    }

}
