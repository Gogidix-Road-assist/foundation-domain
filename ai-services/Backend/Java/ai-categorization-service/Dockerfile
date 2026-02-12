# =============================================================================
# Multi-stage Dockerfile for AI Categorization Service
# =============================================================================
#
# This Dockerfile uses a multi-stage build to:
# 1. Build the application in a Maven container
# 2. Run the application in a minimal JRE container
# 3. Minimize final image size
#
# Build: docker build -t ai-categorization-service:latest .
# Run: docker run -p 8080:8080 ai-categorization-service:latest
#
# =============================================================================

# Stage 1: Builder
FROM maven:3.9-eclipse-temurin-21 AS builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine

# Install curl for health check
RUN apk add --no-cache curl

# Set working directory
WORKDIR /app

# Add non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy jar from builder
COPY --from=builder /app/target/ai-categorization-service-*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM options optimized for containers
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -XX:InitialRAMPercentage=50.0"

# Application properties
ENV SPRING_PROFILES_ACTIVE=prod

# Entry point
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
