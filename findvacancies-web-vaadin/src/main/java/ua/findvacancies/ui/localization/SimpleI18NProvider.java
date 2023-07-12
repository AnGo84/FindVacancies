package ua.findvacancies.ui.localization;

import com.vaadin.flow.i18n.I18NProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.*;

/**
 * https://github.com/vaadin/vaadin-localization-example/tree/v23
 * Simple implementation of {@link I18NProvider}.
 * <p>
 * Actual translations can be found in the 'labelsbundle'_{lang_code}.properties
 * files.
 * <p>
 * Singleton scope.
 */
@Component
@Slf4j
public class SimpleI18NProvider implements I18NProvider {
    public static final String RESOURCE_BUNDLE_FILES = "messages/locales/messages";

    private final List<Locale> locales = Arrays.stream(AppLocaleENUM.values()).map(AppLocaleENUM::getLocale).toList();
    private Map<String, ResourceBundle> resourceBundleMap;

    @PostConstruct
    private void initMap() {
        resourceBundleMap = new HashMap<>();

        // Read translations file for each locale
        for (final Locale locale : getProvidedLocales()) {
            final ResourceBundle resourceBundle = ResourceBundle.getBundle(RESOURCE_BUNDLE_FILES, locale);
            resourceBundleMap.put(locale.getLanguage(), resourceBundle);
        }
    }

    @Override
    public List<Locale> getProvidedLocales() {

        return locales;
    }

    @Override
    public String getTranslation(String key, Locale locale, Object... params) {
        log.debug("Translation for key '{}' with locale: {}", key, locale.getLanguage());
        String rawstring = null;

        try {
            rawstring = resourceBundleMap.get(locale.getLanguage()).getString(key);

            return MessageFormat.format(rawstring, params);

        } catch (final MissingResourceException e) {
            // Translation not found, return error message instead of null as per API
            log.warn("No translation found for key {}", key);
            return String.format("!{%s}", key);
        } catch (final IllegalArgumentException e) {
            // for devs to find where this happened
            log.warn("No translation found for key '{}'", key, e);
            // Incorrect parameters
            return rawstring;
        }

    }

}
