package ua.findvacancies.service;

import ua.findvacancies.model.Vacancy;

import java.util.List;

public interface VacancyDBService {

	int save(Vacancy vacancy);

	List findAll();

}
