package ua.findvacancies.common.service;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import ua.findvacancies.common.export.JobExportService;
import ua.findvacancies.common.export.JobExportServiceRegistry;
import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.scraper.JobScrapingOrchestrator;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Implementation of the JobService interface.
 * This service encapsulates the business logic for scraping and exporting job vacancies,
 * delegating to the JobScrapingOrchestrator and JobExportServiceRegistry.
 */
@Slf4j
public class JobService {

    private final JobScrapingOrchestrator jobScrapingOrchestrator;
    private final JobExportServiceRegistry jobExportServiceRegistry;

    public JobService(JobScrapingOrchestrator jobScrapingOrchestrator,
                      JobExportServiceRegistry jobExportServiceRegistry) {
        this.jobScrapingOrchestrator = jobScrapingOrchestrator;
        this.jobExportServiceRegistry = jobExportServiceRegistry;
    }

    /**
     * Scrapes job vacancies based on the provided criteria and target sites.
     *
     * @param criteria    The search criteria.
     * @param targetSites A set of job sites to scrape. If null or empty, all sites will be scraped.
     * @return A list of matching JobVacancy objects.
     */
    public List<JobVacancy> getJobs(JobSearchCriteria criteria, Set<JobSite> targetSites) {
        log.info("JobService: Scraping jobs with criteria: {} and target sites: {}", criteria, targetSites);
        return jobScrapingOrchestrator.scrapeJobs(criteria, targetSites);
    }

    /**
     * Exports a collection of job vacancies in the specified format.
     *
     * @param vacancies The collection of JobVacancy objects to export.
     * @param format    The desired export format enum.
     * @return A byte array containing the exported data.
     * For text formats, this will be UTF-8 encoded string bytes.
     * For binary formats, this will be the raw binary content.
     */
    public byte[] exportJobs(Collection<JobVacancy> vacancies, ExportFormat format) {
        log.info("JobService: Exporting {} vacancies in {} format.", vacancies.size(), format.getFileExtension());
        Optional<JobExportService> exporterOptional = jobExportServiceRegistry.getExporter(format);

        if (exporterOptional.isEmpty()) {
            log.warn("JobService: Unsupported export format requested: {}", format.getFileExtension());
            return new byte[0];
        }

        JobExportService exporter = exporterOptional.get();
        return exporter.export(vacancies);
    }

    /**
     * Retrieves a set of all available job site names.
     *
     * @return A list of strings representing available job site names.
     */
    public Set<JobSite> getAvailableJobSites() {
        return jobScrapingOrchestrator.getAvailableJobSites();
    }

    /**
     * Retrieves a set of all available export format names.
     *
     * @return A list of strings representing available export format names.
     */
    public Set<ExportFormat> getAvailableExportFormats() {
        return jobExportServiceRegistry.getAllExportFormats();
    }

    /**
     * Method should shut down the JobScrapingOrchestrator gracefully.
     */
    @PreDestroy
    public void shutdown() {
        jobScrapingOrchestrator.shutdown();
    }

}
