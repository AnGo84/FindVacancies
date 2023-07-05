package ua.findvacancies.ui.component;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class NotificationComponent {
    /* https://vaadin.com/docs/v23/components/notification */

    public static void basic(String message, Notification.Position position){
        Notification notification = Notification.show(message, 3000 ,position);
    }

    public static void error(String errorMessage) {

        // When creating a notification using the constructor,
        // the duration is 0-sec by default which means that
        // the notification does not close automatically.
        Notification notification = new Notification();
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);

        Div text = new Div(new Text(errorMessage));

        Icon icon = VaadinIcon.WARNING.create();

        Button closeButton = createCloseButton(notification);

        HorizontalLayout layout = new HorizontalLayout(icon, text, closeButton);
        layout.setAlignItems(FlexComponent.Alignment.CENTER);

        notification.add(layout);
        notification.setPosition(Notification.Position.MIDDLE);
        notification.open();
    }

    private static Button createCloseButton(Notification notification) {
        Button closeButton = new Button(new Icon("lumo", "cross"));
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        closeButton.getElement().setAttribute("aria-label", "Close");
        closeButton.addClickListener(event -> {
            notification.close();
        });

        return closeButton;
    }

}
