package ua.findvacancies.common.scraper.impl;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.service.JobFilterService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
public class DouJobScraper extends AbstractSeleniumScraper {

    /**
     * Maps Ukrainian month names (Genitive/Nominative case) to their month number.
     */
    private static final Map<String, Integer> MONTH_MAP = Map.ofEntries(
            Map.entry("січня", 1),
            Map.entry("лютого", 2),
            Map.entry("березня", 3),
            Map.entry("квітня", 4),
            Map.entry("травня", 5),
            Map.entry("червня", 6),
            Map.entry("липня", 7),
            Map.entry("серпня", 8),
            Map.entry("вересня", 9),
            Map.entry("жовтня", 10),
            Map.entry("листопада", 11),
            Map.entry("грудня", 12)
    );

    public DouJobScraper(final JobFilterService jobFilterService) {
        super(jobFilterService, JobSite.DOU_UA);
    }

    static void main() {
        JobSearchCriteria criteria = JobSearchCriteria.builder()
                .keywords(Set.of("java"))
                .ignoredWords(Set.of("junior", "trainee"))
                .lastNDays(10)
                .maxPagesToScrape(5)
                .build();
        var douScraper = new DouJobScraper(new JobFilterService());
        var jobs = douScraper.scrapeJobs(criteria);
        jobs.forEach(vac -> log.info(vac.toString()));
        log.info("--- Scrape Complete. Found {} vacancies ---", jobs.size());
    }

    @Override
    protected List<WebElement> getVacancyElements(final WebDriver driver) {
        return driver.findElements(By.cssSelector(".l-vacancy"));
    }

    private String replaceWebSymbols(final String text) {
        return text.replace("\u00A0", " ").trim();
    }

    /**
     * Converts a DOU date string (e.g., "5 листопада" or "сьогодні") to a LocalDate object.
     */
    private LocalDate parseDOUDate(final String dateString) {
        String normalizedDate = dateString.toLowerCase(Locale.of("uk", "UA"));
        LocalDate today = LocalDate.now();

        if (normalizedDate.contains("сьогодні")) {
            return today;
        }
        if (normalizedDate.contains("вчора")) {
            return today.minusDays(1);
        }

        // Pattern for "Day Month" (e.g., "5 листопада")
        Pattern pattern = Pattern.compile("(\\d{1,2})\\s+([a-zA-Zа-яА-Я]+)");
        Matcher matcher = pattern.matcher(normalizedDate);

        if (matcher.find()) {
            int dayOfMonth = Integer.parseInt(matcher.group(1));
            String monthNameUa = matcher.group(2);

            Integer monthValue = MONTH_MAP.get(monthNameUa);

            if (monthValue != null) {
                int year = today.getYear();
                // If the month number is greater than the current month number, it must be last year
                if (monthValue > today.getMonthValue()) {
                    year = today.getYear() - 1;
                }
                return LocalDate.of(year, monthValue, dayOfMonth);
            }
        }

        // Default safe fallback: treat unparsable dates as current to prevent premature cutoff
        return today;
    }


    @Override
    protected Elements getJobElements(final Document doc) {
        return doc.select("li.l-vacancy");
    }

    @Override
    public String buildSearchUrl(final JobSearchCriteria criteria, final int page) {
        StringBuilder urlBuilder = new StringBuilder(getSiteUrl()).append("/vacancies/");

        if (criteria != null && criteria.hasKeywords()) {
            urlBuilder.append("?search=");
            String keywordsParam = criteria.getKeywords().stream()
                    .map(s -> URLEncoder.encode(s.trim(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("+"));
            urlBuilder.append(keywordsParam);
        }
        urlBuilder.append("&descr=1");

        return urlBuilder.toString();
    }

    @Override
    protected String getTitle(final Element jobElement) {
        return getTextOrDefault(jobElement.selectFirst("div.title a.vt"));
    }

    @Override
    protected String getJobUrl(final Element jobElement) {
        return getAttributeOrDefault(jobElement.selectFirst("div.title a.vt"), "href");
    }

    @Override
    protected String getCompanyName(final Element jobElement) {
        String textOrDefault = getTextOrDefault(jobElement.selectFirst("div.title a.company"));
        return replaceWebSymbols(textOrDefault);
    }

    @Override
    protected String getCity(final Element jobElement) {
        return getTextOrDefault(jobElement.selectFirst("span.cities"));
    }

    @Override
    protected String getSalary(final Element jobElement) {
        return getTextOrDefault(jobElement.selectFirst("span.salary"));
    }

    @Override
    protected LocalDate getDatePosted(final Element jobElement, final String title) {
        var dateEl = jobElement.selectFirst("div.date");
        if (dateEl == null || dateEl.text().isBlank()) {
            return null;
        }
        return parseDOUDate(dateEl.text());
    }

    @Override
    protected boolean isHot(final Element jobElement) {
        return jobElement.hasClass("__hot");
    }

    @Override
    protected String getDescription(final Element jobElement) {
        return getTextOrDefault(jobElement.selectFirst("div.sh-info"));
    }

    @Override
    protected LocalDate parseDateElementValue(final String dateString) {
        return parseDOUDate(dateString);
    }

    @Override
    protected WebElement getWebElementForMoreResults(final WebDriver driver) {
        return driver.findElement(By.cssSelector("div.more-btn > a"));
    }

}
