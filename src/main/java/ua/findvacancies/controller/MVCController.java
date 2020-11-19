package ua.findvacancies.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import ua.findvacancies.export.XMLDocument;
import ua.findvacancies.model.Provider;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.model.viewdata.ViewSearchParams;
import ua.findvacancies.service.VacancyService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MVCController {
    private final VacancyService vacancyService;
    private List<Vacancy> vacancyList;

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String homePage(Model model) {
        log.info("Open homepage");

        model.addAttribute("viewSearchParams", vacancyService.getDefaultViewSearchParams());
        return "index";
    }

    @RequestMapping(value = "/searchVacancies", method = RequestMethod.GET)
    public String searchVacanciesByWords(Model model) {
        log.info("get searchVacanciesByWords");
        //model.addAttribute("viewSearchParams", vacancyService.getDefaultViewSearchParams());
        return "/index";
    }

    @RequestMapping(value = "/searchVacancies", method = RequestMethod.POST)
    public String searchVacanciesByWords(Model m, @Valid final ViewSearchParams viewSearchParams, final BindingResult result) {
        log.info("New search: {}", viewSearchParams);
        if (result.hasErrors()) {
            logErrors(result);
            return "index";
        }
        vacancyList = vacancyService.getVacancyList(viewSearchParams);
        m.addAttribute("resultVacanciesList", vacancyList);
        return "index";
    }

    private void logErrors(BindingResult result) {
        for (ObjectError objectError : result.getAllErrors()) {
            log.error("Error on: {}", objectError.getDefaultMessage());
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
