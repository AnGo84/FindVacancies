package ua.findvacancies.mvc.model;


import org.springframework.stereotype.Repository;
import ua.findvacancies.mvc.utils.StringUtils;
import ua.findvacancies.mvc.vo.CompareByDate;
import ua.findvacancies.mvc.vo.SearchParams;
import ua.findvacancies.mvc.vo.Vacancy;

import java.util.*;

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

    private void initProviders() {
        Provider providerHH = new Provider(new HHStrategy());
        Provider providerRabota = new Provider(new RabotaUAStrategy());
        Provider providerWork = new Provider(new WorkUAStrategy());
        Provider providerDOU = new Provider(new DOUStrategy());
        providers = new Provider[]{providerHH, providerRabota, providerWork, providerDOU};
    }

    public VacancyModel(Provider[] providers) {
        if (providers == null || providers.length == 0)
            throw new IllegalArgumentException("Wrong data");
        this.providers = providers;
    }

    public List<Vacancy> getVacancyList(String words, int days) {
        List<Vacancy> vacancyList = new ArrayList<>();
        for (Provider provider : providers) {
            vacancyList.addAll(provider.getJavaVacancies(words, days));
        }
        Collections.sort(vacancyList, new CompareByDate());
        return vacancyList;
    }

    public List<Vacancy> getVacancyList(SearchParams searchParams) {

        String words = searchParams.getSearchLine();
        int days = - StringUtils.getDaysFromText(searchParams.getDays());

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
