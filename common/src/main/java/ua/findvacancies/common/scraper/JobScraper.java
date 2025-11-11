package ua.findvacancies.common.scraper;

import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;

import java.util.List;

/**
 * Interface for job scraping services.
 * Defines a common contract for all classes that scrape job vacancies
 * from different job boards.
 */
public interface JobScraper {

    /**
     * Scrapes job vacancies from a specific job board based on provided search criteria.
     *
     * @param criteria The {@link JobSearchCriteria} to apply for filtering.
     * @return A list of {@link JobVacancy} objects found on the job board that match the criteria.
     */
    List<JobVacancy> scrapeJobs(JobSearchCriteria criteria);

    /**
     * Returns the {@link JobSite} enum representing the job site this scraper is responsible for.
     * This provides a type-safe identifier for the scraper.
     * @return The {@link JobSite} of the job site.
     */
    JobSite getJobSite();
}

