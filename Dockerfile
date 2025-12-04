# Stage 1: build with Maven
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copy pom.xml and download dependencies first (cache layer)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build JAR
RUN mvn clean package -DskipTests

# Stage 2: run with lightweight JRE
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose backend port
EXPOSE 8585

# Run the Spring Boot app
ENTRYPOINT ["java", "-jar", "app.jar"]
