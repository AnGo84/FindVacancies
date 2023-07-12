package ua.findvacancies.ui.localization;

import com.vaadin.flow.server.VaadinRequest;
import com.vaadin.flow.server.VaadinService;
import jakarta.servlet.http.Cookie;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

@Slf4j
public class LocaleCookie {

    public static final String COOKIE_LOCALE_NAME = "fv_locale";

    /**
     * Util method that goes through the users cookies and finds a 'fv_locale' cookie,
     * if any.
     */
    public static String findLocaleFromCookie() {
        final Cookie[] cookies = VaadinRequest.getCurrent().getCookies();
        if (cookies == null) {
            return "";
        }
        final Optional<String> cookie = Arrays.stream(cookies).filter(c -> COOKIE_LOCALE_NAME.equals(c.getName())).map(Cookie::getValue).findAny();
        return cookie.orElse("");
    }

    /**
     * Clears the users locale preference by deleting the 'locale' cookie.
     */
    public static void clearLocalePreference() {
        VaadinService.getCurrentResponse().addCookie(new Cookie(COOKIE_LOCALE_NAME, null));
    }

    /**
     * Stores the users locale preference by creating a 'locale' cookie. This cookie
     * will be read on app initialization by the class
     * {@link ConfigureUIServiceInitListener}
     */
    public static void saveLocalePreference(Locale locale) {
        log.info("Cookie save Locale: {}", locale);
        VaadinService.getCurrentResponse().addCookie(new Cookie(COOKIE_LOCALE_NAME, locale.toLanguageTag()));
    }

}
