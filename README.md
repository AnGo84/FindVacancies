# 🧭 FindVacancies

[![Build Status (Travis)](https://travis-ci.com/AnGo84/FindVacancies.svg?branch=master)](https://travis-ci.com/AnGo84/FindVacancies)
[![Build Status (CircleCI)](https://circleci.com/gh/AnGo84/FindVacancies.svg?style=svg)](https://app.circleci.com/pipelines/github/AnGo84/FindVacancies)

A modular Spring Boot application for searching job vacancies from:

- 🌐 [work.ua](https://www.work.ua/)
- 🌐 [dou.ua](https://dou.ua/)
- 🌐 [djinni.co](https://djinni.co/)

---

## 📦 Modules

| Module | Description |
|--------|-------------|
| [`findvacancies`](findvacancies/README.md) | Core logic and service layer |
| [`findvacancies-web-jsp`](findvacancies-web-jsp/README.md) | Web UI using JSP |
| [`findvacancies-web-vaadin`](findvacancies-web-vaadin/README.md) | Web UI using Vaadin |
| [`findvacancies-web-rest`](findvacancies-web-rest/README.md) | REST API |
| [`findvacancies-web-flux`](findvacancies-web-flux/README.md) | Reactive WebFlux API |

---

## 🚀 Getting Started

### Prerequisites

- Java 25+
- Maven 3.9+
- Docker (for local deployment)
- IDE (IntelliJ IDEA recommended)

### Local Deployment

```bash
docker-compose up -d
```

---

## ⚙️ Common Maven Commands

| Task | Command |
|------|---------|
| Run unit tests | `mvn test` |
| Run integration tests | `mvn verify` |
| Build all modules | `mvn clean install` |
| Check dependency updates | `mvn versions:display-dependency-updates` |
| Run OWASP dependency check | `mvn org.owasp:dependency-check-maven:check` |
| Generate site documentation | `mvn site` |

---

## 🧪 Testing

- Unit tests: via **Surefire Plugin**
- Integration tests: via **Failsafe Plugin**
- Selenium-based UI tests supported
- ByteBuddy agent configured for runtime instrumentation

---

## 📌 Release History

| Version | Highlights |
|---------|------------|
| 3.0 | 🔄 Migrated to Java 25, refactored module structure |
| 2.3 | 🚀 Upgraded to Spring Boot 3 |
| 2.2 | ➕ Added Vaadin module |
| 2.1 | 🧩 Modularized project, Java 17 |
| 2.0 | 🔧 Migrated to Spring Boot, Java Config, JUnit 5 |
| 1.1 | 🧪 Spring MVC with XML, JSP, Bootstrap 3, JUnit 4 |

---

## 🛠️ ToDo

- [ ] Switch to stable Spring Boot 4
- [ ] Fix UI styling and responsiveness
