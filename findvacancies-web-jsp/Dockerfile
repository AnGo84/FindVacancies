#https://www.baeldung.com/ops/docker-cache-maven-dependencies
FROM maven:3.8.5-openjdk-17 as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME

ADD pom.xml $HOME

ADD findvacancies/pom.xml $HOME/findvacancies/pom.xml
ADD findvacancies-web-jsp/pom.xml $HOME/findvacancies-web-jsp/pom.xml
ADD findvacancies-web-jsp/tomcat/context.xml $HOME/findvacancies-web-jsp/tomcat/context.xml
ADD findvacancies-web-jsp/tomcat/tomcat-users.xml $HOME/findvacancies-web-jsp/tomcat/tomcat-users.xml

ADD findvacancies-web-rest/pom.xml $HOME/findvacancies-web-rest/pom.xml
ADD findvacancies-web-thymeleaf/pom.xml $HOME/findvacancies-web-thymeleaf/pom.xml
ADD findvacancies-web-vaadin/pom.xml $HOME/findvacancies-web-vaadin/pom.xml

RUN mvn -pl findvacancies verify --fail-never
ADD findvacancies $HOME/findvacancies
RUN mvn -pl findvacancies install

RUN mvn -pl findvacancies-web-jsp verify --fail-never
ADD findvacancies-web-jsp $HOME/findvacancies-web-jsp
RUN mvn -pl findvacancies,findvacancies-web-jsp package

#FROM openjdk:17-jdk-slim
#COPY --from=build /usr/app/findvacancies-web-jsp/target/find-vacancies-jsp*.jar /app/find-vacancies-jsp.jar

FROM tomcat:10.1-jdk17-openjdk-slim
RUN mv /usr/local/tomcat/webapps.dist/* /usr/local/tomcat/webapps/

# works both webapps and webapps-javaee
#COPY --from=build /usr/app/findvacancies-web-jsp/target/find-vacancies-jsp*.war /usr/local/tomcat/webapps/
COPY --from=build /usr/app/findvacancies-web-jsp/target/find-vacancies-jsp*.war /usr/local/tomcat/webapps-javaee/

COPY --from=build /usr/app/findvacancies-web-jsp/tomcat/tomcat-users.xml /usr/local/tomcat/conf/
COPY --from=build /usr/app/findvacancies-web-jsp/tomcat/context.xml /usr/local/tomcat/webapps/manager/META-INF/

EXPOSE 8080

#ENTRYPOINT java -jar /app/find-vacancies-jsp.jar
CMD ["catalina.sh", "run"]
