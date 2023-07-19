package ua.findvacancies.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.model.viewdata.ViewSearchParams;
import ua.findvacancies.service.VacancyService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = RESTController.class)
class RESTControllerTest {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VacancyService mockVacancyService;

    @Test
    public void whenGetVacanciesByValidViewSearchParams() throws Exception {
        String viewSearchParamsAsJSON = "{ \"searchLine\" : \"java\", \"days\": 5, \"sites\":[\"dou\"] }";

        Mockito.when(mockVacancyService.getVacancyList(Mockito.any(ViewSearchParams.class))).thenReturn(getVacancies());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/vacancies")
                .accept(MediaType.APPLICATION_JSON).content(viewSearchParamsAsJSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.OK.value(), response.getStatus());

        String contentAsString = result.getResponse().getContentAsString();

        List<Vacancy> responseVacancies = OBJECT_MAPPER.readValue(contentAsString, new TypeReference<>() {
        });

        assertNotNull(responseVacancies);
        assertFalse(responseVacancies.isEmpty());
        assertEquals(2, responseVacancies.size());

    }

    @Test
    public void whenGetVacanciesByInvalidViewSearchParams() throws Exception {
        String viewSearchParamsAsJSON = "{ }";

        Mockito.when(mockVacancyService.getVacancyList(Mockito.any(ViewSearchParams.class))).thenReturn(getVacancies());

        RequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/api/vacancies")
                .accept(MediaType.APPLICATION_JSON).content(viewSearchParamsAsJSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mockMvc.perform(requestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andReturn();

        MockHttpServletResponse response = result.getResponse();

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());

        String contentAsString = result.getResponse().getContentAsString();

        Map<String, List<String>> responseErrors = OBJECT_MAPPER.readValue(contentAsString, new TypeReference<>() {
        });

        assertNotNull(responseErrors);
        assertFalse(responseErrors.isEmpty());
        assertEquals(1, responseErrors.keySet().size());
        assertEquals(3, responseErrors.get("errors").size());

    }

    private List<Vacancy> getVacancies() {
        List<Vacancy> vacancies = new ArrayList<>();
        Vacancy vacancy1 = Vacancy.builder()
                .city("city")
                .companyName("companyName")
                .date(new Date())
                .salary("salary")
                .siteName("siteName")
                .title("title")
                .isHot(true)
                .build();
        Vacancy vacancy2 = Vacancy.builder()
                .city("city2")
                .companyName("companyName2")
                .date(new Date())
                .salary("salary2")
                .siteName("siteName2")
                .title("title2")
                .isHot(false)
                .build();
        vacancies.add(vacancy1);
        vacancies.add(vacancy2);
        return vacancies;
    }

}
