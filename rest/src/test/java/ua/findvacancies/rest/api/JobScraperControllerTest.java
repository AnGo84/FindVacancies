package ua.findvacancies.rest.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.service.JobService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(JobScraperController.class)
@DisplayName("JobScraperController Tests")
class JobScraperControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobService jobService;

    @Test
    @DisplayName("Should return a list of jobs with 200 OK status")
    void testGetJobs_Success() throws Exception {
        // Given
        var expectedVacancy = JobVacancy.builder()
                .title("Java Developer")
                .jobSite(JobSite.DJINNI_CO)
                .url("http://djinni.co/job-123")
                .datePosted(LocalDate.now())
                .build();

        List<JobVacancy> vacancies = Collections.singletonList(expectedVacancy);

        Mockito.when(jobService.getJobs(Mockito.any(), Mockito.any())).thenReturn(vacancies);

        // When & Then
        mockMvc.perform(get("/api/v1/jobs")
                        .param("keywords", "java")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].title").value(expectedVacancy.getTitle()))
                .andExpect(jsonPath("$[0].jobSite").value(expectedVacancy.getJobSite().name()));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when keywords are missing")
    void testGetJobs_MissingKeywords() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/jobs")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return a list of available sites with 200 OK status")
    void testGetAvailableSites_Success() throws Exception {
        // Given
        Set<JobSite> sites = Set.of(JobSite.DJINNI_CO, JobSite.WORK_UA);
        Mockito.when(jobService.getAvailableJobSites()).thenReturn(sites);

        // When & Then
        mockMvc.perform(get("/api/v1/jobs/sites")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$", containsInAnyOrder("DJINNI_CO", "WORK_UA")));
    }

}
