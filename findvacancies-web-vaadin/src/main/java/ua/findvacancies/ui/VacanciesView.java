package ua.findvacancies.ui;


import com.vaadin.flow.component.ItemLabelGenerator;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.KeyModifier;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.dataview.GridListDataView;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.*;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializablePredicate;
import ua.findvacancies.model.Provider;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.model.viewdata.ViewSearchParams;
import ua.findvacancies.service.VacancyService;
import ua.findvacancies.utils.AppDateUtils;

import java.util.ArrayList;
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
    private GridListDataView<Vacancy> vacanciesDataView;
    private final TextField tableDataSearchTextField;
    // TEMP components
    private final TextField searchResultTextField = new TextField();
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

        final VerticalLayout searchParamsVLayout = createSearchParamsBar();
        searchParamsVLayout.setId("searchParamsVLayout");
        searchParamsVLayout.setPadding(false);

        searchParamsVLayout.setSpacing(false);

        final VerticalLayout barAndGridVLayout = new VerticalLayout();
        barAndGridVLayout.setId("barAndGridVLayout");
        barAndGridVLayout.setFlexGrow(0, searchParamsVLayout);
        barAndGridVLayout.setSizeFull();

        barAndGridVLayout.add(searchParamsVLayout);

        // TODO remove test field
        barAndGridVLayout.add(searchResultTextField);
        //
        tableDataSearchTextField = initTableDataSearchTextField();

        barAndGridVLayout.add(tableDataSearchTextField);

        barAndGridVLayout.add(vacanciesListGrid);

        add(barAndGridVLayout);

    }

    // Filtering https://vaadin.com/docs/v23/components/grid/#filtering
    private TextField initTableDataSearchTextField() {
        TextField searchField = new TextField();
        searchField.setWidth("50%");
        searchField.setPlaceholder(resourceBundle.getString("table.search"));
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        searchField.addValueChangeListener(e -> {
            vacanciesDataView.refreshAll();
            vacanciesDataView.setFilter(getVacancyFilterPredicate(searchField));
        });

        return searchField;
    }

    private SerializablePredicate<Vacancy> getVacancyFilterPredicate(TextField searchField) {
        return vacancy -> {
            String searchTerm = searchField.getValue().trim();


            System.out.println("Search for: " + searchTerm);

            if (searchTerm.isEmpty())
                return true;

            boolean matchesTitle = matchesTerm(vacancy.getTitle(), searchTerm);
            boolean matchesCompanyName = matchesTerm(vacancy.getCompanyName(), searchTerm);
            boolean matchesSiteName = matchesTerm(vacancy.getSiteName(), searchTerm);

            return matchesTitle || matchesCompanyName || matchesSiteName;
        };
    }

    private boolean matchesTerm(String value, String searchTerm) {
        return value.toLowerCase().contains(searchTerm.toLowerCase());
    }

    private VerticalLayout createSearchParamsBar() {

        searchLineTextField = initSearchLineTextField();
        searchSitesComboBox = initSearchSitesComboBox();
        searchDaysIntegerField = initSearchDaysIntegerField();

        searchButton = initSearchButton();

        // About layout
        /*final HorizontalLayout aboutLayout = new HorizontalLayout();
        aboutLayout.setSizeFull();
        H4 aboutTextH4 = new H4(resourceBundle.getString("content.aboutText"));
        aboutTextH4.getStyle().set("margin", "0");

        H6 todayIsTextH6 = new H6(resourceBundle.getString("content.todayIsText") + " " + ViewUtils.getDateAsString(new Date(), resourceBundle.getLocale()));
        todayIsTextH6.getStyle().set("margin", "0");

        aboutLayout.add(aboutTextH4);
        aboutLayout.add(todayIsTextH6);
        aboutLayout.setPadding(false);
        aboutLayout.setMargin(false);*/
        // !About layout

        //Search fields layout
        FormLayout searchParamsFormLayout = new FormLayout();
        searchParamsFormLayout.setId("searchParamsFormLayout");
        searchParamsFormLayout.add(searchLineTextField, searchSitesComboBox, searchDaysIntegerField);
        searchParamsFormLayout.setColspan(searchLineTextField, 3);
        searchParamsFormLayout.setColspan(searchSitesComboBox, 2);
        searchParamsFormLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 3),
                //new FormLayout.ResponsiveStep("320px", 2),
                new FormLayout.ResponsiveStep("700px", 6));
        searchParamsFormLayout.setSizeFull();
        // !Search fields layout

        final VerticalLayout topLayout = new VerticalLayout();
        topLayout.setWidthFull();

        topLayout.add(searchParamsFormLayout);

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
        searchLineTextField.getStyle().set("padding", "0");

        searchLineTextField.setValue(viewSearchParams.getSearchLine());

        searchLineTextField.setLabel(resourceBundle.getString("content.searchText.label"));
        searchLineTextField.setPlaceholder(resourceBundle.getString("content.searchText.placeholder"));

        searchLineTextField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchLineTextField.setTooltipText(resourceBundle.getString("content.explanationText"));
        searchLineTextField.setWidthFull();
        return searchLineTextField;
    }

    private MultiSelectComboBox<String> initSearchSitesComboBox() {
        MultiSelectComboBox<String> searchSitesComboBox = new MultiSelectComboBox<>(
                resourceBundle.getString("content.selectSites"));
        searchSitesComboBox.getStyle().set("padding", "0");

        List<String> siteNames = Stream.of(Provider.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        searchSitesComboBox.setItems(siteNames);
        searchSitesComboBox.select(viewSearchParams.getSites());
        //searchSitesComboBox.setWidth("300px");
        return searchSitesComboBox;

    }

    private IntegerField initSearchDaysIntegerField() {
        //viewSearchParams.getDays();
        IntegerField searchDaysIntegerField = new IntegerField();
        searchDaysIntegerField.getStyle().set("padding", "0");

        searchDaysIntegerField.setValue(Integer.valueOf(viewSearchParams.getDays()));
        searchDaysIntegerField.setStepButtonsVisible(true);
        searchDaysIntegerField.setMin(0);
        searchDaysIntegerField.setMax(366);

        searchDaysIntegerField.setLabel(resourceBundle.getString("content.searchDays.label"));

        //searchDaysIntegerField.setWidth("100px");
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

            // get params
            viewSearchParams.setSearchLine(searchLineTextField.getValue());
            viewSearchParams.setSites(searchSitesComboBox.getSelectedItems());
            int days = searchDaysIntegerField.getValue();
            viewSearchParams.setDays(String.valueOf(days));

            vacancyList = vacancyService.getVacancyList(viewSearchParams);
            System.out.println("viewSearchParams: " + viewSearchParams);

            // TODO remove temp field
            counter += 1;
            searchResultTextField.setValue("Click: " + counter + " List size: " + vacancyList.size());
            //

            vacanciesDataView = vacanciesListGrid.setItems(vacancyList);
        });
        return searchButton;
    }

    private Grid<Vacancy> initVacanciesListGrid() {
        // Create a grid bound to the list
        Grid<Vacancy> grid = new Grid<>();
        grid.setSizeFull();

        grid.addColumn(Vacancy::getTitle).setHeader(resourceBundle.getString("content.tableFieldTitle")).setSortable(true);
        grid.addColumn(Vacancy::getCompanyName).setHeader(resourceBundle.getString("content.tableFieldCompany")).setSortable(true);
        grid.addColumn(Vacancy::getSalary).setHeader(resourceBundle.getString("content.tableFieldSalary")).setSortable(true);
        grid.addColumn(Vacancy::getCity).setHeader(resourceBundle.getString("content.tableFieldCity")).setSortable(true);

        //grid.addColumn(Vacancy::getSiteName).setHeader(resourceBundle.getString("content.tableFieldSite")).setSortable(true);
        grid.addComponentColumn(vacancy -> new Anchor(vacancy.getUrl(), vacancy.getSiteName()))
                .setSortable(true)
                .setHeader(resourceBundle.getString("content.tableFieldSite"))
                .setId("columnURL");

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
                .setSortable(true)
                .setId("columnDateId");
        vacanciesDataView = grid.setItems(new ArrayList<>());

        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);

        return grid;
    }
}
