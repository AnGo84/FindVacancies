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
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.provider.AbstractListDataView;
import com.vaadin.flow.data.renderer.TextRenderer;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.function.SerializablePredicate;
import com.vaadin.flow.server.StreamResource;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.CollectionUtils;
import ua.findvacancies.export.ExportToExcel;
import ua.findvacancies.model.Provider;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.model.viewdata.ViewSearchParams;
import ua.findvacancies.service.VacancyService;
import ua.findvacancies.ui.component.NotificationComponent;
import ua.findvacancies.utils.AppDateUtils;
import ua.findvacancies.utils.MatchesUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class VacanciesView extends HorizontalLayout {

    /*
     * https://vaadin.com/examples-and-demos
     * https://github.com/vaadin/bookstore-example/tree/rtl-demo/src/main/java/org/vaadin/example/bookstore
     *
     * */

    private final VacancyService vacancyService = new VacancyService();

    private final ViewSearchParams viewSearchParams;
    private final Grid<Vacancy> vacanciesListGrid;
    private final TextField tableDataSearchTextField;
    private GridListDataView<Vacancy> vacanciesDataView;
    /* Search params components*/
    private TextField searchLineTextField;
    private MultiSelectComboBox<String> searchSitesComboBox;
    private IntegerField searchDaysIntegerField;
    private Button searchButton;
    // Results GRID
    private List<Vacancy> vacancyList;

    private final Binder<ViewSearchParams> searchParamsBinder = new Binder<>();

    public VacanciesView() {

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

        tableDataSearchTextField = initTableDataSearchTextField();
        FormLayout gridActionFormLayout = initGridActionFormLayout();

        barAndGridVLayout.add(gridActionFormLayout);

        barAndGridVLayout.add(vacanciesListGrid);

        add(barAndGridVLayout);

    }

    private FormLayout initGridActionFormLayout() {
        VerticalLayout exportVLayout = new VerticalLayout();
        exportVLayout.setPadding(false);
        exportVLayout.setSpacing(false);
        exportVLayout.setAlignItems(Alignment.END);
        exportVLayout.add(anchorExportToExcel());

        FormLayout gridActionFormLayout = new FormLayout();
        gridActionFormLayout.add(tableDataSearchTextField, exportVLayout);
        gridActionFormLayout.setResponsiveSteps(
                // Use one column by default
                new FormLayout.ResponsiveStep("0", 1),
                // Use two columns, if layout's width exceeds 500px
                new FormLayout.ResponsiveStep("700px", 2));
        return gridActionFormLayout;
    }

    // Filtering https://vaadin.com/docs/v23/components/grid/#filtering
    private TextField initTableDataSearchTextField() {
        TextField searchField = new TextField();
        searchField.setWidthFull();
        //searchField.setWidth("50%");
        searchField.setPlaceholder(getTranslation("table.search"));
        searchField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        searchField.setValueChangeMode(ValueChangeMode.EAGER);

        searchField.addValueChangeListener(e -> {
            vacanciesDataView.refreshAll();
            AbstractListDataView<Vacancy> temp = vacanciesDataView.setFilter(getVacancyFilterPredicate(searchField));
            log.debug("filtered data: {} ", temp.getItemCount());
        });

        return searchField;
    }

    private Anchor anchorExportToExcel()  {

        StreamResource resource = new StreamResource("export.xlsx",
                () -> {
                    log.info("Start creating Excel file");

                    List<Vacancy> vacancies = vacanciesDataView.getItems().toList();
                    log.info("Try to export list of {}", vacancies.size());

                    Workbook workbook = new XSSFWorkbook();
                    ExportToExcel.createSheet(workbook, vacancies);
                    try {
                        File file = ExportToExcel.createExcelFile(workbook, "tempExport");
                        log.info("Finish creating Excel file");
                        return new FileInputStream(file);
                    } catch (IOException e) {
                        log.error("Error on creating Excel file: {}", e.getLocalizedMessage(), e);
                        //throw new RuntimeException(e);
                        return null;
                    }

                });

        Anchor download = new Anchor(resource, "Download excel file");
        download.getElement().setAttribute("download", true);
        download.removeAll();
        Button button = new Button(getTranslation("content.button.export_to_excel"), new Icon(VaadinIcon.DOWNLOAD_ALT));
        //button.addThemeVariants(ButtonVariant.LUMO_SMALL);
        button.setWidth("150px");
        download.add(button);

        return download;
    }


    private SerializablePredicate<Vacancy> getVacancyFilterPredicate(TextField searchField) {
        log.info("Search for: {}", searchField.getValue());
        return vacancy -> {
            String searchTerm = searchField.getValue().trim();


            if (searchTerm.isEmpty()) {
                return true;
            }

            return MatchesUtils.matches(vacancy, searchTerm);
        };
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

        searchLineTextField.setLabel(getTranslation("content.searchText.label"));
        searchLineTextField.setPlaceholder(getTranslation("content.searchText.placeholder"));

        searchLineTextField.setPrefixComponent(VaadinIcon.SEARCH.create());
        searchLineTextField.setTooltipText(getTranslation("content.explanationText"));
        searchLineTextField.setWidthFull();

        searchParamsBinder.forField(searchLineTextField)
                .asRequired(getTranslation("validation.viewSearchParams.search_line.blank"))
                .bind(ViewSearchParams::getSearchLine, ViewSearchParams::setSearchLine);

        return searchLineTextField;
    }

    private MultiSelectComboBox<String> initSearchSitesComboBox() {
        // Binding example: https://github.com/vaadin-component-factory/multi-combo-box-flow
        MultiSelectComboBox<String> searchSitesComboBox = new MultiSelectComboBox<>(
                getTranslation("content.selectSites"));
        searchSitesComboBox.getStyle().set("padding", "0");

        List<String> siteNames = Stream.of(Provider.values())
                .map(Enum::name)
                .collect(Collectors.toList());

        searchSitesComboBox.setItems(siteNames);

        searchParamsBinder.forField(searchSitesComboBox)
                .withValidator(val -> !CollectionUtils.isEmpty(val), getTranslation("validation.viewSearchParams.sites.blank"))
                .bind(ViewSearchParams::getSites,ViewSearchParams::setSites);

        //searchSitesComboBox.setWidth("300px");
        return searchSitesComboBox;

    }

    private IntegerField initSearchDaysIntegerField() {
        //viewSearchParams.getDays();
        IntegerField searchDaysIntegerField = new IntegerField();
        searchDaysIntegerField.getStyle().set("padding", "0");

        searchDaysIntegerField.setStepButtonsVisible(true);
        searchDaysIntegerField.setMin(0);
        searchDaysIntegerField.setMax(366);

        searchDaysIntegerField.setLabel(getTranslation("content.searchDays.label"));

        searchParamsBinder.forField(searchDaysIntegerField)
                .asRequired(getTranslation("validation.viewSearchParams.days.blank"))
                .withConverter(
                        String::valueOf,
                        Integer::valueOf,
                        // Text to use instead of the NumberFormatException message
                        getTranslation("validation.viewSearchParams.days.min_value"))
                .bind(ViewSearchParams::getDays, ViewSearchParams::setDays);

        //searchDaysIntegerField.setWidth("100px");
        return searchDaysIntegerField;

    }

    private Button initSearchButton() {
        searchParamsBinder.readBean(viewSearchParams);

        Button searchButton = new Button(getTranslation("content.button.FiendVacancies"));
        // A shortcut to focus on the textField by pressing ctrl + W
        searchButton.addFocusShortcut(Key.KEY_W, KeyModifier.CONTROL);

        // Setting theme variant of new production button to LUMO_PRIMARY that
        // changes its background color to blue and its text color to white
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        searchButton.setIcon(VaadinIcon.SEARCH.create());
        // A shortcut to click the new product button by pressing ALT + N
        searchButton.addClickShortcut(Key.KEY_S, KeyModifier.CONTROL);

        searchButton.addClickListener(clickEvent -> {
            try {
                searchParamsBinder.writeBean(viewSearchParams);

                log.debug("Binding viewSearchParams: {}", viewSearchParams);

                vacancyList = vacancyService.getVacancyList(viewSearchParams);
                vacanciesDataView = vacanciesListGrid.setItems(vacancyList);
            } catch (ValidationException e) {
                log.error("Can't binding field: {}", e.getLocalizedMessage(), e);
                NotificationComponent.error(getTranslation("validation.viewSearchParams.binding"));
            }

        });

        return searchButton;
    }

    private Grid<Vacancy> initVacanciesListGrid() {
        // Create a grid bound to the list
        Grid<Vacancy> grid = new Grid<>();
        grid.setSizeFull();
        grid.addColumn(Vacancy::getTitle).setHeader(getTranslation("content.tableFieldTitle")).setSortable(true);
        grid.addColumn(Vacancy::getCompanyName).setHeader(getTranslation("content.tableFieldCompany")).setSortable(true);
        grid.addColumn(Vacancy::getSalary).setHeader(getTranslation("content.tableFieldSalary")).setSortable(true);
        grid.addColumn(Vacancy::getCity).setHeader(getTranslation("content.tableFieldCity")).setSortable(true);

        //grid.addColumn(Vacancy::getSiteName).setHeader(resourceBundle.getString("content.tableFieldSite")).setSortable(true);
        grid.addComponentColumn(vacancy -> new Anchor(vacancy.getUrl(), vacancy.getSiteName()))
                .setSortable(true)
                .setHeader(getTranslation("content.tableFieldSite"))
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
                .setHeader(getTranslation("content.tableFieldDate"))
                .setSortable(true)
                .setId("columnDateId");
        vacanciesDataView = grid.setItems(new ArrayList<>());

        grid.setMultiSort(true, Grid.MultiSortPriority.APPEND);

        return grid;
    }

}
