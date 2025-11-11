package ua.findvacancies.rest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.findvacancies.common.service.JobService;
import ua.findvacancies.common.service.JobServiceBuilder;

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
