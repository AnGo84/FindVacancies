package ua.findvacancies.thymeleafxtmxui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ua.findvacancies")
public class ThymeleafHTMXUIApplication {

    static void main(final String[] args) {
        SpringApplication.run(ThymeleafHTMXUIApplication.class, args);
    }

}
