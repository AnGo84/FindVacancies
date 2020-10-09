package ua.findvacancies.mvc.model;

import ua.findvacancies.mvc.viewdata.Vacancy;

import java.util.List;

public interface Strategy
{
    //List<Vacancy> getVacancies(String words, int days);
    List<Vacancy> getVacancies(SearchParam searchParam);
}
