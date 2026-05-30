# Dockerfile for common-domain-models
# Foundation Domain - Shared Models

# =============================================================================
# Stage 1: Builder
# =============================================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

ARG SERVICE_NAME=common-domain-models
ARG SERVICE_VERSION=1.0.0
ARG BUILD_DATE

LABEL maintainer="gogidix@gogidix.com" \
      org.opencontainers.image.title="common-domain-models" \
      org.opencontainers.image.description="Foundation Domain Common Domain Models Service" \
      org.opencontainers.image.version="${SERVICE_VERSION}" \
      org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.vendor="Gogidix" \
      org.opencontainers.image.source="https://github.com/gogidix/road-assist-saas"

RUN apk add --no-cache curl tar bash procps

WORKDIR /build

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

COPY src ./src

RUN ./mvnw clean package -DskipTests -B && \
    mv target/*.jar app.jar

# =============================================================================
# Stage 2: Runtime
# =============================================================================
FROM eclipse-temurin:21-jre-alpine

RUN apk add --no-cache curl tzdata ca-certificates

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

COPY --from=builder /build/app.jar application.jar

RUN chown -R spring:spring /app

USER spring

EXPOSE 8261

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8261/actuator/health || exit 1

ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+UseG1GC \
               -XX:+UnlockExperimentalVMOptions \
               -XX:+UseStringDeduplication \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=production"

ENV APP_ARGS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/application.jar $APP_ARGS"]
