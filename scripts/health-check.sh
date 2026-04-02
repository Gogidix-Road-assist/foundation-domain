#!/bin/bash
################################################################################
# Foundation-Domain Health Check Script
# Purpose: Check if all services are running and healthy
# Usage: ./scripts/health-check.sh
################################################################################

set -e

BLUE='\033[0;34m'
GREEN='\033[0;32M'
RED='\033[0;31M'
NC='\033[0M'

echo -e "${BLUE}============================================"
echo " Foundation-Domain Health Check"
echo "============================================${NC}"
echo ""

# Service endpoints to check (adjust ports as needed)
declare -A SERVICES=(
    ["API Gateway"]="8304"
    ["Identity Service"]="8888"
    ["Config Service"]="8000"
    ["Monitoring Service"]="8091"
    ["Alerting Service"]="8083"
)

FAILED=0

for service in "${!SERVICES[@]}"; do
    port=${SERVICES[$service]}
    echo -n "Checking $service (port $port)... "

    if curl -f http://localhost:$port/health > /dev/null 2>&1; then
        echo -e "${GREEN}UP${NC}"
    elif curl -f http://localhost:$port/actuator/health > /dev/null 2>&1; then
        echo -e "${GREEN}UP${NC}"
    else
        echo -e "${RED}DOWN${NC}"
        ((FAILED++))
    fi
done

echo ""

if [ $FAILED -gt 0 ]; then
    echo -e "${RED}✗ $FAILED service(s) are down${NC}"
    exit 1
else
    echo -e "${GREEN}✓ All services are healthy${NC}"
    exit 0
fi
