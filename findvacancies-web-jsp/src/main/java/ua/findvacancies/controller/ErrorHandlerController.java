package ua.findvacancies.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;

/**
 * https://www.journaldev.com/2651/spring-mvc-exception-handling-controlleradvice-exceptionhandler-handlerexceptionresolver
 */
@Controller
@Slf4j
public class ErrorHandlerController implements ErrorController {

    @GetMapping(value = "/error")
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
        log.error("Prepare error url: {}", httpRequest.getRequestURI());
        log.error("Prepare error errorCode: {}", httpErrorCode);
        log.error("Prepare error errorInfo: {}", errorInfo);
        log.error("Prepare error errorMsg: {}", errorMsg);

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
        if (ObjectUtils.isEmpty(httpRequest.getAttribute("javax.servlet.error.status_code"))) {
            return 0;
        }
        return (Integer) httpRequest
                .getAttribute("javax.servlet.error.status_code");
    }

    private String getErrorMessage(HttpServletRequest httpRequest) {
        return (String) httpRequest
                .getAttribute("javax.servlet.error.message");
    }

    public String getErrorPath() {
        return "errors/error";
    }

}
