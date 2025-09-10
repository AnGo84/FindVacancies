package ua.findvacancies.common.scraper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.scraper.impl.DjinniJobScraper;
import ua.findvacancies.common.scraper.impl.WorkUaJobScraper;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("JobScrapingOrchestrator Unit Tests")
class JobScrapingOrchestratorTest {

    private static final DjinniJobScraper mockDjinniJobScraper = Mockito.mock(DjinniJobScraper.class);
    private static final WorkUaJobScraper mockWorkUaJobScraper = Mockito.mock(WorkUaJobScraper.class);
    private static final JobScraperRegistry mockJobScraperRegistry = Mockito.mock(JobScraperRegistry.class);

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);
    //private final ExecutorService mockExecutorService = Mockito.mock(ExecutorService.class);
    private final JobScrapingOrchestrator jobScrapingOrchestrator =
            new JobScrapingOrchestrator(mockJobScraperRegistry, executorService);

    @BeforeAll
    static void setUpBeforeAll() {
        when(mockDjinniJobScraper.getJobSite()).thenReturn(JobSite.DJINNI_CO);
        when(mockWorkUaJobScraper.getJobSite()).thenReturn(JobSite.WORK_UA);

        when(mockJobScraperRegistry.getScrapers(Set.of(JobSite.DJINNI_CO, JobSite.WORK_UA)))
                .thenReturn(Set.of(mockDjinniJobScraper, mockWorkUaJobScraper));
    }

    @BeforeEach
    void setUp() throws InterruptedException {
        // Mock the ExecutorService to immediately run the tasks,
        // so we don't have to deal with threads in the test
        /*when(mockExecutorService.invokeAll(anyList())).thenAnswer(invocation -> {
            List<Callable<List<JobVacancy>>> tasks = invocation.getArgument(0);
            return tasks.stream()
                    .map(task -> {
                        try {
                            return (Future<List<JobVacancy>>) mock(Future.class, RETURNS_DEEP_STUBS);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .toList();
        });*/
    }

    @Test
    @DisplayName("Should successfully scrape jobs from multiple sites and combine results")
    void shouldScrapeJobsFromMultipleSites() {
        // Given
        // Mock a successful response from each scraper
        List<JobVacancy> djinniVacancies = List.of(
                JobVacancy.builder().title("Djinni Job 1").jobSite(JobSite.DJINNI_CO).build(),
                JobVacancy.builder().title("Djinni Job 2").jobSite(JobSite.DJINNI_CO).build()
        );
        when(mockDjinniJobScraper.scrapeJobs(any(JobSearchCriteria.class))).thenReturn(djinniVacancies);

        List<JobVacancy> workUaVacancies = List.of(
                JobVacancy.builder().title("Work.ua Job 1").jobSite(JobSite.WORK_UA).build()
        );
        when(mockWorkUaJobScraper.scrapeJobs(any(JobSearchCriteria.class))).thenReturn(workUaVacancies);

        // Define a search criteria
        JobSearchCriteria criteria = JobSearchCriteria.builder().build();

        // When
        List<JobVacancy> allVacancies =
                jobScrapingOrchestrator.scrapeJobs(criteria, Set.of(JobSite.WORK_UA, JobSite.DJINNI_CO));

        // Then
        verify(mockDjinniJobScraper, times(1)).scrapeJobs(criteria);
        verify(mockWorkUaJobScraper, times(1)).scrapeJobs(criteria);

        assertNotNull(allVacancies);
        assertEquals(3, allVacancies.size());
        assertTrue(allVacancies.stream().anyMatch(v -> v.getTitle().equals("Djinni Job 1")));
        assertTrue(allVacancies.stream().anyMatch(v -> v.getTitle().equals("Work.ua Job 1")));
    }

    @Test
    @DisplayName("Should handle a scraper failure gracefully and continue with others")
    void shouldHandleScraperFailure() {
        // Given
        when(mockDjinniJobScraper.scrapeJobs(any(JobSearchCriteria.class)))
                .thenThrow(new RuntimeException("Djinni site is down"));

        List<JobVacancy> workUaVacancies = List.of(
                JobVacancy.builder().title("Work.ua Job 1").jobSite(JobSite.WORK_UA).build()
        );
        when(mockWorkUaJobScraper.scrapeJobs(any(JobSearchCriteria.class))).thenReturn(workUaVacancies);

        JobSearchCriteria criteria = JobSearchCriteria.builder().build();

        // When
        List<JobVacancy> allVacancies =
                jobScrapingOrchestrator.scrapeJobs(criteria, Set.of(JobSite.WORK_UA, JobSite.DJINNI_CO));

        // Then
        //verify(mockDjinniJobScraper, times(1)).scrapeJobs(criteria);
        //verify(mockWorkUaJobScraper, times(1)).scrapeJobs(criteria);

        assertNotNull(allVacancies);
        assertFalse(allVacancies.isEmpty());
        assertEquals(1, allVacancies.size());
        assertEquals("Work.ua Job 1", allVacancies.getFirst().getTitle());
    }

    @Test
    @DisplayName("Should return the correct set of available job sites from the registry")
    void shouldGetAvailableJobSites() {
        // Given
        when(mockJobScraperRegistry.getAvailableJobSites()).thenReturn(Set.of(JobSite.DJINNI_CO, JobSite.WORK_UA));

        // When
        Set<String> availableSites = jobScrapingOrchestrator.getAvailableJobSites();

        // Then
        assertNotNull(availableSites);
        assertEquals(2, availableSites.size());
        assertTrue(availableSites.contains(JobSite.DJINNI_CO.getSiteName()));
        assertTrue(availableSites.contains(JobSite.WORK_UA.getSiteName()));
    }

}
