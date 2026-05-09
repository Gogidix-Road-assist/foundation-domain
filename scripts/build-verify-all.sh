#!/bin/bash
################################################################################
# Foundation-Domain Build Verification Script
# Purpose: Build and verify all 98 Foundation-Domain services
# Usage: ./scripts/build-verify-all.sh
################################################################################

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Counters
TOTAL_SERVICES=0
SUCCESS_COUNT=0
FAILED_COUNT=0
FAILED_SERVICES=()

# Foundation directory
FOUNDATION_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Log file
LOG_FILE="${FOUNDATION_DIR}/build-verification.log"

# Start logging
echo "============================================" | tee "$LOG_FILE"
echo " Foundation-Domain Build Verification" | tee -a "$LOG_FILE"
echo "============================================" | tee -a "$LOG_FILE"
echo "Started: $(date)" | tee -a "$LOG_FILE"
echo "Log file: $LOG_FILE" | tee -a "$LOG_FILE"
echo "" | tee -a "$LOG_FILE"

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

log_warning() {
    echo -e "${YELLOW}⚠ $1${NC}"
}

build_service() {
    local service_path=$1
    local service_name=$(basename "$service_path")

    ((TOTAL_SERVICES++))

    echo -n "Building $service_name... "

    cd "$service_path"

    if mvn clean compile -DskipTests -q > /dev/null 2>&1; then
        log_success "$service_name"
        ((SUCCESS_COUNT++))
        return 0
    else
        log_error "$service_name (BUILD FAILED)"
        ((FAILED_COUNT++))
        FAILED_SERVICES+=("$service_name")
        return 1
    fi
}

################################################################################
# Phase 1: Shared Libraries (Must build first)
################################################################################

log_step "Phase 1: Building Shared Libraries"
echo "----------------------------------------"

SHARED_LIBS_DIR="${FOUNDATION_DIR}/shared-libraries/Backend/Java"

if [ -d "$SHARED_LIBS_DIR" ]; then
    for lib in "$SHARED_LIBS_DIR"/*; do
        if [ -d "$lib" ] && [ -f "$lib/pom.xml" ]; then
            lib_name=$(basename "$lib")
            echo -n "Building library: $lib_name... "

            cd "$lib"
            if mvn clean install -DskipTests -q > /dev/null 2>&1; then
                log_success "$lib_name"
                ((SUCCESS_COUNT++))
            else
                log_error "$lib_name (INSTALL FAILED)"
                ((FAILED_COUNT++))
                FAILED_SERVICES+=("shared-libraries/$lib_name")
            fi
            ((TOTAL_SERVICES++))
        fi
    done
else
    log_warning "Shared libraries directory not found at $SHARED_LIBS_DIR"
fi

################################################################################
# Phase 2: AI Services
################################################################################

echo ""
log_step "Phase 2: Building AI Services (31 services)"
echo "-----------------------------------------------"

AI_SERVICES_DIR="${FOUNDATION_DIR}/ai-services/Backend/Java"

if [ -d "$AI_SERVICES_DIR" ]; then
    for service in "$AI_SERVICES_DIR"/*; do
        if [ -d "$service" ] && [ -f "$service/pom.xml" ]; then
            build_service "$service"
        fi
    done
else
    log_warning "AI services directory not found at $AI_SERVICES_DIR"
fi

################################################################################
# Phase 3: Central Configuration Services
################################################################################

echo ""
log_step "Phase 3: Building Central Configuration Services (9 services)"
echo "----------------------------------------------------------------"

CONFIG_SERVICES_DIR="${FOUNDATION_DIR}/central-configuration/Backend/Java"

if [ -d "$CONFIG_SERVICES_DIR" ]; then
    for service in "$CONFIG_SERVICES_DIR"/*; do
        if [ -d "$service" ] && [ -f "$service/pom.xml" ]; then
            build_service "$service"
        fi
    done
else
    log_warning "Central configuration directory not found at $CONFIG_SERVICES_DIR"
fi

################################################################################
# Phase 4: Centralized Dashboard Services
################################################################################

echo ""
log_step "Phase 4: Building Centralized Dashboard Services (4 services)"
echo "---------------------------------------------------------------"

DASHBOARD_DIR="${FOUNDATION_DIR}/centralized-dashboard/Backend/Java"

if [ -d "$DASHBOARD_DIR" ]; then
    for service in "$DASHBOARD_DIR"/*; do
        if [ -d "$service" ] && [ -f "$service/pom.xml" ]; then
            build_service "$service"
        fi
    done

    # Check for Node.js service
    NODE_SERVICE="${FOUNDATION_DIR}/centralized-dashboard/Backend/Nodes/dashboard-aggregation-service"
    if [ -d "$NODE_SERVICE" ]; then
        ((TOTAL_SERVICES++))
        echo -n "Checking: dashboard-aggregation-service (Node.js)... "
        if [ -f "$NODE_SERVICE/package.json" ]; then
            log_success "Node.js service (package.json exists)"
            ((SUCCESS_COUNT++))
        else
            log_error "Node.js service (no package.json)"
            ((FAILED_COUNT++))
            FAILED_SERVICES+=("dashboard-aggregation-service")
        fi
    fi
else
    log_warning "Centralized dashboard directory not found at $DASHBOARD_DIR"
fi

################################################################################
# Phase 5: Orchestration Services (Pure Infrastructure)
################################################################################

echo ""
log_step "Phase 5: Building Orchestration Services (7 services)"
echo "--------------------------------------------------------"

ORCHESTRATION_DIR="${FOUNDATION_DIR}/orchestration-services/Backend/Java"

if [ -d "$ORCHESTRATION_DIR" ]; then
    for service in "$ORCHESTRATION_DIR"/*; do
        if [ -d "$service" ] && [ -f "$service/pom.xml" ]; then
            build_service "$service"
        fi
    done
else
    log_warning "Orchestration services directory not found at $ORCHESTRATION_DIR"
fi

################################################################################
# Phase 6: Shared Infrastructure Services
################################################################################

echo ""
log_step "Phase 6: Building Shared Infrastructure Services (38 services)"
echo "-----------------------------------------------------------------"

INFRA_DIR="${FOUNDATION_DIR}/shared-infrastructure/Backend/Java"

if [ -d "$INFRA_DIR" ]; then
    for service in "$INFRA_DIR"/*; do
        if [ -d "$service" ] && [ -f "$service/pom.xml" ]; then
            build_service "$service"
        fi
    done
else
    log_warning "Shared infrastructure directory not found at $INFRA_DIR"
fi

################################################################################
# Summary
################################################################################

echo ""
echo "============================================"
echo " Build Verification Summary"
echo "============================================"
echo "Total Services:  $TOTAL_SERVICES"
echo "Successful:      $SUCCESS_COUNT"
echo "Failed:          $FAILED_COUNT"
echo "Success Rate:    $(( SUCCESS_COUNT * 100 / TOTAL_SERVICES ))%"
echo ""

if [ $FAILED_COUNT -gt 0 ]; then
    echo "Failed Services:"
    for service in "${FAILED_SERVICES[@]}"; do
        echo "  - $service"
    done
    echo ""
    log_error "BUILD VERIFICATION FAILED"
    echo "Check the log file for details: $LOG_FILE"
    exit 1
else
    log_success "ALL SERVICES BUILT SUCCESSFULLY!"
    echo "Build verification completed at: $(date)"
    exit 0
fi
