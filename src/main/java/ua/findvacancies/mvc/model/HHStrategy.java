package ua.findvacancies.mvc.model;


import org.apache.poi.ss.usermodel.DateUtil;
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
 * Created by AnGo on 04.05.2016.
 */
public class HHStrategy implements Strategy {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String DATE_FORMAT_SHORT = "dd MMMM";
    //private static final String URL_FORMAT = "http://hh.ua/search/vacancy?text=java+%s&page=%d";
    private static final String URL_FORMAT = "https://hh.ua/search/vacancy?text=%s&enable_snippets=true&clusters=true&currency_code=UAH&area=115&page=%d";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

    private static final String[] months = {
            "января", "февраля", "марта", "апреля", "мая", "июня",
            "июля", "августа", "сентября", "октября", "ноября", "декабря"};
    private static final Locale locale = new Locale("ru");
    private static final DateFormatSymbols dateShotFormatSymbols = DateFormatSymbols.getInstance(locale);
    private static final SimpleDateFormat simpleDateShortFormat = new SimpleDateFormat(DATE_FORMAT_SHORT, locale);

    static {
        //dateShotFormatSymbols.setShortMonths(shortMonths);
        dateShotFormatSymbols.setMonths(months);
        simpleDateShortFormat.setDateFormatSymbols(dateShotFormatSymbols);
    }



    @Override
    public List<Vacancy> getVacancies(String words, int days) {
        List<Vacancy> vacancies = new ArrayList<>();
        try {
            int pageCount = 0;
            while (true) {
                String keyWords = StringUtils.getKeyWordsLine(words);
                Set<String> excludeWords = StringUtils.getExcludeWordsSet(words);

                Document doc = StrategyDocument.getDocument(String.format(URL_FORMAT, keyWords, pageCount++));
                if (doc == null) break;
                Elements elements = doc.select("[data-qa=vacancy-serp__vacancy]");
                if (elements.size() == 0) break;

                for (Element element : elements) {
                    // title
                    String title = element.select("[data-qa=vacancy-serp__vacancy-title]").first().text();
                    if (StringUtils.isStringIncludeWords(title, excludeWords)) {
                        continue;
                    }

                    // url
                    String url = element.select("[data-qa=\"vacancy-serp__vacancy-title\"]").attr("href");

                    // date
                    Date date = getVacationDate(element.select("[data-qa=vacancy-serp__vacancy-date]").first());
                    if (date.compareTo(DateUtils.addDaysToDate(new Date(), days)) == -1) {
                        continue;
                    }

                    //vacancy.setTitle(element.select("[data-qa=vacancy-serp__vacancy-title]").first().text());
                    //vacancy.setTitle(element.select("[data-qa=vacancy-serp__vacancy-title]").first().text());

                    // salary
                    String salary = "";
                    Element elem = element.select("[data-qa=vacancy-serp__vacancy-compensation]").first();
                    if (elem != null)
                        salary = elem.text();

                    //Add Vacancy
                    Vacancy vacancy = new Vacancy();
                    vacancy.setTitle(title);
                    vacancy.setSalary(salary);
                    vacancy.setCity(element.select("[data-qa=vacancy-serp__vacancy-address]").first().text());
                    vacancy.setCompanyName(element.select("[data-qa=vacancy-serp__vacancy-employer]").first().text());
                    vacancy.setSiteName("http://hh.ua/");
                    //vacancy.setUrl(element.select("[data-qa=\"vacancy-serp__vacancy-title\"]").attr("href"));
                    vacancy.setUrl(url);

                    //vacancy.setDate(getVacationDate(vacancy.getUrl()));
                    vacancy.setDate(date);

                    vacancies.add(vacancy);
                }
            }

        } catch (IOException e) {
//            e.printStackTrace();
        }

        return vacancies;

    }

    private Date getVacationDate(Element element) {
        Date now = new Date();
        Date vacationDate = now;
        if (element != null) {
            String stringDate = element.text().replaceAll("\u00a0"," ");
            try {
                //System.out.println("DateElem");
                //System.out.println("String: " + stringDate + " | " + vacationDate);
                vacationDate = simpleDateShortFormat.parse(stringDate);
                //System.out.println("Parse: " + vacationDate);

                vacationDate = DateUtils.changeDateYear(vacationDate, DateUtils.getYearFromDate(now));
                //System.out.println("Change year: " + vacationDate);
                if (vacationDate.compareTo(now)==1){
                    vacationDate = DateUtils.changeDateYear(vacationDate, DateUtils.getYearFromDate(now)-1);
                    //System.out.println("Change year2: " + vacationDate);
                }

            } catch (ParseException e) {
//            e.printStackTrace();
            }
        }
        return vacationDate;
    }

    private Date getVacationDate(String searchString) {
        Date vacationDate = new Date();
        try {
            Document doc = StrategyDocument.getDocument(searchString);
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
    }

}
