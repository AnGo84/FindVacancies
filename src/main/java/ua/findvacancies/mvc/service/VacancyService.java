package ua.findvacancies.mvc.service;

import org.springframework.stereotype.Service;
import ua.findvacancies.mvc.comparators.ComparatorVacanciesByDate;
import ua.findvacancies.mvc.mappers.SearchParamMapper;
import ua.findvacancies.mvc.model.Provider;
import ua.findvacancies.mvc.model.ProviderCallable;
import ua.findvacancies.mvc.model.SearchParam;
import ua.findvacancies.mvc.utils.ViewSearchParamsUtils;
import ua.findvacancies.mvc.viewdata.Vacancy;
import ua.findvacancies.mvc.viewdata.ViewSearchParams;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

@Service
public class VacancyService {
    private static final String DEFAULT_SEARCH = "Java developer";
    private static final int DEFAULT_DAYS = 7;

    private SearchParamMapper searchParamMapper = new SearchParamMapper();

    public VacancyService() {

    }

    public List<Vacancy> getVacancyListByThreads(Set<Provider> providers, SearchParam searchParam) {
        //Получаем ExecutorService утилитного класса Executors с размером пула потоков равному 10
        ExecutorService executor = Executors.newFixedThreadPool(providers.size());
        //создаем список с Future, которые ассоциированы с Callable
        List<Future<List<Vacancy>>> list = new ArrayList<>();
        // создаем экземпляр MyCallable

        for (Provider provider : providers) {
            Callable<List<Vacancy>> callable = new ProviderCallable(provider.getStrategy(), searchParam);
            Future<List<Vacancy>> future = executor.submit(callable);
            list.add(future);
        }

        CopyOnWriteArrayList<Vacancy> vacancyList = new CopyOnWriteArrayList<>();

        for (Future<List<Vacancy>> fut : list) {
            try {
                // печатаем в консоль возвращенное значение Future
                // будет задержка в 1 секунду, потому что Future.get()
                // ждет пока таск закончит выполнение
                //System.out.println(new Date()+ "::" + fut.get());
                vacancyList.addAll(fut.get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        executor.shutdown();

        Collections.sort(vacancyList, new ComparatorVacanciesByDate());
        return vacancyList;
    }

    public List<Vacancy> getVacancyList(Set<Provider> providers, SearchParam searchParam) {
        CopyOnWriteArrayList<Vacancy> vacancyList = new CopyOnWriteArrayList<>();
        for (Provider provider : providers) {

            vacancyList.addAll(provider.getStrategy().getVacancies(searchParam));
        }
        Collections.sort(vacancyList, new ComparatorVacanciesByDate());
        return vacancyList;
    }

    public List<Vacancy> getVacancyListByThreads(ViewSearchParams viewSearchParams) {
        SearchParam searchParam = searchParamMapper.convert(viewSearchParams);

        Set<Provider> providers = ViewSearchParamsUtils.getProvidersSet(viewSearchParams);

        return getVacancyListByThreads(providers, searchParam);
    }

    public ViewSearchParams getDefaultViewSearchParams(){
        return new ViewSearchParams(DEFAULT_SEARCH, java.lang.String.valueOf(DEFAULT_DAYS));
    }
}
