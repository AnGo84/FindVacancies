package ua.findvacancies;

import ua.findvacancies.model.viewdata.ViewSearchParams;
import ua.findvacancies.service.VacancyService;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtils {

    public static ViewSearchParams getViewSearchParams() {
        ViewSearchParams viewSearchParams = new ViewSearchParams();
        viewSearchParams.setDays("5");
        viewSearchParams.setSearchLine("Search Line");
        Set<String> sites = Stream.of("a", "b", "c")
                .collect(Collectors.toCollection(HashSet::new));
        viewSearchParams.setSites(sites);
        return viewSearchParams;
    }

    public static ViewSearchParams getDefaultViewSearchParams() {
        return new VacancyService().getDefaultViewSearchParams();
    }

}
