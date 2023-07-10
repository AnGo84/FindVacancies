package ua.findvacancies.utils;

import org.jsoup.internal.StringUtil;
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

    public static int nth(String source, String pattern, int n) {

        int i = 0, pos = 0, tpos = 0;

        while (i < n) {

            pos = source.indexOf(pattern);
            if (pos > -1) {
                source = source.substring(pos+1);
                tpos += pos+1;
                i++;
            } else {
                return -1;
            }
        }

        return tpos - 1;
    }

}
