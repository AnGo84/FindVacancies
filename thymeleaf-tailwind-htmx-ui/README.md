# Job Parser Thymeleaf HTMX Tailwind UI Module

This module provides a modern, fast, and responsive web user interface for scraping job vacancies from supported job sites and exporting them in various formats. It is built with Spring Boot, using **Thymeleaf** for server-side rendering, **HTMX** for dynamic partial updates, and **Tailwind CSS** for styling. It integrates with the `common` module, which contains the core scraping and export logic.

---

## 🚀 Features

- **Thymeleaf Integration**: Uses Thymeleaf as a server-side Java template engine to render the web pages.
- **Modern UI Stack**: Styled using **Tailwind CSS** for utility-first design and enhanced with **Alpine.js** for simple client-side component logic (e.g., dropdowns and collapse menus).
- **HTMX for Dynamics**: Utilizes **HTMX** to perform partial page swaps (like search results and form validation) without full page reloads, providing a dynamic, SPA-like feel while maintaining server-side rendering benefits.
- **Compact Styling**: UI elements are styled to be compact and space-efficient, adopting a "small" (sm) sizing convention.
- **Form Validation**: Includes server-side form validation via `jakarta.validation` to ensure the integrity of user input.
- **Multilingual Support**: Configured for multilingual support using `spring.messages.basename`.
- **Health and Info Endpoints**: Uses Spring Boot Actuator to expose `health` and `info` endpoints for monitoring.
- **Core Logic Integration**: Integrates with the `common` module to access core scraping and export services.
- **Docker Integration**: Docker-ready for containerized deployment.
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

## 🛠️ Build

### Build the Thymeleaf UI Module

``` Bash
mvn clean install -pl thymeleaf-bootstrap-htmx-ui -am
```

## 💻 Running the Application
This module is a standard Spring Boot application.

### From your IDE:
Run the main application class `ThymeleafHTMXUIApplication.java`.

### From Maven:

``` Bash
mvn spring-boot:run -pl thymeleaf-bootstrap-htmx-ui
```

### From the Command Line:
1. Package the application (if needed):

    ``` Bash
    mvn clean package
    ```

2. Run the JAR:

``` Bash
java -jar thymeleaf-bootstrap-htmx-ui/target/find-vacancies-thymeleaf.jar
```

---

## 🧪 Testing
This module uses **JUnit 5** and **Spring Boot Test** with **MockMvc** for integration testing. 
New tests cover Actuator health checks, HTMX partial updates, i18n switching, and file export functionality.

To run tests:

``` Bash 
mvn test -pl thymeleaf-bootstrap-htmx-ui
```

---

## 🐳 Docker Instructions

This module includes a multi-stage Dockerfile for efficient builds.

### Build the Image

``` Bash 
docker build -f thymeleaf-bootstrap-htmx-ui\Dockerfile -t job-parser-thymeleaf-bootstrap-htmx-ui .
```

### Run the Container

``` Bash 
docker run -p 8080:8080 job-parser-thymeleaf-bootstrap-htmx-ui
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

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/`      | GET    | Displays the main page with the search form |
| `/search`      | POST   | Performs a job search based on the provided criteria and swaps the results fragment |
| `/export`      | GET    | Triggers a file download (JSON, XML, or XLSX) of the current search results |
| `/actuator/health`      | GET    | Spring Boot Actuator health check endpoint |
| `/actuator/info`      | GET    | Spring Boot Actuator information endpoint |
