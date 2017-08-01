package ua.findvacancies.mvc.model;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ua.findvacancies.mvc.utils.DateUtils;
import ua.findvacancies.mvc.utils.StringUtils;
import ua.findvacancies.mvc.vo.Vacancy;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by AnGo on 04.05.2016.
 */
public class HHStrategy implements Strategy {
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    //private static final String URL_FORMAT = "http://hh.ua/search/vacancy?text=java+%s&page=%d";
    private static final String URL_FORMAT = "https://hh.ua/search/vacancy?text=%s&enable_snippets=true&clusters=true&currency_code=UAH&area=115&page=%d";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

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
                    String title = element.select("[data-qa=vacancy-serp__vacancy-title]").first().text();
                    if (StringUtils.isStringIncludeWords(title, excludeWords)){
                        continue;
                    }
                    String url = element.select("[data-qa=\"vacancy-serp__vacancy-title\"]").attr("href");
                    Date date = getVacationDate(url);
                    if (date.compareTo(DateUtils.addDaysToDate(new Date(), days))==-1){
                        continue;
                    }

                    //Add Vacancy
                    Vacancy vacancy = new Vacancy();
                    //vacancy.setTitle(element.select("[data-qa=vacancy-serp__vacancy-title]").first().text());
                    //vacancy.setTitle(element.select("[data-qa=vacancy-serp__vacancy-title]").first().text());
                    vacancy.setTitle(title);

                    String salary = "";
                    Element elem = element.select("[data-qa=vacancy-serp__vacancy-compensation]").first();
                    if (elem != null)
                        salary = elem.text();
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
