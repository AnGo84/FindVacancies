package ua.findvacancies.vaadinui.component;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.VaadinSession;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import ua.findvacancies.common.util.UIUtils;
import ua.findvacancies.vaadinui.model.AppLocale;
import ua.findvacancies.vaadinui.model.AppSessionAttribute;

import java.io.Serial;
import java.time.LocalDate;
import java.util.Locale;

@SpringComponent
@UIScope
@Slf4j
public class TopNavbarComponent extends HorizontalLayout {

    @Serial
    private static final long serialVersionUID = 1L;

    private final H3 dateDisplay = new H3();

    public TopNavbarComponent() {

        setWidthFull();
        setPadding(true);
        setJustifyContentMode(JustifyContentMode.BETWEEN);
        setAlignItems(Alignment.CENTER);

        buildLogo();
        buildDateDisplay();
        buildLanguageSwitcher();

        // Initial update
        //updateContent();
    }

    private void buildLogo() {
        // Mimics: <a><i class="bi bi-code-slash"></i><span>#{app.title}</span></a>
        Anchor logo = new Anchor("/", getTranslation("app.title"));
        logo.getStyle().set("font-size", "1.5rem");
        logo.getStyle().set("text-decoration", "none");

        HorizontalLayout logoLayout = new HorizontalLayout(
                VaadinIcon.CODE.create(), logo
        );
        logoLayout.setAlignItems(Alignment.CENTER);
        add(logoLayout);
    }

    private void buildDateDisplay() {
        // Mimics: <div><span th:text="${currentDate}"></span></div>
        dateDisplay.addClassName("text-secondary");
        dateDisplay.getStyle().set("margin", "0");
        add(dateDisplay);
    }

    private void buildLanguageSwitcher() {
        // Use Vaadin MenuBar to create a dynamic dropdown (mimicking Bootstrap dropdown)
        MenuBar menuBar = new MenuBar();
        menuBar.setThemeName("tertiary");

        MenuItem langItem = menuBar.addItem(createLanguageToggleLabel());
        SubMenu subMenu = langItem.getSubMenu();
        Locale currentLocale = UI.getCurrent().getLocale();
        log.info("Current Locale: {}", currentLocale);
        log.info("Current Locale Language: {}", currentLocale.getLanguage());
        log.info("Current Language: {}", currentLocale.getDisplayLanguage());
        for (AppLocale appLocale : AppLocale.values()) {
            var locale = appLocale.getLocale();
            MenuItem localeItem = subMenu.addItem(
                    // Use I18nHelper to display the language name
                    locale.getDisplayLanguage(currentLocale), e -> switchLanguage(locale)
            );
            localeItem.setCheckable(true);
            if (currentLocale.getLanguage().equals(appLocale.getLocale().getLanguage())) {
                localeItem.setChecked(true);
            }

            // Optional: Add flag icons if you have custom CSS/icons set up
            /*if (locale.getLanguage().equals("en")) {
                localeItem.getElement().setProperty("innerHTML", "<span class='fi fi-gb me-1'></span> English");
            } else if (locale.getLanguage().equals("uk")) {
                localeItem.getElement().setProperty("innerHTML", "<span class='fi fi-ua me-1'></span> Українська");
            }*/
        }
        add(menuBar);
    }

    /**
     * Creates the label for the language toggle button, including the current locale.
     */
    private Component createLanguageToggleLabel() {
        Locale currentLocale = UI.getCurrent().getLocale();

        // This button acts as the dropdown toggle
        Button toggleButton = new Button();
        toggleButton.addThemeName("small");
        toggleButton.setIcon(VaadinIcon.GLOBE.create());
        toggleButton.setText(currentLocale.getDisplayLanguage(currentLocale)); // Shows the name of the current language

        // NOTE: Vaadin MenuBar handles the dropdown toggle, so we only need the content.
        HorizontalLayout layout = new HorizontalLayout(VaadinIcon.GLOBE.create(), new Div(toggleButton.getText()));
        layout.setSpacing(true);
        layout.setAlignItems(Alignment.CENTER);
        return layout;
    }

    /**
     * Action to switch the UI language.
     */
    private void switchLanguage(final Locale newLocale) {
        log.info("Switch to {} from {}", newLocale, UI.getCurrent().getLocale());

        // 1. Store the chosen locale in the VaadinSession.
        // The CustomServiceInitListener will pick this up on the new request.
        VaadinSession.getCurrent().setAttribute(AppSessionAttribute.CUSTOM_LOCALE.getLabel(), newLocale);

        // 2. Set on the current UI instance (optional, but good practice)
        //UI.getCurrent().setLocale(newLocale);

        log.info("Current Locale set to: {}", UI.getCurrent().getLocale());

        // 2. Force a full page reload.
        // The browser makes a new request, which triggers the UIInitListener.
        UI.getCurrent().getPage().reload();

        // NOTE: Vaadin's best practice is to use attach listeners for dynamic translation
        // updates, but reloading is the simplest way to ensure all components
        // using MessageSource/I18nHelper are fully reinitialized with the new locale.
    }

    /**
     * Updates the date display based on the current UI locale.
     */
    public void updateContent() {
        log.info("Update content for locale: {}", getLocale());
        var currentDateText = UIUtils.getDateInfo(getTranslation("today.is"), LocalDate.now(), getLocale());

        // Update the date display
        dateDisplay.setText(currentDateText);
    }

    // Vaadin Flow best practice: update on attach (useful if component is added dynamically)
    @Override
    protected void onAttach(final AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        log.info("Call navBar onAttach. Is initial: {}", attachEvent.isInitialAttach());
        updateContent();
    }

}
