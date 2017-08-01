package ua.findvacancies.mvc.utils;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by AnGo on 27.06.2017.
 */
public class StringUtils {
    public static String getKeyWordsLine(String wordsLine) {
        if (wordsLine == null || wordsLine.equals("")) {
            return "";
        }
        String[] words = wordsLine.split(" ");
        String keyWords = "";
        for (String word : words) {
            if (!isExcludeWord(word)) {
                if (keyWords.equals("")) {
                    keyWords = word;
                } else {
                    keyWords += "+" + word;
                }
            }
        }
        return keyWords;
    }

    private static boolean isExcludeWord(String word) {
        return word.startsWith("-") ? true : false;
    }

    public static Set<String> getExcludeWordsSet(String wordsLine) {
        if (wordsLine == null || wordsLine.equals("")) {
            return null;
        }
        String[] words = wordsLine.split(" ");
        Set<String> excludeWords = new HashSet<>();
        for (String word : words) {
            if (isExcludeWord(word) && !word.replaceFirst("-", "").equals("")) {
                excludeWords.add(word.replaceFirst("-", "").toLowerCase());
            }
        }
        return excludeWords;
    }

    public static boolean isStringIncludeWords(String line, Set<String> words) {
        if (words == null || line == null || words.isEmpty() || line.equals("")) {
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

    public static int getDaysFromText(String daysText) {
        int days = 0;
        if (daysText != null && daysText.length() > 0) {
            days = Integer.parseInt(daysText);
        }
        return days;
    }
}
