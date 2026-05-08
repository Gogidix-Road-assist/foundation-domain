#!/bin/bash

# Services that need Redis dependency
services=(
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

for service in "${services[@]}"; do
    pom="$service/pom.xml"
    
    if [ ! -f "$pom" ]; then
        continue
    fi
    
    # Check if RedisConfig exists
    if [ -f "$service/src/main/java/com/gogidix/rapidassist/api/gateway/infrastructure/config/RedisConfig.java" ] || \
       [ -f "$service/src/main/java/com/gogidix/rapidassist/*/infrastructure/config/RedisConfig.java" ] || \
       find "$service/src" -name "*Redis*.java" -type f -quiet; then
        
        # Check if Redis dependency is missing
        if ! grep -q "spring-boot-starter-data-redis" "$pom"; then
            echo "Adding Redis dependency to $service"
            
            # Add before </dependencies>
            sed -i '/<\/dependencies>/i\
    <dependency>\
      <groupId>org.springframework.boot</groupId>\
      <artifactId>spring-boot-starter-data-redis</artifactId>\
    </dependency>\
' "$pom"
        fi
    fi
done

echo "Done adding Redis dependencies"
