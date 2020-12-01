package ua.findvacancies.model;

import lombok.extern.slf4j.Slf4j;
import ua.findvacancies.model.strategy.Strategy;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
public class ProviderCallable implements Callable<List<Vacancy>> {
    private Strategy strategy;
    private SearchParam searchParam;

    public ProviderCallable(Strategy strategy, SearchParam searchParam) {
        this.strategy = strategy;
        this.searchParam = searchParam;
    }

    @Override
    public List<Vacancy> call() throws Exception {
        log.debug("Started parse strategy: {}" + strategy.getClass().getSimpleName());
        return strategy.getVacancies(searchParam);
    }
}
