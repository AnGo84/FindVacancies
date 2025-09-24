# Job Parser REST Module

This module provides a RESTful API for scraping job vacancies from supported job sites and exporting them in various formats. It is built with Spring Boot and integrates with the `common` module, which contains the core scraping and export logic.

---

## 🚀 Features

- REST endpoints for job scraping
- Integration with `common` module for reusable services
- OpenAPI documentation via SpringDoc
- Health and info endpoints via Spring Boot Actuator
- Docker-ready for containerized deployment

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

### Build the REST Module

``` Bash
mvn clean install -pl rest -am
```

### Run the Application

``` Bash
mvn spring-boot:run -pl rest
```

Or run the packaged JAR:

``` Bash
java -jar rest/target/find-vacancies-rest.jar
```

---

## 🧪 Testing
This module uses **JUnit** 5 and **Spring Boot Test**.

To run tests:

``` Bash 
mvn test -pl rest
```

---

## 🐳 Docker Instructions

This module includes a multi-stage Dockerfile for efficient builds.

### Build the Image

``` Bash 
docker build -f rest\Dockerfile -t job-parser-rest .
```

### Run the Container

``` Bash 
docker run -p 8080:8080 job-parser-rest
```

---

## 📚 API Documentation

OpenAPI documentation is available at:

```
http://localhost:8080/swagger-ui.html
```

---

## 🔧 Configuration
Basic configuration is provided via **application.yml**:

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

## 📂 REST Endpoints

``` 
GET /api/v1/jobs
```

Scrapes job vacancies based on search criteria.

**Query Parameters**:
- keywords (required): Comma-separated keywords (e.g., java,spring)
- ignoredWords: Words to exclude (e.g., junior,trainee)
- lastNDays: Filter by recency (default: 5)
- maxPagesToScrape: Limit pages per site
- sites: Comma-separated site names (e.g., djinni.co,work.ua)

```
GET /api/v1/jobs/sites
```
Returns a list of all supported job sites.
