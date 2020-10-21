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

public class DOUStrategy extends AbstractStrategy {
    public static final String URL_FORMAT = "https://jobs.dou.ua/vacancies/?search=%s&city=Киев";
    public static final String HTTPS_DOU_UA = "https://dou.ua/";

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

    public DOUStrategy(DocumentConnect documentConnect) {
        this.documentConnect = documentConnect;
    }

    @Override
    public List<Vacancy> getVacancies(SearchParam searchParam) {
        if (searchParam==null){
            return Collections.emptyList();
        }
        List<Vacancy> vacancies = new ArrayList<>();
        try {
            System.out.println("SearchParam: " + searchParam);
            System.out.println("SearchLine: " + searchParam.getKeyWordsSearchLine());
            while (true) {
                String documentURL = String.format(URL_FORMAT, searchParam.getKeyWordsSearchLine());
                //System.out.println("documentURL: " + documentURL);
                Document doc = documentConnect.getDocument(documentURL);
                //getDocument(searchString, pageCount++);
                if (doc == null) break;
                Element vacancyListIdEl = doc.getElementById("vacancyListId");
                //System.out.println("vacanciesListEl: " + vacancyListIdEl);

                Element moreBtnEl = doc.getElementsByClass("more-btn").first();
                //System.out.println("moreBtnEl: "+ moreBtnEl);

                //Elements vacanciesListEl = vacancyListIdEl.getElementsByClass("lt").first().getElementsByClass("l-vacancy");
                Elements vacanciesListEl = vacancyListIdEl.getElementsByClass("l-vacancy");
                //System.out.println("vacanciesListEl: "+ vacanciesListEl);
                System.out.println("vacanciesListEl size: " + vacanciesListEl.size());
                if (vacanciesListEl.size() == 0) break;

                for (Element element : vacanciesListEl) {
                    String vacancyURL = element.getElementsByTag("a").attr("href");
                    System.out.println("URL: " + vacancyURL);
                    Vacancy vacancy = getVacancy(vacancyURL);
                    Elements hotsEl = element.getElementsByClass("__hot");
                    boolean isHotVacancy = !CollectionUtils.isEmpty(hotsEl);
                    vacancy.setHot(isHotVacancy);
                    vacancy.setSiteName(HTTPS_DOU_UA);

                    System.out.println("DOU: " + vacancy);

                    if (VacancyUtils.isNotApplyToSearch(vacancy, searchParam)) {
                        continue;
                    }

                    vacancies.add(vacancy);
                    /*// title
                    Element titleElement = element.getElementsByClass("vt").first();
                    String title = titleElement.text();

                    if (AppStringUtils.isStringIncludeWords(title, searchParam.getExcludeWords())) {
                        continue;
                    }

                    // url
                    String url = titleElement.getElementsByTag("a").attr("href");

                    // date
                    Date date = getVacationDate(url);
                    if (date.compareTo(AppDateUtils.addDaysToDate(new Date(), searchParam.getDays())) == -1) {
                        continue;
                    }
                    // company
                    String companyName = element.getElementsByClass("company").first().text();

                    // site
                    String siteName = HTTPS_DOU_UA;

                    // salary
                    String salary = "";

                    // city (it's important)
                    String city = element.getElementsByClass("cities").first().text().replaceAll("\"","");

                    // add vacancy to the list
                    Vacancy vacancy = new Vacancy();
                    vacancy.setTitle(title);
                    vacancy.setSalary(salary);
                    vacancy.setCity(city);
                    vacancy.setCompanyName(companyName);
                    vacancy.setSiteName(siteName);
                    vacancy.setUrl(url);
                    vacancy.setDate(date);
                    vacancies.add(vacancy);
                    System.out.println("DOU: " + vacancy.getUrl());*/
                }

                break;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return vacancies;
    }

    @Override
    public Vacancy getVacancy(String vacancyURL) {

        Vacancy vacancy = new Vacancy();
        String vacancyCompanyName = "";
        String vacancyData = "";
        String vacancyTitle = "";
        String vacancyCity = "";
        String vacancySalary = "";
        try {
            Document vacancyDoc = documentConnect.getDocument(vacancyURL);
            System.out.println("VacancyDoc: " + vacancyDoc);
            if (vacancyDoc != null) {
                Element vacancyEl = vacancyDoc.getElementsByClass("b-vacancy").first();
                Element vacancyCompanyInfoEl = vacancyEl.getElementsByClass("b-compinfo").first();
                vacancyCompanyName = getTextGetTextByClass(vacancyCompanyInfoEl, "l-n");
                vacancyData = getTextGetTextByClass(vacancyEl, "date");
                vacancyTitle = getTextGetTextByClass(vacancyEl, "g-h2");
                vacancyCity = getTextGetTextByClass(vacancyEl, "place");
                vacancySalary = getTextGetTextByClass(vacancyEl, "salary");

                vacancy.setTitle(vacancyTitle);
                vacancy.setUrl(vacancyURL);
                vacancy.setCity(vacancyCity);
                vacancy.setSalary(vacancySalary);
                vacancy.setCompanyName(vacancyCompanyName);
                vacancy.setDate(parseVacationDate(vacancyData));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //System.out.println("DOU: " + vacancy);
        return vacancy;
    }

    private String getTextGetTextByClass(Element element, String className) {
        Elements classEls = element.getElementsByClass(className);
        if (CollectionUtils.isEmpty(classEls)) {
            return "";
        }
        return classEls.first().text();
    }

    private Date parseVacationDate(String dataString) {
        try {
            return simpleDateFormatRU.parse(dataString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            return simpleDateFormatUA.parse(dataString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }
}
