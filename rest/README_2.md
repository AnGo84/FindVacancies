# 📄 Job Vacancy Parser REST Service

This module provides a RESTful API for scraping job vacancies from various online job boards and exporting them in multiple formats. It serves as the main entry point for interacting with the core job parsing logic defined in the common module.

---

## 📚 Table of Contents

- [Description](#description)
- [Technologies Used](#technologies-used)
- [How to Run](#how-to-run)
- [API Endpoints](#api-endpoints)
  - [Get Job Vacancies](#get-job-vacancies)
  - [Export Job Vacancies](#export-job-vacancies)
  - [Get Available Job Sites](#get-available-job-sites)
  - [Get Available Export Formats](#get-available-export-formats)
- [Swagger UI](#swagger-ui)
- [Health Check](#health-check)
- [Docker Support](#docker-support)

---

## 📝 Description

The REST module exposes endpoints that allow users to:

- Initiate job scraping based on keywords, ignored terms, date filters, and job sites.
- Retrieve parsed job vacancies in JSON format.
- Export vacancies in multiple formats: JSON, XML, XLSX.
- Query supported job sites and export formats.

Powered by **Spring Boot**, it connects seamlessly to the common module for parsing and export logic.

---

## 🛠 Technologies Used

- Java 17+
- Spring Boot 3.3.1
- Maven
- SpringDoc OpenAPI (Swagger UI support)
- Lombok
- Jsoup *(via common module)*
- Jackson *(for JSON/XML serialization)*
- Apache POI *(for Excel export)*

---

## 🚀 How to Run

### 🔧 Prerequisites

- Java 17+ installed
- Maven installed

### 📦 Build the Project

```bash
cd job-parser-app/
mvn clean install
```

### ▶️ Run the REST Service

```bash
cd rest
mvn spring-boot:run
```

App will run at: `http://localhost:8080`

---

## 🌐 API Endpoints

> All endpoints are under `/api/v1/jobs`

---

### 🔍 Get Job Vacancies

- **Endpoint:** `GET /api/v1/jobs`
- **Description:** Scrapes jobs based on parameters.

#### Query Parameters

| Name                  | Type            | Description |
|-----------------------|-----------------|-------------|
| `keywords`            | List<String>    | e.g. `java,spring` |
| `ignoredWords`        | List<String>    | e.g. `junior,trainee` |
| `lastNDays`           | Integer         | Default: `5` |
| `maxPagesToScrape`    | Integer         | Default: `2` |
| `minJobElementsPerPage` | Integer       | Default: `3` |
| `sites`               | Set<JobSite>    | e.g. `djinni.co,work.ua` |

#### Example

```http
GET /api/v1/jobs?keywords=java,spring&lastNDays=7&sites=djinni.co
```

**Response:** `200 OK` — JSON array of `JobVacancy`

---

### 📤 Export Job Vacancies

- **Endpoint:** `GET /api/v1/jobs/export/{format}`
- **Description:** Scrapes and exports jobs in selected format.

#### Path Variable

| Name     | Type   | Description |
|----------|--------|-------------|
| `format` | String | `json`, `xml`, `xlsx` |

#### Query Parameters

Same as **Get Job Vacancies**

#### Example

```http
GET /api/v1/jobs/export/xlsx?keywords=python&lastNDays=10
```

**Response:** `200 OK` — Downloadable Excel file (`.xlsx`) with proper headers

---

### 🌍 Get Available Job Sites

- **Endpoint:** `GET /api/v1/jobs/sites`
- **Description:** Lists supported job boards.

#### Example

```http
GET /api/v1/jobs/sites
```

**Response:** `200 OK`
```json
["djinni.co", "work.ua"]
```

---

### 💾 Get Available Export Formats

- **Endpoint:** `GET /api/v1/jobs/export/formats`
- **Description:** Lists supported export formats.

#### Example

```http
GET /api/v1/jobs/export/formats
```

**Response:** `200 OK`
```json
["json", "xml", "xlsx"]
```

---

## 📘 Swagger UI

You can browse and test all endpoints at:

```url
http://localhost:8080/swagger-ui.html
```

---

## ❤️ Health Check

Spring Boot Actuator exposes service health info.

- **Endpoint:** `GET /actuator/health`

#### Example Response

```json
{
  "status": "UP",
  "components": {
    "diskSpace": {
      "status": "UP",
      "details": {
        "total": 1000000000,
        "free": 500000000,
        "threshold": 10000000,
        "exists": true
      }
    },
    "ping": {
      "status": "UP"
    }
  }
}
```

---

## 🐳 Docker Support

This module includes Docker support via:

- `Dockerfile`
- `docker-compose.yml`

### 🧱 Building and Running

```bash
docker-compose up --build
```

To run without rebuilding:

```bash
docker-compose up
```

Run in background:

```bash
docker-compose up -d
```

### 🛑 Stopping

```bash
docker-compose down
```

Once running, access the service at:

```url
http://localhost:8080
```
