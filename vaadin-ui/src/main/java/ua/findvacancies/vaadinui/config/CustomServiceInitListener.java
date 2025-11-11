package ua.findvacancies.vaadinui.config;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.UIInitEvent;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ua.findvacancies.vaadinui.model.AppSessionAttribute;

import java.util.Locale;

/**
 * A global listener that runs when the Vaadin service is initialized.
 * It registers a UIInitListener to apply the persisted locale from the session.
 */
@Component
@Slf4j
public class CustomServiceInitListener implements VaadinServiceInitListener {

    public static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    @Override
    public void serviceInit(final ServiceInitEvent event) {
        VaadinService service = event.getSource();

        service.addUIInitListener(this::uiLocaleChange);
    }

    private void uiLocaleChange(final UIInitEvent event) {
        UI ui = event.getUI();
        // This method runs after the UI is created but before any components are attached.
        Locale sessionLocale = (Locale) ui.getSession().getAttribute(AppSessionAttribute.CUSTOM_LOCALE.getLabel());

        if (sessionLocale == null) {
            event.getUI().setLocale(DEFAULT_LOCALE);
            log.info("UI initialized. Set default locale: {}", DEFAULT_LOCALE);
        } else if (!ui.getLocale().equals(sessionLocale)) {
            ui.setLocale(sessionLocale);
            log.info("UI initialized. Overriding default locale to: {}", sessionLocale);
        } else {
            log.info("UI initialized. Current locale: {}", sessionLocale);
        }
    }

}
