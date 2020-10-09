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
import ua.findvacancies.mvc.service.VacancyService;
import ua.findvacancies.mvc.viewdata.ViewSearchParams;
import ua.findvacancies.mvc.viewdata.Vacancy;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Controller
public class MVCController {
    private static final String DEFAULT_SEARCH = "Java developer";
    private static final int DEFAULT_DAYS = 30;

    private List<Vacancy> vacancyList;

    private static final Logger rootLogger = LogManager.getRootLogger();

    @Autowired
    private VacancyService model;

    @RequestMapping(value = "/")
    public ModelAndView homePage() {
        //rootLogger.info("Start");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/index");
        mav.addObject("viewSearchParams", new ViewSearchParams(DEFAULT_SEARCH, java.lang.String.valueOf(DEFAULT_DAYS),null) );
        return mav;
    }


    @RequestMapping(value = "/searchVacancies", method = RequestMethod.GET)
    public String searchVacanciesByWords(Model model) {
        ViewSearchParams viewSearchParams = new ViewSearchParams(DEFAULT_SEARCH, java.lang.String.valueOf(DEFAULT_DAYS));
        //SearchParams searchParams = new SearchParams(DEFAULT_SEARCH,DEFAULT_DAYS);
        model.addAttribute("viewSearchParams", viewSearchParams);
        return "/index";
    }

    @RequestMapping(value = "/searchVacancies", method = RequestMethod.POST)
    //public String searchVacanciesByWords(@RequestParam(value = "searchLine") String searchLine, @RequestParam(value = "days") String daysText,
    //@ModelAttribute("searchParams")
    public String searchVacanciesByWords(Model m, @Valid final ViewSearchParams viewSearchParams, final BindingResult result) {
        //System.out.println("searchLine1= " + searchLine + " | days1= " + daysText + " | " + "searchLine2= " + searchParams.getSearchLine() + " | days2= " + searchParams.getDays());
        rootLogger.info("New search:" + viewSearchParams);
        if (result.hasErrors()) {
            for (ObjectError objectError : result.getAllErrors()) {
                rootLogger.error("Error on: " + objectError.getDefaultMessage());
            }
            //m.addAttribute("message", "Error!!!!!!!!!!!");
            return "/index";
        }
        //int days = StringUtils.getDaysFromText(searchParams.getDays());
        ////vacancyList = model.getVacancyList(searchLine, -days);
        //vacancyList = model.getVacancyList(searchParams.getSearchLine(), -days);
        vacancyList = model.getVacancyListByThreads(viewSearchParams);
        m.addAttribute("resultVacanciesList", vacancyList);
        return "/index";
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
