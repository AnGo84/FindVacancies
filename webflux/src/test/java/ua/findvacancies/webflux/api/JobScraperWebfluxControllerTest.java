package ua.findvacancies.webflux.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.service.JobService;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebFluxTest(JobScraperWebfluxController.class)
class JobScraperWebfluxControllerTest {

    private static final Set<JobSite> availableSites = Set.of(JobSite.DOU_UA, JobSite.DJINNI_CO);

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private JobService jobService;

    @BeforeEach
    void setUp(ApplicationContext context) {
        this.webTestClient = WebTestClient.bindToApplicationContext(context).build();
    }

    @Test
    void testGetAvailableSites() {
        // Given
        Mockito.when(jobService.getAvailableJobSites()).thenReturn(availableSites);

        // When | Then
        webTestClient.get().uri("/api/v1/jobs/sites")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(Set.class)
                .isEqualTo(Set.of("DOU_UA", "DJINNI_CO"));
    }

    @Test
    void testSearchJobs() {
        // Given
        var mockVacancies = getJobVacancies();
        Mockito.when(jobService.getJobs(Mockito.any(), Mockito.any())).thenReturn(mockVacancies);

        // When | Then
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/v1/jobs")
                        .queryParam("keywords", "java")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(JobVacancy.class)
                .hasSize(2)
                .isEqualTo(mockVacancies);
    }

    @Test
    @DisplayName("Should return 400 Bad Request when keywords are missing")
    void testGetJobs_MissingKeywords() {
        // When & Then
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/v1/jobs").build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

    private List<JobVacancy> getJobVacancies() {
        final var vacancy1 = JobVacancy.builder()
                .title("Java Developer")
                .jobSite(JobSite.DJINNI_CO)
                .city("city1")
                .datePosted(LocalDate.now())
                .build();

        final var vacancy2 = JobVacancy.builder()
                .title("title2")
                .jobSite(JobSite.WORK_UA)
                .city("city2")
                .url("http://work.ua/company-2-job-2")
                .companyName("companyName2")
                .datePosted(LocalDate.now().minusDays(1))
                .salary("salary2")
                .isHot(true)
                .build();
        return List.of(vacancy1, vacancy2);
    }
}