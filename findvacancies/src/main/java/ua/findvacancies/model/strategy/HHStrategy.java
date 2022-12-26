package ua.findvacancies.model.strategy;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;
import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.Vacancy;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class HHStrategy extends AbstractStrategy {
    public static final String DATE_FORMAT = "dd MMMM yyyy";

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

    @Override
    public String getSiteURL() {
        return "http://hh.ua/";
    }

    @Override
    public String getSiteURLPattern() {
        //private static final String URL_FORMAT = "http://hh.ua/search/vacancy?text=java+%s&page=%d";
        return "https://hh.ua/search/vacancy?text=%s&enable_snippets=true&clusters=true&currency_code=UAH&area=115&page=%d";
    }

    @Override
    public List<Vacancy> getVacancies(SearchParam searchParam) {
        if (searchParam == null) {
            return Collections.emptyList();
        }
        initVacanciesList();
        try {
            int pageCount = 0;
            while (true) {
                String keyWords = searchParam.getKeyWordsSearchLine();

                Document doc = documentConnect.getDocument(String.format(getSiteURLPattern(), keyWords, pageCount++));
                if (doc == null) {
                    break;
                }
                Elements elements = doc.select("[data-qa=vacancy-serp__vacancy]");
                if (elements.size() == 0) break;

                for (Element element : elements) {
                    String vacancyURL = element.select("[data-qa=vacancy-serp__vacancy-title]").attr("href");
                    Vacancy vacancy = getVacancy(vacancyURL);

                    vacancy.setSiteName(getSiteURL());

                    checkAndAddVacancyToList(vacancy, searchParam);
                }
            }

        } catch (Exception e) {
            log.error("Error on parsing HH: {}", e.getMessage(), e);
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
            log.error("Error on parsing vacancy by url {}: {}", vacancyURL, e.getMessage(), e);
        }
        return new Vacancy();
    }

    private Date parseVacationDate(String dateString) {
        String parsingDate;
        try {
            String[] dataWords = dateString.split(" ");
            if (dataWords.length > 3) {
                parsingDate = dataWords[2] + " " + dataWords[3] + " " + dataWords[4];
            } else {
                parsingDate = dataWords[2].replaceAll(String.valueOf(NON_BREAKING_SPACE_CHAR), " ");
            }
            return simpleDateFormat.parse(parsingDate);
        } catch (ParseException e) {
            log.error("Error on parsing vacancy date '{}': {}", dateString, e.getMessage(), e);
        }
        return new Date();
    }

}
