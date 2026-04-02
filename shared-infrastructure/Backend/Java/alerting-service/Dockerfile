# Dockerfile for alerting-service
# Foundation Domain - Shared Infrastructure

FROM eclipse-temurin:21-jdk-alpine AS builder

ARG SERVICE_NAME=alerting-service
ARG SERVICE_VERSION=1.0.0

LABEL maintainer="gogidix@gogidix.com" \
      org.opencontainers.image.title="alerting-service"

RUN apk add --no-cache curl tar bash
WORKDIR /build

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src ./src
RUN ./mvnw clean package -DskipTests -B && mv target/*.jar app.jar

FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache curl tzdata ca-certificates
RUN addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

COPY --from=builder /build/app.jar application.jar
RUN chown -R spring:spring /app
USER spring

EXPOSE 8301

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8301/actuator/health || exit 1

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/application.jar"]
