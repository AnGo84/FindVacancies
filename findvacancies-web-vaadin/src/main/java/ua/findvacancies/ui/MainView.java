package ua.findvacancies.ui;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.i18n.I18NProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ua.findvacancies.ui.component.MenuItemComponent;
import ua.findvacancies.ui.component.NotificationComponent;
import ua.findvacancies.ui.localization.AppLocaleENUM;
import ua.findvacancies.ui.localization.LocaleCookie;
import ua.findvacancies.utils.ViewUtils;

import java.util.Date;
import java.util.Locale;

@Route("")
@PageTitle("Find Vacancies")
@CssImport("./styles/app_styles.css")
@Slf4j
public class MainView extends AppLayout {

    public MainView(@Autowired I18NProvider i18NProvider) {

        log.info("Current locale is: {}", UI.getCurrent().getLocale() );

        //cookieLang = LocaleCookie.findLocaleFromCookie();

        HorizontalLayout navbarH = initNavBar(i18NProvider);
        navbarH.setId("navbarHLayout");

        addToNavbar(navbarH);

        VerticalLayout content = new VerticalLayout();
        content.setId("contextVLayout");

        content.setPadding(false);
        content.setSpacing(false);
        content.setSizeFull();

        VacanciesView mainContent = new VacanciesView();

        content.add(mainContent);
        content.add(getFooterWrapper());
        content.expand(mainContent);

        setContent(content);

    }

    private HorizontalLayout initNavBar(I18NProvider i18NProvider) {
        // Title
        H1 titleH1 = new H1("Find Vacancies");
        titleH1.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", " 0");
        //titleH1.setWidth("400px");
        titleH1.setWidth("100%");

        VerticalLayout titleVLayout = new VerticalLayout(titleH1);
        titleVLayout.setId("titleVLayout");
        titleVLayout.getStyle().set("padding", " 0.5em");
        titleVLayout.setMargin(false);
        // /Title

        // Today is
        H3 todayIsTextH3 = new H3(getTranslation("content.todayIsText") + " " + ViewUtils.getDateAsString(new Date(), getLocale()));

        todayIsTextH3.getStyle().set("margin", "0");
        VerticalLayout todayIsVLayout = new VerticalLayout(todayIsTextH3);
        todayIsVLayout.setId("todayIsVLayout");
        todayIsVLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        todayIsVLayout.getStyle().set("padding", " 0.5em");
        todayIsVLayout.setMargin(false);
        // /Today is

        // language
        MenuBar languageMenuBar = getLanguageMenuBar();
        VerticalLayout languageMenuBarVLayout = new VerticalLayout(languageMenuBar);

        languageMenuBarVLayout.setId("languageMenuBarVLayout");
        languageMenuBarVLayout.getStyle().set("padding", " 0.5em");
        languageMenuBarVLayout.setWidthFull();
        languageMenuBarVLayout.setAlignItems(FlexComponent.Alignment.END);
        // /language

        HorizontalLayout navbarHLayout = new HorizontalLayout(titleVLayout, todayIsVLayout, languageMenuBarVLayout);
        navbarHLayout.setId("navbarHLayout");

        navbarHLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        navbarHLayout.setPadding(false);
        navbarHLayout.setMargin(false);
        //navbarHLayout.setSpacing(true);

        navbarHLayout.setWidthFull();
        // /navbarHLayout
        return navbarHLayout;
    }

    /*public Locale getLocale() {
        final UI currentUI = UI.getCurrent();
        return currentUI == null ? Locale.getDefault() : currentUI.getLocale();
    }*/

    // component for selecting another language
    public MenuBar getLanguageMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setId("languageMenuBar");

        menuBar.setOpenOnHover(true);
        menuBar.addThemeVariants(MenuBarVariant.LUMO_END_ALIGNED, MenuBarVariant.LUMO_SMALL);

        MenuItem menuItemLanguage = MenuItemComponent.createIconItem(menuBar, VaadinIcon.GLOBE, getTranslation("navMenu.language"),
                null);

        SubMenu subMenuLanguage = menuItemLanguage.getSubMenu();

        String currentLanguage = UI.getCurrent().getLocale().getLanguage();

        for (AppLocaleENUM value : AppLocaleENUM.values()) {
            MenuItem subMenuItem = subMenuLanguage.addItem(getTranslation(value.getI18Message()), onClickLanguageMenuEventListener(menuItemLanguage, value.getLocale()));
            subMenuItem.setCheckable(true);
            if (currentLanguage.equals(value.getLocale().getLanguage())){
                subMenuItem.setChecked(true);
            }
        }
        return menuBar;
    }

    private ComponentEventListener<ClickEvent<MenuItem>> onClickLanguageMenuEventListener(MenuItem allLanguagesMenuItem, Locale locale) {

        return event -> {
            saveLocalePreference(locale);
            setSubMenuItemCheckedValue(allLanguagesMenuItem, false);
            event.getSource().setChecked(true);
        };
    }

    private void saveLocalePreference(Locale locale) {
        log.info("Set and save to cookie new locale: {}", locale);

        UI.getCurrent().setLocale(locale);
        UI.getCurrent().getPage().reload();

        LocaleCookie.saveLocalePreference(locale);
        NotificationComponent.basic(getTranslation("view.locale.action.saved"), Notification.Position.TOP_END);
    }

    private static void setSubMenuItemCheckedValue(MenuItem menuItemLanguage, boolean isChecked) {
        menuItemLanguage.getSubMenu().getItems().forEach(subMenuItem -> subMenuItem.setChecked(isChecked));
    }

    // TODO refactoring footer
    private Component getFooterWrapper() {
        FlexLayout footerWrapper = new FlexLayout();
        footerWrapper.setId("footerWrapper");
        // Align the footer to the end of the wrapper
        footerWrapper.setAlignItems(FlexComponent.Alignment.END);
        // Make the footer always last in the parent using FlexBox order
        footerWrapper.getElement().getStyle().set("order", "999");
        Component outerFooter = getOuterFooter();
        footerWrapper.add(outerFooter);
        footerWrapper.setWidthFull();
        footerWrapper.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        footerWrapper.getStyle().set("background-color", "#E8EBEF");
        return footerWrapper;
    }

    private Component getOuterFooter() {

        Icon icon = new Icon(VaadinIcon.COPYRIGHT);
        Text label = new Text(getTranslation("footer.copyRight") + ViewUtils.getCopyrightCurrentYear());

        FlexLayout footer = new FlexLayout(icon, label);
        footer.getStyle().set("padding", " 0.5em");

        // Set the alignment
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        return footer;
    }

}
