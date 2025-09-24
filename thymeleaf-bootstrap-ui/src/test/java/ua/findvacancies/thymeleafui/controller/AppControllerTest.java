package ua.findvacancies.thymeleafui.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.LocaleResolver;
import ua.findvacancies.common.mapper.JobSearchCriteriaMapper;
import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.model.dto.SearchCriteriaDto;
import ua.findvacancies.common.service.JobService;
import ua.findvacancies.common.store.DataStore;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AppController.class)
@DisplayName("AppController Web Tests")
class AppControllerTest {

    private static final Set<JobSite> AVAILABLE_SITES = Set.of(JobSite.DJINNI_CO, JobSite.WORK_UA);

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobService mockJobService;

    @MockitoBean
    private JobSearchCriteriaMapper mockJobSearchCriteriaMapper;

    @MockitoBean
    private LocaleResolver mockLocaleResolver;

    @MockitoBean
    private DataStore<JobVacancy> mockDataStore;

    @Autowired
    private MessageSource messageSource;

    @BeforeEach
    void beforeAll() {
        Mockito.when(mockLocaleResolver.resolveLocale(Mockito.any())).thenReturn(Locale.ENGLISH);
        Mockito.when(mockJobService.getAvailableJobSites()).thenReturn(AVAILABLE_SITES);
    }

    @Test
    @DisplayName("Should return the index page with search criteria in the model")
    void testIndexPage() throws Exception {
        // Given
        Mockito.when(mockDataStore.getAll()).thenReturn(Collections.emptyList());

        // When
        mockMvc.perform(get("/"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("currentDate"))
                .andExpect(model().attributeExists("searchCriteria"))
                .andExpect(model().attribute("searchCriteria", equalTo(new SearchCriteriaDto())))
                .andExpect(model().attribute("availableJobSites", equalTo(AVAILABLE_SITES)));

        // Then
        Mockito.verify(mockDataStore, Mockito.only()).getAll();
    }

    @Test
    @DisplayName("Should perform a search and return results on success")
    void testSearch_Success() throws Exception {
        // Given
        List<JobVacancy> testJobVacanciesList = getTestJobVacanciesList();

        Mockito.when(mockJobSearchCriteriaMapper.toEntity(Mockito.any(SearchCriteriaDto.class)))
                .thenReturn(JobSearchCriteria.builder().build());
        Mockito.when(mockJobService.getJobs(Mockito.any(JobSearchCriteria.class), Mockito.any()))
                .thenReturn(testJobVacanciesList);

        // When
        mockMvc.perform(post("/search")
                        .param("searchLine", "Java Developer")
                        .param("sites", JobSite.DJINNI_CO.toString())
                )
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("jobVacancies"))
                .andExpect(model().attribute("jobVacancies", hasSize(1)))
                .andExpect(model().attributeExists("searchResultsMessage"));

        // Then
        Mockito.verify(mockDataStore, Mockito.times(1)).clear();
        ArgumentCaptor<List> listCaptor = ArgumentCaptor.forClass(List.class);
        Mockito.verify(mockDataStore, Mockito.times(1)).addAll(listCaptor.capture());
        Assertions.assertEquals(testJobVacanciesList, listCaptor.getValue());
    }

    @Test
    @DisplayName("Should return to the form page on validation errors")
    void testSearch_ValidationErrors() throws Exception {
        // Given
        BindingResult bindingResult = Mockito.mock(BindingResult.class);
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        // When
        mockMvc.perform(post("/search")
                        .param("searchLine", ""))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().hasErrors())
                .andExpect(model().attributeHasFieldErrors("searchCriteria", "searchLine", "sites"));

        // Then
        Mockito.verify(mockJobService, Mockito.never()).getJobs(Mockito.any(), Mockito.any());
        Mockito.verify(mockDataStore, Mockito.never()).clear();
        Mockito.verify(mockDataStore, Mockito.never()).addAll(Mockito.any());
    }

    @Test
    @DisplayName("Should export jobs and return the file content")
    void testExportJobs_Success() throws Exception {
        // Given
        List<JobVacancy> testJobVacanciesList = getTestJobVacanciesList();

        byte[] dummyContent = "dummy file content".getBytes();

        Mockito.when(mockDataStore.getAll()).thenReturn(testJobVacanciesList);
        Mockito.when(mockJobService.exportJobs(Mockito.any(), Mockito.eq(ExportFormat.JSON)))
                .thenReturn(dummyContent);

        // When | Then
        mockMvc.perform(get("/export")
                        .param("format", ExportFormat.JSON.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"job_vacancies.json\""))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().bytes(dummyContent));

        // Then
        Mockito.verify(mockDataStore, Mockito.only()).getAll();
        Mockito.verify(mockDataStore, Mockito.never()).clear();
    }

    @Test
    @DisplayName("Should return a bad request if no vacancies are available for export")
    void testExportJobs_NoVacancies() throws Exception {
        // Given
        Mockito.when(mockDataStore.getAll()).thenReturn(Collections.emptyList());

        // When
        mockMvc.perform(get("/export")
                        .sessionAttr("jobVacancies", Collections.emptyList())
                        .param("format", "XML"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("No data to export"));

        // Then
        Mockito.verify(mockDataStore, Mockito.only()).getAll();
        Mockito.verify(mockDataStore, Mockito.never()).clear();
    }

    private List<JobVacancy> getTestJobVacanciesList() {
        return List.of(JobVacancy.builder().title("Test Job").jobSite(JobSite.DJINNI_CO).build());
    }

}
