# ---------- Stage 1: Build the JAR ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only the pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy the rest of the project and build it
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run the app ----------
FROM openjdk:17-jdk-alpine
WORKDIR /app

# Copy the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (Render detects it automatically, but it’s good practice)
EXPOSE 8080

# Run the JAR
ENTRYPOINT ["java", "-jar", "app.jar"]
