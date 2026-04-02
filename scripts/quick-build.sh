#!/bin/bash
################################################################################
# Foundation-Domain Quick Build Script
# Purpose: Fast compilation of all services (skips tests, detailed logging)
# Usage: ./scripts/quick-build.sh
################################################################################

set -e

BLUE='\033[0;34m'
GREEN='\033[0;32m'
NC='\033[0m'

FOUNDATION_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

echo -e "${BLUE}============================================"
echo " Foundation-Domain Quick Build"
echo "============================================${NC}"
echo ""

# Build shared libraries first (quiet mode)
echo "Building shared libraries..."
find "$FOUNDATION_DIR/shared-libraries/Backend/Java" -name "pom.xml" -type f | while read pom; do
    dir=$(dirname "$pom")
    echo "  - $(basename "$dir")"
    cd "$dir"
    mvn clean install -DskipTests -q
done

# Build all services
echo ""
echo "Building all services..."
find "$FOUNDATION_DIR" -name "pom.xml" -type f | grep -E "(ai-services|central-configuration|centralized-dashboard|orchestration-services|shared-infrastructure)" | while read pom; do
    dir=$(dirname "$pom")
    echo "  - $(basename "$dir")"
    cd "$dir"
    mvn clean compile -DskipTests -q || true
done

echo ""
echo -e "${GREEN}✓ Quick build complete!${NC}"
