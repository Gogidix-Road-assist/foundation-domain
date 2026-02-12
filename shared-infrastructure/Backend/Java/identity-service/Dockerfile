# Dockerfile for identity-service
# Foundation Domain - Shared Infrastructure

FROM eclipse-temurin:21-jdk-alpine AS builder

ARG SERVICE_NAME=identity-service
ARG SERVICE_VERSION=1.0.0
ARG BUILD_DATE

LABEL maintainer="gogidix@gogidix.com" \
      org.opencontainers.image.title="identity-service" \
      org.opencontainers.image.description="Foundation Domain Identity and Authentication Service" \
      org.opencontainers.image.version="${SERVICE_VERSION}"

RUN apk add --no-cache curl tar bash procps
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

EXPOSE 8316

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8316/actuator/health || exit 1

ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=production"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/application.jar"]
