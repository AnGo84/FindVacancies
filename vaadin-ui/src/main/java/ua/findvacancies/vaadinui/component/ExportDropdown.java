package ua.findvacancies.vaadinui.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.contextmenu.ContextMenu;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.server.streams.DownloadEvent;
import org.jetbrains.annotations.NotNull;
import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.service.JobService;
import ua.findvacancies.common.store.DataStore;
import ua.findvacancies.common.util.FileUtils;

import java.io.OutputStream;

public class ExportDropdown extends HorizontalLayout {

    private final JobService jobService;
    private final DataStore<JobVacancy> dataStore;

    /**
     * Creates the Export Dropdown component.
     *
     * @param jobService Service to get available formats and perform export logic.
     * @param dataStore  Data store to retrieve the current vacancies for export.
     */
    public ExportDropdown(final JobService jobService, final DataStore dataStore) {
        this.jobService = jobService;
        this.dataStore = dataStore;

        // --- 1. Setup the Dropdown Button ---
        Button exportButton = new Button("Export", VaadinIcon.FILE_REFRESH.create());

        // Use Lumo utilities for primary style
        exportButton.addThemeVariants(com.vaadin.flow.component.button.ButtonVariant.LUMO_PRIMARY);

        // --- 2. Setup the Context Menu (The Dropdown) ---
        ContextMenu contextMenu = new ContextMenu(exportButton);
        contextMenu.setOpenOnClick(true); // Open the menu when the button is clicked (not just right-click)

        // Add a frame on Item:
        /*contextMenu.setOverlayClassName(LumoUtility.Border.ALL);
        contextMenu.setOverlayClassName(LumoUtility.Background.BASE);*/
        /*contextMenu.setOverlayClassName(LumoUtility.Border.ALL + " " + LumoUtility.Background.BASE);*/

        // --- 3. Populate the Context Menu dynamically ---
        populateMenu(contextMenu);

        // --- 4. Add the button/menu to this component ---
        add(exportButton);

    }

    /**
     * Dynamically creates menu items with Anchors for all available export formats.
     */
    private void populateMenu(final ContextMenu contextMenu) {
        // Clear previous items if this menu were to be refreshed
        contextMenu.removeAll();

        for (ExportFormat format : jobService.getAvailableExportFormats()) {
            // Create the Anchor element which triggers the file download
            Anchor exportLink = createExportLink(format);

            // Add the Anchor as the content of the Context Menu Item
            // Note: Menu items accept any Vaadin Component.
            contextMenu.addItem(exportLink);
        }
    }


    /**
     * Reuses your existing logic to create a StreamResource download link.
     */
    private Anchor createExportLink(final ExportFormat format) {

        Anchor downloadLink = getDownloadLink(format);
        downloadLink.getStyle().set("padding", "var(--lumo-space-s) var(--lumo-space-m)");
        downloadLink.getStyle().set("color", "inherit"); // Inherit text color
        downloadLink.getStyle().set("text-decoration", "none"); // Remove underline

        // Add hover effect via CSS to mimic menu item behavior
        downloadLink.getElement().addEventListener("mouseover", event -> downloadLink.getStyle().set("background",
                "var(--lumo-primary-color-10pct)"));
        downloadLink.getElement().addEventListener("mouseout", event -> downloadLink.getStyle().set("background",
                "transparent"));

        return downloadLink;
    }

    @NotNull
    private Anchor getDownloadLink(final ExportFormat format) {
        Anchor downloadLink = new Anchor((DownloadEvent event) -> {
            event.setFileName(FileUtils.getFileName(format));

            event.getResponse().setHeader("Cache-Control", "public, max-age=3600");
            try (OutputStream outputStream = event.getOutputStream()) {
                final var vacancies = dataStore.getAll();
                byte[] fileContent = jobService.exportJobs(vacancies, format);
                outputStream.write(fileContent);
            }
        }, format.getFileExtension().toUpperCase());


        // Add minimal styling to make it look like a menu item
        downloadLink.setWidthFull();
        return downloadLink;
    }

}
