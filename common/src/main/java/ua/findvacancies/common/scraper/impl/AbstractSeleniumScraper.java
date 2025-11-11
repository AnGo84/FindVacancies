package ua.findvacancies.common.scraper.impl;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.scraper.WebDriverService;
import ua.findvacancies.common.service.JobFilterService;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public abstract class AbstractSeleniumScraper extends AbstractScraper {

    public static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(10);

    protected AbstractSeleniumScraper(final JobFilterService jobFilterService,
                                      final JobSite jobSite) {
        super(jobFilterService, jobSite);
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
        log.debug("Starting to scrape jobs for {}. Criteria: {}", getJobSite(), criteria);
        WebDriver driver = null;
        int iteration = 1;

        Map<String, JobVacancy> scrappedVacanciesMap = new HashMap<>();
        try {
            driver = WebDriverService.getDriver();
            String url = buildSearchUrl(criteria, 0);
            driver.get(url);

            final int effectiveMaxPages = criteria.getMaxPagesToScrapeOrDefault();

            boolean hasMoreResults = true;

            while (hasMoreResults && iteration <= effectiveMaxPages) {
                log.debug("Iteration {}. Vacancies on start: {}. Scraping url {}",
                        iteration, scrappedVacanciesMap.size(), driver.getCurrentUrl());

                int vacanciesOnPageCount = getVacancyElements(driver).size();
                log.debug("Found {} vacancies for iteration {}.", vacanciesOnPageCount, iteration);

                List<JobVacancy> currentVacancies = getVisibleVacancies(driver, iteration);
                log.debug("Scrap {} vacancies for iteration {}.", currentVacancies.size(), iteration);

                currentVacancies.forEach(vacancy ->
                        scrappedVacanciesMap.putIfAbsent(vacancy.getUrl(), vacancy)
                );

                // TODO move to parent
                if (hasRecentNonHotVacancy(scrappedVacanciesMap.values(), criteria.getLastNDays())) {
                    log.debug("At least one non-hot vacancy was posted after the allowed timeframe (>{} days).",
                            criteria.getLastNDays());
                    hasMoreResults = false;
                }

                log.debug("Prepare for 'more-btn' to click");
                if (hasMoreResults) {
                    hasMoreResults = getMoreResults(driver, vacanciesOnPageCount);
                }
                iteration++;
            }

            log.info("Scrape Complete for {}. Criteria: {}", getJobSite(), criteria);

        } catch (Exception e) {
            log.error("An error occurred during scraping: {}", e.getMessage(), e);
        } finally {
            if (driver != null) {
                driver.quit();
            }
        }

        var result = scrappedVacanciesMap.values().stream()
                .filter(jobVacancy -> matchCriteria(jobVacancy, criteria))
                .toList();

        log.debug("Finished scraping for {}. Found {} vacancies matching criteria across {} iterations.",
                getJobSite(), result.size(), iteration - 1);
        return result;

    }

    protected List<JobVacancy> getVisibleVacancies(final WebDriver driver, final int iteration) {
        log.debug("Starting Jsoup parsing from Selenium source for iteration {}.", iteration);

        String pageSource = driver.findElement(By.cssSelector("body")).getAttribute("outerHTML");

        if (pageSource == null || pageSource.isBlank()) {
            return List.of();
        }

        Document doc = Jsoup.parse(pageSource);
        var vacancyElements = getJobElements(doc);

        log.debug("Jsoup found {} vacancies for iteration {}.", vacancyElements.size(), iteration);

        List<JobVacancy> list = vacancyElements.stream()
                .map(element -> getJobVacancy(element, iteration))
                .filter(Objects::nonNull)
                .flatMap(Optional::stream)
                .toList();

        log.debug("Correctly parsed {} vacancies fro iteration {}.", list.size(), iteration);
        return list;
    }

    private boolean getMoreResults(final WebDriver driver, final int vacanciesOnPageCount) {
        log.debug("Before 'more-btn' to click we have: {} vacancies on page.", vacanciesOnPageCount);
        try {
            WebElement loadMoreLink = getWebElementForMoreResults(driver);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", loadMoreLink);
            loadMoreLink.click();

            WebDriverWait shortWait = new WebDriverWait(driver, DEFAULT_TIMEOUT);
            // Wait until the total number of vacancies is demonstrably higher
            return shortWait.until(webDriver -> getVacancyElements(webDriver).size() > vacanciesOnPageCount);

        } catch (NoSuchElementException e) {
            log.error("No element found: {}.", e.getMessage());
            return false;
        } catch (TimeoutException _) {
            log.warn("Timeout reached after {} seconds. Assuming no more vacancies available.",
                    DEFAULT_TIMEOUT.getSeconds());
            return false;
        }
    }

    protected abstract WebElement getWebElementForMoreResults(WebDriver driver);

    protected abstract List<WebElement> getVacancyElements(WebDriver driver);
}
