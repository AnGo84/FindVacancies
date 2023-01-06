package ua.findvacancies.ui;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.HasMenuItems;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.router.Route;

@Route("")
public class MainView extends AppLayout {

    public MainView() {
        VerticalLayout todosList = new VerticalLayout();

        H1 title = new H1("Find Vacancies");
        title.getStyle().set("font-size", "var(--lumo-font-size-l)");
                //.set("margin", "0");

        //addToNavbar(true,  title , getMainMenuBar());
        addToNavbar( title , getMainMenuBar());

        Div content = getMainContextTemp(todosList);

        setContent(content);

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


    public MenuBar getMainMenuBar(){
        MenuBar menuBar = new MenuBar();

        /*menuBar.getStyle().set("position", "absolute")
                .set("right", "10px").set("top", "0");*/
        menuBar.getStyle().set("float", "right");

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
}