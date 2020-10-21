package ua.findvacancies.mvc.comparators;

import ua.findvacancies.mvc.model.Vacancy;

import java.util.Comparator;

public class ComparatorVacanciesByDate implements Comparator<Vacancy> {

    @Override
    public int compare(Vacancy o1, Vacancy o2) {
        return o2.getDate().compareTo(o1.getDate());
    }
}
