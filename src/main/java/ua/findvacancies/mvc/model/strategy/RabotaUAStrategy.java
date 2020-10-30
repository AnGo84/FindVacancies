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
    private static final String URL_FORMAT = "https://rabota.ua/ua/zapros/%s/киев/pg%d";
    private static final String SITE_URL = "https://rabota.ua";
    //private static final String DATE_FORMAT = "dd MMM yyyy";
    private static final String WORD_SEPARATOR = "-";
    /*private static final String[] shortMonths = {
            "янв", "фев", "мар", "апр", "май", "июн",
            "июл", "авг", "сен", "окт", "ноя", "дек"};
    private static final Locale locale = new Locale("ru");
    private static final DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(locale);
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, locale);
    private static final String DATE_NUMBER_FORMAT = "dd.MM.yyyy";
    private static final SimpleDateFormat simpleDateNumberFormat = new SimpleDateFormat(DATE_NUMBER_FORMAT);

    static {
        dateFormatSymbols.setShortMonths(shortMonths);
        simpleDateFormat.setDateFormatSymbols(dateFormatSymbols);
    }*/
    //public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

    private final DocumentConnect documentConnect;

    public RabotaUAStrategy(DocumentConnect documentConnect) {
        this.documentConnect = documentConnect;
    }

    @Override
    //public List<Vacancy> getVacancies(String words, int days) {
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
                System.out.println("Page " + pageCount + ", URL: " + searchPageURL);
                Document doc = documentConnect.getDocument(searchPageURL);
                //System.out.println("Doc: " + doc);
                //getDocument(searchString, pageCount++);
                if (doc == null) {
                    break;
                }

                Elements vacancyListTableEl = doc.getElementsByClass("f-vacancylist-tablewrap");
                //System.out.println("vacanciesListEl: " + vacancyListTableEl);

                if (CollectionUtils.isEmpty(vacancyListTableEl)) {
                    break;
                }
                Elements vacanciesListEl = vacancyListTableEl.first().getElementsByClass("card");
                System.out.println("vacanciesListEl size: " + vacanciesListEl.size());

                if (vacanciesListEl.size() == 0) {
                    break;
                }

                int vacancyOnPage = 0;
                for (Element element : vacanciesListEl) {
                    String vacancyURL = element.getElementsByClass("card-title").first().getElementsByTag("a").attr("href");
                    String fullVacancyURL = SITE_URL + vacancyURL;
                    //System.out.println("url: " + vacancyURL);
                    //System.out.println("url: " + fullVacancyURL);
                    Vacancy vacancy = getVacancy(fullVacancyURL);

                    //vacancy.setHot("true".equalsIgnoreCase(element.attr("data-is-hot")));

                    vacancy.setSiteName(SITE_URL);

                    System.out.println("RabotaUA: " + vacancy);

                    if (VacancyUtils.isNotApplyToSearch(vacancy, searchParam)) {
                        continue;
                    }

                    vacancies.add(vacancy);

                    /*vacancyOnPage++;
                    // title
                    Element titleElement = element.getElementsByClass("f-vacancylist-vacancytitle").first();
                    String title = titleElement.text();

                    // url
                    String vacancyURL = SITE_URL + titleElement.getElementsByTag("a").attr("href");

                    // isHot
                    Element hotElement = element.getElementsByClass("fi-hot").first();

                    // f-vacancylist-agotime
                    Element agoTimeEl = element.getElementsByClass("f-vacancylist-bottomblock").first();
                    String agoTime = agoTimeEl.getElementsByClass("f-vacancylist-agotime").text();

                    System.out.println("Page " + pageCount + ", vacancy " + vacancyOnPage + ", Vacancy URL: " + vacancyURL +
                            ", isHot: " + (hotElement == null) + ", added: " + agoTime);
                    boolean isJustAdd = (agoTime.contains("минут") || agoTime.contains("час") || hotElement != null);

                    if (AppStringUtils.isStringIncludeWords(title, searchParam.getExcludeWords())) {
                        continue;
                    }


                    // date
                    Date date = getVacationDate(vacancyURL);
                    if (date.compareTo(AppDateUtils.addDaysToDate(new Date(), searchParam.getDays())) == -1 && !isJustAdd) {
                        hasData = false;
                        break;
                    }

                    // salary + city Block
                    Element salaryCityEl = element.getElementsByClass("f-vacancylist-characs-block").first();
                    Elements ee = salaryCityEl.getAllElements();
                    // city (it's important)
                    String city = "";
                    String salary = "";
                    for (Element elem : ee) {
//                        System.out.println("  "+elem.tagName() + ":  " + elem.text() + "  : " + elem.className());
                        // salary
                        if (elem.tagName().equals("p") && elem.className().equals("fd-beefy-soldier -price")) {
                            salary = elem.text();
//                            System.out.println("  Salary:  " + salary + "  : " + elem.className());
                        }
                        // city
                        if (elem.tagName().equals("p") && elem.className().equals("fd-merchant")) {
                            city = elem.text();
//                            System.out.println("  City:  " + city + "  : " + elem.className());
                        }
                        if (elem.tagName().equals("span")) {
                            city = city.replace(elem.text(), "");
//                            System.out.println("  Span:  " + elem.text());
//                            System.out.println("  New City:  " + city);
                        }
                    }

                    // company
                    String companyName = element.getElementsByClass("f-vacancylist-companyname").first().text();

                    // site
                    String siteName = SITE_URL;

                    // add vacancy to the list
                    Vacancy vacancy = new Vacancy();
                    vacancy.setTitle(title);
                    vacancy.setSalary(salary);
                    vacancy.setCity(city);
                    vacancy.setCompanyName(companyName);
                    vacancy.setSiteName(siteName);
                    vacancy.setUrl(vacancyURL);
                    vacancy.setDate(date);
                    vacancies.add(vacancy);
                    //System.out.println("RABOTA_UA: " + vacancy.getUrl());*/
                }

            }

        } catch (IOException e) {
//            e.printStackTrace();
        }

        return vacancies;
    }

    @Override
    public Vacancy getVacancy(String vacancyURL) {
        System.out.println("vacancyURL: " + vacancyURL);
        Vacancy vacancy = new Vacancy();
        try {
            Document vacancyDoc = documentConnect.getDocument(vacancyURL);
            Element vacancyScriptEl = vacancyDoc.getElementsByAttributeValue("href", vacancyURL).first();
            while (true) {
                vacancyScriptEl = vacancyScriptEl.nextElementSibling();
                if (vacancyScriptEl == null) {
                    break;
                }
                if (vacancyScriptEl.getElementsByTag("script") != null) {
                    vacancy = getVacancyFromJSON(vacancyScriptEl.data());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        vacancy.setUrl(vacancyURL);
        //System.out.println("DOU: " + vacancy);
        return vacancy;
    }

    private Vacancy getVacancyFromJSON(String vacancyElData) {
        Vacancy vacancy = new Vacancy();
        String vacancyCompanyName = "";
        String vacancyTitle = "";
        String vacancyCity = "";
        String vacancyDate = "";
        String vacancySalary = "";
        int vacancySalaryInt;
        boolean isHot;
        String vacancyJSON = vacancyElData.substring(vacancyElData.indexOf("="), vacancyElData.lastIndexOf(";")).trim();
        vacancyJSON = vacancyJSON.substring(1);
        //System.out.println("vacancyJSON: " + vacancyJSON);
        if (!Strings.isBlank(vacancyJSON)) {
            JSONObject jsonObject = new JSONObject(vacancyJSON);
            vacancyCompanyName = jsonObject.getString("vacancy_CompanyName");
            vacancyTitle = jsonObject.getString("vacancy_Name");
            vacancyCity = jsonObject.getString("vacancy_CityName");
            isHot = jsonObject.getBoolean("vacancy_IsHot");
            vacancyDate = jsonObject.getString("vacancy_VacancyDate");
            vacancySalaryInt = jsonObject.getInt("vacancy_Salary");

            vacancy.setTitle(vacancyTitle);
            vacancy.setCity(vacancyCity);
            vacancy.setSalary(vacancySalaryInt > 0 ? String.valueOf(vacancySalaryInt) : "");
            vacancy.setCompanyName(vacancyCompanyName);
            vacancy.setDate(parseVacationDate(vacancyDate));
            vacancy.setHot(isHot);
        }
        return vacancy;
    }


    private Date parseVacationDate(String dateString) {
        //System.out.println("Date text: " + dateString);
        try {
            return simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }

    /*private Date getVacationDate(String searchString) {
        //"":"2020-10-29T11:01:00.88"
        Date vacationDate = new Date();
        System.out.println("New: " + vacationDate);
        try {
            Document doc = StrategyDocument.getDocument(searchString);
            if (doc != null) {
                Element element = doc.getElementsByClass("f-vacancy-header-wrapper").first();
                //Element element = doc.getElementById("d-date");
                if (element != null) {
                    //System.out.println("ItemProp "+ element.getElementsByAttribute("itemprop").first());

                    Element dateElement = element.getElementsByClass("f-date-holder").first();
                    if (dateElement != null) {
                        //System.out.println("DateElem");
                        //System.out.println("String1: " + dateElement.text() + " | " + vacationDate);
                        vacationDate = simpleDateFormat.parse(dateElement.text());
                        //System.out.println("Parse: " + vacationDate);
                    }
                } else if ((element = doc.getElementById("d-date")) != null) {
                    Element dateElement = element.getElementsByClass("d-ph-value").first();
                    if (dateElement != null) {
                        //System.out.println("DateElem");
                        //System.out.println("String2: " + dateElement.text() + " | " + vacationDate);
                        vacationDate = simpleDateNumberFormat.parse(dateElement.text());
                        //System.out.println("Parse: " + vacationDate);
                    }
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
        } catch (ParseException e) {
//            e.printStackTrace();
        }

        return vacationDate;
    }*/
}
