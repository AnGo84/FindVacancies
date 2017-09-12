package ua.findvacancies.mvc;

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
import ua.findvacancies.mvc.model.Site;
import ua.findvacancies.mvc.model.VacancyModel;
import ua.findvacancies.mvc.utils.StringUtils;
import ua.findvacancies.mvc.vo.SearchParams;
import ua.findvacancies.mvc.vo.Vacancy;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by AnGo on 24.06.2017.
 */
@Controller
@RequestMapping({"/FindVacancies","/"})
public class MVCController {
    private static final String DEFAULT_SEARCH = "Java developer";
    private static final int DEFAULT_DAYS = 30;
    private static final String DEFAULT_XML_FILE_NAME = "Vacancies.xml";

    private List<Vacancy> vacancyList;

    private static final Logger rootLogger = LogManager.getRootLogger();

    @Autowired
    private VacancyModel model;

//    @Autowired
//    private SearchParamsValidator searchParamsValidator;

    @RequestMapping(value = "/")
    public ModelAndView homePage() {
        //System.out.println("Home ok! ");
        rootLogger.info("Start");

        ModelAndView mav = new ModelAndView();
        mav.setViewName("/index");
        //mav.addObject("searchWords", DEFAULT_SEARCH);
        //mav.addObject("searchDays", DEFAULT_DAYS);
        mav.addObject("searchParams", new SearchParams(DEFAULT_SEARCH, java.lang.String.valueOf(DEFAULT_DAYS),null) );
        return mav;
    }


    @RequestMapping(value = "/searchVacancies", method = RequestMethod.GET)
    public java.lang.String searchVacanciesByWords(Model model) {
        SearchParams searchParams = new SearchParams(DEFAULT_SEARCH, java.lang.String.valueOf(DEFAULT_DAYS));
        //SearchParams searchParams = new SearchParams(DEFAULT_SEARCH,DEFAULT_DAYS);
        model.addAttribute("searchParams", searchParams);
        return "/index";
    }

    @RequestMapping(value = "/searchVacancies", method = RequestMethod.POST)
    //public String searchVacanciesByWords(@RequestParam(value = "searchLine") String searchLine, @RequestParam(value = "days") String daysText,
    public java.lang.String searchVacanciesByWords(Model m, @Valid final SearchParams searchParams, final BindingResult result) {
        //System.out.println("searchLine1= " + searchLine + " | days1= " + daysText + " | " + "searchLine2= " + searchParams.getSearchLine() + " | days2= " + searchParams.getDays());
        rootLogger.info("New search:" + searchParams);
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
        vacancyList = model.getVacancyList(searchParams);
        m.addAttribute("resultVacanciesList", vacancyList);
        return "/index";
    }


    @RequestMapping(value = "/excelExport", method = RequestMethod.GET)
    public ModelAndView excelExport() {
        //System.out.println("Excel export is called");

        //excelDocument - look file-export-config.xml
        return new ModelAndView("/excelDocument", "modelObject", vacancyList);
    }

    @RequestMapping(value = "/xmlExport", method = RequestMethod.GET)
    public void xmlExport(HttpServletResponse response) {
        //System.out.println("XML export is called");

        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename=" + DEFAULT_XML_FILE_NAME);
        try {
            File file = new File(DEFAULT_XML_FILE_NAME);
            XMLDocument.writeVacanciesXMLFile(file, vacancyList);
            try (OutputStream out = response.getOutputStream();
                 FileInputStream in = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
        } catch (IOException | JAXBException e) {
            rootLogger.error("Export to XML error: " + e.getMessage());
        }

    }

    @ModelAttribute("sites")
    public Object initializeProfiles() {
        return Site.values();
    }
}
