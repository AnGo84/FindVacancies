package ua.findvacancieswebflux.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import ua.findvacancies.service.VacancyService;

@Configuration
public class AppConfig {

	@Bean
	public VacancyService getVacancyService() {
		return new VacancyService();
	}

	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasenames("classpath:messages/i18n/messages");
		messageSource.setFallbackToSystemLocale(true);
		messageSource.setDefaultEncoding("UTF-8");
		messageSource.setUseCodeAsDefaultMessage(true);
		return messageSource;
	}

}
