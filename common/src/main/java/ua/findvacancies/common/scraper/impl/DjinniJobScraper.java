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
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service responsible for scraping job vacancies from djinni.co.
 * This class implements the JobScraper interface and uses JsoupDocumentFetcher
 * to obtain the HTML document, then parses its content to extract job details.
 * It also applies filtering based on provided JobSearchCriteria.
 */
@Slf4j
public class DjinniJobScraper extends AbstractSiteScraper {

    public static final DateTimeFormatter JOB_DATETIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public DjinniJobScraper(final JsoupDocumentFetcher documentFetcher, final JobFilterService jobFilterService) {
        super(documentFetcher, jobFilterService, JobSite.DJINNI_CO);
    }

    @Override
    public Elements getJobElements(Document doc) {
        return doc.select("ul.list-jobs li[id^=job-item-]");
    }

    /**
     * Constructs the URL for djinni.co jobs based on search criteria and page number.
     * Format: <a href="https://djinni.co/jobs/?all_keywords=java+developer&page=N"><a/>
     *
     * @param criteria The search criteria.
     * @param page     The page number.
     * @return The constructed URL.
     */
    @Override
    public String buildSearchUrl(JobSearchCriteria criteria, int page) {
        StringBuilder urlBuilder = new StringBuilder(getSiteUrl()).append("/jobs/");
        boolean firstParam = true;

        if (criteria != null && criteria.hasKeywords()) {
            urlBuilder.append("?all_keywords=");
            String keywordsParam = criteria.getKeywords().stream()
                    .map(s -> URLEncoder.encode(s.trim(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("+"));
            urlBuilder.append(keywordsParam);
            firstParam = false;
        }

        if (page > 1) {
            if (firstParam) {
                urlBuilder.append("?");
            } else {
                urlBuilder.append("&");
            }
            urlBuilder.append("page=").append(page);
        }

        return urlBuilder.toString();
    }

    @Override
    public String getTitle(Element jobElement) {
        return getTextOrDefault(jobElement.selectFirst("a.job-item__title-link"));
    }

    @Override
    public String getJobUrl(Element jobElement) {
        Element linkEl = jobElement.selectFirst("a.job-item__title-link");
        return getSiteUrl() + getAttributeOrDefault(linkEl, "href");
    }

    @Override
    public String getCompanyName(Element jobElement) {
        return getTextOrDefault(jobElement.selectFirst("a[data-analytics=company_page]"));
    }

    @Override
    public String getCity(Element jobElement) {
        return getTextOrDefault(jobElement.selectFirst("span.location-text"));
    }

    @Override
    public String getSalary(Element jobElement) {
        return getTextOrDefault(jobElement.selectFirst("span.public-salary"));
    }

    @Override
    public LocalDate getDatePosted(Element jobElement, String title) {
        Optional<String> dateTooltipString =
                Optional.ofNullable(jobElement.selectFirst("span.text-nowrap[data-toggle=tooltip]"))
                        .map(element -> element.attr("title"));

        if (dateTooltipString.isPresent()) {
            return parseDateElementValue(dateTooltipString.get());
        } else {
            log.warn("Could not find date tooltip for vacancy: {}", title);
            return null;
        }
    }

    @Override
    public boolean isHot(Element jobElement) {
        return jobElement.select("span.icon-hot").first() != null;
    }

    @Override
    public String getDescription(Element jobElement) {
        return getTextOrDefault(jobElement.selectFirst("span.js-truncated-text"));
    }

    /**
     * Helper method to parse the date string from Djinni.co's tooltip.
     * Expected format: "HH:mm dd.MM.yyyy" (e.g., "15:39 17.07.2025")
     *
     * @param dateString The date string from the data-original-title attribute.
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
            log.warn("Could not parse Djinni job date string '{}'. Error: {}", dateString, e.getMessage());
            return null;
        }
    }

}
