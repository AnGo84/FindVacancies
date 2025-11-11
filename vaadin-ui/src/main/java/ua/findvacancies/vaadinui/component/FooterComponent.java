package ua.findvacancies.vaadinui.component;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class FooterComponent extends HorizontalLayout {

    public FooterComponent() {
        // 1. Footer Content
        Span copyright = new Span("© 2025 Job Vacancy Search");

        // 2. Vaadin/Lumo Styling (Bootstrap equivalent: bg-light text-center text-secondary py-2 mt-2)
        addClassNames(
                LumoUtility.Background.BASE, // White/light background
                LumoUtility.TextColor.SECONDARY, // Gray text color
                LumoUtility.Padding.Vertical.XSMALL, // Vertical padding (py-2)
                LumoUtility.Margin.Top.MEDIUM // Top margin (mt-2)
        );

        // 3. Layout Setup
        setWidthFull(); // Full width
        setJustifyContentMode(JustifyContentMode.CENTER); // Center the content

        add(copyright);
    }

}
