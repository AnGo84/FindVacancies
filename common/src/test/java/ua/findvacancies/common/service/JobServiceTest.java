package ua.findvacancies.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.findvacancies.common.TestDataFactory;
import ua.findvacancies.common.export.JobExportService;
import ua.findvacancies.common.export.JobExportServiceRegistry;
import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.scraper.JobScrapingOrchestrator;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("JobService Unit Tests")
class JobServiceTest {

    @Mock
    private JobScrapingOrchestrator jobScrapingOrchestrator;

    @Mock
    private JobExportServiceRegistry jobExportServiceRegistry;

    @Mock
    private JobExportService jobExportService;

    @InjectMocks
    private JobService jobService;

    private JobSearchCriteria criteria;
    private List<JobVacancy> vacancies;
    private Set<JobSite> sites;

    @BeforeEach
    void setUp() {
        criteria = JobSearchCriteria.builder()
                .keywords(Set.of("Java"))
                .build();
        sites = Set.of(JobSite.DOU_UA);

        vacancies = List.of(
                TestDataFactory.buildJovVacancy("Java Engineer"),
                TestDataFactory.buildJovVacancy("QA Engineer")
        );
    }

    @Test
    @DisplayName("Should retrieve and filter vacancies from the orchestrator")
    void shouldRetrieveAndFilterVacancies() {
        // Given that the orchestrator returns a list of vacancies
        when(jobScrapingOrchestrator.scrapeJobs(criteria, sites)).thenReturn(vacancies);

        // When the service's getVacancies method is called
        List<JobVacancy> result = jobService.getJobs(criteria, sites);

        // Then the result should match the list from the orchestrator
        assertEquals(vacancies.size(), result.size());
        assertEquals(vacancies.get(0).getTitle(), result.get(0).getTitle());
        assertEquals(vacancies.get(1).getTitle(), result.get(1).getTitle());

        // Verify that the orchestrator was called with the correct parameters
        verify(jobScrapingOrchestrator).scrapeJobs(criteria, sites);
    }

    @Test
    @DisplayName("Should export vacancies using the correct exporter")
    void shouldExportVacancies() {
        // Given an export format and a list of vacancies
        ExportFormat format = ExportFormat.JSON;
        byte[] expectedBytes = "json data".getBytes();

        // Configure the registry to return the mock exporter for the given format
        when(jobExportServiceRegistry.getExporter(format)).thenReturn(Optional.of(jobExportService));

        // Configure the mock exporter to return the expected byte array
        when(jobExportService.export(vacancies)).thenReturn(expectedBytes);

        // When the exportJobs method is called
        byte[] result = jobService.exportJobs(vacancies, format);

        // Then the result should match the expected byte array
        assertArrayEquals(expectedBytes, result);

        // Verify that the registry and exporter were called as expected
        verify(jobExportServiceRegistry).getExporter(format);
        verify(jobExportService).export(vacancies);
    }

    @Test
    @DisplayName("Should return an empty byte array for unsupported export format")
    void shouldReturnEmptyByteArrayForUnsupportedFormat() {
        // Given that the registry returns an empty Optional (unsupported format)
        ExportFormat format = ExportFormat.XLSX;
        when(jobExportServiceRegistry.getExporter(format)).thenReturn(Optional.empty());

        // When the exportJobs method is called
        byte[] result = jobService.exportJobs(vacancies, format);

        // Then the result should be an empty byte array
        assertArrayEquals(new byte[0], result);

        // Verify that the exporter was never called
        verify(jobExportServiceRegistry).getExporter(format);
        verifyNoInteractions(jobExportService);
    }

    @Test
    @DisplayName("Should retrieve available job sites from the orchestrator")
    void shouldRetrieveAvailableJobSites() {
        // Given a set of available sites from the orchestrator
        Set<JobSite> availableSites = Set.of(JobSite.DOU_UA, JobSite.WORK_UA);
        when(jobScrapingOrchestrator.getAvailableJobSites()).thenReturn(availableSites);

        // When the service's method is called
        Set<JobSite> result = jobService.getAvailableJobSites();

        // Then the result should match the sites from the orchestrator
        assertEquals(availableSites, result);

        // Verify that the orchestrator method was called
        verify(jobScrapingOrchestrator).getAvailableJobSites();
    }

    @Test
    @DisplayName("Should retrieve available export formats from the registry")
    void shouldRetrieveAvailableExportFormats() {
        // Given a set of available export formats from the registry
        Set<ExportFormat> availableFormats = Set.of(ExportFormat.JSON, ExportFormat.XML);
        when(jobExportServiceRegistry.getAllExportFormats()).thenReturn(availableFormats);

        // When the service's method is called
        Set<ExportFormat> result = jobService.getAvailableExportFormats();

        // Then the result should be a set of format names
        assertEquals(2, result.size());
        assertTrue(result.containsAll(availableFormats));

        // Verify that the registry method was called
        verify(jobExportServiceRegistry).getAllExportFormats();
    }

    @Test
    @DisplayName("Should shut down the orchestrator")
    void shouldShutdownOrchestrator() {
        // When the shutdown method is called
        jobService.shutdown();

        // Then the orchestrator's shutdown method should be called
        verify(jobScrapingOrchestrator).shutdown();
    }

}
