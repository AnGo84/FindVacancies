package ua.findvacancies.comparators;

import ua.findvacancies.model.Vacancy;

import java.util.Comparator;

public class ComparatorVacanciesByDate implements Comparator<Vacancy> {

    @Override
    public int compare(Vacancy o1, Vacancy o2) {
        if (o1 == null || o2 == null) {
            return -1;
        }
        if (o1.getDate() == null || o2.getDate() == null) {
            return -1;
        }
        return o2.getDate().compareTo(o1.getDate());
    }
}
