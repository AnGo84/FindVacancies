package ua.findvacancies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//public class Application extends SpringBootServletInitializer {
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /*@Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(Application.class);
    }*/
}
