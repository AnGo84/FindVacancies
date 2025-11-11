package ua.findvacancies.common.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UIUtilsTest {

    @Test
    void testStandardFormatting() {
        LocalDate date = LocalDate.of(2025, 9, 17);
        String result = UIUtils.getDateInfo("Today is", date, Locale.US);
        assertEquals("Today is 17.09.2025", result);
    }

    @Test
    void testNullPrefix() {
        LocalDate date = LocalDate.of(2025, 9, 17);
        String result = UIUtils.getDateInfo(null, date, Locale.US);
        assertEquals(" 17.09.2025", result);
    }

    @Test
    void testNullLocaleUsesDefault() {
        LocalDate date = LocalDate.of(2025, 9, 17);
        String result = UIUtils.getDateInfo("Date:", date, null);
        assertTrue(result.startsWith("Date: 17.09.2025"));
    }

    @Test
    void testNullDateThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                UIUtils.getDateInfo("Today is", null, Locale.US));
    }

    @Test
    void testDifferentLocale() {
        LocalDate date = LocalDate.of(2025, 9, 17);
        String result = UIUtils.getDateInfo("Heute ist", date, Locale.GERMANY);
        assertEquals("Heute ist 17.09.2025", result);
    }

}