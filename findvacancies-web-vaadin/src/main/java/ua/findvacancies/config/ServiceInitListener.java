package ua.findvacancies.config;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.spring.annotation.SpringComponent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.ObjectUtils;
import ua.findvacancies.ui.localization.LocaleCookie;

import javax.servlet.http.Cookie;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

/**
 * Class allowing us to customize the startup process whenever the Application
 * starts and/or whenever a users navigates to the app.
 */
@SpringComponent
@RequiredArgsConstructor
@Slf4j
public class ServiceInitListener implements VaadinServiceInitListener {

    private final I18NProvider i18nProvider;

    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent) {
        serviceInitEvent.getSource().addUIInitListener(uiInitEvent -> {

            // Whenever a new user arrives, determine locale
            initLanguage(uiInitEvent.getUI());
        });
    }

    private void initLanguage(UI ui) {
        log.info("Locales: {}", i18nProvider.getProvidedLocales());
        Optional<Cookie> localeCookie = Optional.empty();

        Cookie[] cookies = VaadinService.getCurrentRequest().getCookies();
        if (cookies != null) {
            localeCookie = Arrays.stream(cookies).filter(cookie -> LocaleCookie.COOKIE_LOCALE_NAME.equals(cookie.getName())).findFirst();
        }

        Locale currentLocale;

        if (localeCookie.isPresent() && !ObjectUtils.isEmpty(localeCookie.get().getValue())) {
            // Cookie found, use that
            currentLocale = Locale.forLanguageTag(localeCookie.get().getValue());
        } else {
            // Try to use Vaadin's browser currentLocale detection
            currentLocale = VaadinService.getCurrentRequest().getLocale();
        }

        log.info("Get currentLocale: {}", currentLocale);

        final String currentLocaleLanguage = currentLocale.getLanguage();
        boolean currentLocaleIsAvailableInList = i18nProvider.getProvidedLocales().stream().anyMatch(locale -> currentLocaleLanguage.equals(locale.getLanguage()));
        // If the detection fails, default to the first language we support.
        if (currentLocaleLanguage.equals("") || !currentLocaleIsAvailableInList) {
            currentLocale = i18nProvider.getProvidedLocales().get(0);
        }
        log.info("Provided locale: {}", currentLocale);
        ui.setLocale(currentLocale);
    }

}
