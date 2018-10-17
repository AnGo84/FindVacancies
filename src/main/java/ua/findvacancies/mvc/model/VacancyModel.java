package ua.findvacancies.mvc.model;


import org.springframework.stereotype.Repository;
import ua.findvacancies.mvc.utils.StringUtils;
import ua.findvacancies.mvc.vo.CompareByDate;
import ua.findvacancies.mvc.vo.SearchParams;
import ua.findvacancies.mvc.vo.Vacancy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by AnGo on 05.05.2016.
 */

@Repository
public class VacancyModel {
    //    private View view;
    private Provider[] providers;

    public VacancyModel() {
        initProviders();
//        providers = new Provider[]{providerWork};
    }

    public VacancyModel(Provider[] providers) {
        if (providers == null || providers.length == 0)
            throw new IllegalArgumentException("Wrong data");
        this.providers = providers;
    }

    private void initProviders() {
        Provider providerHH = new Provider(new HHStrategy());
        Provider providerRabota = new Provider(new RabotaUAStrategy());
        Provider providerWork = new Provider(new WorkUAStrategy());
        Provider providerDOU = new Provider(new DOUStrategy());
        providers = new Provider[]{providerHH, providerRabota, providerWork, providerDOU};
    }

    public List<Vacancy> getVacancyList(String words, int days) {
        //Получаем ExecutorService утилитного класса Executors с размером gпула потоков равному 10
        ExecutorService executor = Executors.newFixedThreadPool(providers.length);
        //создаем список с Future, которые ассоциированы с Callable
        List<Future<List<Vacancy>>> list = new ArrayList<>();
        // создаем экземпляр MyCallable

        for (Provider provider: providers) {
            Callable<List<Vacancy>> callable = new ProviderCallable(provider.getStrategy(), words,days);
            //сабмитим Callable таски, которые будут
            //выполнены пулом потоков
            Future<List<Vacancy>> future = executor.submit(callable);
            //добавляя Future в список,
            //мы сможем получить результат выполнения
            list.add(future);
        }

        CopyOnWriteArrayList<Vacancy> vacancyList = new CopyOnWriteArrayList<>();

        for(Future<List<Vacancy>> fut : list){
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
        //without threads
        /*
        CopyOnWriteArrayList<Vacancy> vacancyList = new CopyOnWriteArrayList<>();
        for (Provider provider : providers) {

            vacancyList.addAll(provider.getJavaVacancies(words, days));
        }
        */
        Collections.sort(vacancyList, new CompareByDate());
        return vacancyList;
    }

    public List<Vacancy> getVacancyList(SearchParams searchParams) {

        String words = searchParams.getSearchLine();
        int days = -StringUtils.getDaysFromText(searchParams.getDays());

        Set<Provider> providerSet = searchParams.getProviderSet();
        if (providerSet != null) {
            providers = searchParams.getProviderSet().toArray(new Provider[searchParams.getProviderSet().size()]);
        }

//        List<Vacancy> vacancyList = new ArrayList<>();
//        for (Provider provider : providers) {
//            vacancyList.addAll(provider.getJavaVacancies(words, -days));
//        }
//        Collections.sort(vacancyList, new CompareByDate());
//        return vacancyList;
        return getVacancyList(words, days);
    }
}
