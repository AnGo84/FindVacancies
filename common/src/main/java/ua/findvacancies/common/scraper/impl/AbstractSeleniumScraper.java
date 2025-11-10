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
import ua.findvacancies.common.scraper.JsoupDocumentFetcher;
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

    protected AbstractSeleniumScraper(final JobFilterService jobFilterService,
                                      final JobSite jobSite) {
        super(jobFilterService, jobSite);
    }


    @Override
    public JobSite getJobSite() {
        return jobSite;
    }

    @Override
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
    public List<JobVacancy> scrapeJobs(JobSearchCriteria criteria) {
        log.info("Starting to scrape jobs from {}. Criteria: {}", getJobSite(), criteria);
        WebDriver driver = null;

        Map<String, JobVacancy> scrappedVacanciesMap = new HashMap<>();
        try {
            driver = WebDriverService.getDriver();
            String url = buildSearchUrl(criteria, 0);
            driver.get(url);

            final int effectiveMaxPages = criteria.getMaxPagesToScrapeOrDefault();


            boolean hasMoreResults = true;
            int iteration = 1;
            // --- 2. Loop and Click "Load More" ---
            while (hasMoreResults && iteration <= effectiveMaxPages) {
                log.info("Iteration {}. Vacancies on start: {}. Scraping page {}",
                        iteration, scrappedVacanciesMap.size(), driver.getCurrentUrl());
                int vacanciesOnPageCount = getVacancyElements(driver).size();
                log.info("Found {} vacancies on the current page for iteration {}.", vacanciesOnPageCount, iteration);

                // Scrape the current visible vacancies before and after the potential click
                List<JobVacancy> currentVacancies = getVisibleVacancies(driver, iteration);

                log.info("Scrap {} vacancies on the current page for iteration {}.",
                        currentVacancies.size(), iteration);

                currentVacancies.forEach(vacancy ->
                        scrappedVacanciesMap.putIfAbsent(vacancy.getUrl(), vacancy)
                );

                if (hasRecentNonHotVacancy(scrappedVacanciesMap.values(), criteria.getLastNDays())) {
                    log.debug("At least one non-hot vacancy was posted after the allowed timeframe (>{} days).",
                            criteria.getLastNDays());

                    hasMoreResults = false;
                }

                // --- 2. Prepare for Click ---
                log.info("Prepare for 'more-btn' to click");
                if (hasMoreResults) {
                    hasMoreResults = getMoreResults(driver, vacanciesOnPageCount);
                }
                iteration++;
            }

            // --- 4. Final Output ---
            log.info("--- Scrape Complete ---");

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
        log.info("Total vacancies found within the last {} days: {}",
                criteria.getLastNDays(), result.size());
        return result;

    }

    protected List<JobVacancy> getVisibleVacancies(WebDriver driver, int iteration) {
        log.info("Starting Jsoup parsing from Selenium source for iteration {}.", iteration);

        // For DOU, the simplest is often to get the entire body's HTML.
        String pageSource = driver.findElement(By.cssSelector("body")).getAttribute("outerHTML");

        if (pageSource == null || pageSource.isBlank()) {
            return List.of();
        }
        // 2. Parse the HTML source with Jsoup
        Document doc = Jsoup.parse(pageSource);
        var vacancyElements = getJobElements(doc);
        // 3. Use Jsoup's fast selector to get all vacancy <li> elements

        log.info("Jsoup found {} vacancies for iteration {}.", vacancyElements.size(), iteration);

        List<JobVacancy> list = vacancyElements.stream()
                .map(element -> getJobVacancy(element, iteration))
                .filter(Objects::nonNull)
                .flatMap(Optional::stream)
                .toList();

        log.info("Correctly parsed {} vacancies fro iteration {}.", list.size(), iteration);

        return list;
    }

    private boolean getMoreResults(WebDriver driver, int vacanciesOnPageCount) {
        log.info("Before 'more-btn' to click we have: {} vacancies.", vacanciesOnPageCount);
        try {
            // Find and click the actual link
            WebElement loadMoreLink = getWebElementForMoreResults(driver);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", loadMoreLink);
            loadMoreLink.click();
            log.info("Clicked 'More Vacancies'. Initial count: {}. Waiting for new content...", vacanciesOnPageCount);

            // --- 3. Robust Wait ---
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(10)); // Set a max wait time
            // Wait until the total number of vacancies is demonstrably higher
            return shortWait.until(webDriver -> getVacancyElements(webDriver).size() > vacanciesOnPageCount);

        } catch (NoSuchElementException e) {
            log.info("No more 'more-btn' button found. Exiting loop.");
            return false;
        } catch (TimeoutException e) {
            // New content did not appear fast enough
            log.warn("Timeout reached after 30 seconds. Assuming no more vacancies available.");
            return false;
        }
    }

    protected abstract WebElement getWebElementForMoreResults(WebDriver driver);

    protected abstract List<WebElement> getVacancyElements(WebDriver driver);
}
