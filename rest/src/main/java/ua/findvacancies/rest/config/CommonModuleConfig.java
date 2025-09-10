package ua.findvacancies.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.findvacancies.common.export.impl.ExcelExportService;
import ua.findvacancies.common.export.JobExportService;
import ua.findvacancies.common.export.JobExportServiceRegistry;
import ua.findvacancies.common.export.impl.JsonExportService;
import ua.findvacancies.common.export.impl.XmlExportService;
import ua.findvacancies.common.scraper.impl.DjinniJobScraper;
import ua.findvacancies.common.scraper.JobScraper;
import ua.findvacancies.common.scraper.JobScraperRegistry;
import ua.findvacancies.common.scraper.JobScrapingOrchestrator;
import ua.findvacancies.common.scraper.JsoupDocumentFetcher;
import ua.findvacancies.common.scraper.impl.WorkUaJobScraper;
import ua.findvacancies.common.service.JobFilterService;
import ua.findvacancies.common.service.JobService;
import ua.findvacancies.common.service.JobServiceBuilder;

import java.util.Set;

/**
 * Spring configuration class for defining beans from the common module.
 */
@Configuration
public class CommonModuleConfig {

    @Bean
    public JobService jobService() {
        return new JobServiceBuilder().build();
    }

}
