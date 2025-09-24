# Common Module

This module serves as the foundational layer of the job scraping and export application. It encapsulates all shared components, core service interfaces, and the central logic for assembling the application's main services.

Currently, supports scraping from:
- [work.ua](https://www.work.ua)
- [djinni.co](https://djinni.co)

---

## Key Components

### Core Services
- **`JobService`**: Central class that encapsulates scraping and exporting logic.
- **`JobServiceBuilder`**: Implements the Builder pattern to construct `JobService` instances with flexible configuration.

### Scraping
- **`JobScraper`**: Interface for all scrapers
    - Concrete implementations:
        - `DjinniJobScraper`
        - `WorkUaJobScraper`
- **`JobScraperRegistry`**: Manages all available scraper instances
- **`JobScrapingOrchestrator`**: Coordinates scraping across multiple sites

### Exporting
- **`JobExportService`**: Interface for exporting job data
  - Concrete implementations:
      - `JsonExportService`
      - `XmlExportService`
      - `ExcelExportService`
- **`JobExportServiceRegistry`**: Registry for managing export services


### Other
- **`JobSite`**: Enum for supported job sites
- **`ExportFormat`**: Enum for supported export formats
- **`JobSearchCriteria`**: Represents search filters like keywords, date range, and pagination
- **`SetBasedDataStore`**: A generic data store implementation that uses a Set to ensure data uniqueness.

---

## Integration

This module is written in plain Java and can be reused as a `.jar` in other modules such as:
- REST API layer
- Thymeleaf-based UI
- CLI or batch processing tools

## Quick Start
This module uses [Apache Maven](https://maven.apache.org/) for building and testing.

### Maven
Add this to your consuming module's `pom.xml`:

```xml
<dependency>
    <groupId>ua.findvacancies</groupId>
    <artifactId>common</artifactId>
</dependency>
```

### Build the Module

To compile and package the `common` module into a `.jar` file:

```bash
mvn clean package -pl common
```

Skip Tests During Build

```bash
mvn clean package -pl common -DskipTests
```


### Run Tests

```bash
mvn test -pl common
```

Run Tests with Summary Output

```bash
mvn test -pl common -Dsurefire.printSummary=true
```