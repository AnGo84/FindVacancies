package ua.findvacancies.model.strategy;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;
import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.Vacancy;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
public class RabotaUAStrategy extends AbstractStrategy {
    public static final String WORD_SEPARATOR = "-";

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

    private final DocumentConnect documentConnect;

    @Override
    public String getSiteURL() {
        return "https://rabota.ua";
    }

    @Override
    public String getSiteURLPattern() {
        //"https://rabota.ua/jobsearch/vacancy_list?regionId=1&keyWords=%s&pg%d";
        return "https://rabota.ua/ua/zapros/%s/киев/pg%d";
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
                Document doc = documentConnect.getDocument(searchPageURL);

                if (doc == null) {
                    break;
                }
                Elements vacancyListTableEl = doc.getElementsByClass("f-vacancylist-tablewrap");

                if (CollectionUtils.isEmpty(vacancyListTableEl)) {
                    break;
                }
                Elements vacanciesListEl = vacancyListTableEl.first().getElementsByClass("card");
                if (vacanciesListEl.size() == 0) {
                    break;
                }

                for (Element element : vacanciesListEl) {
                    String vacancyURL = element.getElementsByClass("card-title").first().getElementsByTag("a").attr("href");
                    if (vacancyURL.startsWith("/")) {
                        vacancyURL = getSiteURL() + vacancyURL;
                    }
                    Vacancy vacancy = getVacancy(vacancyURL);
                    vacancy.setSiteName(getSiteURL());

                    checkAndAddVacancyToList(vacancy, searchParam);
                }
            }

        } catch (IOException e) {
            log.error("Error on parsing RabotaUA: {}", e.getMessage(), e);
        }

        return vacancies;
    }

    @Override
    public Vacancy getVacancy(String vacancyURL) {
        Vacancy vacancy = new Vacancy();
        try {
            Document vacancyDoc = documentConnect.getDocument(vacancyURL);
            if (vacancyDoc != null) {
                Elements scriptsEls = vacancyDoc.getElementsByTag("script");
                Element vacancyScriptEl = scriptsEls.stream()
                        .filter(el -> el.data().startsWith("var ruavars ="))
                        .findFirst()
                        .orElse(null);
                if (vacancyScriptEl != null) {
                    vacancy = getVacancyFromJSON(vacancyScriptEl.data());
                    vacancy.setUrl(vacancyURL);
                }
            }
        } catch (Exception e) {
            log.error("Error on parsing vacancy by url {}: {}", vacancyURL, e.getMessage(), e);
        }
        return vacancy;
    }

    private Vacancy getVacancyFromJSON(String vacancyElData) {
        String vacancyDate = "";
        int vacancySalaryInt;
        String vacancyJSON = vacancyElData.substring(vacancyElData.indexOf("="), vacancyElData.lastIndexOf(";")).trim();
        vacancyJSON = vacancyJSON.substring(1);
        if (!Strings.isBlank(vacancyJSON)) {
            JSONObject jsonObject = new JSONObject(vacancyJSON);
            vacancyDate = jsonObject.getString("vacancy_VacancyDate");
            vacancySalaryInt = jsonObject.getInt("vacancy_Salary");
            return Vacancy.builder()
                    .companyName(jsonObject.getString("vacancy_CompanyName"))
                    .title(jsonObject.getString("vacancy_Name"))
                    .city(jsonObject.getString("vacancy_CityName"))
                    .isHot(jsonObject.getBoolean("vacancy_IsHot"))
                    .salary(vacancySalaryInt > 0 ? String.valueOf(vacancySalaryInt) : "")
                    .date(parseVacationDate(vacancyDate))
                    .build();
        }
        return new Vacancy();
    }

    private Date parseVacationDate(String dateString) {
        try {
            return simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            log.warn("Error on parsing vacancy date '{}': {}", dateString, e.getMessage());
        }
        return new Date();
    }

}
