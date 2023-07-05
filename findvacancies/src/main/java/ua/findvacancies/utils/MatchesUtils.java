package ua.findvacancies.utils;

import org.springframework.util.ObjectUtils;
import ua.findvacancies.model.Vacancy;

import java.util.function.Predicate;

public class MatchesUtils {

    public static boolean matchesTerm(String value, String searchTerm) {
        if (ObjectUtils.isEmpty(value) || ObjectUtils.isEmpty(searchTerm)){
            return false;
        }
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    public static boolean matches(Vacancy vacancy, String searchTerm) {
        if (ObjectUtils.isEmpty(vacancy) || ObjectUtils.isEmpty(searchTerm)){
            return false;
        }
        Predicate<Vacancy> matchesTitle = vac -> MatchesUtils.matchesTerm(vac.getTitle(), searchTerm);
        Predicate<Vacancy> matchesSalary = vac -> MatchesUtils.matchesTerm(vac.getSalary(), searchTerm);
        Predicate<Vacancy> matchesCity = vac -> MatchesUtils.matchesTerm(vac.getCity(), searchTerm);
        Predicate<Vacancy> matchesCompanyName = vac -> MatchesUtils.matchesTerm(vac.getCompanyName(), searchTerm);
        Predicate<Vacancy> matchesSiteName = vac -> MatchesUtils.matchesTerm(vac.getSiteName(), searchTerm);
        Predicate<Vacancy> matchesURL = vac -> MatchesUtils.matchesTerm(vac.getUrl(), searchTerm);
        Predicate<Vacancy> matchesDate = vac -> MatchesUtils.matchesTerm(AppDateUtils.formatToString(vac.getDate()), searchTerm);

        return matchesTitle
                .or(matchesSalary)
                .or(matchesCity)
                .or(matchesCompanyName)
                .or(matchesSiteName)
                .or(matchesURL)
                .or(matchesDate)
                .test(vacancy);
    }

}
