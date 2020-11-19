package ua.findvacancies.model.strategy;

import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.Vacancy;

import java.util.List;

public interface Strategy
{
    List<Vacancy> getVacancies(SearchParam searchParam);
    Vacancy getVacancy(String vacancyURL);
}
