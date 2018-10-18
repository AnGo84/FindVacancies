package ua.findvacancies.mvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorController {
    //https://www.journaldev.com/2651/spring-mvc-exception-handling-controlleradvice-exceptionhandler-handlerexceptionresolver
    @RequestMapping(value = "errors", method = RequestMethod.GET)
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest, Exception ex) {
        System.out.println("Get error");
        ModelAndView errorPage = new ModelAndView("/errors/error");
        //System.out.println("Get view status:" + errorPage.getStatus() + " " + errorPage.getViewName());
        String errorInfo = "";
        String errorMsg = getErrorMessage(httpRequest);
        //System.out.println("Get error msg: " + errorMsg);
        int httpErrorCode = getErrorCode(httpRequest);
        //System.out.println("Get error code: " + httpErrorCode);
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

        errorPage.addObject("errorInfo", errorInfo);
        //errorPage.addObject("errorMsg", errorMsg);

        errorPage.addObject("exception", ex);
        errorPage.addObject("url", httpRequest.getRequestURI());

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
}
