# Multi-stage Dockerfile for identity-access-service
# Foundation Domain - Shared Infrastructure
# Optimized for production deployment

FROM eclipse-temurin:21-jdk-alpine AS builder

ARG SERVICE_NAME=identity-access-service
ARG SERVICE_VERSION=1.0.0
ARG BUILD_DATE

LABEL maintainer="gogidix@gogidix.com" \
      org.opencontainers.image.title="identity-access-service" \
      org.opencontainers.image.description="Foundation Domain Identity and Access Management" \
      org.opencontainers.image.version="${SERVICE_VERSION}" \
      org.opencontainers.image.created="${BUILD_DATE}"

RUN apk add --no-cache curl tar bash procps
WORKDIR /build

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw && ./mvnw dependency:go-offline -B
COPY src ./src

RUN ./mvnw clean package -DskipTests -B && \
    mv target/*.jar app.jar && \
    ls -lh app.jar

FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache curl tzdata ca-certificates && \
    addgroup -S spring && adduser -S spring -G spring
WORKDIR /app

COPY --from=builder /build/app.jar application.jar
RUN chown -R spring:spring /app && \
    ls -lh application.jar
USER spring

EXPOSE 8315

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8315/actuator/health || exit 1

ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UnlockExperimentalVMOptions \
               -XX:+UseStringDeduplication \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.jmx.enabled=false \
               -Dspring.profiles.active=production"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/application.jar"]
