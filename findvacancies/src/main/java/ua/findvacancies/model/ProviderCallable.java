package ua.findvacancies.model;

import lombok.extern.slf4j.Slf4j;
import ua.findvacancies.model.strategy.Strategy;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
public class ProviderCallable implements Callable<List<Vacancy>> {
    private final Strategy strategy;
    private final SearchParam searchParam;

    public ProviderCallable(Strategy strategy, SearchParam searchParam) {
        this.strategy = strategy;
        this.searchParam = searchParam;
    }

    @Override
    public List<Vacancy> call() throws Exception {
        log.info("Start parse strategy: {}", strategy.getClass().getSimpleName());
        List<Vacancy> vacancies = strategy.getVacancies(searchParam);
        log.info("Finish parse strategy: {}; result items: {}", strategy.getClass().getSimpleName(), vacancies.size());
        return vacancies;
    }
}
