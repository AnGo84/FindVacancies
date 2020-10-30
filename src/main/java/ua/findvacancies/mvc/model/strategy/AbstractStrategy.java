package ua.findvacancies.mvc.model.strategy;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.util.CollectionUtils;

public abstract class AbstractStrategy implements Strategy{

    public static final char NON_BREAKING_SPACE_CHAR = '\u00A0';

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
