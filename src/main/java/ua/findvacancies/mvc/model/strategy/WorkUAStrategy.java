package ua.findvacancies.mvc.model.strategy;


import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ua.findvacancies.mvc.model.SearchParam;
import ua.findvacancies.mvc.model.Vacancy;
import ua.findvacancies.mvc.utils.AppDateUtils;
import ua.findvacancies.mvc.utils.AppStringUtils;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by AnGo on 22.06.2017.
 */
public class WorkUAStrategy implements Strategy {
    private static final String URL_FORMAT = "https://www.work.ua/jobs-kyiv-%s/?page=%d";

    private static final String HTTPS_WORK_UA = "https://work.ua";
    private static final String WORD_SEPARATOR = "+";

    private static final String DATE_FORMAT = "dd.MM.yyyy";
    private static final String DATE_FORMAT_TEXT = "dd MMMM yyyy";
    private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT);

    private static final char PARSE_SYMBOL = '\u00A0';

    private static final String[] months = {
            "січня", "лютого", "березня", "квітня", "травня", "червня",
            "липня", "серпня", "вересня", "жовтня", "листопада", "грудня"};

    private static final Locale locale = new Locale("ua");
    private static final DateFormatSymbols dateShotFormatSymbols = DateFormatSymbols.getInstance(locale);
    private static final SimpleDateFormat simpleDateTextFormat = new SimpleDateFormat(DATE_FORMAT_TEXT, locale);

    static {
        //dateShotFormatSymbols.setShortMonths(shortMonths);
        dateShotFormatSymbols.setMonths(months);
        simpleDateTextFormat.setDateFormatSymbols(dateShotFormatSymbols);
    }

    @Override
    public Vacancy getVacancy(String vacancyURL) {
        return null;
    }


    @Override
    //public List<Vacancy> getVacancies(String words, int days) {
    public List<Vacancy> getVacancies(SearchParam searchParam) {
        List<Vacancy> vacancies = new ArrayList<>();

        try {
            int pageCount = 0;
            boolean hasData = true;
            while (hasData) {
                //System.out.println(String.format(URL_FORMAT, keyWords, pageCount));

                String searchPageURL = String.format(URL_FORMAT, searchParam.getKeyWordsSearchLine(), ++pageCount);
                System.out.println("Page " + pageCount + ", URL: " + searchPageURL);
                Document doc = StrategyDocument.getDocument(searchPageURL);
                if (doc == null) break;

                Elements elements = doc.getElementsByClass("card-hover");

                if (elements.size() == 0) {
                    break;
                }
                int vacancyOnPage = 0;
                for (Element element : elements) {

                    if (element.getElementsByClass("row").first() != null || element.getElementsByClass("logotype-slides").first()
                            != null || element.getElementsByClass("clearfix").first() != null) {
                        continue;
                    }
                    vacancyOnPage++;
                    // title
                    Element titleElement = element.getElementsByClass("card").first();
                    // url
                    String vacancyURL = HTTPS_WORK_UA + titleElement.getElementsByTag("a").attr("href");

                    // isHot
                    Element hotElement = element.getElementsByClass("label-hot").first();

                    System.out.println("Page " + pageCount + ", vacancy " + vacancyOnPage + ", Vacancy URL: "
                            + vacancyURL + ", isHot: " + (hotElement != null));
                    //+ ", added: " + agoTime

                    if (titleElement == null) {
                        continue;
                    }
                    titleElement = titleElement.getElementsByTag("h2").first();
                    //System.out.println("TITLE2: " + titleElement);
                    if (titleElement == null) {
                        continue;
                    }

                    if (titleElement.getElementsByTag("a").first() == null) {
                        continue;
                    }
                    String title = titleElement.getElementsByTag("a").first().text();
                    if (AppStringUtils.isStringIncludeWords(title, searchParam.getExcludeWords())) {
                        continue;
                    }

                    // date
                    Date date;
                    if (hotElement != null) {
                        date = new Date();
                    } else {
                        date = getVacationDate(vacancyURL);
                    }
                    System.out.println("Date: " + date);
                    if (date.compareTo(AppDateUtils.addDaysToDate(new Date(), searchParam.getDays())) == -1) {
                        hasData = false;
                        break;
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
                    String city = "";
                    if (nextToTitle.getElementsByClass("text-muted").first() != null && nextToTitle.getElementsByClass("text-muted").first().nextElementSibling() != null
                            && nextToTitle.getElementsByClass("text-muted").first().nextElementSibling().getElementsByTag("span").first().text() != null) {
                        String line = nextToTitle.getElementsByClass("text-muted").first().nextElementSibling().getElementsByTag("span").first().text();

                        String[] lines = line.split(" · ");
                        city = lines[0];
                    }
                    // site
                    String siteName = HTTPS_WORK_UA;

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

                    System.out.println("WORK_UA added: " + vacancy.getUrl());
                }
            }

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("Error: " + e.getMessage());
        }

        return vacancies;
    }


    private Date getVacationDate(String searchString) {
        Date vacationDate = new Date();

        try {
            Document doc = StrategyDocument.getDocument(searchString);
            if (doc != null) {

                //Elements elements = doc.getElementsByClass("design-verse");
                Elements elements = doc.getElementsByClass("card");
                if (elements.size() != 0) {
                    //elements = doc.getElementsByClass("card");
                    if (elements.size() != 0) {
                        elements = doc.getElementsByClass("cut-bottom-print");
                        if (elements.size() != 0) {
                            elements = doc.getElementsByClass("text-muted");
                            if (elements.size() != 0) {
                                String stringDate = elements.first().text();
                                System.out.println("Element 'text-muted' present: " + (elements != null) + ", value: " + stringDate);
                                stringDate = stringDate.substring(stringDate.lastIndexOf(PARSE_SYMBOL) + 1, stringDate.length());
                                System.out.println("Parse date: " + stringDate);
                                vacationDate = simpleDateTextFormat.parse(stringDate);
                                System.out.println("Get date: " + vacationDate);
                            }
                            else {
                                System.out.println("Element 'text-muted' is null");
                            }
                        }
                        else {
                            System.out.println("Element 'cut-bottom-print' is null");
                        }
                    }
                    else {
                        System.out.println("Element 'card' is null");
                    }
                }
                else {
                    System.out.println("Element 'design-verse/card' is null");
                }

            } else {
                System.out.println("Element " + searchString + " is null");
            }
        } catch (IOException e) {
//            e.printStackTrace();
        } catch (ParseException e) {
            System.out.println(e.getMessage());
//            e.printStackTrace();
        }

        return vacationDate;
    }
}
