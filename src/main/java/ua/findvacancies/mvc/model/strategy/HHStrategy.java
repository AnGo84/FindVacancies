package ua.findvacancies.mvc.model.strategy;


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

public class HHStrategy extends AbstractStrategy {
    //public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String DATE_FORMAT = "dd MMMM yyyy";
    //public static final String DATE_FORMAT_SHORT = "dd MMMM";
    public static final String SITE_URL = "http://hh.ua/";

    //private static final String URL_FORMAT = "http://hh.ua/search/vacancy?text=java+%s&page=%d";
    public static final String URL_FORMAT = "https://hh.ua/search/vacancy?text=%s&enable_snippets=true&clusters=true&currency_code=UAH&area=115&page=%d";

    private static final String[] months = {
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"};
    private static final Locale locale = new Locale("ru");
    //private static final DateFormatSymbols dateShotFormatSymbols = DateFormatSymbols.getInstance(locale);
    private static final DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(locale);
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, locale);
    //private static final SimpleDateFormat simpleDateShortFormat = new SimpleDateFormat(DATE_FORMAT_SHORT, locale);

    static {
        //dateShotFormatSymbols.setShortMonths(shortMonths);
        //dateShotFormatSymbols.setMonths(months);
        //simpleDateShortFormat.setDateFormatSymbols(dateShotFormatSymbols);
        dateFormatSymbols.setMonths(months);
        simpleDateFormat.setDateFormatSymbols(dateFormatSymbols);
    }

    private final DocumentConnect documentConnect;

    public HHStrategy(DocumentConnect documentConnect) {
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
            System.out.println("SearchParam: " + searchParam);
            System.out.println("SearchLine: " + searchParam.getKeyWordsSearchLine());
            int pageCount = 0;
            while (true) {
                String keyWords = searchParam.getKeyWordsSearchLine();

                Document doc = documentConnect.getDocument(String.format(URL_FORMAT, keyWords, pageCount++));
                if (doc == null) {
                    break;
                }
                Elements elements = doc.select("[data-qa=vacancy-serp__vacancy]");
                //System.out.println("elements: " + elements);
                System.out.println("elements: " + elements.size());
                if (elements.size() == 0) break;

                for (Element element : elements) {
                    String vacancyURL = element.select("[data-qa=vacancy-serp__vacancy-title]").attr("href");
                    System.out.println("URL: " + vacancyURL);
                    Vacancy vacancy = getVacancy(vacancyURL);

                    vacancy.setSiteName(SITE_URL);

                    System.out.println("HH: " + vacancy);

                    if (VacancyUtils.isNotApplyToSearch(vacancy, searchParam)) {
                        continue;
                    }

                    vacancies.add(vacancy);

                    /*// title
                    String title = element.select("[data-qa=vacancy-serp__vacancy-title]").first().text();
                    if (AppStringUtils.isStringIncludeWords(title, searchParam.getExcludeWords())) {
                        continue;
                    }

                    // date
                    Date date = getVacationDate(element.select("[data-qa=vacancy-serp__vacancy-date]").first());
                    if (date.compareTo(AppDateUtils.addDaysToDate(new Date(), searchParam.getDays())) == -1) {
                        continue;
                    }

                    // salary
                    String salary = "";
                    Element elem = element.select("[data-qa=vacancy-serp__vacancy-compensation]").first();
                    if (elem != null)
                        salary = elem.text();

                    //Add Vacancy
                    //Vacancy vacancy = new Vacancy();
                    vacancy.setTitle(title);
                    vacancy.setSalary(salary);
                    vacancy.setCity(element.select("[data-qa=vacancy-serp__vacancy-address]").first().text());
                    vacancy.setCompanyName(element.select("[data-qa=vacancy-serp__vacancy-employer]").first().text());
                    vacancy.setSiteName("http://hh.ua/");
                    //vacancy.setUrl(element.select("[data-qa=\"vacancy-serp__vacancy-title\"]").attr("href"));
                    vacancy.setUrl(vacancyURL);

                    //vacancy.setDate(getVacationDate(vacancy.getUrl()));
                    vacancy.setDate(date);

                    vacancies.add(vacancy);

                    System.out.println("HH: " + vacancy.getUrl());*/
                }
            }

        } catch (Exception e) {
//            e.printStackTrace();
        }

        return vacancies;

    }

    @Override
    public Vacancy getVacancy(String vacancyURL) {

        Vacancy vacancy = new Vacancy();
        String vacancyCompanyName = "";
        String vacancyDate = "";
        String vacancyTitle = "";
        String vacancyCity = "";
        String vacancySalary = "";
        try {
            Document vacancyDoc = documentConnect.getDocument(vacancyURL);
            //System.out.println("VacancyDoc: " + vacancyDoc);
            if (vacancyDoc != null) {
                Elements vacancyCompanyEls = vacancyDoc.select("[data-qa=vacancy-company-name]");
                if (!CollectionUtils.isEmpty(vacancyCompanyEls)) {
                    vacancyCompanyName = vacancyCompanyEls.first().getElementsByTag("span").first().getElementsByTag("span").first().text();
                }
                vacancyDate = getTextByClassName(vacancyDoc, "vacancy-creation-time");
                vacancyTitle = getTextBySelect(vacancyDoc, "[data-qa=vacancy-title]");
                vacancyCity = getTextByClassName(vacancyDoc, "[data-qa=vacancy-view-location]");

                vacancySalary = vacancyDoc.getElementsByClass("vacancy-salary").first().getElementsByTag("span").text();

                vacancy.setTitle(vacancyTitle);
                vacancy.setUrl(vacancyURL);
                vacancy.setCity(vacancyCity);
                vacancy.setSalary(vacancySalary);
                vacancy.setCompanyName(vacancyCompanyName);
                vacancy.setDate(parseVacationDate(vacancyDate));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println("DOU: " + vacancy);
        return vacancy;
    }

/*    private Date getVacationDate(Element element) {
        Date now = new Date();
        Date vacationDate = now;
        if (element != null) {
            String stringDate = element.text().replaceAll("\u00a0", " ");
            try {
                //System.out.println("DateElem");
                //System.out.println("String: " + stringDate + " | " + vacationDate);
                //vacationDate = simpleDateShortFormat.parse(stringDate);
                //System.out.println("Parse: " + vacationDate);

                vacationDate = AppDateUtils.changeDateYear(vacationDate, AppDateUtils.getYearFromDate(now));
                //System.out.println("Change year: " + vacationDate);
                if (vacationDate.compareTo(now) == 1) {
                    vacationDate = AppDateUtils.changeDateYear(vacationDate, AppDateUtils.getYearFromDate(now) - 1);
                    //System.out.println("Change year2: " + vacationDate);
                }

            } catch (Exception e) {
//            e.printStackTrace();
            }
        }
        return vacationDate;
    }

    private Date getVacationDate(String searchString) {
        Date vacationDate = new Date();
        try {
            Document doc = documentConnect.getDocument(searchString);
            if (doc != null) {
                Element element = doc.getElementsByClass("vacancy-sidebar").first();
                if (element != null) {
                    String stringDate = element.getElementsByClass("vacancy-sidebar__publication-date").first().attr("datetime");
                    vacationDate = simpleDateFormat.parse(stringDate);
                }
            }
        } catch (IOException e) {
//            e.printStackTrace();
        } catch (ParseException e) {
//            e.printStackTrace();
        }

        return vacationDate;
    }*/


    private Date parseVacationDate(String dateString) {
        //System.out.println("Date text: " + dateString);
        try {
            String[] dataWords = dateString.split(" ");
            //replace non-breaking space (&nbsp;) with space
            //.replaceAll("&nbsp;"," ");
            dateString = dataWords[2].replaceAll(String.valueOf(NON_BREAKING_SPACE_CHAR), " ");
            return simpleDateFormat.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        /*try {
            return simpleDateFormatUA.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }*/
        return new Date();
    }

}
