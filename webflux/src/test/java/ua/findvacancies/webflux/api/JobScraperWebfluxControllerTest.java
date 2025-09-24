package ua.findvacancies.webflux.api;

import org.junit.jupiter.api.BeforeEach;
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

@WebFluxTest(JobScraperWebfluxController.class)
class JobScraperWebfluxControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private JobService jobService;

    private Set<JobSite> availableSites;
    private List<JobVacancy> mockVacancies;

    @BeforeEach
    void setUp(ApplicationContext context) {
        this.webTestClient = WebTestClient.bindToApplicationContext(context).build();
        availableSites = Set.of(JobSite.DOU_UA, JobSite.DJINNI_CO);

        mockVacancies = getJobVacancies();

    }

    @Test
    void testGetAvailableSites() {
        Mockito.when(jobService.getAvailableJobSites()).thenReturn(availableSites);

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
        Mockito.when(jobService.getJobs(Mockito.any(), Mockito.any())).thenReturn(mockVacancies);

        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/api/v1/jobs")
                        .queryParam("keywords", "java")
                        //.queryParam("sites", "djinni.co")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(JobVacancy.class)
                .hasSize(2)
                .isEqualTo(mockVacancies);
    }

    private List<JobVacancy> getJobVacancies() {
        final var vacancy1 = JobVacancy.builder()
                .title("Java Developer")
                .jobSite(JobSite.DJINNI_CO)
                .city("city1")
                .datePosted(LocalDate.now())
                .build();

        JobVacancy vacancy2 = JobVacancy.builder()
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