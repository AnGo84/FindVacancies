package ua.findvacancies;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.LoadingIndicatorConfiguration;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


//@JsModule("@vaadin/vaadin-lumo-styles/presets/compact.js")
@CssImport("./styles/app_styles.css")
//
@SpringBootApplication
public class ApplicationVaadin implements AppShellConfigurator, VaadinServiceInitListener {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationVaadin.class);
    }

    @Override
    public void configurePage(AppShellSettings settings) {
        AppShellConfigurator.super.configurePage(settings);
        settings.addFavIcon("icon", "icons/icon.png", "192x192");
        settings.addLink("shortcut icon", "icons/logo_16.ico");
    }


    // Displaying a Modal Curtain https://vaadin.com/docs/v23/advanced/loading-indicator
    // TODO - solve the problem with <vaadin-connection-indicator> to avoid scrolling
    // the issues https://github.com/vaadin/flow/issues/12696
    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent) {
        serviceInitEvent.getSource().addUIInitListener(uiInitEvent -> {
            LoadingIndicatorConfiguration conf = uiInitEvent.getUI().getLoadingIndicatorConfiguration();

            // disable default theme -> loading indicator isn't shown
            conf.setApplyDefaultTheme(false);
        });
    }
}
