package ua.findvacancies.mvc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.core.io.ClassPathResource;
import ua.findvacancies.mvc.model.SearchParam;
import ua.findvacancies.mvc.model.viewdata.ViewSearchParams;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestUtils {
    public static Date createDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.AM_PM);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);

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

    public static SearchParam getSearchParams() {
        SearchParam searchParam = new SearchParam();
        searchParam.setDays(5);
        searchParam.setSearchLine("Search Line -with -exclude");
        searchParam.setKeyWords(Stream.of("search", "line")
                .collect(Collectors.toCollection(HashSet::new)));
        searchParam.setExcludeWords(Stream.of("with", "exclude")
                .collect(Collectors.toCollection(HashSet::new)));
        return searchParam;
    }

    public static Document getDocumentByClassPath(String filePath) throws IOException {
        return Jsoup.parse(new ClassPathResource(filePath).getFile(), "UTF-8");
    }

    public static Document getDocumentFromText(String text) throws IOException {
        return Jsoup.parse(text, "UTF-8");
    }
}
