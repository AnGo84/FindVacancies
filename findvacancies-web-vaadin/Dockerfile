#https://www.baeldung.com/ops/docker-cache-maven-dependencies
FROM maven:3.8.5-openjdk-17 as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME

ADD pom.xml $HOME

ADD findvacancies/pom.xml $HOME/findvacancies/pom.xml
ADD findvacancies-web-jsp/pom.xml $HOME/findvacancies-web-jsp/pom.xml
ADD findvacancies-web-rest/pom.xml $HOME/findvacancies-web-rest/pom.xml
ADD findvacancies-web-thymeleaf/pom.xml $HOME/findvacancies-web-thymeleaf/pom.xml
ADD findvacancies-web-vaadin/pom.xml $HOME/findvacancies-web-vaadin/pom.xml

RUN mvn -pl findvacancies verify --fail-never
ADD findvacancies $HOME/findvacancies
RUN mvn -pl findvacancies install

RUN mvn -pl findvacancies-web-vaadin verify --fail-never
ADD findvacancies-web-vaadin $HOME/findvacancies-web-vaadin
RUN mvn -pl findvacancies,findvacancies-web-vaadin package


FROM openjdk:17-jdk-slim
COPY --from=build /usr/app/findvacancies-web-vaadin/target/find-vacancies-vaadin*.jar /app/find-vacancies-vaadin.jar

EXPOSE 8080

ENTRYPOINT java -jar /app/find-vacancies-vaadin.jar
