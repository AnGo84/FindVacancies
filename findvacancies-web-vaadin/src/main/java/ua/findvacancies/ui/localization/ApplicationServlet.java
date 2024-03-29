package ua.findvacancies.ui.localization;

import com.vaadin.flow.server.InitParameters;
import com.vaadin.flow.server.VaadinServlet;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;


@WebServlet(urlPatterns = "/*", name = "slot", asyncSupported = true, loadOnStartup = 1,
        initParams = {@WebInitParam(name = InitParameters.I18N_PROVIDER, value = "ua.findvacancies.localization.SimpleI18NProvider")})
public class ApplicationServlet extends VaadinServlet {
}
