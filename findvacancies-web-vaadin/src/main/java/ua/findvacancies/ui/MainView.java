package ua.findvacancies.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.AppShellSettings;
import ua.findvacancies.utils.ViewUtils;

import java.util.Locale;
import java.util.ResourceBundle;

@Route("")
@PageTitle("Find Vacancies")
public class MainView extends AppLayout implements AppShellConfigurator{

    //private transient ResourceBundle resourceBundle = ResourceBundle.getBundle("messages/locales/messages", UI.getCurrent().getLocale());
    private transient ResourceBundle resourceBundle;

    public MainView() {
        resourceBundle = ResourceBundle.getBundle("messages/locales/messages", getLocale());

        H1 title = new H1("Find Vacancies" );
        title.getStyle().set("font-size", "var(--lumo-font-size-l)");
        //.set("margin", "0");
        //title.setWidth("400px");
        title.setWidth("90%");

        //Label titleLabel = new Label("Find Vacancies");

        MenuBar languageMenuBar = getLanguageMenuBar();

        //FlexLayout languageMenuBarLayout = new FlexLayout(languageMenuBar);
        VerticalLayout languageMenuBarLayout = new VerticalLayout(languageMenuBar);

        languageMenuBarLayout.setId("languageMenuBarLayout");
        //languageMenuBarLayout.getStyle().set("padding"," 0.5em");
        languageMenuBarLayout.getStyle().set("padding"," 0.5em");
        languageMenuBarLayout.setWidthFull();

        // Set the alignment
        //languageMenuBarLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.R);
        languageMenuBarLayout.setAlignItems(FlexComponent.Alignment.END);

        //HorizontalLayout navbar = new HorizontalLayout(titleLabel, languageMenuBarLayout);
        HorizontalLayout navbar = new HorizontalLayout(title, languageMenuBarLayout);
        /*navbar.setPadding(true);
        navbar.setSpacing(true);
        navbar.setMargin(true);*/

        navbar.setWidthFull();
        // /navbar

        //addToNavbar(true,  title , getMainMenuBar());
        addToNavbar(navbar);

        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSizeFull();

        /*VerticalLayout todosList = new VerticalLayout();
        Div mainContent = getMainContextTemp(todosList);

        mainContent.setId("mainContent");
        mainContent.getStyle().set("padding", " 1em");*/

        VacanciesView mainContent = new VacanciesView(resourceBundle);

        //footerWrapper.setComponentAlignment(myComponent, Alignment.MIDDLE_CENTER)

        content.add(mainContent);
        content.add(getFooterWrapper());
        content.expand(mainContent);

        setContent(content);

    }

    @Override
    public void configurePage(AppShellSettings settings) {
        AppShellConfigurator.super.configurePage(settings);
        settings.addFavIcon("icon", "icons/icon.png", "192x192");
        settings.addLink("shortcut icon", "icons/logo_16.ico");
    }

    //public static Locale getLocale() {
    public Locale getLocale() {
        final UI currentUI = UI.getCurrent();
        return currentUI == null ? Locale.getDefault() : currentUI.getLocale();
    }

    private Div getMainContextTemp(VerticalLayout todosList) {
        TextField taskField = new TextField();

        Button addButton = new Button("Add");
        addButton.addClickListener(click -> {
            Checkbox checkbox = new Checkbox(taskField.getValue());
            todosList.add(checkbox);
        });
        addButton.addClickShortcut(Key.ENTER);

        Div content = new Div();
        content.add(
                new H1("Vaadin Todo"),
                todosList,
                new HorizontalLayout(
                        taskField,
                        addButton
                )
        );
        return content;
    }


    public MenuBar getLanguageMenuBar() {
        MenuBar menuBar = new MenuBar();

        /*menuBar.getStyle().set("position", "absolute")
                .set("right", "10px").set("top", "0");*/
        //menuBar.getStyle().set("float", "right");

        menuBar.setOpenOnHover(true);
        menuBar.addThemeVariants(MenuBarVariant.LUMO_END_ALIGNED, MenuBarVariant.LUMO_SMALL);

        //menuWithSmallTheme.addThemeVariants(MenuBarVariant.LUMO_SMALL);

        MenuItem menuItemLanguage = createIconItem(menuBar, VaadinIcon.GLOBE, "Language",
                null);

        SubMenu subMenuLanguage = menuItemLanguage.getSubMenu();
        MenuItem englishSubItem = subMenuLanguage.addItem("English");
        englishSubItem.setCheckable(true);
        englishSubItem.setChecked(true);
        MenuItem ukraineSubItem = subMenuLanguage.addItem("Ukraine");
        ukraineSubItem.setCheckable(true);
        ukraineSubItem.setChecked(false);

        return menuBar;
    }

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

    // TODO refactoring

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

        //Icon icon = UIUtils.createIcon(LumoUtility.IconSize.SMALL, LumoUtility.TextColor.SUCCESS, VaadinIcon.CHECK);
        Icon icon = new Icon(VaadinIcon.COPYRIGHT);

        //Label label = UIUtils.createLabel(LumoUtility.FontSize.XSMALL, LumoUtility.TextColor.BODY, "Online");
        //Label label = new Label(ViewUtils.getCopyrightInfo());
        Label label = new Label(resourceBundle.getString("footer.copyRight") + ViewUtils.getCopyrightCurrentYear());


        FlexLayout footer = new FlexLayout(icon, label);
        footer.getStyle().set("padding", " 0.5em");

        // Set the alignment
        //footer.setAlignItems(FlexComponent.Alignment.CENTER);
        footer.setWidthFull();
        footer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);

        return footer;
    }
}