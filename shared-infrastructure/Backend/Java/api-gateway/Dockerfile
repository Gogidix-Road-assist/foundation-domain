# Multi-stage Dockerfile for api-gateway
# Foundation Domain - Shared Infrastructure
# Optimized for production deployment with best practices

# Stage 1: Build stage with full JDK
FROM eclipse-temurin:21-jdk-alpine AS builder

ARG SERVICE_NAME=api-gateway
ARG SERVICE_VERSION=1.0.0
ARG BUILD_DATE

LABEL maintainer="gogidix@gogidix.com" \
      org.opencontainers.image.title="api-gateway" \
      org.opencontainers.image.description="Foundation Domain API Gateway - Multi-tenant routing and rate limiting" \
      org.opencontainers.image.version="${SERVICE_VERSION}" \
      org.opencontainers.image.created="${BUILD_DATE}"

# Install build dependencies
RUN apk add --no-cache curl tar bash procps

WORKDIR /build

# Download dependencies first (for better layer caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source and build
COPY src ./src

# Build application and create optimized JAR
RUN ./mvnw clean package -DskipTests -B && \
    mv target/*.jar app.jar && \
    # Verify the JAR was created
    ls -lh app.jar

# Stage 2: Runtime stage with minimal JRE
FROM eclipse-temurin:21-jre-alpine

# Install runtime dependencies only
RUN apk add --no-cache curl tzdata ca-certificates && \
    # Create non-root user
    addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy JAR from builder stage
COPY --from=builder /build/app.jar application.jar

# Set proper ownership
RUN chown -R spring:spring /app && \
    # Verify the JAR was copied
    ls -lh application.jar

# Switch to non-root user
USER spring

# Expose service port
EXPOSE 8304

# Health check configuration
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8304/actuator/health || exit 1

# JVM optimizations for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+UnlockExperimentalVMOptions \
               -XX:+UseStringDeduplication \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.jmx.enabled=false \
               -Dspring.profiles.active=production"

# Application entrypoint
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/application.jar"]

