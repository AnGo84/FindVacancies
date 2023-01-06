package ua.findvacancies.controller;

import jakarta.validation.Valid;
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
import ua.findvacancies.export.ExcelDocument;
import ua.findvacancies.export.XMLDocument;
import ua.findvacancies.model.Provider;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.model.viewdata.ViewSearchParams;
import ua.findvacancies.service.VacancyService;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class AppController {
    private final VacancyService vacancyService;
    private List<Vacancy> vacancyList;

    @RequestMapping(value = {"/", "/index"})
    public ModelAndView homePage() {
        log.info("Open homepage");
        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("viewSearchParams", vacancyService.getDefaultViewSearchParams());
        return modelAndView;
    }

    @RequestMapping(value = "/searchVacancies", method = RequestMethod.GET)
    public String searchVacanciesByWords(Model model) {
        log.info("get searchVacanciesByWords");
        return "/index";
    }

    @RequestMapping(value = "/searchVacancies", method = RequestMethod.POST)
    public String searchVacanciesByWords(Model model, @Valid final ViewSearchParams viewSearchParams, final BindingResult result) {
        log.info("New search: {}", viewSearchParams);
        if (result.hasErrors()) {
            logErrors(result);
            return "index";
        }
        vacancyList = vacancyService.getVacancyList(viewSearchParams);
        model.addAttribute("resultVacanciesList", vacancyList);
        return "index";
    }

    @RequestMapping(value = "/excelExport", method = RequestMethod.GET)
    public ModelAndView excelExport() {
        log.info("Export to XLS");
        return new ModelAndView(new ExcelDocument(), ExcelDocument.OBJECT_NAME, vacancyList);
    }

    @RequestMapping(value = "/xmlExport", method = RequestMethod.GET)
    public void xmlExport(HttpServletResponse response) throws JAXBException, IOException {
        log.info("Export to XML");
        try {
            new XMLDocument().build(response, vacancyList);
        } catch (JAXBException | IOException e) {
            log.error("Export to XML error: {}", e.getMessage(), e);
            throw e;
        }
    }

    @ModelAttribute("sites")
    public Object initializeProfiles() {
        return Provider.values();
    }

    private void logErrors(BindingResult result) {
        for (ObjectError objectError : result.getAllErrors()) {
            log.error("Error on: {}", objectError.getDefaultMessage());
        }
    }
}
