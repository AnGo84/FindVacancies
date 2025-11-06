package ua.findvacancies.vaadinui.config;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.LoadingIndicatorConfiguration;
import com.vaadin.flow.component.page.Page;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.UIInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.context.annotation.Configuration;

/**
 * AppShell configures global settings for the Vaadin application.
 * It disables the default loading indicator theme to allow custom styling or behavior.
 * Implements VaadinServiceInitListener to hook into UI initialization and modify UI-level configurations.
 */
@Configuration
public class AppShell implements AppShellConfigurator, VaadinServiceInitListener {

    @Override
    public void serviceInit(ServiceInitEvent serviceInitEvent) {
        serviceInitEvent.getSource().addUIInitListener(this::initShell);
    }

    private void initShell(UIInitEvent uiInitEvent) {
        LoadingIndicatorConfiguration conf = uiInitEvent.getUI().getLoadingIndicatorConfiguration();
        // disable default theme -> loading indicator isn't shown
        conf.setApplyDefaultTheme(false);
    }

}
