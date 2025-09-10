# Job Parser WebFlux Module

This module provides a reactive, non-blocking API for scraping job vacancies from supported job sites and exporting them in various formats. Built with Spring Boot WebFlux, it integrates with the shared `common` module to orchestrate scraping and exporting logic asynchronously.

---

## 🚀 Features

- Reactive REST API using Spring WebFlux
- Streaming job vacancy results via `Flux`
- Integration with `common` module for core scraping/export logic
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

!!!!!
And define the version property:
<properties>
<!-- Replace with the actual version of the common module -->
<common.version>SET_ME</common.version>
</properties>



## 🛠️ Build & Run
Build the WebFlux Module
mvn clean install -pl webflux -am


Run the Application
mvn spring-boot:run -pl webflux


Or run the packaged JAR:
java -jar webflux/target/find-vacancies-webflux.jar



🧪 Testing
This module includes tests for:
- API endpoints (JobScraperWebfluxController)
- Actuator health and info endpoints
  To run tests:
  mvn test -pl webflux



🐳 Docker Instructions
This module includes a multi-stage Dockerfile for efficient builds.
Build the Image
docker build -t job-parser-webflux .


Run the Container
docker run -p 8080:8080 job-parser-webflux



📚 API Documentation
OpenAPI documentation is available at:
http://localhost:8080/swagger-ui.html



🔧 Configuration
Basic configuration is provided via application.yaml (not shown here). Actuator endpoints are enabled for health and info monitoring.

📂 REST Endpoints
GET /api/v1/jobs
Scrapes job vacancies based on search criteria and returns them as a reactive stream (Flux<JobVacancy>).
Query Parameters:
- keywords (required): Comma-separated keywords (e.g., java,spring)
- ignoredWords: Words to exclude (e.g., junior,trainee)
- lastNDays: Filter by recency (e.g., 5)
- maxPagesToScrape: Limit pages per site
- sites: Comma-separated site names (e.g., djinni.co,work.ua)
  GET /api/v1/jobs/sites
  Returns a list of all supported job sites as a reactive Mono<Set<String>>.
