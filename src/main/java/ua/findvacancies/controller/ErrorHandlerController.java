package ua.findvacancies.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Controller
public class ErrorHandlerController implements ErrorController {
    //https://www.journaldev.com/2651/spring-mvc-exception-handling-controlleradvice-exceptionhandler-handlerexceptionresolver
    @RequestMapping(value = "/error", method = RequestMethod.GET)
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest, Exception ex) {
        log.error("Get error: {}", ex.getMessage(), ex);
        ModelAndView errorPage = new ModelAndView(getErrorPath());
        String errorInfo = "";
        String errorMsg = getErrorMessage(httpRequest);
        int httpErrorCode = getErrorCode(httpRequest);
        switch (httpErrorCode) {
            case 400: {
                errorInfo = "Http Error Code: 400. Bad Request";
                break;
            }
            case 401: {
                errorInfo = "Http Error Code: 401. Unauthorized";
                break;
            }
            case 404: {
                errorInfo = "Http Error Code: 404. Resource not found";
                break;
            }
            case 500: {
                errorInfo = "Http Error Code: 500. Internal Server Error";
                break;
            }
        }

        errorPage.addObject("url", httpRequest.getRequestURI());
        errorPage.addObject("timestamp", new Date());
        errorPage.addObject("errorCode", httpErrorCode);
        errorPage.addObject("errorInfo", errorInfo);
        errorPage.addObject("errorMsg", errorMsg);

        errorPage.addObject("exception", ex);

        return errorPage;
    }

    //https://www.tutorialspoint.com/servlets/servlets-exception-handling.htm
    private int getErrorCode(HttpServletRequest httpRequest) {
        return (Integer) httpRequest
                .getAttribute("javax.servlet.error.status_code");
    }

    private String getErrorMessage(HttpServletRequest httpRequest) {
        return (String) httpRequest
                .getAttribute("javax.servlet.error.message");
    }

    //@Override
    public String getErrorPath() {
        return "errors/error";
    }
}
