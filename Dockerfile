FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY aiven-ca.pem /tmp/aiven-ca.pem
RUN keytool -import -trustcacerts \
    -keystore $JAVA_HOME/lib/security/cacerts \
    -storepass changeit \
    -noprompt \
    -alias aiven-root-ca \
    -file /tmp/aiven-ca.pem
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]