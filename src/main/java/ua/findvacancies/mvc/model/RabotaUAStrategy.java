package ua.findvacancies.mvc.model;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ua.findvacancies.mvc.utils.DateUtils;
import ua.findvacancies.mvc.utils.StringUtils;
import ua.findvacancies.mvc.vo.Vacancy;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by AnGo on 22.06.2017.
 */
public class RabotaUAStrategy implements Strategy {
    private static final String URL_FORMAT = "https://rabota.ua/jobsearch/vacancy_list?regionId=1&keyWords=%s&pg=%d";
    private static final String HTTPS_RABOTA_UA = "https://rabota.ua";
    private static final String DATE_FORMAT = "dd MMM yyyy";


    private static final String[] shortMonths = {
            "янв", "фев", "мар", "апр", "май", "июн",
            "июл", "авг", "сен", "окт", "ноя", "дек"};
    private static final Locale locale = new Locale("ru");
    private static final DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(locale);
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, locale);

    static {
        dateFormatSymbols.setShortMonths(shortMonths);
        simpleDateFormat.setDateFormatSymbols(dateFormatSymbols);
    }

    private static final String DATE_NUMBER_FORMAT = "dd.MM.yyyy";
    private static final SimpleDateFormat simpleDateNumberFormat = new SimpleDateFormat(DATE_NUMBER_FORMAT);

    @Override
    public List<Vacancy> getVacancies(String words, int days) {
        List<Vacancy> vacancies = new ArrayList<>();

        try {
            int pageCount = 1;
            while (true) {
                String keyWords = StringUtils.getKeyWordsLine(words);
                Set<String> excludeWords = StringUtils.getExcludeWordsSet(words);

                Document doc = StrategyDocument.getDocument(String.format(URL_FORMAT, keyWords, pageCount++));
                //getDocument(searchString, pageCount++);
                if (doc == null) break;

                Elements elements = doc.getElementsByClass("f-vacancylist-vacancyblock");

                if (elements.size() == 0) break;

                for (Element element : elements) {
                    // title
                    Element titleElement = element.getElementsByClass("f-vacancylist-vacancytitle").first();
                    String title = titleElement.text();
                    if (StringUtils.isStringIncludeWords(title, excludeWords)) {
                        continue;
                    }

                    // url
                    String url = HTTPS_RABOTA_UA + titleElement.getElementsByTag("a").attr("href");

                    // date
                    Date date = getVacationDate(url);
                    if (date.compareTo(DateUtils.addDaysToDate(new Date(), days)) == -1) {
                        continue;
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
                    String siteName = HTTPS_RABOTA_UA;


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

                    //System.out.println("RABOTA_UA: " + vacancy.getUrl());
                }
            }

        } catch (IOException e) {
//            e.printStackTrace();
        }

        return vacancies;
    }

    private Date getVacationDate(String searchString) {
        Date vacationDate = new Date();
        //System.out.println("New: " +  vacationDate);
        try {
            Document doc = StrategyDocument.getDocument(searchString);
            if (doc != null) {
                Element element = doc.getElementsByClass("f-vacancy-header-wrapper").first();
                //Element element = doc.getElementById("d-date");
                if (element != null) {

                    Element dateElement = element.getElementsByClass("f-date-holder").first();
                    if (dateElement != null) {
                        //System.out.println("DateElem");
                        //System.out.println("String1: " + dateElement.text() + " | " + vacationDate);
                        vacationDate = simpleDateFormat.parse(dateElement.text());
                        //System.out.println("Parse: " + vacationDate);
                    }
                }
                else if ((element = doc.getElementById("d-date"))!=null ){
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
    }
}
