package ua.findvacancies.mvc.model;



import ua.findvacancies.mvc.vo.Vacancy;

import java.util.List;

/**
 * Created by AnGo on 04.05.2016.
 */
public interface Strategy
{
    List<Vacancy> getVacancies(String words, int days);
}
