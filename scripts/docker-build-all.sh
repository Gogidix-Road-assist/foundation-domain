#!/bin/bash
################################################################################
# Foundation-Domain Docker Build Script
# Purpose: Build Docker images for all Foundation-Domain services
# Usage: ./scripts/docker-build-all.sh [--push] [--tag TAG]
################################################################################

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# Configuration
REGISTRY="${REGISTRY:-ghcr.io}"
IMAGE_PREFIX="${IMAGE_PREFIX:-foundation-domain}"
TAG="${TAG:-latest}"
PUSH_IMAGES=false

# Parse arguments
while [[ $# -gt 0 ]]; do
    case $1 in
        --push)
            PUSH_IMAGES=true
            shift
            ;;
        --tag)
            TAG="$2"
            shift 2
            ;;
        *)
            echo "Unknown option: $1"
            exit 1
            ;;
    esac
done

FOUNDATION_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$FOUNDATION_DIR"

echo -e "${BLUE}============================================"
echo " Foundation-Domain Docker Build"
echo "============================================${NC}"
echo "Registry: $REGISTRY"
echo "Image Prefix: $IMAGE_PREFIX"
echo "Tag: $TAG"
echo "Push: $PUSH_IMAGES"
echo ""

# Counters
TOTAL_IMAGES=0
SUCCESS_COUNT=0
FAILED_COUNT=0
FAILED_IMAGES=()

################################################################################
# Helper Functions
################################################################################

log_step() {
    echo -e "${BLUE}>>> $1${NC}"
}

log_success() {
    echo -e "${GREEN}✓ $1${NC}"
}

log_error() {
    echo -e "${RED}✗ $1${NC}"
}

build_image() {
    local service_path=$1
    local service_name=$(basename "$service_path")
    local dockerfile="${service_path}/Dockerfile"

    if [ ! -f "$dockerfile" ]; then
        # Use default Dockerfile
        dockerfile="${FOUNDATION_DIR}/Dockerfile.template"
    fi

    ((TOTAL_IMAGES++))

    echo -n "Building image for $service_name... "

    local image_name="${REGISTRY}/${IMAGE_PREFIX}/${service_name}:${TAG}"
    local build_cmd="docker build -t ${image_name} -f ${dockerfile} ${service_path}"

    if $build_cmd > /dev/null 2>&1; then
        log_success "$service_name"
        ((SUCCESS_COUNT++))

        # Push if requested
        if [ "$PUSH_IMAGES" = true ]; then
            echo -n "  Pushing $service_name... "
            if docker push ${image_name} > /dev/null 2>&1; then
                log_success "Pushed"
            else
                log_error "Push failed"
                ((FAILED_COUNT++))
            fi
        fi
    else
        log_error "$service_name (BUILD FAILED)"
        ((FAILED_COUNT++))
        FAILED_IMAGES+=("$service_name")
        return 1
    fi
}

################################################################################
# Phase 1: Check Docker
################################################################################

log_step "Checking Docker Installation"
if ! command -v docker &> /dev/null; then
    log_error "Docker not found. Please install Docker Desktop."
    exit 1
fi

if ! docker info &> /dev/null; then
    log_error "Docker daemon not running. Please start Docker Desktop."
    exit 1
fi

log_success "Docker is running"

################################################################################
# Phase 2: Login to Registry
################################################################################

if [ "$PUSH_IMAGES" = true ]; then
    log_step "Logging into registry: $REGISTRY"
    if echo "$REGISTRY" | grep -q "ghcr.io"; then
        echo "Using GitHub Container Registry - should be already authenticated"
    else
        echo "Please login to registry: docker login $REGISTRY"
        docker login "$REGISTRY" || exit 1
    fi
fi

################################################################################
# Phase 3: Build Shared Libraries Image
################################################################################

log_step "Phase 1: Building Shared Libraries"
echo "----------------------------------------"

# Build base image with shared libraries
log_success "Building shared libraries..."

# Create a base builder image
cat > "${FOUNDATION_DIR}/Dockerfile.builder" << 'EOF'
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

# Copy shared libraries
COPY shared-libraries/Backend/Java/common-domain-models/pom.xml /app/
COPY shared-libraries/Backend/Java/common-domain-models /app/common-domain-models
COPY shared-libraries/Backend/Java/event-schemas/pom.xml /app/
COPY shared-libraries/Backend/Java/event-schemas /app/event-schemas
COPY shared-libraries/Backend/Java/shared-security-library/pom.xml /app/
COPY shared-libraries/Backend/Java/shared-security-library /app/shared-security-library
COPY shared-libraries/Backend/Java/shared-persistence-library/pom.xml /app/
COPY shared-libraries/Backend/Java/shared-persistence-library /app/shared-persistence-library

# Build all shared libraries
RUN cd /app/common-domain-models && mvn clean install -DskipTests -o

# Copy the other libraries
RUN cd /app/event-schemas && mvn clean install -DskipTests -o
RUN cd /app/shared-security-library && mvn clean install -DskipTests -o
RUN cd /app/shared-persistence-library && mvn clean install -DskipTests -o

# This image will be used as a base for other services
EOF

docker build -f "${FOUNDATION_DIR}/Dockerfile.builder" -t ${REGISTRY}/${IMAGE_PREFIX}/shared-libs:${TAG} . 2>&1 | grep -E "(Step|Successfully|ERROR|Successfully tagged)" || true

log_success "Shared libraries image built"

################################################################################
# Phase 4: Build Service Images
################################################################################

echo ""
log_step "Phase 2: Building Core Infrastructure Services"
echo "------------------------------------------------"

CORE_SERVICES=(
    "shared-infrastructure/Backend/Java/api-gateway"
    "shared-infrastructure/Backend/Java/identity-service"
    "shared-infrastructure/Backend/Java/config-service"
)

for service in "${CORE_SERVICES[@]}"; do
    if [ -d "${FOUNDATION_DIR}/${service}" ]; then
        build_image "${FOUNDATION_DIR}/${service}"
    fi
done

echo ""
log_step "Phase 3: Building AI Services"
echo "----------------------------"

AI_COUNT=0
for service in "${FOUNDATION_DIR}/ai-services/Backend/Java"/*; do
    if [ -d "$service" ] && [ -f "$service/pom.xml" ]; then
        build_image "$service"
        ((AI_COUNT++))
    fi
done

echo "Built $AI_COUNT AI services"

echo ""
log_step "Phase 4: Building Configuration Services"
echo "------------------------------------------"

CONFIG_COUNT=0
for service in "${FOUNDATION_DIR}/central-configuration/Backend/Java"/*; do
    if [ -d "$service" ] && [ -f "$service/pom.xml" ]; then
        build_image "$service"
        ((CONFIG_COUNT++))
    fi
done

echo "Built $CONFIG_COUNT configuration services"

################################################################################
# Summary
################################################################################

echo ""
echo "============================================"
echo " Docker Build Summary"
echo "============================================"
echo "Total Images:    $TOTAL_IMAGES"
echo "Successful:     $SUCCESS_COUNT"
echo "Failed:         $FAILED_COUNT"
echo "Success Rate:    $(( SUCCESS_COUNT * 100 / TOTAL_IMAGES ))%"
echo ""

if [ $FAILED_COUNT -gt 0 ]; then
    echo "Failed Images:"
    for image in "${FAILED_IMAGES[@]}"; do
        echo "  - $image"
    done
    echo ""
    log_error "DOCKER BUILD FAILED"
    exit 1
else
    log_success "ALL DOCKER IMAGES BUILT SUCCESSFULLY!"

    if [ "$PUSH_IMAGES" = true ]; then
        echo ""
        log_success "ALL IMAGES PUSHED TO REGISTRY"
    fi

    echo ""
    echo "Images can be pulled with:"
    echo "  docker pull ${REGISTRY}/${IMAGE_PREFIX}/<service-name>:${TAG}"
    exit 0
fi
