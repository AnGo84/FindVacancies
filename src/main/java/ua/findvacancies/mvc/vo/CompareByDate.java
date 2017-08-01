package ua.findvacancies.mvc.vo;

import java.util.Comparator;

/**
 * Created by AnGo on 22.07.2017.
 */
public class CompareByDate implements Comparator<Vacancy> {


    @Override
    public int compare(Vacancy o1, Vacancy o2) {
        return o2.getDate().compareTo(o1.getDate());
    }
}
