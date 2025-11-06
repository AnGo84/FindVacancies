# Job Vacancy Vaadin UI Module

This module provides a Vaadin Flow UI for scraping job vacancies from supported job sites and exporting them in various formats. It is built with Spring Boot and integrates with the `common` module, which contains the core scraping and export logic.
---

## 🚀 Features

- **Vaadin Flow Integration**: It uses Vaadin Flow to build the user interface with server-side Java, eliminating the need for separate frontend development.
- **Lumo Theme & Utility Classes**: The UI is styled using Vaadin's Lumo Theme and utility classes for a responsive, modern design.
- **Form Validation**: The module includes form validation via jakarta.validation to ensure the integrity of user input.
- **Multilingual Support**: It is configured for multilingual support using spring.messages.basename.
- **Core Logic Integration**: It integrates with the common module to access core scraping and export services.
- **Docker Integration**: Docker-ready for containerized deployment

---

## Maven Setup

To use the `common` module in this project, add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>ua.findvacancies</groupId>
    <artifactId>common</artifactId>
    <version>${common.version}</version>
</dependency>
```

And define the version property:

```xml
<properties>
    <!-- Replace with the actual version of the common module -->
    <common.version>SET_ME</common.version>
</properties>
```

For using the latest version remove `<version>` tag

---

## 🛠️ Build & Run

### Build the Vaadin UI Module

``` Bash
mvn clean install -pl vaadin-ui -am
```

### Run the Application

``` Bash
mvn spring-boot:run -pl vaadin-ui
```

Or run the packaged JAR:

``` Bash
java -jar vaadin-ui/target/find-vacancies-vaadin.jar
```

---

## 🧪 Testing
This module uses **JUnit** 5 and **Spring Boot Test**.

To run tests:

``` Bash 
mvn test -pl thymeleaf-bootstrap-ui
```

---

## 🐳 Docker Instructions

This module includes a multi-stage Dockerfile for efficient builds.

### Build the Image

``` Bash 
docker build -f vaadin-ui\Dockerfile -t job-parser-vaadin-ui .
```

### Run the Container

``` Bash 
docker run -p 8080:8080 job-parser-vaadin-ui
```

---

## 📚 API Documentation

OpenAPI documentation is available at:

```
http://localhost:8080/swagger-ui.html
```

---

## 🔧 Configuration
Basic configuration is provided via the `application.yml` file, which enables the `health` and `info` endpoints from Spring Boot Actuator. The health endpoint is configured to always show details. The `application.yml` also sets up multilingual support by defining `spring.messages.basename`.

``` yaml
 management:
  endpoints:
    web:
      exposure:
        include: health,info
  endpoint:
    health:
      show-details: always
 ```

## 📂 UI Endpoints

| Endpoint | Method | Description                                |
|----------|--------|--------------------------------------------|
| `/`      | GET    | Displays the main view                     |
| `/actuator/health`      | GET    | Spring Boot Actuator health check endpoint |
| `/actuator/info`      | GET    | Spring Boot Actuator information endpoint  |
