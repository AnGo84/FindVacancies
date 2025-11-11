package ua.findvacancies.vaadinui.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import lombok.extern.slf4j.Slf4j;

import java.io.Serial;

/**
 * Vaadin component mimicking the Bootstrap loading modal (loading-modal.html).
 * Shows a spinner and informative messages during long-running tasks.
 */
@SpringComponent
@UIScope
@Slf4j
public class LoadingModalComponent extends Dialog {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Div spinner = new Div();
    private final Paragraph processMessage = new Paragraph();
    private final Paragraph waitMessage = new Paragraph();

    public LoadingModalComponent() {
        //this.i18nHelper = i18nHelper;

        // 1. Configure Dialog
        // Corresponds to data-bs-backdrop="static" and data-bs-keyboard="false"
        setId("loadingModal");
        setCloseOnEsc(false);
        setCloseOnOutsideClick(false);
        setModal(true); // Ensures it overlays everything

        // 2. Configure Spinner (Mimics Bootstrap spinner-border)
        // Vaadin doesn't have a built-in spinner, so we use a custom style.
        // For a quick fix, we'll use a FontAwesome style icon and animation
        // OR rely on Lumo's built-in progress indicator styling (less direct).

        // A simple way is to use the Icon component and apply a spinning animation.
        Icon loadingIcon = VaadinIcon.CIRCLE_THIN.create();
        loadingIcon.setSize("3rem");
        loadingIcon.addClassName("text-primary");

        // Add a basic CSS class for rotation (requires custom CSS/theme setup,
        // but we'll add the class name for future implementation)
        loadingIcon.getElement().setAttribute("aria-hidden", "true");
        loadingIcon.addClassName("vaadin-spinner-rotate"); // Placeholder for CSS animation

        // 3. Configure Messages
        processMessage.getStyle().set("font-size", "1.25rem"); // fs-5
        processMessage.addClassName("mt-3");
        processMessage.setText(getTranslation("modal.process.message"));

        waitMessage.addClassNames("mt-1", "text-secondary");
        waitMessage.setText(getTranslation("modal.wait.message"));

        var closeButton = new Button("Close modal");
        closeButton.addClickListener(e -> this.close());

        // 4. Build Layout
        VerticalLayout modalContent = new VerticalLayout(
                loadingIcon, // Using Icon as the spinner placeholder
                processMessage,
                waitMessage,
                closeButton
        );
        modalContent.setAlignItems(FlexComponent.Alignment.CENTER);
        modalContent.setSpacing(true);

        // Set content to the Dialog
        add(modalContent);

        // Styling the modal content wrapper to look centered and small (modal-dialog modal-dialog-centered)
        this.setWidth("20em");
    }

    // For Vaadin, we need to show and hide the dialog programmatically.
    // The methods are inherited from Dialog.

    /**
     * Shows the loading modal.
     */
    public void showLoading() {
        log.info("Show loading modal");
        // The equivalent of super.open() or this.open()
        //this.setOpened(true);
        this.open();
    }

    /**
     * Hides the loading modal.
     */
    public void hideLoading() {
        log.info("Hide modal!!!");
        // 2. Execute a direct JavaScript command to ensure the overlay attribute is removed.
        // This is a direct instruction to the browser, which is the most reliable channel.

        /*UI ui = UI.getCurrent();
        log.info("UI: {}", ui);
        ui.getPage().executeJs("document.querySelector('vaadin-dialog-overlay[opened]').removeAttribute('opened');"+
                "console.log('Closed modal');");*/

        log.info("Is attached: {}, isPresent: {}", this.isAttached(), this.getUI().isPresent());
        this.setOpened(false);
        log.info("Is attached: {}, isPresent: {}", this.isAttached(), this.getUI().isPresent());

    }

}
