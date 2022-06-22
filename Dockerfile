#FROM openjdk:8-jdk-alpine
FROM openjdk:18-ea-11-jdk-alpine
RUN addgroup -S spring_docker && adduser -S spring_docker -G spring_docker
USER spring_docker:spring_docker
VOLUME /tmp
ARG JAR_FILE=target/FindVacancies.war
COPY ${JAR_FILE} findVacancies.war
ENTRYPOINT ["java","-jar","/findVacancies.war", "--logging.path=/logs/logs", "--spring.config.location=classpath:/application.properties"]