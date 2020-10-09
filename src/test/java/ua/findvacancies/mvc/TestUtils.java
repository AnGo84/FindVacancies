package ua.findvacancies.mvc;

import ua.findvacancies.mvc.viewdata.ViewSearchParams;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtils {
    public static Date createDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    public static ViewSearchParams getViewSearchParams() {
        ViewSearchParams viewSearchParams = new ViewSearchParams();
        viewSearchParams.setDays("5");
        viewSearchParams.setSearchLine("Search Line");
        Set<String> sites = Stream.of("a", "b", "c")
                .collect(Collectors.toCollection(HashSet::new));
        viewSearchParams.setSites(sites);
        return viewSearchParams;
    }
}
