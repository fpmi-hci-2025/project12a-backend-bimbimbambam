FROM openjdk:17-jdk-alpine
WORKDIR /app
COPY target/demo.jar demo.jar
CMD ["java", "-jar", "demo.jar"]
