package ua.findvacancies.thymeleafui.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import ua.findvacancies.common.mapper.JobSearchCriteriaMapper;
import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.model.dto.SearchCriteriaDto;
import ua.findvacancies.common.service.JobService;
import ua.findvacancies.common.store.DataStore;
import ua.findvacancies.common.util.UIUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Controller
@Slf4j
public class AppController {

    public static final String MAIN_PAGE = "index";

    private final LocaleResolver localeResolver;
    private final HttpServletRequest request;
    private final MessageSource messageSource;
    private final JobSearchCriteriaMapper jobSearchCriteriaMapper;

    private final JobService jobService;

    private final DataStore<JobVacancy> dataStore;

    public AppController(LocaleResolver localeResolver,
                         HttpServletRequest request,
                         MessageSource messageSource,
                         JobSearchCriteriaMapper jobSearchCriteriaMapper,
                         JobService jobService,
                         DataStore<JobVacancy> dataStore) {
        this.localeResolver = localeResolver;
        this.request = request;
        this.messageSource = messageSource;
        this.jobSearchCriteriaMapper = jobSearchCriteriaMapper;
        this.jobService = jobService;
        this.dataStore = dataStore;
    }

    @ModelAttribute("availableJobSites")
    public Set<JobSite> getAvailableJobSites() {
        return jobService.getAvailableJobSites();
    }

    @ModelAttribute("currentDate")
    public String getCurrentDate() {
        Locale currentLocale = localeResolver.resolveLocale(request);
        String todayPrefix = messageSource.getMessage("today.is", null, currentLocale);
        return UIUtils.getDateInfo(todayPrefix, LocalDate.now(), currentLocale);
    }

    @ModelAttribute("currentLocale")
    public String getCurrentLocale() {
        Locale currentLocale = localeResolver.resolveLocale(request);
        return currentLocale.getLanguage();
    }

    @GetMapping("/")
    public String index(Model model, Locale locale) {
        model.addAttribute("searchCriteria", new SearchCriteriaDto());
        updateModelAttributes(model, locale, dataStore.getAll());
        return MAIN_PAGE;
    }

    @PostMapping("/search")
    public String search(@Valid @ModelAttribute("searchCriteria") SearchCriteriaDto searchCriteriaDto,
                         BindingResult bindingResult,
                         Model model,
                         Locale locale) {

        if (bindingResult.hasErrors()) {
            log.warn("Validation Errors occurred. Returning search form with errors.");
            for (ObjectError objectError : bindingResult.getAllErrors()) {
                log.error("Error on: {}", objectError.getDefaultMessage());
            }
            return MAIN_PAGE;
        }
        dataStore.clear();
        log.info("Performing job search with DTO: {}", searchCriteriaDto);

        JobSearchCriteria jobSearchCriteria = jobSearchCriteriaMapper.toEntity(searchCriteriaDto);

        List<JobVacancy> jobVacancies = jobService.getJobs(jobSearchCriteria, searchCriteriaDto.getSites());
        dataStore.addAll(jobVacancies);

        updateModelAttributes(model, locale, jobVacancies);

        return MAIN_PAGE;
    }

    private void updateModelAttributes(Model model, Locale locale, Collection<JobVacancy> jobVacancies) {
        model.addAttribute("jobVacancies", jobVacancies);
        model.addAttribute("searchResultsMessage",
                messageSource.getMessage("search.results.message", new Object[]{jobVacancies.size()}, locale));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportJobs(@RequestParam("format") ExportFormat format) {

        Collection<JobVacancy> jobVacancies = dataStore.getAll();

        if (jobVacancies.isEmpty()) {
            return ResponseEntity.badRequest().body("No data to export".getBytes());
        }

        try {
            byte[] fileContent = jobService.exportJobs(jobVacancies, format);
            String filename = "job_vacancies." + format.getFileExtension();
            MediaType mediaType = MediaType.parseMediaType(format.getMimeType());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(mediaType)
                    .body(fileContent);
        } catch (Exception e) {
            log.error("Error during export: {}", e.getMessage());
            return ResponseEntity.internalServerError().body(null);
        }
    }

}
