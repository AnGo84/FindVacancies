package ua.findvacancies.thymeleafxtmxui.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.service.JobService;
import ua.findvacancies.common.store.DataStore;

import java.util.List;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AppController.class)
@DisplayName("AppController MVC Integration Tests")
class AppControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobService jobService;
    @MockitoBean
    private DataStore<JobVacancy> dataStore;

    @Test
    @DisplayName("GET /: Should handle locale change and display correct title")
    void indexPage_shouldChangeLocaleWhenLangParamIsPresent() throws Exception {
        // 1. Test default locale (English)
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Job Vacancy Search")));

        // 2. Test locale change to Ukrainian ('uk')
        mockMvc.perform(get("/").param("lang", "uk"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Пошук вакансій")));
    }

    @Test
    @DisplayName("POST /search: Should return search-results fragment on successful search")
    void search_shouldReturnSearchResultsFragmentOnSuccess() throws Exception {
        // Given
        var testJobVacanciesList = getTestJobVacanciesList();
        Mockito.when(jobService.getJobs(Mockito.any(), Mockito.any()))
                .thenReturn(testJobVacanciesList);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("searchLine", "Java");
        params.add("days", "7");
        params.add("sites", JobSite.WORK_UA.name());

        // When | Then
        mockMvc.perform(post("/search")
                        .params(params)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("fragments/search-results :: resultsTable"))
                .andExpect(model().attributeExists("jobVacancies"))
                .andExpect(model().attribute("jobVacancies", Matchers.hasSize(1)));
    }

    @Test
    @DisplayName("POST /search: Should return search-form fragment on validation failure")
    void search_shouldReturnSearchFormFragmentOnValidationError() throws Exception {
        // Given
        MultiValueMap<String, String> invalidParams = new LinkedMultiValueMap<>();
        invalidParams.add("searchLine", "");
        invalidParams.add("days", "0");
        invalidParams.add("sites", "DOU");

        //When | Then
        mockMvc.perform(post("/search")
                        .params(invalidParams)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .locale(Locale.ENGLISH))
                .andExpect(status().isOk())
                .andExpect(view().name("fragments/search-form :: formContent"))
                .andExpect(model().attributeHasFieldErrors("searchCriteria", "searchLine", "sites"));
    }

    @Test
    @DisplayName("GET /export: Should trigger file download for XLSX format")
    void exportJobs_shouldReturnXlsxFile() throws Exception {
        // Given
        Mockito.when(dataStore.getAll()).thenReturn(getTestJobVacanciesList());

        byte[] mockFileContent = "test content".getBytes();
        Mockito.when(jobService.exportJobs(Mockito.any(), Mockito.any(ExportFormat.class)))
                .thenReturn(mockFileContent);

        // When | Then
        mockMvc.perform(get("/export").param("format", "XLSX"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(mockFileContent))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"job_vacancies.xlsx\""))
                .andExpect(content().contentType(
                        MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")));
    }

    private List<JobVacancy> getTestJobVacanciesList() {
        return List.of(JobVacancy.builder().title("Test Job").jobSite(JobSite.DJINNI_CO).build());
    }

}
