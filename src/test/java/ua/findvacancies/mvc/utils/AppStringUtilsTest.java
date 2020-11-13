package ua.findvacancies.mvc.utils;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AppStringUtilsTest {

    @Test
    public void isStringIncludeWords() {
        final String line = "Java developer";
        Set<String> set = new HashSet();
        set.add("java");
        assertTrue(AppStringUtils.isStringIncludeWords(line, set));

        set.clear();
        set.add("senior");
        assertFalse(AppStringUtils.isStringIncludeWords(line, set));

        assertFalse(AppStringUtils.isStringIncludeWords(null, set));
        assertFalse(AppStringUtils.isStringIncludeWords(null, null));
        assertFalse(AppStringUtils.isStringIncludeWords(line, null));
        assertFalse(AppStringUtils.isStringIncludeWords("", new HashSet<>()));
        set.clear();
        assertFalse(AppStringUtils.isStringIncludeWords("", set));
    }

    @Test
    public void getDaysFromText() {
        assertEquals(2, AppStringUtils.getNumberFromText("2"));
        assertEquals(0, AppStringUtils.getNumberFromText(null));
        assertEquals(0, AppStringUtils.getNumberFromText(""));
        assertEquals(0, AppStringUtils.getNumberFromText("notNumber"));
    }
}