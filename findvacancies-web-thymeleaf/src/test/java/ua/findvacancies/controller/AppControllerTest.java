package ua.findvacancies.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ua.findvacancies.TestUtils;
import ua.findvacancies.model.Provider;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.service.VacancyService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppController.class)
class AppControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private VacancyService mockVacancyService;

	private final ObjectMapper objectMapper = new ObjectMapper();


	@Test
	public void whenGetHomePage() throws Exception {
		when(mockVacancyService.getDefaultViewSearchParams()).thenReturn(TestUtils.getDefaultViewSearchParams());

		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("viewSearchParams", equalTo(TestUtils.getDefaultViewSearchParams())))
				.andExpect(model().attribute("availableProviders", equalTo(Provider.values())));

	}

	@Test
	public void whenGetSearchVacanciesByWords() throws Exception {
		when(mockVacancyService.getDefaultViewSearchParams()).thenReturn(TestUtils.getDefaultViewSearchParams());

        /*mockMvc.perform(get("/searchVacancies"))
                .andExpect(status().isPermanentRedirect())
                .andExpect(forwardedUrl("/WEB-INF/view//index.jsp"));*/
		mockMvc.perform(get("/searchVacancies"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/"));
	}

	@Test
	public void whenPostSearchVacanciesByWords() throws Exception {
		when(mockVacancyService.getDefaultViewSearchParams()).thenReturn(TestUtils.getViewSearchParams());
		when(mockVacancyService.getVacancyList(any())).thenReturn(getVacancies());
		String viewSearchParams = objectMapper.writeValueAsString(TestUtils.getViewSearchParams());
		mockMvc.perform(post("/searchVacancies")
						.param("viewSearchParams", viewSearchParams)
						.flashAttr("viewSearchParams", TestUtils.getViewSearchParams()))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(model().attribute("resultVacanciesList", hasSize(2)));
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
