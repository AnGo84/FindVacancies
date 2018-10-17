package ua.findvacancies.mvc.model;

import ua.findvacancies.mvc.vo.Vacancy;

import javax.xml.transform.sax.SAXSource;
import java.util.List;
import java.util.concurrent.Callable;

public class ProviderCallable implements Callable<List<Vacancy>> {
    private Strategy strategy;
    private String words;
    private int days;

    public ProviderCallable(Strategy strategy, String words, int days) {
        this.strategy = strategy;
        this.words = words;
        this.days = days;
    }

    @Override
    public List<Vacancy> call() throws Exception {
        System.out.println("Started parse strategy: " + strategy.getClass().getSimpleName());
        return strategy.getVacancies(words, days);
    }
}
