package ua.findvacancieswebflux;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.reactive.config.EnableWebFlux;


@EnableWebFlux
@SpringBootApplication
public class ApplicationWebFlux {

	public static void main(String[] args) {
		SpringApplication.run(ApplicationWebFlux.class, args);
	}

}
