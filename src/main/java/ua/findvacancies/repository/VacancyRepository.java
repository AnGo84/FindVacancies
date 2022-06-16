package ua.findvacancies.repository;

import ua.findvacancies.model.Vacancy;

import java.util.List;

public interface VacancyRepository {

	int save(Vacancy vacancy);

	List findAll();

}

