package ua.findvacancies.mvc.model.strategy;


import org.apache.logging.log4j.util.Strings;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;
import ua.findvacancies.mvc.model.SearchParam;
import ua.findvacancies.mvc.model.Vacancy;
import ua.findvacancies.mvc.utils.VacancyUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class RabotaUAStrategy extends AbstractStrategy {
    public static final int ELEMENTS_BEFORE_JSON = 1;
    //private static final String URL_FORMAT = "https://rabota.ua/jobsearch/vacancy_list?regionId=1&keyWords=%s&pg%d";
    public static final String URL_FORMAT = "https://rabota.ua/ua/zapros/%s/киев/pg%d";
    public static final String WORD_SEPARATOR = "-";

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final String SITE_URL = "https://rabota.ua";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

    private final DocumentConnect documentConnect;

    public RabotaUAStrategy(DocumentConnect documentConnect) {
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
            boolean hasData = true;
            while (hasData) {
                String searchPageURL = String.format(URL_FORMAT, searchParam.getKeyWordsSearchLine(WORD_SEPARATOR), ++pageCount);
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
                        vacancyURL = SITE_URL + vacancyURL;
                    }
                    Vacancy vacancy = getVacancy(vacancyURL);
                    vacancy.setSiteName(SITE_URL);
                    if (VacancyUtils.isApplyToSearch(vacancy, searchParam)) {
                        vacancies.add(vacancy);
                    }
                }
            }

        } catch (IOException e) {
//            e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return new Date();
    }

}
