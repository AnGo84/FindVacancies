package ua.findvacancies.ui;

import com.vaadin.flow.component.*;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import ua.findvacancies.utils.ViewUtils;

import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

@Route("")
@PageTitle("Find Vacancies")
//
@CssImport("./styles/app_styles.css")
public class MainView extends AppLayout {
    //private transient ResourceBundle resourceBundle = ResourceBundle.getBundle("messages/locales/messages", UI.getCurrent().getLocale());
    private final transient ResourceBundle resourceBundle;

    public MainView() {
        resourceBundle = ResourceBundle.getBundle("messages/locales/messages", getLocale());

        HorizontalLayout navbarH = initNavBar();
        navbarH.setId("navbarHLayout");

        addToNavbar(navbarH);

        VerticalLayout content = new VerticalLayout();
        content.setId("contextVLayout");

        content.setPadding(false);
        content.setSpacing(false);
        content.setSizeFull();

        VacanciesView mainContent = new VacanciesView(resourceBundle);

        content.add(mainContent);
        content.add(getFooterWrapper());
        content.expand(mainContent);

        setContent(content);

    }

    private HorizontalLayout initNavBar() {
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

        H3 todayIsTextH3 = new H3(resourceBundle.getString("content.todayIsText") + " " + ViewUtils.getDateAsString(new Date(), resourceBundle.getLocale()));
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
        // Set the alignment
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

    public Locale getLocale() {
        final UI currentUI = UI.getCurrent();
        return currentUI == null ? Locale.getDefault() : currentUI.getLocale();
    }

    public MenuBar getLanguageMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setId("languageMenuBar");

        menuBar.setOpenOnHover(true);
        menuBar.addThemeVariants(MenuBarVariant.LUMO_END_ALIGNED, MenuBarVariant.LUMO_SMALL);

        MenuItem menuItemLanguage = createIconItem(menuBar, VaadinIcon.GLOBE, resourceBundle.getString("navMenu.Language"),
                null);

        //menuBar.getStyle().set("color", "var(--lumo-header-text-color)");

        SubMenu subMenuLanguage = menuItemLanguage.getSubMenu();
        MenuItem englishSubItem = subMenuLanguage.addItem(resourceBundle.getString("navMenu.Language.EN"));
        englishSubItem.addClickListener(onClickLanguageMenuEventListener(menuItemLanguage, englishSubItem));

        MenuItem ukraineSubItem = subMenuLanguage.addItem(resourceBundle.getString("navMenu.Language.UA"));
        ukraineSubItem.addClickListener(onClickLanguageMenuEventListener(menuItemLanguage, ukraineSubItem));

        menuItemLanguage.getSubMenu().getItems().forEach(subMenuItem -> subMenuItem.setCheckable(true));
        englishSubItem.setChecked(true);
        return menuBar;
    }

    // TODO remove to utils
    private static ComponentEventListener<ClickEvent<MenuItem>> onClickLanguageMenuEventListener(MenuItem allLanguagesMenuItem, MenuItem activeMenuItem) {
        return (ComponentEventListener<ClickEvent<MenuItem>>) event -> {setSubMenuItemCheckedValue(allLanguagesMenuItem, false); activeMenuItem.setChecked(true);};
    }
    private static void setSubMenuItemCheckedValue(MenuItem menuItemLanguage, boolean isChecked) {
        menuItemLanguage.getSubMenu().getItems().forEach(subMenuItem -> subMenuItem.setChecked(isChecked));
    }
    // /END_TODO
    private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName,
                                    String label, String ariaLabel) {
        return createIconItem(menu, iconName, label, ariaLabel, false);
    }

    private MenuItem createIconItem(HasMenuItems menu, VaadinIcon iconName,
                                    String label, String ariaLabel, boolean isChild) {
        Icon icon = new Icon(iconName);

        if (isChild) {
            icon.getStyle().set("width", "var(--lumo-icon-size-s)");
            icon.getStyle().set("height", "var(--lumo-icon-size-s)");
            icon.getStyle().set("marginRight", "var(--lumo-space-s)");
        }

        MenuItem item = menu.addItem(icon, e -> {
        });

        if (ariaLabel != null) {
            item.getElement().setAttribute("aria-label", ariaLabel);
        }

        if (label != null) {
            item.add(new Text(label));
        }

        return item;
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
        Label label = new Label(resourceBundle.getString("footer.copyRight") + ViewUtils.getCopyrightCurrentYear());

        FlexLayout footer = new FlexLayout(icon, label);
        footer.getStyle().set("padding", " 0.5em");

        // Set the alignment
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        return footer;
    }

}