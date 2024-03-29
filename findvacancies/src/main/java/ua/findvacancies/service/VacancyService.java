package ua.findvacancies.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.findvacancies.comparators.ComparatorVacanciesByDate;
import ua.findvacancies.mappers.SearchParamMapper;
import ua.findvacancies.model.ProviderCallable;
import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.model.strategy.Strategy;
import ua.findvacancies.model.viewdata.ViewSearchParams;
import ua.findvacancies.utils.ViewSearchParamsUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Service
@Slf4j
public class VacancyService {
	public static final String DEFAULT_SEARCH = "Java developer";
	public static final int DEFAULT_DAYS = 7;

	private final SearchParamMapper searchParamMapper = new SearchParamMapper();

	public List<Vacancy> getVacancyListByThreads(Set<Strategy> strategies, SearchParam searchParam) {
		ExecutorService executor = Executors.newFixedThreadPool(strategies.size());

		List<Future<List<Vacancy>>> futuresList = strategies.stream()
				.map(strategy -> new ProviderCallable(strategy, searchParam))
				.map(executor::submit)
				.toList();

		CopyOnWriteArrayList<Vacancy> vacancyList = new CopyOnWriteArrayList<>();

		for (Future<List<Vacancy>> future : futuresList) {
			try {
				vacancyList.addAll(future.get());
			} catch (InterruptedException | ExecutionException e) {
				log.error("Error on getting data: {}", e.getMessage(), e);
			}
		}
		executor.shutdown();

		vacancyList.sort(new ComparatorVacanciesByDate());
		return vacancyList;
	}

	public List<Vacancy> getVacancyList(Set<Strategy> strategies, SearchParam searchParam) {
		CopyOnWriteArrayList<Vacancy> vacancyList = new CopyOnWriteArrayList<>();
		for (Strategy strategy : strategies) {
			vacancyList.addAll(strategy.getVacancies(searchParam));
		}
		vacancyList.sort(new ComparatorVacanciesByDate());
		return vacancyList;
	}

	public List<Vacancy> getVacancyList(ViewSearchParams viewSearchParams) {
		SearchParam searchParam = searchParamMapper.convert(viewSearchParams);
		return getVacancyListByThreads(ViewSearchParamsUtils.getStrategiesSet(viewSearchParams), searchParam);
	}

	public ViewSearchParams getDefaultViewSearchParams() {
		return new ViewSearchParams(DEFAULT_SEARCH, String.valueOf(DEFAULT_DAYS), new HashSet<>());
	}
}
