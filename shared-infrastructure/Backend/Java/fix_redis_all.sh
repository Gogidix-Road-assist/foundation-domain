#!/bin/bash

# Fix all 27 failed services
cd Foundation-Domain/shared-infrastructure/Backend/Java

# List of services that use Redis and need the dependency
redis_services=(
    "api-gateway"
    "audit-correlation-service"
    "billing-service"
    "data-privacy-consent-service"
    "idempotency-service"
    "logging-aggregation-service"
    "metrics-telemetry-service"
    "notification-service"
    "payment-service"
    "payments-adapter-service"
    "policy-engine-service"
    "pricing-service"
    "rate-limiting-service"
    "reporting-read-model-service"
    "request-routing-service"
    "service-health-monitor-service"
    "tenant-org-service"
    "user-profile-service"
    "waf-policy-service"
)

echo "Fixing services..."
for service in "${redis_services[@]}"; do
    pom="$service/pom.xml"

    if [ ! -f "$pom" ]; then
        echo "  $service: no pom.xml"
        continue
    fi

    # Check if Redis dependency exists
    if grep -q "spring-boot-starter-data-redis" "$pom"; then
        echo "  $service: Redis dependency already exists"
    else
        echo "  $service: adding Redis dependency"

        # Add dependency before </dependencies>
        sed -i '/<\/dependencies>/i\
    <dependency>\
      <groupId>org.springframework.boot</groupId>\
      <artifactId>spring-boot-starter-data-redis</artifactId>\
    </dependency>\
' "$pom"
    fi
done

echo "Fix complete!"
