package ua.findvacancies.ui;


import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.H6;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.LocalDateRenderer;
import com.vaadin.flow.data.renderer.LocalDateTimeRenderer;
import com.vaadin.flow.data.renderer.TextRenderer;
import ua.findvacancies.model.Provider;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.model.viewdata.ViewSearchParams;
import ua.findvacancies.service.VacancyService;
import ua.findvacancies.utils.AppDateUtils;
import ua.findvacancies.utils.ViewUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VacanciesView extends HorizontalLayout {

    /*
     * https://vaadin.com/examples-and-demos
     * https://github.com/vaadin/bookstore-example/tree/rtl-demo/src/main/java/org/vaadin/example/bookstore
     *
     * */
    private final transient ResourceBundle resourceBundle;

    private final VacancyService vacancyService = new VacancyService();

    private final ViewSearchParams viewSearchParams;
    private final Grid<Vacancy> vacanciesListGrid;
    // TEMP components
    private final TextField buttonResult = new TextField();
    /* Search params components*/
    private TextField searchLineTextField;
    private MultiSelectComboBox<String> searchSitesComboBox;
    private IntegerField searchDaysIntegerField;
    private Button searchButton;
    // Results GRID
    private List<Vacancy> vacancyList;
    private int counter = 0;

    public VacanciesView(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;

        viewSearchParams = vacancyService.getDefaultViewSearchParams();

        // Sets the width and the height of InventoryView to "100%".
        setSizeFull();

        vacanciesListGrid = initVacanciesListGrid();

        final VerticalLayout topLayout = createSearchParamsBar();

        /*final HorizontalLayout topLayout = createTopBar();
        grid = new ProductGrid();
        grid.setDataProvider(dataProvider);
        // Allows user to select a single row in the grid.
        grid.asSingleSelect().addValueChangeListener(
                event -> viewLogic.rowSelected(event.getValue()));
        form = new ProductForm(viewLogic);
        form.setCategories(DataService.get(UI.getCurrent().getLocale()).getAllCategories());
        final VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.add(topLayout);
        barAndGridLayout.add(grid);
        barAndGridLayout.setFlexGrow(1, grid);
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();
        barAndGridLayout.expand(grid);

        add(barAndGridLayout);
        add(form);

        viewLogic.init();*/
        final VerticalLayout barAndGridLayout = new VerticalLayout();
        barAndGridLayout.setFlexGrow(0, topLayout);
        barAndGridLayout.setSizeFull();

        barAndGridLayout.add(topLayout);

        // TODO
        barAndGridLayout.add(buttonResult);

        barAndGridLayout.add(vacanciesListGrid);

        add(barAndGridLayout);

    }

    private VerticalLayout createSearchParamsBar() {
        //Locale currentLocale = UI.getCurrent().getLocale();

        searchLineTextField = initSearchLineTextField();
        searchSitesComboBox = initSearchSitesComboBox();
        searchDaysIntegerField = initSearchDaysIntegerField();

        /*
        // Apply the filter to grid's data provider. TextField value is never
        filter.addValueChangeListener(
                event -> dataProvider.setFilter(event.getValue()));
        */
        searchButton = initSearchButton();


        // About layout
        final HorizontalLayout aboutLayout = new HorizontalLayout();
        aboutLayout.setSizeFull();
        H4 aboutTextH4 = new H4(resourceBundle.getString("content.aboutText"));
        aboutTextH4.getStyle().set("margin", "0");

        H6 todayIsTextH6 = new H6(resourceBundle.getString("content.todayIsText") + " " + ViewUtils.getDateAsString(new Date(), resourceBundle.getLocale()));
        todayIsTextH6.getStyle().set("margin", "0");

        aboutLayout.add(aboutTextH4);
        aboutLayout.add(todayIsTextH6);
        aboutLayout.setPadding(false);
        aboutLayout.setMargin(false);
        // !About layout

        //Search fields layout
        HorizontalLayout searchFieldsLayout = new HorizontalLayout();
        searchFieldsLayout.setWidthFull();
        searchFieldsLayout.setMargin(false);
        searchFieldsLayout.setPadding(false);

        searchFieldsLayout.add(searchLineTextField);
        searchFieldsLayout.add(searchSitesComboBox);
        searchFieldsLayout.add(searchDaysIntegerField);

        searchFieldsLayout.expand(searchFieldsLayout);
        // !Search fields layout


        final VerticalLayout topLayout = new VerticalLayout();
        topLayout.setWidthFull();
        //topLayout.setPadding(false);

        topLayout.add(aboutLayout);
        topLayout.add(searchFieldsLayout);
        //topLayout.add(new H4(resourceBundle.getString("content.explanationText")));
        topLayout.add(searchButton);

        //topLayout.setVerticalComponentAlignment(Alignment.START, filter);
        //topLayout.expand(filter);
        topLayout.expand(searchLineTextField);

        topLayout.setAlignItems(Alignment.CENTER);
        return topLayout;
    }

    private TextField initSearchLineTextField() {
        TextField searchLineTextField = new TextField();

        searchLineTextField.setValue(viewSearchParams.getSearchLine());

        searchLineTextField.setLabel(resourceBundle.getString("content.searchText.label"));
        searchLineTextField.setPlaceholder(resourceBundle.getString("content.searchText.placeholder"));

        //searchLineTextField.setPrefixComponent(new Icon("lumo", "search"));
        searchLineTextField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchLineTextField.setTooltipText(resourceBundle.getString("content.explanationText"));
        searchLineTextField.setWidthFull();
        return searchLineTextField;
    }

    private MultiSelectComboBox<String> initSearchSitesComboBox() {
        MultiSelectComboBox<String> searchSitesComboBox = new MultiSelectComboBox<>(
                resourceBundle.getString("content.selectSites"));

        List<String> siteNames = Stream.of(Provider.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        searchSitesComboBox.setItems(siteNames);
        searchSitesComboBox.select(viewSearchParams.getSites());
        //searchSitesComboBox.setItemLabelGenerator(Country::getName);
        searchSitesComboBox.setWidth("300px");
        return searchSitesComboBox;

    }

    private IntegerField initSearchDaysIntegerField() {
        //viewSearchParams.getDays();
        IntegerField searchDaysIntegerField = new IntegerField();
        searchDaysIntegerField.setValue(Integer.valueOf(viewSearchParams.getDays()));
        searchDaysIntegerField.setStepButtonsVisible(true);
        searchDaysIntegerField.setMin(0);
        searchDaysIntegerField.setMax(366);

        searchDaysIntegerField.setLabel(resourceBundle.getString("content.searchDays.label"));

        searchDaysIntegerField.setWidth("100px");
        return searchDaysIntegerField;

    }

    private Button initSearchButton() {
        Button searchButton = new Button(resourceBundle.getString("content.button.FiendVacancies"));
        // A shortcut to focus on the textField by pressing ctrl + W
        searchButton.addFocusShortcut(Key.KEY_W, KeyModifier.CONTROL);

        // Setting theme variant of new production button to LUMO_PRIMARY that
        // changes its background color to blue and its text color to white
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.setIcon(VaadinIcon.SEARCH.create());
        //newProduct.addClickListener(click -> viewLogic.newProduct());
        // A shortcut to click the new product button by pressing ALT + N
        searchButton.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);
        searchButton.addClickListener(clickEvent -> {
            // TODO
            counter += 1;
            buttonResult.setValue("Click: " + counter);

            // get params
            viewSearchParams.setSearchLine(searchLineTextField.getValue());
            viewSearchParams.setSites(searchSitesComboBox.getSelectedItems());
            int days = searchDaysIntegerField.getValue();
            viewSearchParams.setDays(String.valueOf(days));

            vacancyList = vacancyService.getVacancyList(viewSearchParams);
            System.out.println("viewSearchParams: " + viewSearchParams);
            vacanciesListGrid.setItems(vacancyList);
        });
        return searchButton;
    }

    private Grid initVacanciesListGrid() {
        // Create a grid bound to the list
        Grid<Vacancy> grid = new Grid<>();
        grid.setSizeFull();
        //grid.setItems(people);
        grid.addColumn(Vacancy::getTitle).setHeader(resourceBundle.getString("content.tableFieldTitle"));
        grid.addColumn(Vacancy::getCompanyName).setHeader(resourceBundle.getString("content.tableFieldCompany"));
        grid.addColumn(Vacancy::getSalary).setHeader(resourceBundle.getString("content.tableFieldSalary"));
        grid.addColumn(Vacancy::getCity).setHeader(resourceBundle.getString("content.tableFieldCity"));
        grid.addColumn(Vacancy::getSiteName).setHeader(resourceBundle.getString("content.tableFieldSite"));
        //grid.addColumn(Vacancy::getDate)
        grid.addColumn(
                        new TextRenderer<>(new ItemLabelGenerator<Vacancy>() {
                            @Override
                            public String apply(Vacancy gridItem) {
                                return AppDateUtils.formatToString(gridItem.getDate());
                            }
                        })
                )
                .setHeader(resourceBundle.getString("content.tableFieldDate"))
                .setId("columnDateId");
        grid.setItems(new ArrayList<>());
        return grid;
    }
}
