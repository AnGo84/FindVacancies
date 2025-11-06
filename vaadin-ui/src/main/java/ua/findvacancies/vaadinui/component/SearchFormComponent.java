package ua.findvacancies.vaadinui.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.dto.SearchCriteriaDto;
import ua.findvacancies.common.service.JobService;

import java.io.Serial;

@SpringComponent
@UIScope // Scoped to the current browser tab, as search criteria is session-specific
@Slf4j
public class SearchFormComponent extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = 1L;

    // Dependencies
    private final JobService jobService;

    // Data and Binding
    private final Binder<SearchCriteriaDto> binder = new Binder<>(SearchCriteriaDto.class);

    // UI Components
    private final TextField searchLine = new TextField();
    private final IntegerField days = new IntegerField();
    private final MultiSelectComboBox<JobSite> sites = new MultiSelectComboBox<>();
    private final TextField ignoreWords = new TextField();
    private final Button searchButton = new Button();
    // Advanced Options
    //private final VerticalLayout advancedOptionsLayout = new VerticalLayout();
    private final VerticalLayout advancedOptionsLayout = new VerticalLayout(ignoreWords);
    private final Button toggleAdvancedButton = new Button();
    private final HorizontalLayout toggleRow = new HorizontalLayout(toggleAdvancedButton);

    private final HorizontalLayout searchFieldsRow = new HorizontalLayout();

    private SearchCriteriaDto searchCriteria;

    // Constructor with injected dependencies (Spring Boot feature)
    public SearchFormComponent(JobService jobService) {
        this.jobService = jobService;

        // Initial setup
        configureComponents();
        configureBinder();
        buildLayout();

        // Initialize the DTO
        this.searchCriteria = new SearchCriteriaDto();
        binder.setBean(this.searchCriteria);
    }

    private void configureComponents() {
        // --- Input Fields ---
        searchLine.setLabel(getTranslation("search.keywords.label"));
        searchLine.setPlaceholder(getTranslation("search.keywords.placeholder"));
        searchLine.setRequired(true);
        searchLine.setWidthFull();

        days.setLabel(getTranslation("search.days.label"));
        days.setPlaceholder(String.valueOf(JobSearchCriteria.DEFAULT_LAST_N_DAYS));
        days.setRequiredIndicatorVisible(true);
        days.setStepButtonsVisible(true);
        days.setMin(0);
        days.setWidthFull();

        sites.setLabel(getTranslation("search.sites.label"));
        sites.setPlaceholder(getTranslation("search.sites.placeholder"));
        sites.setItems(jobService.getAvailableJobSites());
        sites.setItemLabelGenerator(JobSite::getSiteName);
        sites.setWidthFull();

        ignoreWords.setLabel(getTranslation("search.ignore.words.label"));
        ignoreWords.setPlaceholder(getTranslation("search.ignore.words.placeholder"));
        ignoreWords.setWidthFull();

        // --- Search Button ---
        searchButton.setText(getTranslation("search.button"));
        searchButton.setIcon(VaadinIcon.SEARCH.create());
        //searchButton.setThemeName("primary large");
        searchButton.setThemeName("primary");
        searchButton.setWidthFull();

        // --- Advanced Options ---
        advancedOptionsLayout.add(ignoreWords);
        advancedOptionsLayout.setPadding(false);
        advancedOptionsLayout.setVisible(false);

        toggleAdvancedButton.setText(getTranslation("search.options.advanced.button"));
        toggleAdvancedButton.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        toggleAdvancedButton.addClickListener(e -> toggleAdvancedOptions());
    }

    private void configureBinder() {
        // Validation messages would come from the MessageSource with proper i18n keys
        binder.forField(searchLine)
                .asRequired(getTranslation("searchParams.SearchLine.Blank"))
                .bind(SearchCriteriaDto::getSearchLine, SearchCriteriaDto::setSearchLine);

        binder.forField(days)
                .asRequired(getTranslation("searchParams.Days.Blank"))
                .withValidator(
                        value -> value != null && value >= 0, // Enforce min value constraint logic (>= 0)
                        getTranslation("searchParams.Days.MinValue") // The message for the min value violation
                )
                .bind(SearchCriteriaDto::getDays, SearchCriteriaDto::setDays);

        binder.forField(sites)
                .asRequired(getTranslation("searchParams.Sites.Blank"))
                .bind(SearchCriteriaDto::getSites, SearchCriteriaDto::setSites);

        binder.bind(ignoreWords, SearchCriteriaDto::getIgnoreWords, SearchCriteriaDto::setIgnoreWords);

        // Disable search button if binder has validation errors
        binder.addStatusChangeListener(e -> searchButton.setEnabled(binder.isValid()));
    }

    private void buildLayout() {
        // 1. Title
        H2 title = new H2(getTranslation("search.form.title"));
        title.addClassNames("text-center", "mb-2");

        // 2. Main Input Row (Flex-enabled container, wraps on small screens)
        searchFieldsRow.setWidthFull();
        //searchFieldsRow.setFlexGrow(FlexLayout.FlexWrap.WRAP); // Crucial for responsiveness
        searchFieldsRow.getElement().getStyle().set("flex-wrap", "wrap");

        // Apply column classes to the components' parent DIVs (the components themselves)

        // searchLine: col-12, col-lg-6
        searchLine.getElement().getClassList().add("col-12");
        searchLine.getElement().getClassList().add("col-lg-6");

        // days: col-12, col-md-4, col-lg-2
        days.getElement().getClassList().add("col-12");
        days.getElement().getClassList().add("col-md-4");
        days.getElement().getClassList().add("col-lg-2");

        // sites: col-12, col-md-8, col-lg-4
        sites.getElement().getClassList().add("col-12");
        sites.getElement().getClassList().add("col-md-8");
        sites.getElement().getClassList().add("col-lg-4");

        // Add components to the row
        searchFieldsRow.add(searchLine, sites, days);

        // 3. Button Row (Full width)
        HorizontalLayout buttonRow = new HorizontalLayout(searchButton);
        buttonRow.setWidthFull();

        // 4. Advanced Options Section

        advancedOptionsLayout.setVisible(false);
        advancedOptionsLayout.setPadding(false);
        advancedOptionsLayout.setSpacing(true);

        // 5. Toggle Row
        //HorizontalLayout toggleRow = new HorizontalLayout(toggleAdvancedButton);
        toggleRow.setWidthFull();
        toggleRow.setJustifyContentMode(JustifyContentMode.END);

        // 6. Assemble everything
        this.add(
                title,
                searchFieldsRow,
                toggleRow,
                advancedOptionsLayout,
                buttonRow
        );

        // Apply styling to mimic the card layout
        this.getElement().getStyle().set("padding", "var(--lumo-space-m)");
        this.getElement().getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        this.getElement().getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        this.getElement().getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");

    }

    private void toggleAdvancedOptions() {
        boolean visible = advancedOptionsLayout.isVisible();
        advancedOptionsLayout.setVisible(!visible);

        // Update the icon, mirroring the index.html logic
        if (visible) {
            toggleAdvancedButton.setIcon(VaadinIcon.PLUS_CIRCLE.create());
        } else {
            toggleAdvancedButton.setIcon(VaadinIcon.MINUS_CIRCLE.create());
        }
    }

    // --- Public methods for the MainView to interact ---

    public Button getSearchButton() {
        return searchButton;
    }

    public SearchCriteriaDto getSearchCriteria() {
        return this.searchCriteria;
    }

    /**
     * Attempts to validate and return the DTO.
     *
     * @return The DTO if valid, otherwise null.
     */
    public SearchCriteriaDto validateAndGetCriteria() {
        if (binder.isValid()) {
            return binder.getBean();
        }
        return null;
    }

}
