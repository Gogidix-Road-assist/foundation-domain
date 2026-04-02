#!/bin/bash

# Foundation-Domain Complete Build Script
# Compiles all 80+ services across all Foundation-Domain modules

set -e  # Exit on error

TOTAL_SERVICES=0
SUCCESSFUL=0
FAILED=0
FAILED_SERVICES=()

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo "========================================="
echo "Foundation-Domain Complete Build"
echo "========================================="
echo ""

# Function to compile a service
compile_service() {
    local service_path=$1
    local service_name=$(basename "$service_path")

    TOTAL_SERVICES=$((TOTAL_SERVICES + 1))

    echo -e "${YELLOW}[${TOTAL_SERVICES}]${NC} Compiling: $service_name"

    if cd "$service_path" && mvn clean compile -DskipTests -q; then
        echo -e "${GREEN}✓${NC} SUCCESS: $service_name"
        SUCCESSFUL=$((SUCCESSFUL + 1))
        return 0
    else
        echo -e "${RED}✗${NC} FAILED: $service_name"
        FAILED=$((FAILED + 1))
        FAILED_SERVICES+=("$service_name")
        return 1
    fi
}

# Export function for subshells
export -f compile_service
export TOTAL_SERVICES SUCCESSFUL FAILED

echo "Phase 1: Shared Infrastructure Core Services"
echo "----------------------------------------------"

# Critical infrastructure services first
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/api-gateway"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/identity-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/access-control-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/identity-access-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/mfa-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/session-token-service"

echo ""
echo "Phase 2: Central Configuration Services"
echo "----------------------------------------"

compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/central-configuration/Backend/Java/config-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/central-configuration/Backend/Java/feature-flags-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/central-configuration/Backend/Java/policy-configuration-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/central-configuration/Backend/Java/rate-limit-policy-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/central-configuration/Backend/Java/tenancy-configuration-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/central-configuration/Backend/Java/country-localization-config-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/central-configuration/Backend/Java/dynamic-routing-config-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/central-configuration/Backend/Java/release-rollout-config-service"

echo ""
echo "Phase 3: Shared Infrastructure - API Management"
echo "-----------------------------------------------"

compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/api-keys-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/rate-limiting-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/request-routing-service"

echo ""
echo "Phase 4: Shared Infrastructure - Monitoring & Observability"
echo "-----------------------------------------------------------"

compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/service-health-monitor-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/metrics-telemetry-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/logging-aggregation-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/audit-correlation-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/event-audit-service"

echo ""
echo "Phase 5: Shared Infrastructure - Business Services"
echo "--------------------------------------------------"

compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/billing-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/payment-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/pricing-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/shared-infrastructure/Backend/Java/payments-adapter-service"

echo ""
echo "Phase 6: AI Services - Core Analytics"
echo "-------------------------------------"

compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/ai-services/Backend/Java/analytics-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/ai-services/Backend/Java/data-analytics-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/ai-services/Backend/Java/customer-behaviour-analytics-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/ai-services/Backend/Java/sentiment-analysis-service"

echo ""
echo "Phase 7: AI Services - Machine Learning"
echo "----------------------------------------"

compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/ai-services/Backend/Java/ai-training-ml-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/ai-services/Backend/Java/ai-anomaly-detection-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/ai-services/Backend/Java/ai-data-prediction-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/ai-services/Backend/Java/fraud-detection-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/ai-services/Backend/Java/predictive-maintenance-service"

echo ""
echo "Phase 8: Centralized Dashboard"
echo "-------------------------------"

compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/centralized-dashboard/Backend/Java/dashboard-analytics-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/centralized-dashboard/Backend/Java/dashboard-configuration-service"
compile_service "c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/centralized-dashboard/Backend/Java/dashboard-reporting-service"

echo ""
echo "========================================="
echo "Build Summary"
echo "========================================="
echo "Total Services: $TOTAL_SERVICES"
echo -e "${GREEN}Successful: $SUCCESSFUL${NC}"
echo -e "${RED}Failed: $FAILED${NC}"

if [ $FAILED -gt 0 ]; then
    echo ""
    echo -e "${RED}Failed Services:${NC}"
    for service in "${FAILED_SERVICES[@]}"; do
        echo "  - $service"
    done
    exit 1
else
    echo ""
    echo -e "${GREEN}✓ All services compiled successfully!${NC}"
    exit 0
fi
