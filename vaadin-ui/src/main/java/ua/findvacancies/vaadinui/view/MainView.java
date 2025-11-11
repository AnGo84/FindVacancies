package ua.findvacancies.vaadinui.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import lombok.extern.slf4j.Slf4j;
import ua.findvacancies.common.mapper.JobSearchCriteriaMapper;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.model.dto.SearchCriteriaDto;
import ua.findvacancies.common.service.JobService;
import ua.findvacancies.common.store.DataStore;
import ua.findvacancies.vaadinui.component.FooterComponent;
import ua.findvacancies.vaadinui.component.SearchFormComponent;
import ua.findvacancies.vaadinui.component.SearchResultsComponent;
import ua.findvacancies.vaadinui.component.TopNavbarComponent;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

@CssImport("./styles/shared-styles.css")
@Route("")
@Slf4j
public class MainView extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = 1L;

    private final JobService jobService;
    private final DataStore<JobVacancy> dataStore;
    private final JobSearchCriteriaMapper criteriaMapper;

    // Components
    private final TopNavbarComponent topNavbar;
    private final SearchFormComponent searchForm;
    private final SearchResultsComponent searchResults;
    private final Div contentContainer = new Div();
    private final FooterComponent footer = new FooterComponent();

    public MainView(final JobService jobService,
                    final DataStore<JobVacancy> dataStore,
                    final JobSearchCriteriaMapper criteriaMapper,
                    final TopNavbarComponent topNavbar,
                    final SearchFormComponent searchForm,
                    final SearchResultsComponent searchResults) {

        log.info("Start main with locale: {}", UI.getCurrent().getLocale().getLanguage());

        this.jobService = jobService;
        this.dataStore = dataStore;
        this.criteriaMapper = criteriaMapper;
        this.topNavbar = topNavbar;
        this.searchForm = searchForm;
        this.searchResults = searchResults;

        // Configure main layout
        setSizeFull();
        setPadding(false);
        setMargin(false);

        // 1. Add the Top-Navbar
        add(topNavbar);

        // 2. Content Container (Placeholder for the routed view)
        contentContainer.setSizeFull(); // Makes the content container take up available space

        // Add styling for internal padding if needed
        contentContainer.getElement().getStyle().set("padding", "var(--lumo-space-m)");

        // 2.1 Add the Search Form and Results
        contentContainer.add(searchForm, searchResults, footer);
        add(contentContainer);
        expand(contentContainer);

        // Connect the Search button action
        searchForm.getSearchButton().addClickListener(e -> performSearch());

        // Initialize results display (clear old results from previous session/startup)
        dataStore.clear();
        searchResults.updateResults();
    }

    /**
     * Executes the search in a background thread to prevent UI freezing
     * and shows/hides the loading modal.
     */
    private void performSearch() {
        log.info("Start performSearch");
        SearchCriteriaDto dto = searchForm.validateAndGetCriteria();

        if (dto == null) {
            log.info("Validation failed on the form.");
            return;
        }

        // 1. Show loading indicator on the UI thread
        //final LoadingModalComponent loadingModal = new  LoadingModalComponent();
        //loadingModal.showLoading();

        // Capture the current UI instance for safe updates from the background thread
        UI ui = UI.getCurrent();

        // 2. Execute the long-running process in a separate thread
        // Start background thread for blocking I/O (scraping)
        //var serviceInteraction = new Thread(() -> {
        //new Thread(() -> {

        // Use a list initialized here to hold results for safe access later
        List<JobVacancy> jobVacancies = new ArrayList<>();

        try {
            // 1. BLOCKING CODE: Execute the job search
            JobSearchCriteria criteria = criteriaMapper.toEntity(dto);
            jobVacancies = jobService.getJobs(criteria, dto.getSites()); //

            // 2. DATA STORE UPDATE (Run on background thread for performance)
            dataStore.clear(); //
            dataStore.addAll(jobVacancies); //

        } catch (Exception e) {
            // LOG AND NOTIFY: Handle error if scraping or data store failed
            log.error("Search failed: {}", e.getMessage());
            ui.accessSynchronously(() ->
                    Notification.show("Search failed: " + e.getMessage(), 5000,
                                    Notification.Position.MIDDLE)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR)
            );
        } finally {

            //log.info("UI: {}", ui);

            List<JobVacancy> finalJobVacancies = jobVacancies;
            ui.accessSynchronously(() -> {
                try {
                    searchResults.updateResults();
                } catch (Exception uiException) {
                    log.error("Error updating search results UI: {}", uiException.getMessage());
                }
                    /*try {
                        loadingModal.hideLoading();
                    } catch (Exception e) {
                        log.error("Error on hide modal: {}", e.getMessage());
                    }*/
                log.info("Search complete. Found {} vacancies.", finalJobVacancies.size());
            });
        }
        //}).start();

    }

}
