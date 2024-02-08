package ua.findvacancieswebflux.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.model.viewdata.ViewSearchParams;
import ua.findvacancies.service.VacancyService;

import java.util.List;

//https://www.baeldung.com/spring-webflux-cors
@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class FindVacanciesController {

	private final VacancyService vacancyService;

	@PostMapping(value = "/vacancies")
	@ResponseStatus(HttpStatus.OK)
	public Flux<Vacancy> getVacanciesByViewSearchParams(@Valid @RequestBody final ViewSearchParams viewSearchParams) {
		log.info("New search: {}", viewSearchParams);

		List<Vacancy> vacancyList = vacancyService.getVacancyList(viewSearchParams);
		log.info("For search '{}' returns {} records", viewSearchParams, vacancyList.size());
		return Flux.fromIterable(vacancyList);

	}

}
