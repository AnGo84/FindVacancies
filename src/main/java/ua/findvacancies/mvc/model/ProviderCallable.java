package ua.findvacancies.mvc.model;

import ua.findvacancies.mvc.viewdata.Vacancy;

import java.util.List;
import java.util.concurrent.Callable;

public class ProviderCallable implements Callable<List<Vacancy>> {
    private Strategy strategy;
    private SearchParam searchParam;

    public ProviderCallable(Strategy strategy, SearchParam searchParam) {
        this.strategy = strategy;
        this.searchParam = searchParam;
    }

    @Override
    public List<Vacancy> call() throws Exception {
        System.out.println("Started parse strategy: " + strategy.getClass().getSimpleName());
        return strategy.getVacancies(searchParam);
    }
}
