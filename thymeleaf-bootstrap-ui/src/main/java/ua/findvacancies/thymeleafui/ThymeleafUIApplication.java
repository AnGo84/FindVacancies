package ua.findvacancies.thymeleafui;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "ua.findvacancies")
public class ThymeleafUIApplication {

    public static void main(String[] args) {
        SpringApplication.run(ThymeleafUIApplication.class, args);
    }
}
