# Stage 1: Build webflux module and its dependencies
FROM maven:3.9.11-eclipse-temurin-25 AS builder

WORKDIR /app

# Copy the entire project (root pom + all modules)
COPY . .

# Build only webflux and its dependencies
RUN mvn clean package -pl webflux -am -DskipTests

# Stage 2: Runtime image
FROM eclipse-temurin:25-jre

WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/webflux/target/*.jar app.jar

# Install Chrome (minimal)
RUN apt-get update && apt-get install -y wget gnupg \
 && wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - \
 && echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google-chrome.list \
 && apt-get update && apt-get install -y google-chrome-stable \
 && apt-get clean && rm -rf /var/lib/apt/lists/*

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]