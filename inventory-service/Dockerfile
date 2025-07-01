# # syntax=docker/dockerfile:1
# FROM openjdk:17-jdk-slim
# WORKDIR /app
# COPY target/inventory-service-0.0.1-SNAPSHOT.jar app.jar
# EXPOSE 8082
# EXPOSE 9090
# ENTRYPOINT ["java", "-jar", "app.jar"]


FROM openjdk:17-jdk-slim
WORKDIR /app

# Install grpc_health_probe
# These commands need network access dudocker compose ring build time.
# Ensure your build environment has network access if building behind a proxy etc.
RUN apt-get update && apt-get install -y curl && \
    curl -LO https://github.com/grpc-ecosystem/grpc-health-probe/releases/download/v0.4.15/grpc_health_probe-linux-amd64 && \
    chmod +x grpc_health_probe-linux-amd64 && \
    mv grpc_health_probe-linux-amd64 /usr/local/bin/grpc_health_probe && \
    apt-get clean && rm -rf /var/lib/apt/lists/*

COPY target/inventory-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8082
EXPOSE 9090
ENTRYPOINT ["java", "-jar", "app.jar"]