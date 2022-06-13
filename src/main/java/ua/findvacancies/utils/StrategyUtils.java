package ua.findvacancies.utils;


import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

public class StrategyUtils {

    public static final String WORD_SEPARATOR = " ";
    public static final String EXCLUDE_WORD_PREFIX = "-";

    public static Set<String> getKeyWordsSet(String wordsLine) {
        if (StringUtils.isBlank(wordsLine)) {
            return new HashSet<>();
        }
        String[] words = wordsLine.split(WORD_SEPARATOR);
        Set<String> keyWords = new HashSet<>();
        for (String word : words) {
            if (!isExcludeWord(word) && !word.trim().equals("")) {
                keyWords.add(word.trim().toLowerCase());
            }
        }
        return keyWords;
    }

    private static boolean isExcludeWord(String word) {
        return word.startsWith(EXCLUDE_WORD_PREFIX);
    }

    public static Set<String> getExcludeWordsSet(String wordsLine) {
        if (StringUtils.isBlank(wordsLine)) {
            return new HashSet<>();
        }
        String[] words = wordsLine.split(WORD_SEPARATOR);
        Set<String> excludeWords = new HashSet<>();
        for (String word : words) {
            if (isExcludeWord(word) && !word.replaceFirst(EXCLUDE_WORD_PREFIX, "").equals("")) {
                excludeWords.add(word.replaceFirst(EXCLUDE_WORD_PREFIX, "").toLowerCase());
            }
        }
        return excludeWords;
    }

}
