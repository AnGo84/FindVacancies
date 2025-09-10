# --- Stage 1: Build the application ---
# Use a Maven image with JDK 21 for building the Spring Boot application
FROM maven:3.9.6-eclipse-temurin-21 AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the parent pom.xml and the common module's pom.xml first
# This allows Maven to download dependencies in a separate layer,
# which can be cached if only pom.xml files change.
COPY pom.xml .
COPY common/pom.xml common/

# Copy the rest module's pom.xml
COPY rest/pom.xml rest/

# Download all dependencies for the 'common' and 'rest' modules
# -pl common,rest: Specifies to only build these projects
# -am: Also build modules that the specified projects depend on
RUN mvn dependency:go-offline -B -pl common,rest -am

# Copy the entire source code of the 'common' and 'rest' modules
COPY common/src common/src
COPY rest/src rest/src

# Package the application (this will build 'rest' and implicitly 'common')
# -DskipTests: Skips running tests during the Docker build (run tests separately in CI/CD)
# -pl rest: Specifies to build only the 'rest' project
# -am: Also build modules that 'rest' depends on (i.e., 'common')
RUN mvn clean install -DskipTests -pl rest -am

# --- Stage 2: Create the final runtime image ---
# Use a smaller JRE-only image for the final application, specifically JRE 21 based on Ubuntu Jammy
FROM eclipse-temurin:21-jre-jammy

# Set the working directory for the application
WORKDIR /app

# Copy the executable JAR from the builder stage
# The JAR will be located in the 'target' directory of the 'rest' module
COPY --from=builder /app/rest/target/*.jar app.jar

# Expose the port that the Spring Boot application runs on
EXPOSE 8080

# Define the entrypoint for the container
# This command runs the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
