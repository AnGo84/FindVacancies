package ua.findvacancies.mvc.model;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ua.findvacancies.mvc.utils.AppDateUtils;
import ua.findvacancies.mvc.utils.AppStringUtils;
import ua.findvacancies.mvc.viewdata.Vacancy;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by AnGo on 31.08.2017.
 */
public class DOUStrategy implements Strategy {
    private static final String URL_FORMAT = "https://jobs.dou.ua/vacancies/?search=%s&city=Киев";
    private static final String HTTPS_DOU_UA = "https://dou.ua/";
    private static final String WORD_SEPARATOR = "+";

    private static final String DATE_FORMAT = "dd MMMM yyyy";

//    private static final String[] shortMonths = {
//            "янв", "фев", "мар", "апр", "май", "июн",
//            "июл", "авг", "сен", "окт", "ноя", "дек"};
    private static final String[] months = {
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"};
    private static final Locale locale = new Locale("ru");
    private static final DateFormatSymbols dateFormatSymbols = DateFormatSymbols.getInstance(locale);
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT, locale);

    static {
        //dateFormatSymbols.setShortMonths(shortMonths);
        dateFormatSymbols.setMonths(months);
        simpleDateFormat.setDateFormatSymbols(dateFormatSymbols);
    }

    @Override
    //public List<Vacancy> getVacancies(String words, int days) {
    public List<Vacancy> getVacancies(SearchParam searchParam) {
        List<Vacancy> vacancies = new ArrayList<>();
        System.out.println("SearchParam: " + searchParam);
        System.out.println("SearchLine: " + searchParam.getKeyWordsSearchLine());
        try {
            //int pageCount = 1;
            //while (true) {
            String keyWords = searchParam.getKeyWordsSearchLine();
            while (true) {


                //Document doc = StrategyDocument.getDocument(String.format(URL_FORMAT, keyWords, pageCount++));
                String documentURL = String.format(URL_FORMAT, searchParam.getKeyWordsSearchLine());
                System.out.println("documentURL: "+ documentURL);
                Document doc = StrategyDocument.getDocument(documentURL);
                //getDocument(searchString, pageCount++);
                if (doc == null) break;

                Elements elements = doc.getElementsByClass("l-vacancy");

                if (elements.size() == 0) break;

                for (Element element : elements) {
                    // title
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
                    //System.out.println("DOU: " + vacancy.getUrl());
                }

                break;

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
                Element element = doc.getElementsByClass("b-vacancy").first();
                //System.out.println(element.text());
                if (element != null) {

                    Element dateElement = element.getElementsByClass("date").first();
                    if (dateElement != null) {
                        //System.out.println("DateElem");
                        //System.out.println("String: " + dateElement.text() + " | " + vacationDate);
                        vacationDate = simpleDateFormat.parse(dateElement.text());
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
