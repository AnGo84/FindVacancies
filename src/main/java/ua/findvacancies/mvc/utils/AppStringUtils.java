package ua.findvacancies.mvc.utils;

import org.jsoup.helper.StringUtil;
import org.springframework.util.CollectionUtils;

import java.util.Set;

public class AppStringUtils {

    public static boolean isStringIncludeWords(String line, Set<String> words) {
        if (CollectionUtils.isEmpty(words) || StringUtil.isBlank(line)) {
            return false;
        }
        boolean isInclude = false;
        for (String word : words) {
            if (line.toLowerCase().contains(word)) {
                isInclude = true;
                break;
            }
        }
        return isInclude;
    }

    public static int getNumberFromText(String numberText) {
        try {
            return Integer.parseInt(numberText);
        }
        catch (NumberFormatException e){
            return 0;
        }
    }
}
