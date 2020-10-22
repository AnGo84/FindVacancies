package ua.findvacancies.mvc.model.strategy;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;

public abstract class AbstractStrategy implements Strategy{

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
