package ua.findvacancies.vaadinui.view;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLayout;
import lombok.extern.slf4j.Slf4j;
import ua.findvacancies.common.mapper.JobSearchCriteriaMapper;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.model.dto.SearchCriteriaDto;
import ua.findvacancies.common.service.JobService;
import ua.findvacancies.common.store.DataStore;
import ua.findvacancies.vaadinui.component.FooterComponent;
import ua.findvacancies.vaadinui.component.LoadingModalComponent;
import ua.findvacancies.vaadinui.component.SearchFormComponent;
import ua.findvacancies.vaadinui.component.SearchResultsComponent;
import ua.findvacancies.vaadinui.component.TopNavbarComponent;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

// The @Route annotation maps this class to the application's root URL ("/")
@CssImport("./styles/shared-styles.css")
@Route("")
@Slf4j
//public class MainView extends VerticalLayout implements RouterLayout {
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
    //private final LoadingModalComponent loadingModal;
    private final Div contentContainer = new Div();
    private final FooterComponent footer = new FooterComponent();

    public MainView(JobService jobService,
                    DataStore<JobVacancy> dataStore,
                    JobSearchCriteriaMapper criteriaMapper,
                    TopNavbarComponent topNavbar,
                    SearchFormComponent searchForm,
                    SearchResultsComponent searchResults) {

        log.info("Start main with locale: {}", UI.getCurrent().getLocale().getLanguage());

        this.jobService = jobService;
        this.dataStore = dataStore;
        this.criteriaMapper = criteriaMapper;
        this.topNavbar = topNavbar;
        this.searchForm = searchForm;
        this.searchResults = searchResults;
        //this.loadingModal = new LoadingModalComponent();


        /*var availableLocales = i18NProvider.getProvidedLocales();
        log.info("Available locales: {}", availableLocales);*/

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

        // *** CRITICAL: Force Content Container to grow ***
        //setFlexGrow(1, contentContainer); // Content takes all available vertical space

        // 3. Footer
        // if add here it will be hiding by search results.
        // TODO: Need refactoring
        //add(footer);
        expand(contentContainer);

        // Connect the Search button action
        searchForm.getSearchButton().addClickListener(e -> performSearch());

        // Initialize results display (clear old results from previous session/startup)
        dataStore.clear();
        searchResults.updateResults();
    }

    // TODO: do we really need this method and implementation of RouterLayout at all?
    /*@Override
    public void showRouterLayoutContent(HasElement content) {
        // Clear previous content
        contentContainer.removeAll();

        // Add the new routed view content
        // You must cast the HasElement to Component to add it to the Div/Layout
        if (content instanceof Component component) {
            contentContainer.add(component);
        } else {
            // Handle case where content is not a standard Vaadin Component
            // (e.g., if it's just a raw Element wrapper, which is rare for a View)
            contentContainer.getElement().appendChild(content.getElement());
        }
    }*/

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
