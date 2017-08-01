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
 * Created by AnGo on 22.06.2017.
 */
public class WorkUAStrategy implements Strategy {
    private static final String URL_FORMAT = "https://www.work.ua/jobs-kyiv-%s/?page=%d";
    private static final String HTTPS_WORK_UA = "https://work.ua";

    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

    private static final char PARSE_SYMBOL = '\u00A0';

    @Override
    public List<Vacancy> getVacancies(String words, int days) {
        List<Vacancy> vacancies = new ArrayList<>();

        try {
            int pageCount = 1;
            while (true) {
                String keyWords = StringUtils.getKeyWordsLine(words);
                Set<String> excludeWords = StringUtils.getExcludeWordsSet(words);

                //System.out.println(String.format(URL_FORMAT, keyWords, pageCount));

                Document doc = StrategyDocument.getDocument(String.format(URL_FORMAT, keyWords, pageCount++));
                if (doc == null) break;

                Elements elements = doc.getElementsByClass("card");

                if (elements.size() == 0) {
                    break;
                }

                for (Element element : elements) {
                    if (element.getElementsByClass("row").first() != null || element.getElementsByClass("logotype-slides").first() != null || element.getElementsByClass("clearfix").first() != null) {
                        continue;
                    }

                    // title
                    Element titleElement = element.getElementsByTag("h2").first();
                    String title = titleElement.getElementsByTag("a").first().text();
                    if (StringUtils.isStringIncludeWords(title, excludeWords)){
                        continue;
                    }

                    // url
                    String url = HTTPS_WORK_UA + element.getElementsByTag("a").attr("href");

                    // date
                    Date date = getVacationDate(url);
                    if (date.compareTo(DateUtils.addDaysToDate(new Date(), days))==-1){
                        //System.out.println("True for Date " + (new Date()) + " VS 10 " + date);
                        continue;
                    }

                    // salary
                    Element salaryEl = titleElement.getElementsByClass("nowrap").first();
                    String salary = "";
                    if (salaryEl != null) {
                        salary = salaryEl.text();
                    }

                    Element nextToTitle = titleElement.nextElementSibling();

                    // company
                    String companyName = nextToTitle.getElementsByTag("span").first().text();

                    // city (it's important)
                    String line = nextToTitle.getElementsByClass("text-muted").first().nextElementSibling().getElementsByTag("span").first().text();
                    String[] lines = line.split(" · ");
                    String city = lines[0];

                    // site
                    String siteName = HTTPS_WORK_UA;

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
                Elements elements = doc.getElementsByClass("card");
                if (elements.size() != 0) {
                    for (Element element : elements) {
                        if (element.getElementsByClass("menu-action-more").first() != null) {
                            continue;
                        }
                        element = element.getElementsByClass("add-top").first();

                        if (element != null) {
                            element = element.getElementsByClass("text-muted").first();
                            if (element != null) {
                                String stringDate = element.text();
                                if (!stringDate.equals("Горячая вакансия")) {
                                    //System.out.println("String: " + element.text() + " | " + vacationDate + " | " + stringDate.substring(stringDate.lastIndexOf(PARSE_SYMBOL)+1, stringDate.length()));
                                    vacationDate = simpleDateFormat.parse(stringDate.substring(stringDate.lastIndexOf(PARSE_SYMBOL) + 1, stringDate.length()));

                                }
                            }
                        }
                        break;
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
