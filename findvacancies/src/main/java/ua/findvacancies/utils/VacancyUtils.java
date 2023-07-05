package ua.findvacancies.utils;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.Vacancy;

import java.util.Date;

public class VacancyUtils {
    public static boolean isApplyToSearch(Vacancy vacancy, SearchParam searchParam) {
        if (isEmpty(vacancy) || searchParam == null) {
            return false;
        }
        if(vacancy.isHot() || isDateApplyToSearchPeriod(vacancy.getDate(), searchParam.getDays())){
            return true;
        }

        if (AppStringUtils.isStringIncludeWords(vacancy.getTitle(), searchParam.getExcludeWords())) {
            return true;
        }
        return false;
    }

    private static boolean isDateApplyToSearchPeriod(Date date, int lastDays) {
        if (date == null) {
            return false;
        }
        Date fromDate = lastDays==0? new Date():AppDateUtils.addDaysToDate(new Date(), -lastDays);
        return date.compareTo(fromDate) > -1;
    }

    public static boolean isEmpty(Vacancy vacancy) {
        if (vacancy == null) {
            return true;
        }
        return ObjectUtils.isEmpty(vacancy.getUrl());
    }

}
