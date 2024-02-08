package ua.findvacancieswebflux.controller;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.model.viewdata.ViewSearchParams;
import ua.findvacancies.service.VacancyService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = FindVacanciesController.class)
class FindVacanciesControllerTest {

	@Autowired
	private WebTestClient webClient;

	@MockBean
	private VacancyService mockVacancyService;

	@Test
	public void whenGetVacanciesByValidViewSearchParams() {
		List<Vacancy> expectedVacanciesList = getVacancies();
		ViewSearchParams viewSearchParams = new ViewSearchParams("java", "5", Set.of("dou"));

		Mockito.when(mockVacancyService.getVacancyList(Mockito.any(ViewSearchParams.class))).thenReturn(expectedVacanciesList);

		webClient.post()
				.uri("/api/vacancies")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromObject(viewSearchParams))
				.exchange()
				.expectStatus().isOk()
				.expectBodyList(Vacancy.class)
				.contains(expectedVacanciesList.get(0))
				.hasSize(2);

		Mockito.verify(mockVacancyService, times(1)).getVacancyList(eq(viewSearchParams));

	}

	@Test
	public void whenGetVacanciesByInvalidViewSearchParams() throws Exception {
		ViewSearchParams viewSearchParams = new ViewSearchParams();

		webClient.post()
				.uri("/api/vacancies")
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromObject(viewSearchParams))
				.exchange()
				.expectStatus().isBadRequest()

				.expectBody()
				.consumeWith(System.out::println)
				.jsonPath("$.errors").isMap()
				.jsonPath("$.errors.length()").isEqualTo(3)
				.jsonPath("$.errors").value(Matchers.hasKey("searchLine"))
				.jsonPath("$.errors").value(Matchers.hasKey("days"))
				.jsonPath("$.errors").value(Matchers.hasKey("sites"));

		Mockito.verify(mockVacancyService, never()).getVacancyList(eq(viewSearchParams));

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