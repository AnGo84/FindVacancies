package ua.findvacancies.ui.localization;

import lombok.Getter;

import java.util.Locale;

@Getter
public enum AppLocaleENUM {

    /*
     * Use no-country versions, so that e.g. both en_US and en_GB work.
     */
    ENGLISH(new Locale("en"), "navMenu.language.EN"),
    UKRAINIAN(new Locale("uk"), "navMenu.language.UA");

    private final Locale locale;
    private final String i18Message;

    AppLocaleENUM(Locale locale, String i18Message) {
        this.locale = locale;
        this.i18Message = i18Message;
    }

}
