package ua.findvacancies.common;

import lombok.extern.slf4j.Slf4j;
import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.service.JobServiceBuilder;

import java.util.List;
import java.util.Set;

/**
 * A simple runner class to test the JobScrapingOrchestrator and associated services.
 * This sets up a minimal Spring context to demonstrate the scraping functionality
 * outside a full web application.
 */
@Slf4j
public class JobScraperMain {

    public static void main(String[] args) {
        JobSearchCriteria criteria = JobSearchCriteria.builder()
                .keywords(Set.of("java", "developer"))
                .ignoredWords(Set.of("junior", "trainee"))
                .lastNDays(7)
                .maxPagesToScrape(2)
                .build();

        Set<JobSite> targetSites = Set.of(JobSite.WORK_UA);

        var jobService = new JobServiceBuilder().build();
        log.info("Running job scraping with criteria: {} and target sites: {}", criteria, targetSites);
        try {
            List<JobVacancy> vacancies = jobService.getJobs(criteria, targetSites);

            log.info("\n--- Scraped Job Vacancies (Total: {}) ---", vacancies.size());
            if (vacancies.isEmpty()) {
                log.info("No vacancies found matching the criteria.");
            } else {
                vacancies.forEach(vacancy ->
                        log.info(" [{}] Title: {}, Company: {}, City: {}, Salary: {}, Date: {}, URL: {}, Description:"
                                        + " {}",
                                vacancy.getJobSite(), vacancy.getTitle(), vacancy.getCompanyName(), vacancy.getCity(),
                                vacancy.getSalary(), vacancy.getDatePosted(), vacancy.getUrl(),
                                vacancy.getDescription())
                );
            }

            // Example: Export to JSON and print the first 500 characters
            if (!vacancies.isEmpty()) {
                byte[] jsonData = jobService.exportJobs(vacancies, ExportFormat.JSON);
                log.info("\n--- Exported JSON (first 500 chars) ---");
                log.info(new String(jsonData, 0, Math.min(jsonData.length, 500)));
            }

        } catch (Exception e) {
            log.error("An error occurred during job scraping or export: {}", e.getMessage(), e);
        } finally {
            // Manually shut down the orchestrator's executor service
            jobService.shutdown();
            log.info("JobScraperRunner finished.");
        }

    }

}
