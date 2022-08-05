package ua.findvacancies.model.strategy;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;
import ua.findvacancies.model.SearchParam;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.utils.VacancyUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractStrategy implements Strategy{

    public static final char NON_BREAKING_SPACE_CHAR = '\u00A0';
    public List<Vacancy> vacancies;

    public abstract String getSiteURL();
    public abstract String getSiteURLPattern();

    public void initVacanciesList(){
        vacancies = new ArrayList<>();
    }

    public void checkAndAddVacancyToList(Vacancy vacancy, SearchParam searchParam){
        if (VacancyUtils.isApplyToSearch(vacancy, searchParam)) {
            vacancies.add(vacancy);
        }
    }

    public String getTextFromFirstElByClassName(Elements elements, String className) {
        if(CollectionUtils.isEmpty(elements)){
            return "";
        }
        return getTextByClassName(elements.first(), className);
    }

    public String getTextByClassName(Element element, String className) {
        Elements classEls = element.getElementsByClass(className);
        return getTextFromFirstEl(classEls);
    }

    public String getTextBySelect(Element element, String select) {
        Elements classEls = element.select(select);
        return getTextFromFirstEl(classEls);
    }

    private String getTextFromFirstEl(Elements classEls) {
        if (CollectionUtils.isEmpty(classEls)) {
            return "";
        }
        return classEls.first().text();
    }
}
