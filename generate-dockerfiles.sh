#!/bin/bash

# ==============================================================================
# Generate Dockerfiles for All Foundation Services
# ==============================================================================
# This script creates Dockerfiles and railway.toml files for all 78 services
#
# Usage:
#   chmod +x generate-dockerfiles.sh
#   ./generate-dockerfiles.sh
# ==============================================================================

set -e

# Colors
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${BLUE}============================================${NC}"
echo -e "${BLUE}Generating Dockerfiles for 78 Services${NC}"
echo -e "${BLUE}============================================${NC}"
echo ""

# Define all services by domain
declare -A SERVICES=(
    # Central-Configuration (8 services)
    ["central-configuration"]="config-service feature-flags-service country-localization-config-service dynamic-routing-config-service policy-configuration-service rate-limit-policy-service release-rollout-config-service tenancy-configuration-service"

    # AI-Services (27 services)
    ["ai-services"]="ai-anomaly-detection-service ai-chatbot-service ai-content-generator-service ai-data-prediction-service ai-document-analyzer-service ai-image-recognition-service ai-leads-generator-service ai-recommendation-engine-service ai-sentiment-analysis-service ai-speech-recognition-service ai-text-summarization-service ai-training-ml-service ai-translation-service ai-voice-assistant-service analytics-service customer-behaviour-analytics-service customer-support-chatbot-service data-analytics-service document-intelligence-service dynamic-pricing-service fraud-detection-service intelligent-dispatch-service predictive-maintenance-service recommendation-engine-service route-optimization-service sentiment-analysis-service vendors-product-listing-ai-service"

    # Centralized-Dashboard Java (3 services)
    ["centralized-dashboard-java"]="dashboard-configuration-service dashboard-analytics-service dashboard-reporting-service"

    # Shared-Infrastructure (39 services)
    ["shared-infrastructure"]="access-control-service alerting-service anti-fraud-rules-service anti-fraud-signals-service api-gateway api-keys-service audit-correlation-service billing-service courier-adapter-service currency-converter-service database-management-service data-privacy-consent-service event-audit-service geo-location-service idempotency-service identity-access-service identity-service insurer-adapter-service integration-adapters-service logging-aggregation-service maps-geocoding-adapter-service metrics-telemetry-service mfa-service notification-service onboarding-service payments-adapter-service payment-service policy-engine-service pricing-service rate-limiting-service reporting-read-model-service request-routing-service service-health-monitor-service service-registry-discovery session-token-service template-messaging-service tenant-org-service user-profile-service webhook-delivery-service"
)

# Counter
total_created=0

# Function to create Dockerfile for a Java service
create_java_dockerfile() {
    local service_dir=$1
    local service_name=$2

    cat > "$service_dir/Dockerfile" << 'EOF'
# Multi-stage Dockerfile for Spring Boot services
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Expose service port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Run application
ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=75.0", "-jar", "app.jar"]
EOF

    echo -e "${GREEN}✓${NC} Created Dockerfile for ${service_name}"
    ((total_created++))
}

# Function to create railway.toml for a service
create_railway_toml() {
    local service_dir=$1
    local service_name=$2
    local port=$3

    cat > "$service_dir/railway.toml" << EOF
[build]
builder = "DOCKERFILE"
dockerfilePath = "Dockerfile"

[deploy]
healthcheckPath = "/actuator/health"
healthcheckTimeout = 300
restartPolicyType = "ON_FAILURE"
numReplicas = 1

[[services]]
name = "${service_name}"
sourceDir = "/"

[[services.ports]]
port = ${port}
type = "HTTP"

[env]
PORT = "${port}"
SPRING_PROFILES_ACTIVE = "production"
EOF

    echo -e "${GREEN}✓${NC} Created railway.toml for ${service_name}"
}

# Function to create .railway.env.template
create_env_template() {
    local service_dir=$1
    local service_name=$2

    cat > "$service_dir/.railway.env.template" << 'EOF'
# Environment Variables Template for Railway Deployment
# Copy this file to Railway's environment variables

# Database Configuration
MONGODB_URI=mongodb+srv://<username>:<password>@cluster0.xxxxx.mongodb.net/rapid_assist_foundation?retryWrites=true&w=majority

# Service Configuration
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=8080

# Application Name
SPRING_APPLICATION_NAME=SERVICE_NAME

# Logging
LOGGING_LEVEL_COM_GOGIDIX=INFO
LOGGING_PATTERN_CONSOLE="%d{yyyy-MM-dd HH:mm:ss} - %msg%n"

# Management/Actuator
MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,metrics
MANAGEMENT_ENDPOINT_HEALTH_SHOW-DETAILS=always

# MongoDB Database Name
SPRING_DATA_MONGODB_DATABASE=rapid_assist_foundation
EOF

    # Replace SERVICE_NAME placeholder
    sed -i "s/SERVICE_NAME/$service_name/g" "$service_dir/.railway.env.template"

    echo -e "${GREEN}✓${NC} Created .railway.env.template for ${service_name}"
}

# Process Central-Configuration (Ports 8000-8007)
echo -e "${BLUE}Processing Central-Configuration Services...${NC}"
port=8000
for service in ${SERVICES["central-configuration"]}; do
    service_dir="Rapid-Assist/Foundation-Domain/central-configuration/Backend/Java/$service"
    if [ -d "$service_dir" ]; then
        create_java_dockerfile "$service_dir" "$service"
        create_railway_toml "$service_dir" "$service" $port
        create_env_template "$service_dir" "$service"
        ((port++))
    else
        echo -e "${YELLOW}⚠${NC}  Directory not found: $service_dir"
    fi
done
echo ""

# Process AI-Services (Ports 8100-8126)
echo -e "${BLUE}Processing AI-Services...${NC}"
port=8100
for service in ${SERVICES["ai-services"]}; do
    service_dir="Rapid-Assist/Foundation-Domain/ai-services/Backend/Java/$service"
    if [ -d "$service_dir" ]; then
        create_java_dockerfile "$service_dir" "$service"
        create_railway_toml "$service_dir" "$service" $port
        create_env_template "$service_dir" "$service"
        ((port++))
    else
        echo -e "${YELLOW}⚠${NC}  Directory not found: $service_dir"
    fi
done
echo ""

# Process Centralized-Dashboard Java (Ports 8200-8202)
echo -e "${BLUE}Processing Centralized-Dashboard Java Services...${NC}"
port=8200
for service in ${SERVICES["centralized-dashboard-java"]}; do
    service_dir="Rapid-Assist/Foundation-Domain/centralized-dashboard/Backend/Java/$service"
    if [ -d "$service_dir" ]; then
        create_java_dockerfile "$service_dir" "$service"
        create_railway_toml "$service_dir" "$service" $port
        create_env_template "$service_dir" "$service"
        ((port++))
    else
        echo -e "${YELLOW}⚠${NC}  Directory not found: $service_dir"
    fi
done
echo ""

# Process Shared-Infrastructure (Ports 8300-8338)
echo -e "${BLUE}Processing Shared-Infrastructure Services...${NC}"
port=8300
for service in ${SERVICES["shared-infrastructure"]}; do
    service_dir="Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/$service"
    if [ -d "$service_dir" ]; then
        create_java_dockerfile "$service_dir" "$service"
        create_railway_toml "$service_dir" "$service" $port
        create_env_template "$service_dir" "$service"
        ((port++))
    else
        echo -e "${YELLOW}⚠${NC}  Directory not found: $service_dir"
    fi
done
echo ""

# Create Dockerfile for Node.js service
echo -e "${BLUE}Processing Node.js Dashboard Service...${NC}"
node_service_dir="Rapid-Assist/Foundation-Domain/centralized-dashboard/Backend/Nodes/dashboard-aggregation-service"
if [ -d "$node_service_dir" ]; then
    cat > "$node_service_dir/Dockerfile" << 'EOF'
FROM node:18-alpine AS build

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci --only=production

# Copy source code
COPY . .

# Runtime stage
FROM node:18-alpine

WORKDIR /app

# Copy node_modules and source from build stage
COPY --from=build /app/node_modules ./node_modules
COPY --from=build /app . .

# Create non-root user
RUN addgroup -S node && adduser -S node -G node
USER node:node

# Expose port
EXPOSE 3000

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=30s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:3000/health || exit 1

# Start application
CMD ["node", "server.js"]
EOF

    cat > "$node_service_dir/railway.toml" << EOF
[build]
builder = "DOCKERFILE"
dockerfilePath = "Dockerfile"

[deploy]
healthcheckPath = "/health"
healthcheckTimeout = 300
restartPolicyType = "ON_FAILURE"
numReplicas = 1

[[services]]
name = "dashboard-aggregation-service"
sourceDir = "/"

[[services.ports]]
port = 3000
type = "HTTP"

[env]
PORT = "3000"
NODE_ENV = "production"
EOF

    echo -e "${GREEN}✓${NC} Created Dockerfile for dashboard-aggregation-service (Node.js)"
    ((total_created++))
else
    echo -e "${YELLOW}⚠${NC}  Directory not found: $node_service_dir"
fi
echo ""

# Summary
echo -e "${BLUE}============================================${NC}"
echo -e "${GREEN}Dockerfile Generation Complete!${NC}"
echo -e "${BLUE}============================================${NC}"
echo ""
echo "Total files created: $total_created services × 3 files = $((total_created * 3)) files"
echo ""
echo "Files created for each service:"
echo "  - Dockerfile (container configuration)"
echo "  - railway.toml (Railway deployment config)"
echo "  - .railway.env.template (environment variables template)"
echo ""
echo -e "${YELLOW}Next steps:${NC}"
echo "1. Review generated files"
echo "2. Update MONGODB_URI in .railway.env.template files"
echo "3. Push to dev branch: git push origin dev"
echo "4. GitHub Actions will deploy all 78 services"
echo ""
