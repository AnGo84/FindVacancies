package ua.findvacancies.mvc.utils;

import org.springframework.util.StringUtils;
import ua.findvacancies.mvc.model.SearchParam;
import ua.findvacancies.mvc.model.Vacancy;

import java.util.Date;

public class VacancyUtils {
    public static boolean isNotApplyToSearch(Vacancy vacancy, SearchParam searchParam) {
        if (isEmpty(vacancy) || searchParam == null) {
            return true;
        }
        if (AppStringUtils.isStringIncludeWords(vacancy.getTitle(), searchParam.getExcludeWords())) {
            return true;
        }
        if(!vacancy.isHot() && !isDateApplyToSearchPeriod(vacancy.getDate(), searchParam.getDays())){
            return true;
        }

        return false;
    }

    private static boolean isDateApplyToSearchPeriod(Date date, int lastDays) {
        if (date == null) {
            return false;
        }
        return date.compareTo(AppDateUtils.addDaysToDate(new Date(), -lastDays)) > -1;
    }

    public static boolean isEmpty(Vacancy vacancy) {
        if (vacancy == null) {
            return true;
        }
        return StringUtils.isEmpty(vacancy.getUrl());
    }
}
