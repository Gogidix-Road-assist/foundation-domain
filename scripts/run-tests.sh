#!/bin/bash
################################################################################
# Foundation-Domain Test Runner
# Purpose: Run tests for all services
# Usage: ./scripts/run-tests.sh [service-name]
################################################################################

set -e

BLUE='\033[0;34m'
GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

FOUNDATION_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

if [ -n "$1" ]; then
    # Test specific service
    SERVICE_NAME=$1
    echo "Running tests for $SERVICE_NAME..."

    SERVICE_PATH=$(find "$FOUNDATION_DIR" -type d -name "$SERVICE_NAME")
    if [ -z "$SERVICE_PATH" ]; then
        echo "Service $SERVICE_NAME not found"
        exit 1
    fi

    cd "$SERVICE_PATH"
    mvn test
else
    # Run all tests
    echo -e "${BLUE}============================================"
    echo " Running All Foundation-Domain Tests"
    echo "============================================${NC}"
    echo ""

    TOTAL_TESTS=0
    PASSED_TESTS=0

    # Find all services with tests
    find "$FOUNDATION_DIR" -name "pom.xml" -type f | grep -E "(ai-services|central-configuration|centralized-dashboard|orchestration-services|shared-infrastructure)" | while read pom; do
        dir=$(dirname "$pom")
        service_name=$(basename "$dir")

        cd "$dir"

        # Check if tests exist
        if [ -d "src/test" ] && [ "$(find src/test -name "*.java" | wc -l)" -gt 0 ]; then
            ((TOTAL_TESTS++))
            echo "Testing $service_name..."

            if mvn test -q; then
                echo -e "${GREEN}✓ $service_name${NC}"
                ((PASSED_TESTS++))
            else
                echo -e "${RED}✗ $service_name${NC}"
            fi
        fi
    done

    echo ""
    echo "Test Results: $PASSED_TESTS/$TOTAL_TESTS passed"

    if [ $PASSED_TESTS -lt $TOTAL_TESTS ]; then
        exit 1
    fi
fi

echo ""
echo -e "${GREEN}✓ Tests complete!${NC}"
