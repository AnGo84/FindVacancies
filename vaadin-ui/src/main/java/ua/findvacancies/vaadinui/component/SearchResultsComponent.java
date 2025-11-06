package ua.findvacancies.vaadinui.component;

import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H4;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.service.JobService;
import ua.findvacancies.common.store.DataStore;

import java.io.Serial;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Locale;

@SpringComponent
@UIScope
@Slf4j
//@Route(value = "", layout = MainLayout.class)
public class SearchResultsComponent extends VerticalLayout {

    @Serial
    private static final long serialVersionUID = 1L;

    // Dependencies
    private final DataStore<JobVacancy> dataStore;
    private final JobService jobService;

    // UI Components
    private final Grid<JobVacancy> resultsGrid = new Grid<>(JobVacancy.class, false);
    private final Paragraph resultsMessage = new Paragraph();
    private final Div resultsContainer = new Div();
    private final HorizontalLayout headerLayout = new HorizontalLayout();

    // Create the Export Dropdown
    private final ExportDropdown exportDropdown;

    // TODO: remove from here
    // Date formatter matching the Thymeleaf example (dd.MM.yyyy)
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.ENGLISH);

    public SearchResultsComponent(DataStore<JobVacancy> dataStore, JobService jobService) {
        this.dataStore = dataStore;
        this.jobService = jobService;
        this.exportDropdown = new ExportDropdown(jobService, dataStore);

        //
        setSizeFull();

        // Initial setup
        configureGrid();
        configureExportDropdown();
        buildLayout();

        // Hide initial view until a search is performed
        this.setVisible(false);
    }

    private void configureGrid() {
        // --- Column Configuration (matching search-results.html) ---

        // Title Column (as a hyperlink)
        resultsGrid.addComponentColumn(vacancy -> {
            Anchor link = new Anchor(vacancy.getUrl(), vacancy.getTitle());
            link.setTarget("_blank");
            return link;
        }).setHeader(getTranslation("table.header.title")).setFlexGrow(3);

        // Company Name
        resultsGrid.addColumn(JobVacancy::getCompanyName).setHeader(getTranslation("table.header.company")).setFlexGrow(2);

        // Site Name
        resultsGrid.addColumn(vacancy -> vacancy.getJobSite().getSiteName())
                .setHeader(getTranslation("table.header.site")).setFlexGrow(1);

        // Date Posted (formatted)
        resultsGrid.addColumn(vacancy -> vacancy.getDatePosted().format(dateFormatter))
                .setHeader(getTranslation("table.header.date")).setFlexGrow(1).setKey("date");

        // Salary
        resultsGrid.addColumn(JobVacancy::getSalary).setHeader(getTranslation("table.header.salary")).setFlexGrow(1);

        // City
        resultsGrid.addColumn(JobVacancy::getCity).setHeader(getTranslation("table.header.city")).setFlexGrow(1);

        // Hot Status (as a badge/icon)
        resultsGrid.addComponentColumn(vacancy -> {
                    if (vacancy.isHot()) {
                        // Mimics the Thymeleaf's 🔥 badge
                        Div hotBadge = new Div();
                        hotBadge.setText("🔥");
                        hotBadge.getElement().setAttribute("title", "HOT");
                        hotBadge.getElement().getStyle().set("text-align", "center");
                        return hotBadge;
                    }
                    return new Div();
                }).setHeader("HOT")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(0)
                .setWidth("100px");

        resultsGrid.setAllRowsVisible(true); // Small result set likely
        resultsGrid.setWidthFull();
    }

    private void configureExportDropdown() {

        headerLayout.setWidthFull();
        headerLayout.setJustifyContentMode(JustifyContentMode.BETWEEN);
        headerLayout.add(new H4(getTranslation("search.results.title")), exportDropdown);
    }

    private void buildLayout() {
        // Result Message and Grid
        resultsMessage.addClassName("text-muted");
        resultsContainer.add(resultsGrid);
        resultsContainer.setWidthFull();

        this.add(headerLayout, resultsMessage, resultsContainer);

        // Styling to mimic the 'card card-body' from search-results.html
        this.getElement().getStyle().set("padding", "var(--lumo-space-m)");
        this.getElement().getStyle().set("border", "1px solid var(--lumo-contrast-10pct)");
        this.getElement().getStyle().set("border-radius", "var(--lumo-border-radius-m)");
        this.getElement().getStyle().set("box-shadow", "var(--lumo-box-shadow-s)");
    }

    /**
     * Updates the grid data and visibility based on the current DataStore content.
     */
    public void updateResults() {
        Collection<JobVacancy> vacancies = dataStore.getAll();

        // Set new items to the grid
        resultsGrid.setItems(vacancies);
        this.setVisible(true);

        if (vacancies.isEmpty()) {
            resultsMessage.setText(getTranslation("search.results.empty"));
            this.exportDropdown.setEnabled(false);

            //this.setVisible(false); // Hide the whole component if empty

            return;
        }
        this.exportDropdown.setEnabled(true);

        // Update the results message (mimics AppController's updateModelAttributes)
        String message = getTranslation("search.results.message", vacancies.size());
        resultsMessage.setText(message);

        //this.setVisible(true);
    }

}
