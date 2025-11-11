package ua.findvacancies.vaadinui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.findvacancies.common.mapper.JobSearchCriteriaMapper;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.service.JobService;
import ua.findvacancies.common.service.JobServiceBuilder;
import ua.findvacancies.common.store.DataStore;
import ua.findvacancies.common.store.impl.SetBasedDataStore;

/**
 * Spring configuration class for defining beans from the common module.
 */
@Configuration
public class CommonModuleConfig {

    @Bean
    public JobService jobService() {
        return new JobServiceBuilder().build();
    }

    @Bean
    public JobSearchCriteriaMapper jobSearchCriteriaMapper() {
        return new JobSearchCriteriaMapper();
    }

    @Bean
    public DataStore<JobVacancy> dataStore() {
        return new SetBasedDataStore<>();
    }

}
