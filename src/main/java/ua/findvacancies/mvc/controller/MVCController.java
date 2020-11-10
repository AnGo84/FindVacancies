package ua.findvacancies.mvc.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ua.findvacancies.mvc.export.XMLDocument;
import ua.findvacancies.mvc.model.Provider;
import ua.findvacancies.mvc.model.Vacancy;
import ua.findvacancies.mvc.model.viewdata.ViewSearchParams;
import ua.findvacancies.mvc.service.VacancyService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
public class MVCController {

    private static final Logger rootLogger = LogManager.getRootLogger();
    private final VacancyService vacancyService;
    private List<Vacancy> vacancyList;

    @Autowired
    public MVCController(VacancyService vacancyService) {
        this.vacancyService = vacancyService;
    }

    @RequestMapping(value = "/")
    public ModelAndView homePage() {
        //rootLogger.info("Start");
        ModelAndView mav = new ModelAndView();
        mav.setViewName("/index");
        mav.addObject("viewSearchParams", vacancyService.getDefaultViewSearchParams());
        return mav;
    }


    @RequestMapping(value = "/searchVacancies", method = RequestMethod.GET)
    public String searchVacanciesByWords(Model model) {
        model.addAttribute("viewSearchParams", vacancyService.getDefaultViewSearchParams());
        return "/index";
    }

    @RequestMapping(value = "/searchVacancies", method = RequestMethod.POST)
    public String searchVacanciesByWords(Model m, @Valid final ViewSearchParams viewSearchParams, final BindingResult result) {
        rootLogger.info("New search:" + viewSearchParams);
        if (result.hasErrors()) {
            logErrors(result);
            return "/index";
        }
        vacancyList = vacancyService.getVacancyList(viewSearchParams);
        m.addAttribute("resultVacanciesList", vacancyList);
        return "/index";
    }

    private void logErrors(BindingResult result) {
        for (ObjectError objectError : result.getAllErrors()) {
            rootLogger.error("Error on: " + objectError.getDefaultMessage());
        }
    }

    @RequestMapping(value = "/excelExport", method = RequestMethod.GET)
    public ModelAndView excelExport() {
        //excelDocument - look file-export-config.xml
        return new ModelAndView("/excelDocument", "modelObject", vacancyList);
    }

    @RequestMapping(value = "/xmlExport", method = RequestMethod.GET)
    public void xmlExport(HttpServletResponse response) {
        new XMLDocument().buildXMLDocument(response, vacancyList);
    }

    @ModelAttribute("sites")
    public Object initializeProfiles() {
        return Provider.values();
    }
}
