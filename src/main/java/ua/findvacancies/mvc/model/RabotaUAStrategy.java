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
    //private static final String URL_FORMAT = "https://rabota.ua/jobsearch/vacancy_list?regionId=1&keyWords=%s&pg%d";
    private static final String URL_FORMAT = "https://rabota.ua/zapros/%s/киев/pg%d";

    private static final String HTTPS_RABOTA_UA = "https://rabota.ua";
    private static final String DATE_FORMAT = "dd MMM yyyy";

    private static final String WORD_SEPARATOR = "-";

    private static final String[] shortMonths = {
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
    }

    @Override
    public List<Vacancy> getVacancies(String words, int days) {
        List<Vacancy> vacancies = new ArrayList<>();

        try {
            int pageCount = 0;
            boolean hasData = true;
            while (hasData) {
                String keyWords = StringUtils.getKeyWordsLine(words, WORD_SEPARATOR);
                Set<String> excludeWords = StringUtils.getExcludeWordsSet(words);
                String searchPageURL = String.format(URL_FORMAT, keyWords, ++pageCount);
                System.out.println("Page " + pageCount + ", URL: " + searchPageURL);
                Document doc = StrategyDocument.getDocument(searchPageURL);

                //getDocument(searchString, pageCount++);
                if (doc == null) {
                    break;
                }

                Elements elements = doc.getElementsByClass("f-vacancylist-vacancyblock");


                if (elements.size() == 0) break;
                int vacancyOnPage = 0;
                for (Element element : elements) {
                    vacancyOnPage++;
                    // title
                    Element titleElement = element.getElementsByClass("f-vacancylist-vacancytitle").first();
                    String title = titleElement.text();

                    // url
                    String vacancyURL = HTTPS_RABOTA_UA + titleElement.getElementsByTag("a").attr("href");

                    // isHot
                    Element hotElement = element.getElementsByClass("fi-hot").first();

                    // f-vacancylist-agotime
                    Element agoTimeEl = element.getElementsByClass("f-vacancylist-bottomblock").first();
                    String agoTime = agoTimeEl.getElementsByClass("f-vacancylist-agotime").text();

                    System.out.println("Page " + pageCount + ", vacancy " + vacancyOnPage + ", Vacancy URL: " + vacancyURL +
                            ", isHot: " + (hotElement == null) + ", added: " + agoTime);
                    boolean isJustAdd = (agoTime.contains("минут") || agoTime.contains("час") || hotElement != null);

                    if (StringUtils.isStringIncludeWords(title, excludeWords)) {
                        continue;
                    }


                    // date
                    Date date = getVacationDate(vacancyURL);
                    if (date.compareTo(DateUtils.addDaysToDate(new Date(), days)) == -1 && !isJustAdd) {
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
                    String siteName = HTTPS_RABOTA_UA;

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
    }
}
