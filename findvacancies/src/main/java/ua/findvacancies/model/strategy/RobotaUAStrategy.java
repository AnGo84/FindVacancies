package ua.findvacancies.model.strategy;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;
import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.Vacancy;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class RobotaUAStrategy extends AbstractStrategy {
    public static final int ELEMENTS_BEFORE_JSON = 1;
    public static final String WORD_SEPARATOR = "-";

    private static final String DATE_FORMAT = "dd MMMM yyyy";

    private static final String[] monthsUA = {
            "січня", "лютого", "березня", "квітня", "травня", "червня",
            "липня", "серпня", "вересня", "жовтня", "листопада", "грудня"};
    private static final Locale locale = new Locale("uk");

    private static final DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(locale);
    private static final SimpleDateFormat simpleDateFormatUA = new SimpleDateFormat(DATE_FORMAT, locale);

    private final DocumentConnect documentConnect;

    static {
        dateFormatSymbols.setMonths(monthsUA);
        simpleDateFormatUA.setDateFormatSymbols(dateFormatSymbols);
    }

    @Override
    public String getSiteURL() {
        return "https://robota.ua";
    }

    @Override
    public String getSiteURLPattern() {
        return "https://robota.ua/zapros/%s/ukraine?page=%d";
    }

    @Override
    public List<Vacancy> getVacancies(SearchParam searchParam) {
        if (searchParam == null) {
            return Collections.emptyList();
        }
        initVacanciesList();
        try {
            int pageCount = 0;
            boolean hasData = true;
            while (hasData) {
                String searchPageURL = String.format(getSiteURLPattern(), searchParam.getKeyWordsSearchLine(WORD_SEPARATOR), ++pageCount);

                log.info("searchPageURL: {}", searchPageURL);

                Document doc = documentConnect.getDocument(searchPageURL);

                if (doc == null) {
                    break;
                }

                log.info(doc.html());

                Element vacancyListTagEl = doc.getElementsByAttribute("alliance-jobseeker-desktop-vacancies-list").first();
                if (vacancyListTagEl == null) {
                    log.info("vacancyListTagEl  is null");

                    break;
                }

                Elements vacanciesListEl = doc.getElementsByTag("alliance-vacancy-card-desktop");

                if (CollectionUtils.isEmpty(vacanciesListEl)) {
                    break;
                }

                for (Element element : vacanciesListEl) {
                    String vacancyURL = element.getElementsByTag("a").first().attr("href");
                    if (vacancyURL.startsWith("/")) {
                        vacancyURL = getSiteURL() + vacancyURL;
                    }
                    Vacancy vacancy = getVacancy(vacancyURL);
                    vacancy.setSiteName(getSiteURL());

                    checkAndAddVacancyToList(vacancy, searchParam);
                }
            }

        } catch (IOException e) {
            log.error("Error on parsing RobotaUA: {}", e.getMessage(), e);
        }

        return vacancies;
    }

    @Override
    public Vacancy getVacancy(String vacancyURL) {
        String vacancyDate = "";
        Vacancy vacancy = new Vacancy();
        try {
            Document vacancyDoc = documentConnect.getDocument(vacancyURL);
            if (vacancyDoc != null) {

                Element vacancyEl = vacancyDoc.getElementsByTag("lib-content").first();

                String vacancyTitle = vacancyEl.getElementsByTag("h1").first().text();
                log.info("vacancyTitle: {}", vacancyTitle);
                String vacancyTitle2 = vacancyEl.getElementsByAttributeValue("data-id", "vacancy-title").first().text();
                log.info("vacancyTitle2: {}", vacancyTitle2);
                String vacancySalary = vacancyEl.getElementsByAttributeValue("data-id", "vacancy-salary-from-to").first().text();
                log.info("vacancySalary: {}", vacancySalary);
                String vacancyCity = vacancyEl.getElementsByAttributeValue("data-id", "vacancy-city").first().text();
                log.info("vacancyCity: {}", vacancyCity);


                Element vacancyCompanyParentEl = vacancyEl.getElementsByTag("_ngcontent-app-desktop-c90").get(2).getElementsByTag("a").first();
                String vacancyCompany = vacancyCompanyParentEl.getElementsByTag("span").text();
                log.info("vacancyCompany: {}", vacancyCity);

                Elements vacancyDataParentEl = vacancyEl.getElementsByTag("santa-tooltip");
                vacancyDate = getTextFromFirstElByClassName(vacancyDataParentEl, "santa-typo-additional");

                return Vacancy.builder()
                        .companyName(vacancyCompany)
                        .title(vacancyTitle2)
                        .city(vacancyCity)
                        .salary(vacancySalary)
                        //.isHot(!CollectionUtils.isEmpty(vacancyEl.getElementsByClass("label-hot")))
                        .url(vacancyURL)
                        .date(parseVacationDate(vacancyDate))
                        .build();

            }
        } catch (Exception e) {
            log.error("Error on parsing vacancy by url {}: {}", vacancyURL, e.getMessage(), e);
        }
        return vacancy;
    }

    private Date parseVacationDate(String dateString) {
        try {
            return simpleDateFormatUA.parse(dateString);
        } catch (ParseException e) {
            log.warn("Error on parsing vacancy date '{}': {}", dateString, e.getMessage());

        }
        return new Date();
    }

}
