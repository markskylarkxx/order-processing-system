# syntax=docker/dockerfile:1
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/inventory-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]
