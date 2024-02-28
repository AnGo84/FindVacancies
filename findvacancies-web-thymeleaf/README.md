# FindVacancies

Web application based on Spring Boot with Thymeleaf template engine for searching for vacancies from popular sites:
- [work.ua](https://www.work.ua/)
- [DOU.ua](https://dou.ua/)
- [grc.ua](https://grc.ua/)
- [djinni.co](https://djinni.co/)

To search, you must select one or more sites, set the last days period and enter keywords.
If necessary, some results can be excluded for some words. Just put the ** "-" ** in front of them. For example, to exclude junior positions for vacancies for "java developer", keywords line will be "java developer -junior".

Results table can be exporting to **XML** or **XLSX** files.

<details><summary><b>Screenshot with example:</b></summary>

| ![](src/main/resources/images/Search_result.jpg) |
|:------------------------------------------------:|
|               *Page with results*                |

| ![](src/main/resources/images/Search_screen.jpg) |
|:------------------------------------------------:|
|                 *Waiting dialog*                 |

| ![](src/main/resources/images/Invalid_params.jpg) |
|:-------------------------------------------------:|
|                *Validation errors*                |
</details>

### Technology stack:
- Framework: Spring boot 3
- Build: Maven
- Containers: Docker, Docker-compose
- Front: thymeleaf, Bootstrap
- Utils: Actuator

### **For work needs:**
- IDE
- JDK 17
- Maven

### **Current version:**
Release 2.3

### **Create Docker build**
From project root:
```
docker build -f findvacancies-web-thymeleaf\Dockerfile -t fv-thymeleaf .
```

### **Release notes:**
**Version Release 2.3:** Add Thymeleaf module
