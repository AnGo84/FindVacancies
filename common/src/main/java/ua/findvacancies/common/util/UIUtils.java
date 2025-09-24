package ua.findvacancies.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class UIUtils {

    private UIUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getDateInfo(String datePrefix, LocalDate date, Locale locale) {
        if (datePrefix == null) {
            datePrefix = "";
        }
        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", locale);
        return datePrefix + " " + date.format(formatter);
    }

}
