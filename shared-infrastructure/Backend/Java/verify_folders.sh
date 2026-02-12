#!/bin/bash

SERVICES=(
  "api-gateway"
  "service-registry-discovery"
  "access-control-service"
  "identity-service"
  "identity-access-service"
  "mfa-service"
  "data-privacy-consent-service"
  "waf-policy-service"
  "audit-correlation-service"
  "billing-service"
  "payment-service"
  "payments-adapter-service"
  "pricing-service"
  "policy-engine-service"
  "insurer-adapter-service"
  "notification-service"
  "reporting-read-model-service"
  "tenant-org-service"
  "user-profile-service"
  "geo-location-service"
  "database-indexing-service"
  "database-management-service"
  "idempotency-service"
  "integration-adapters-service"
  "logging-aggregation-service"
  "metrics-telemetry-service"
  "request-routing-service"
  "service-health-monitor-service"
)

echo "==================================================================="
echo "VERIFICATION REPORT: Empty Folder Detection in All Services"
echo "==================================================================="
echo ""

EMPTY_FOLDERS=0
TOTAL_ISSUES=0

for SERVICE in "${SERVICES[@]}"; do
  echo "Checking: $SERVICE"
  
  # Check if main source directories exist
  if [ ! -d "$SERVICE/src/main/java" ]; then
    echo "  ❌ MISSING: src/main/java"
    TOTAL_ISSUES=$((TOTAL_ISSUES + 1))
  fi
  
  if [ ! -d "$SERVICE/src/test/java" ]; then
    echo "  ❌ MISSING: src/test/java"
    TOTAL_ISSUES=$((TOTAL_ISSUES + 1))
  fi
  
  # Count Java files in main source
  MAIN_COUNT=$(find "$SERVICE/src/main/java" -name "*.java" 2>/dev/null | wc -l)
  TEST_COUNT=$(find "$SERVICE/src/test/java" -name "*.java" 2>/dev/null | wc -l)
  
  echo "  → Main Java files: $MAIN_COUNT"
  echo "  → Test Java files: $TEST_COUNT"
  
  if [ "$MAIN_COUNT" -eq 0 ]; then
    echo "  ⚠️  WARNING: No Java files in src/main/java"
    EMPTY_FOLDERS=$((EMPTY_FOLDERS + 1))
    TOTAL_ISSUES=$((TOTAL_ISSUES + 1))
  fi
  
  echo ""
done

echo "==================================================================="
echo "SUMMARY"
echo "==================================================================="
echo "Total Issues Found: $TOTAL_ISSUES"
echo "Services with Empty Main Source: $EMPTY_FOLDERS"
echo ""

if [ $TOTAL_ISSUES -eq 0 ]; then
  echo "✅ SUCCESS: All services have proper structure with source files"
else
  echo "⚠️  WARNING: Found $TOTAL_ISSUES issues that need attention"
fi

echo "==================================================================="
