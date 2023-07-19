package ua.findvacancies.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.model.viewdata.ViewSearchParams;
import ua.findvacancies.service.VacancyService;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/")
@Slf4j
public class RESTController {

    private final VacancyService vacancyService;

    @PostMapping(value = "/vacancies")
    public ResponseEntity<List<Vacancy>> getVacanciesByViewSearchParams(@Valid @RequestBody final ViewSearchParams viewSearchParams) {
        log.info("New search: {}", viewSearchParams);

        List<Vacancy> vacancyList = vacancyService.getVacancyList(viewSearchParams);
        return new ResponseEntity<>(vacancyList, HttpStatus.OK);
    }

}
