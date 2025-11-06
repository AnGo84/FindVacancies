package ua.findvacancies.vaadinui.util;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Utility component for fetching internationalized messages (translations).
 * It simplifies MessageSource usage across Vaadin components.
 */
@Component
public class I18nHelper {

    private final MessageSource messageSource;

    // Vaadin's main locale is typically managed by the UI context.
    // For simplicity, we'll fetch the default Locale here.
    // In a production Vaadin app, the current UI locale should be passed as an argument.

    public I18nHelper(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Gets the translation for a given key using the specified locale.
     *
     * @param key The message key.
     * @param locale The locale to use for translation.
     * @param args Optional arguments to format the message.
     * @return The translated message or a fallback string if the key is not found.
     */
    public String getTranslation(String key, Locale locale, Object... args) {
        try {
            // Null array for args is passed if no args are provided.
            return messageSource.getMessage(key, args, locale);
        } catch (Exception e) {
            // Fallback: display the key itself, bracketed for easy identification
            return "!!" + key + "!!";
        }
    }

    /**
     * A convenience method to get the translation using the default locale (or a hardcoded one).
     * NOTE: This should be improved to fetch the actual current Vaadin UI locale.
     */
    public String getTranslation(String key, Locale locale ) {
        // Placeholder: use a hardcoded or default locale for now.
        // We'll address dynamic locale in the MainView/TopBar component later.
        return getTranslation(key, locale, new Object[0]);
    }

}
