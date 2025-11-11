package ua.findvacancies.thymeleafui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ua.findvacancies")
public class ThymeleafUIApplication {

    static void main(final String[] args) {
        SpringApplication.run(ThymeleafUIApplication.class, args);
    }
}
