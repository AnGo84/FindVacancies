package ua.findvacancies.webflux;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ua.findvacancies")
@OpenAPIDefinition(
        info = @Info(
                title = "Job Vacancy Parser API",
                version = "1.0",
                description = "WebFlux API for scraping and exporting job vacancies from various online job boards.",
                contact = @Contact(name = "AnGo")
        )
)
public class JobParserWebfluxApplication {

    public static void main(String[] args) {
        SpringApplication.run(JobParserWebfluxApplication.class, args);
    }

}

