#https://www.baeldung.com/ops/docker-cache-maven-dependencies
FROM maven:3.8.5-openjdk-17 as build
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME

ADD pom.xml $HOME

ADD findvacancies/pom.xml $HOME/findvacancies/pom.xml
ADD findvacancies-web-flux/pom.xml $HOME/findvacancies-web-flux/pom.xml
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

ENV \
    USER_NAME=loto1001 \
    USER_NAME_ID=1000 \
    USER_GROUP_ID=1000 \
    WORKDIR=/usr/src/java

WORKDIR ${WORKDIR}

# create and set non-root USER
RUN set -xe \
    && groupadd -g ${USER_GROUP_ID} ${USER_NAME} \
    && adduser --home ${WORKDIR} --shell /bin/bash --no-create-home --disabled-password \
    --gecos "" --ingroup ${USER_NAME} --uid ${USER_NAME_ID} ${USER_NAME} \
    && chown -R ${USER_NAME}:${USER_NAME} ${WORKDIR}

COPY --from=build --chown="${USER_NAME}:${USER_NAME}" /usr/app/findvacancies-web-vaadin/target/find-vacancies-vaadin*.jar /app/find-vacancies-vaadin.jar

EXPOSE 8080

ENTRYPOINT java -jar /app/find-vacancies-vaadin.jar

USER ${USERNAME}
