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
 * Created by AnGo on 31.08.2017.
 */
public class DOUStrategy implements Strategy {
    private static final String URL_FORMAT = "https://jobs.dou.ua/vacancies/?category=Java&search=%s&city=Киев";
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
    public List<Vacancy> getVacancies(String words, int days) {
        List<Vacancy> vacancies = new ArrayList<>();

        try {
            //int pageCount = 1;
            //while (true) {
            while (true) {
                String keyWords = StringUtils.getKeyWordsLine(words, WORD_SEPARATOR);
                Set<String> excludeWords = StringUtils.getExcludeWordsSet(words);

                //Document doc = StrategyDocument.getDocument(String.format(URL_FORMAT, keyWords, pageCount++));
                Document doc = StrategyDocument.getDocument(String.format(URL_FORMAT, keyWords));
                //getDocument(searchString, pageCount++);
                if (doc == null) break;

                Elements elements = doc.getElementsByClass("l-vacancy");

                if (elements.size() == 0) break;

                for (Element element : elements) {
                    // title
                    Element titleElement = element.getElementsByClass("vt").first();
                    String title = titleElement.text();

                    if (StringUtils.isStringIncludeWords(title, excludeWords)) {
                        continue;
                    }

                    // url
                    String url = titleElement.getElementsByTag("a").attr("href");

                    // date
                    Date date = getVacationDate(url);
                    if (date.compareTo(DateUtils.addDaysToDate(new Date(), days)) == -1) {
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
