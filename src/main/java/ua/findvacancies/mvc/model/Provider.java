package ua.findvacancies.mvc.model;


import ua.findvacancies.mvc.vo.Vacancy;

import java.util.List;

/**
 * Created by AnGo on 04.05.2016.
 */
public class Provider
{
    private Strategy strategy;

    public Provider(Strategy strategy)
    {
        this.strategy = strategy;
    }

    public void setStrategy(Strategy strategy)
    {
        this.strategy = strategy;
    }

    public List<Vacancy> getJavaVacancies(String words, int days){
        return strategy.getVacancies(words, days);
    }
}
