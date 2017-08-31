package ua.findvacancies.mvc.model;


import org.springframework.stereotype.Repository;
import ua.findvacancies.mvc.vo.CompareByDate;
import ua.findvacancies.mvc.vo.Vacancy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by AnGo on 05.05.2016.
 */

@Repository
public class VacancyModel {
    //    private View view;
    private Provider[] providers;

    public VacancyModel() {
        Provider providerHH = new Provider(new HHStrategy());
        Provider providerRabota = new Provider(new RabotaUAStrategy());
        Provider providerWork = new Provider(new WorkUAStrategy());
        Provider providerDOU = new Provider(new DOUStrategy());
        providers = new Provider[]{providerHH, providerRabota, providerWork, providerDOU};
//        providers = new Provider[]{providerWork};
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
}
