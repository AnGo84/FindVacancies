package ua.findvacancies.common.service;

import ua.findvacancies.common.export.JobExportService;
import ua.findvacancies.common.export.JobExportServiceRegistry;
import ua.findvacancies.common.export.impl.ExcelExportService;
import ua.findvacancies.common.export.impl.JsonExportService;
import ua.findvacancies.common.export.impl.XmlExportService;
import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.scraper.JobScraper;
import ua.findvacancies.common.scraper.JobScraperRegistry;
import ua.findvacancies.common.scraper.JobScrapingOrchestrator;
import ua.findvacancies.common.scraper.JsoupDocumentFetcher;
import ua.findvacancies.common.scraper.impl.DjinniJobScraper;
import ua.findvacancies.common.scraper.impl.DouJobScraper;
import ua.findvacancies.common.scraper.impl.WorkUaJobScraper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A builder class for creating a highly configurable instance of the {@link JobService}.
 * This builder provides a fluent API to specify which job sites to scrape and which
 * formats to export to. It encapsulates the complex logic of assembling the
 * required {@link JobScrapingOrchestrator} and {@link JobExportServiceRegistry} instances.
 * <p>
 * The builder includes default values for all its components, allowing for
 * easy creation of a fully-featured {@link JobService} instance without
 * needing to specify every detail.
 */
public class JobServiceBuilder {

    private JsoupDocumentFetcher documentFetcher;
    private JobFilterService jobFilterService;
    private Set<JobSite> jobSites;
    private Set<ExportFormat> exportFormats;

    /**
     * Constructs a new builder with default components.
     * This is a great starting point, as it provides a fully functional
     * service with common defaults.
     */
    public JobServiceBuilder() {
        this.documentFetcher = new JsoupDocumentFetcher();
        this.jobFilterService = new JobFilterService();
        this.jobSites = getDefaultJobSites();
        this.exportFormats = getDefaultExportFormats();
    }

    /**
     * Gets the default set of job sites to scrape from.
     *
     * @return A set containing the default job sites.
     */
    private static Set<JobSite> getDefaultJobSites() {
        return Set.of(JobSite.DJINNI_CO, JobSite.WORK_UA, JobSite.DOU_UA);
    }

    /**
     * Gets the default set of export formats.
     * By default, it includes all available formats defined in the {@link ExportFormat} enum.
     *
     * @return A set of all default export formats.
     */
    private static Set<ExportFormat> getDefaultExportFormats() {
        return new HashSet<>(Arrays.asList(ExportFormat.values()));
    }

    /**
     * Creates a set of all default job scrapers, each initialized with the
     * provided document fetcher and filter service.
     *
     * @param documentFetcher  The fetcher to use for the scrapers.
     * @param jobFilterService The filter service to use for the scrapers.
     * @return A set containing all default {@link JobScraper} instances.
     */
    private static Set<JobScraper> getDefaultScrapers(final JsoupDocumentFetcher documentFetcher,
                                                      final JobFilterService jobFilterService) {
        return Set.of(new DjinniJobScraper(documentFetcher, jobFilterService),
                new WorkUaJobScraper(documentFetcher, jobFilterService),
                new DouJobScraper(jobFilterService));
    }

    /**
     * Creates a set of all default job export services.
     *
     * @return A set containing all default {@link JobExportService} instances.
     */
    private static Set<JobExportService> getDefaultExporters() {
        return Set.of(new JsonExportService(), new XmlExportService(), new ExcelExportService());
    }

    /**
     * Sets a custom JsoupDocumentFetcher to be used by the scrapers.
     *
     * @param documentFetcher The custom fetcher instance.
     * @return The current builder instance for method chaining.
     */
    public JobServiceBuilder withDocumentFetcher(final JsoupDocumentFetcher documentFetcher) {
        this.documentFetcher = documentFetcher;
        return this;
    }

    /**
     * Sets a custom JobFilterService to be used by the scrapers.
     *
     * @param jobFilterService The custom filter service instance.
     * @return The current builder instance for method chaining.
     */
    public JobServiceBuilder withJobFilterService(final JobFilterService jobFilterService) {
        this.jobFilterService = jobFilterService;
        return this;
    }

    /**
     * Specifies the set of job sites to scrape. A defensive copy of the set
     * is made to ensure immutability.
     *
     * @param jobSites The set of job sites to use.
     * @return The current builder instance for method chaining.
     */
    public JobServiceBuilder withJobSites(final Set<JobSite> jobSites) {
        this.jobSites = new HashSet<>(jobSites);
        return this;
    }

    /**
     * Specifies the set of export formats to use. A defensive copy of the set
     * is made to ensure immutability.
     *
     * @param exportFormats The set of export formats to use.
     * @return The current builder instance for method chaining.
     */
    public JobServiceBuilder withExportFormats(final Set<ExportFormat> exportFormats) {
        this.exportFormats = new HashSet<>(exportFormats);
        return this;
    }

    /**
     * Builds and returns a new {@link JobService} instance based on the
     * configurations provided to the builder. This method correctly assembles
     * all internal components and passes them to the JobService constructor.
     *
     * @return A fully configured {@link JobService} instance.
     * @throws IllegalStateException if the job sites or export formats sets are empty.
     */
    public JobService build() {
        if (jobSites.isEmpty()) {
            throw new IllegalStateException("Job sites set cannot be empty.");
        }
        if (exportFormats.isEmpty()) {
            throw new IllegalStateException("Export formats set cannot be empty.");
        }

        final Set<JobScraper> jobScrapers =
                getDefaultScrapers(documentFetcher, jobFilterService).stream()
                        .filter(scraper -> jobSites.contains(scraper.getJobSite()))
                        .collect(Collectors.toUnmodifiableSet());

        final JobScrapingOrchestrator jobOrchestrator =
                new JobScrapingOrchestrator(new JobScraperRegistry(jobScrapers));

        final Set<JobExportService> jobExportServices =
                getDefaultExporters().stream()
                        .filter(exporter -> exportFormats.contains(exporter.getFormat()))
                        .collect(Collectors.toUnmodifiableSet());

        final JobExportServiceRegistry jobExportServiceRegistry = new JobExportServiceRegistry(jobExportServices);

        return new JobService(jobOrchestrator, jobExportServiceRegistry);
    }

}
