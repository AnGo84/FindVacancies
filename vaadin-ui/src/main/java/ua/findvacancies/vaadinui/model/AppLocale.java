package ua.findvacancies.vaadinui.model;

import lombok.Getter;

import java.util.Locale;

@Getter
public enum AppLocale {

    ENGLISH(Locale.ENGLISH),
    UKRAINIAN(Locale.of("uk"));

    private final Locale locale;

    AppLocale(final Locale locale) {
        this.locale = locale;
    }

}
