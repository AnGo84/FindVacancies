package ua.findvacancies.model.strategy;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.utils.AppStringUtils;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Slf4j
@RequiredArgsConstructor
public class GRCStrategy extends AbstractStrategy {
    private static final String DATE_FORMAT_TEXT = "dd MMMM yyyy";

    private static final String[] months = {
            "січня", "лютого", "березня", "квітня", "травня", "червня",
            "липня", "серпня", "вересня", "жовтня", "листопада", "грудня"};

    private static final Locale locale = new Locale("ua");
    private static final DateFormatSymbols dateShotFormatSymbols = DateFormatSymbols.getInstance(locale);
    private static final SimpleDateFormat simpleDateTextFormat = new SimpleDateFormat(DATE_FORMAT_TEXT, locale);

    static {
        dateShotFormatSymbols.setMonths(months);
        simpleDateTextFormat.setDateFormatSymbols(dateShotFormatSymbols);
    }

    private final DocumentConnect documentConnect;

    @Override
    public String getSiteURL() {
        return "https://grc.ua";
    }


    @Override
    public String getSiteURLPattern() {
        return "https://grc.ua/vacancies?search=%s&page=%d";
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
                String searchPageURL = String.format(getSiteURLPattern(), searchParam.getKeyWordsSearchLine(), ++pageCount);

                log.debug("URL: {}", searchPageURL);

                Document doc = documentConnect.getDocument(searchPageURL);

                //log.info(doc.html());

                if (doc == null) {
                    break;
                }

                Elements vacanciesListEl = doc.getElementsByClass("css-ax3w0k");

                if (CollectionUtils.isEmpty(vacanciesListEl)) {
                    log.debug("vacanciesListEl is null or empty");
                    break;
                }
                for (Element element : vacanciesListEl) {
                    Element vacancyEl = element.getElementsByClass("css-0").first();
                    String vacancyURL = vacancyEl.getElementsByTag("a").attr("href");
                    if (vacancyURL.startsWith("/")) {
                        vacancyURL = getSiteURL() + vacancyURL;
                    }
                    Vacancy vacancy = getVacancy(vacancyURL);
                    vacancy.setSiteName(getSiteURL());

                    checkAndAddVacancyToList(vacancy, searchParam);
                }
            }

        } catch (Exception e) {
            log.error("Error on parsing GRC: {}", e.getMessage(), e);
        }

        return vacancies;
    }

    @Override
    public Vacancy getVacancy(String vacancyURL) {
        log.debug("getVacancy: {}", vacancyURL);
        String vacancyDate = "";
        try {
            Document vacancyDoc = documentConnect.getDocument(vacancyURL);
            if (vacancyDoc != null) {
                Element vacancyEl = vacancyDoc.getElementsByClass("css-13yw1fu").first();
                vacancyDate = vacancyEl.getElementsByClass("css-1p41x9r").text();

                String vacancyCity = "";
                Element vacancyCityEl = vacancyEl.getElementsByClass("css-vrn8d5-StyledContactLeft").first();
                if (vacancyCityEl!=null){
                    vacancyCity = vacancyCityEl.getElementsByTag("p").text();
                }
                String vacancySalary = "";
                Element vacancySalaryEl = vacancyEl.getElementsByClass("css-13bru7h").first();
                if (vacancySalaryEl!=null){
                    vacancySalary = vacancySalaryEl.getElementsByTag("p").text();
                }

                return Vacancy.builder()
                        .companyName(vacancyEl.getElementsByClass("css-is0jc4-StyledContactLink").text())
                        .title(vacancyEl.getElementsByClass("css-zugvvd").text())
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
        return new Vacancy();
    }

    private Date parseVacationDate(String dateString) {
        try {
            int dateEndPosition = AppStringUtils.nth(dateString, " ", 3);
            if (dateEndPosition > 0) {
                dateString = dateString.substring(0, dateEndPosition);
            }

            return simpleDateTextFormat.parse(dateString);

        } catch (ParseException e) {
            log.warn("Error on parsing vacancy date '{}': {}", dateString, e.getMessage());
        }
        return new Date();
    }

}
