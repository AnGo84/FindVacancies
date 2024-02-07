# FindVacanciesfor
Build status: [![build_status](https://travis-ci.com/AnGo84/FindVacancies.svg?branch=master)](https://travis-ci.com/AnGo84/FindVacancies.svg)
[![<AnGo84>](https://circleci.com/gh/AnGo84/FindVacancies.svg?style=svg)](https://app.circleci.com/pipelines/github/AnGo84/FindVacancies)

[//]: # ([![BCH compliance]&#40;https://bettercodehub.com/edge/badge/AnGo84/FindVacancies?branch=master&#41;]&#40;https://bettercodehub.com/&#41;)

The application for searching for vacancies from the popular sites:
- [work.ua](https://www.work.ua/)
- [DOU.ua](https://dou.ua/)
- [grc.ua](https://grc.ua/)
- [djinni.co](https://djinni.co/)

Separated on modules:
- [Base logic](findvacancies/README.md)
- [Web application with JSP](findvacancies-web-jsp/README.md)
- [Web application with Vaadin](findvacancies-web-vaadin/README.md)
- [REST application](findvacancies-web-rest/README.md)

### **For work needs:**
- IDE
- JDK 17
- Maven

### **Current version:**
Release 2.3

### **Release notes:**
**Version Release 2.3:** Switch to Spring Boot 3
**Version Release 2.2:** Add Vaadin module
**Version Release 2.1:** Switch to Java 17, separate on modules 
**Version Release 2.0:** Spring MVC replacing with Spring Boot, add Java Configuration, add JUnit 5
**Version Release 1.1:** Spring MVC app with XML Configuration, JSP, Bootstrap 3, JUnit 4

### **ToDo:**
- add Docker files
- add Docker-compose
- fix jsp run in container
