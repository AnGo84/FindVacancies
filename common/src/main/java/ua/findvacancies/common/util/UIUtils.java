package ua.findvacancies.common.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

public final class UIUtils {

    private UIUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getDateInfo(final String datePrefix, final LocalDate date, final Locale locale) {

        if (date == null) {
            throw new IllegalArgumentException("Date must not be null");
        }

        var prefix = Optional.ofNullable(datePrefix).orElse("");

        var currentLocale = Optional.ofNullable(locale).orElse(Locale.getDefault());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", currentLocale);
        return prefix + " " + date.format(formatter);
    }

}
