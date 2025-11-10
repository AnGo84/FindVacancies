# Stage 1: Build with Maven and Java 21
FROM maven:3.9.4-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy the full project (including parent pom and all modules)
COPY . .

# Build only the current module and its dependencies
RUN mvn clean package -pl thymeleaf-tailwind-htmx-ui -am -DskipTests

# Stage 2: Run with JDK 21
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/thymeleaf-tailwind-htmx-ui/target/*.jar app.jar

# Install Chrome (minimal)
RUN apt-get update && apt-get install -y wget gnupg \
 && wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
 && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
 && apt-get update && apt-get install -y google-chrome-stable \
 && apt-get clean && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]