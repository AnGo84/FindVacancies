# --- Stage 1: Build the application ---
# Use a Maven image with JDK 21 for building the Spring Boot application
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the parent pom.xml and the common module's pom.xml first for caching
COPY pom.xml .
COPY common/pom.xml common/

# Copy the Vaadin module's pom.xml
COPY vaadin-ui/pom.xml vaadin-ui/

# Download all dependencies for the 'common' and 'vaadin-ui' modules
RUN mvn dependency:go-offline -B -pl common,vaadin-ui -am

# Copy the entire source code of the 'common' and 'vaadin-ui' modules
COPY common/src common/src
COPY vaadin-ui/src vaadin-ui/src

# Package the application (builds 'vaadin-ui' and 'common')
# The resulting JAR will be named 'find-vacancies-vaadin.jar'
RUN mvn clean install -DskipTests -pl vaadin-ui -am

# --- Stage 2: Create the final runtime image ---
# Use a smaller JRE-only image for the final application
FROM eclipse-temurin:21-jre-jammy

# Set the working directory for the application
WORKDIR /app

# Copy the executable JAR from the builder stage
# Uses the finalName "find-vacancies-vaadin" from the pom.xml
COPY --from=builder /app/vaadin-ui/target/find-vacancies-vaadin.jar app.jar

# Expose the port that the Spring Boot application runs on
EXPOSE 8080

# Define the entrypoint for the container
ENTRYPOINT ["java", "-jar", "app.jar"]