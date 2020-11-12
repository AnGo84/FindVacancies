package ua.findvacancies.mvc.model.strategy;


import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;
import ua.findvacancies.mvc.model.SearchParam;
import ua.findvacancies.mvc.model.Vacancy;
import ua.findvacancies.mvc.utils.VacancyUtils;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
public class HHStrategy extends AbstractStrategy {
    public static final String DATE_FORMAT = "dd MMMM yyyy";
    public static final String SITE_URL = "http://hh.ua/";

    //private static final String URL_FORMAT = "http://hh.ua/search/vacancy?text=java+%s&page=%d";
    public static final String URL_FORMAT = "https://hh.ua/search/vacancy?text=%s&enable_snippets=true&clusters=true&currency_code=UAH&area=115&page=%d";

    private static final String[] months = {
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"};
    private static final Locale locale = new Locale("ru");
    private static final DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(locale);
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, locale);

    static {
        dateFormatSymbols.setMonths(months);
        simpleDateFormat.setDateFormatSymbols(dateFormatSymbols);
    }

    private final DocumentConnect documentConnect;

    public HHStrategy(DocumentConnect documentConnect) {
        this.documentConnect = documentConnect;
    }

    @Override
    public List<Vacancy> getVacancies(SearchParam searchParam) {
        if (searchParam == null) {
            return Collections.emptyList();
        }
        List<Vacancy> vacancies = new ArrayList<>();
        try {
            int pageCount = 0;
            while (true) {
                String keyWords = searchParam.getKeyWordsSearchLine();

                Document doc = documentConnect.getDocument(String.format(URL_FORMAT, keyWords, pageCount++));
                if (doc == null) {
                    break;
                }
                Elements elements = doc.select("[data-qa=vacancy-serp__vacancy]");
                if (elements.size() == 0) break;

                for (Element element : elements) {
                    String vacancyURL = element.select("[data-qa=vacancy-serp__vacancy-title]").attr("href");
                    Vacancy vacancy = getVacancy(vacancyURL);

                    vacancy.setSiteName(SITE_URL);

                    if (VacancyUtils.isApplyToSearch(vacancy, searchParam)) {
                        vacancies.add(vacancy);
                    }
                }
            }

        } catch (Exception e) {
//            e.printStackTrace();
        }

        return vacancies;
    }

    @Override
    public Vacancy getVacancy(String vacancyURL) {
        String vacancyCompanyName = "";
        String vacancyDate = "";
        try {
            Document vacancyDoc = documentConnect.getDocument(vacancyURL);
            if (vacancyDoc != null) {
                Elements vacancyCompanyEls = vacancyDoc.select("[data-qa=vacancy-company-name]");
                if (!CollectionUtils.isEmpty(vacancyCompanyEls)) {
                    vacancyCompanyName = vacancyCompanyEls.first().getElementsByTag("span").first().getElementsByTag("span").first().text();
                }
                vacancyDate = getTextByClassName(vacancyDoc, "vacancy-creation-time");

                return Vacancy.builder()
                                .title(getTextBySelect(vacancyDoc, "[data-qa=vacancy-title]"))
                                .url(vacancyURL)
                                .city(getTextByClassName(vacancyDoc, "[data-qa=vacancy-view-location]"))
                                .salary(vacancyDoc.getElementsByClass("vacancy-salary").first().getElementsByTag("span").text())
                                .companyName(vacancyCompanyName)
                                .date(parseVacationDate(vacancyDate))
                                .build();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Vacancy();
    }

    private Date parseVacationDate(String dateString) {
        try {
            String[] dataWords = dateString.split(" ");
            dateString = dataWords[2].replaceAll(String.valueOf(NON_BREAKING_SPACE_CHAR), " ");
            return simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

}
