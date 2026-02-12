# Dockerfile for user-profile-service
# Foundation Domain - Shared Infrastructure

# =============================================================================
# Stage 1: Builder
# =============================================================================
FROM eclipse-temurin:21-jdk-alpine AS builder

# Build arguments
ARG SERVICE_NAME=user-profile-service
ARG SERVICE_VERSION=1.0.0
ARG BUILD_DATE

# Labels
LABEL maintainer="gogidix@gogidix.com" \
      org.opencontainers.image.title="user-profile-service" \
      org.opencontainers.image.description="Foundation Domain User Profile Service" \
      org.opencontainers.image.version="${SERVICE_VERSION}" \
      org.opencontainers.image.created="${BUILD_DATE}" \
      org.opencontainers.image.vendor="Gogidix" \
      org.opencontainers.image.source="https://github.com/gogidix/road-assist-saas"

# Install build dependencies
RUN apk add --no-cache curl tar bash procps

# Set working directory
WORKDIR /build

# Copy Maven wrapper and pom.xml first (for better caching)
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Download dependencies (cached layer)
RUN chmod +x mvnw && ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean package -DskipTests -B && \
    mv target/*.jar app.jar

# =============================================================================
# Stage 2: Runtime
# =============================================================================
FROM eclipse-temurin:21-jre-alpine

# Install runtime dependencies
RUN apk add --no-cache curl tzdata ca-certificates

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Set working directory
WORKDIR /app

# Copy jar from builder
COPY --from=builder /build/app.jar application.jar

# Change ownership
RUN chown -R spring:spring /app

# Switch to non-root user
USER spring

# Expose port
EXPOSE 8260

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8260/actuator/health || exit 1

# JVM options for production
ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:InitialRAMPercentage=50.0 \
               -XX:+UseG1GC \
               -XX:+UnlockExperimentalVMOptions \
               -XX:+UseStringDeduplication \
               -Djava.security.egd=file:/dev/./urandom \
               -Dspring.profiles.active=production"

# Application arguments
ENV APP_ARGS=""

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/application.jar $APP_ARGS"]
