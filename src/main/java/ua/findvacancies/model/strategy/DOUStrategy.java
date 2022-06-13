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
public class DOUStrategy extends AbstractStrategy {

    private static final String DATE_FORMAT = "dd MMMM yyyy";

    private static final String[] monthsRU = {
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"};

    private static final String[] monthsUA = {
            "січня", "лютого", "березня", "квітня", "травня", "червня",
            "липня", "серпня", "вересня", "жовтня", "листопада", "грудня"};
    private static final Locale locale = new Locale("ru");

    private static final DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(locale);
    private static final SimpleDateFormat simpleDateFormatRU = new SimpleDateFormat(DATE_FORMAT, locale);
    private static final SimpleDateFormat simpleDateFormatUA = new SimpleDateFormat(DATE_FORMAT, locale);

    static {
        dateFormatSymbols.setMonths(monthsRU);
        simpleDateFormatRU.setDateFormatSymbols(dateFormatSymbols);
        dateFormatSymbols.setMonths(monthsUA);
        simpleDateFormatUA.setDateFormatSymbols(dateFormatSymbols);
    }

    private final DocumentConnect documentConnect;

    public DOUStrategy() {
    }

    @Override
    public String getSiteURL() {
        return "https://dou.ua/";
    }

    @Override
    public String getSiteURLPattern() {
        return "https://jobs.dou.ua/vacancies/?search=%s&city=Киев";
    }

    @Override
    public List<Vacancy> getVacancies(SearchParam searchParam) {
        if (searchParam == null) {
            return Collections.emptyList();
        }
        initVacanciesList();
        try {
            while (true) {
                String documentURL = String.format(getSiteURLPattern(), searchParam.getKeyWordsSearchLine());
                Document doc = documentConnect.getDocument(documentURL);
                if (doc == null) {
                    break;
                }
                Element vacancyListIdEl = doc.getElementById("vacancyListId");
                Element moreBtnEl = doc.getElementsByClass("more-btn").first();
                Elements vacanciesListEl = vacancyListIdEl.getElementsByClass("l-vacancy");
                if (vacanciesListEl.size() == 0) {
                    break;
                }

                for (Element element : vacanciesListEl) {
                    String vacancyURL = element.getElementsByTag("a").attr("href");

                    Vacancy vacancy = getVacancy(vacancyURL);
                    Elements hotsEl = element.getElementsByClass("__hot");
                    boolean isHotVacancy = !CollectionUtils.isEmpty(hotsEl);
                    vacancy.setHot(isHotVacancy);
                    vacancy.setSiteName(getSiteURL());

                    checkAndAddVacancyToList(vacancy, searchParam);
                }
                break;
            }
        } catch (Exception e) {
            log.error("Error on parsing DOU: {}", e.getMessage(), e);
        }
        return vacancies;
    }

    @Override
    public Vacancy getVacancy(String vacancyURL) {

        try {
            Document vacancyDoc = documentConnect.getDocument(vacancyURL);
            if (vacancyDoc != null) {
                Element vacancyEl = vacancyDoc.getElementsByClass("b-vacancy").first();
                Element vacancyCompanyInfoEl = vacancyEl.getElementsByClass("b-compinfo").first();

                String vacancyDate = getTextByClassName(vacancyEl, "date");

                return Vacancy.builder()
                        .title(getTextByClassName(vacancyEl, "g-h2"))
                        .date(parseVacationDate(vacancyDate))
                        .companyName(getTextByClassName(vacancyCompanyInfoEl, "l-n"))
                        .city(getTextByClassName(vacancyEl, "place"))
                        .salary(getTextByClassName(vacancyEl, "salary"))
                        .url(vacancyURL)
                        .build();

            }
        } catch (Exception e) {
            log.error("Error on parsing vacancy by url {}: {}", vacancyURL, e.getMessage(), e);
        }
        return new Vacancy();
    }

    private Date parseVacationDate(String dateString) {
        try {
            return simpleDateFormatRU.parse(dateString);
        } catch (ParseException e) {
            log.error("Error on parsing vacancy date '{}': {}", dateString, e.getMessage(), e);
        }
        try {
            return simpleDateFormatUA.parse(dateString);
        } catch (ParseException e) {
            log.error("Error on parsing vacancy date '{}': {}", dateString, e.getMessage(), e);
        }
        return new Date();
    }

}
