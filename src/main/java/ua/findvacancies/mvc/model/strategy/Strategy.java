package ua.findvacancies.mvc.model.strategy;

import ua.findvacancies.mvc.model.SearchParam;
import ua.findvacancies.mvc.model.Vacancy;

import java.util.List;

public interface Strategy
{
    List<Vacancy> getVacancies(SearchParam searchParam);
    Vacancy getVacancy(String vacancyURL);
}
