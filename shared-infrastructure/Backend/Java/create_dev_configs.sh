#!/bin/bash

# Array of services that need dev configuration
SERVICES=(
  "api-gateway:rapid_assist_gateway_dev:8304"
  "service-registry-discovery:rapid_assist_registry_dev:8761"
  "identity-service:rapid_assist_identity_dev:8081"
  "identity-access-service:rapid_assist_identity_access_dev:8082"
  "mfa-service:rapid_assist_mfa_dev:8083"
  "data-privacy-consent-service:rapid_assist_privacy_dev:8084"
  "waf-policy-service:rapid_assist_waf_dev:8085"
  "audit-correlation-service:rapid_assist_audit_dev:8086"
  "billing-service:rapid_assist_billing_dev:8087"
  "payment-service:rapid_assist_payment_dev:8088"
  "payments-adapter-service:rapid_assist_payments_adapter_dev:8089"
  "pricing-service:rapid_assist_pricing_dev:8090"
  "policy-engine-service:rapid_assist_policy_dev:8091"
  "insurer-adapter-service:rapid_assit_insurer_adapter_dev:8092"
  "notification-service:rapid_assist_notification_dev:8093"
  "reporting-read-model-service:rapid_assist_reporting_dev:8094"
  "tenant-org-service:rapid_assist_tenant_dev:8095"
  "user-profile-service:rapid_assist_user_profile_dev:8096"
  "geo-location-service:rapid_assist_geo_dev:8097"
  "database-indexing-service:rapid_assist_db_indexing_dev:8098"
  "database-management-service:rapid_assist_db_management_dev:8099"
  "idempotency-service:rapid_assist_idempotency_dev:8100"
  "integration-adapters-service:rapid_assist_integration_dev:8101"
  "logging-aggregation-service:rapid_assist_logging_dev:8102"
  "metrics-telemetry-service:rapid_assist_metrics_dev:8103"
  "request-routing-service:rapid_assist_routing_dev:8104"
  "service-health-monitor-service:rapid_assist_health_dev:8105"
)

for item in "${SERVICES[@]}"; do
  IFS=':' read -r SERVICE DB_NAME PORT <<< "$item"
  
  echo "Creating dev config for $SERVICE..."
  
  cat > "$SERVICE/src/main/resources/application-dev.yml" << DEVYMLEOF
# =============================================================================
# ${SERVICE} - Development Configuration
# =============================================================================

server:
  port: $PORT
  servlet:
    context-path: /api/v1

spring:
  application:
    name: ${SERVICE}

  # MongoDB Configuration
  data:
    mongodb:
      host: localhost
      port: 27017
      database: ${DB_NAME}
      auto-index-creation: true

  # Redis Configuration
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8

  # Kafka Configuration
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializers.JsonSerializer

# Actuator Configuration
management:
  endpoints:
    web:
      exposure:
        include: '*'
  endpoint:
    health:
      show-details: always

# OpenAPI Configuration
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
  show-actuator: true

# Logging Configuration
logging:
  level:
    com.gogidix.rapidassist: DEBUG
    org.springframework.data.mongodb: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg [tenantId=%X{tenantId}][%X{traceId},%X{spanId}]%n"
DEVYMLEOF

  echo "Created: $SERVICE/src/main/resources/application-dev.yml"
done

echo "All development configurations created successfully!"
