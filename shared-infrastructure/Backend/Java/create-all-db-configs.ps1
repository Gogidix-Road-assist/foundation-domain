# Complete MongoDB Database Configuration for All 41 Services
$services = @{
    "access-control-service" = "rapid_assist_access_control_dev"
    "alerting-service" = "rapid_assist_alerting_dev"
    "anti-fraud-rules-service" = "rapid_assist_anti_fraud_rules_dev"
    "anti-fraud-signals-service" = "rapid_assist_anti_fraud_signals_dev"
    "api-gateway" = "rapid_assist_gateway_dev"
    "api-keys-service" = "rapid_assist_api_keys_dev"
    "audit-correlation-service" = "rapid_assist_audit_correlation_dev"
    "billing-service" = "rapid_assist_billing_dev"
    "courier-adapter-service" = "rapid_assist_courier_adapter_dev"
    "currency-converter-service" = "rapid_assist_currency_converter_dev"
    "database-indexing-service" = "rapid_assist_db_indexing_dev"
    "database-management-service" = "rapid_assist_db_management_dev"
    "data-privacy-consent-service" = "rapid_assist_data_privacy_consent_dev"
    "event-audit-service" = "rapid_assist_event_audit_dev"
    "geo-location-service" = "rapid_assist_geo_dev"
    "idempotency-service" = "rapid_assist_idempotency_dev"
    "identity-access-service" = "rapid_assist_identity_access_dev"
    "identity-service" = "rapid_assist_identity_dev"
    "insurer-adapter-service" = "rapid_assist_insurer_adapter_dev"
    "integration-adapters-service" = "rapid_assist_integration_adapters_dev"
    "logging-aggregation-service" = "rapid_assist_logging_dev"
    "mfa-service" = "rapid_assist_mfa_dev"
    "notification-service" = "rapid_assist_notification_dev"
    "onboarding-service" = "rapid_assist_onboarding_dev"
    "payments-adapter-service" = "rapid_assist_payments_adapter_dev"
    "payment-service" = "rapid_assist_payment_dev"
    "policy-engine-service" = "rapid_assist_policy_dev"
    "pricing-service" = "rapid_assist_pricing_dev"
    "rate-limiting-service" = "rapid_assist_rate_limiting_dev"
    "reporting-read-model-service" = "rapid_assist_reporting_dev"
    "request-routing-service" = "rapid_assist_routing_dev"
    "service-health-monitor-service" = "rapid_assist_health_dev"
    "service-registry-discovery" = "rapid_assist_registry_dev"
    "session-token-service" = "rapid_assist_session_token_dev"
    "template-messaging-service" = "rapid_assist_template_messaging_dev"
    "tenant-org-service" = "rapid_assist_tenant_dev"
    "user-profile-service" = "rapid_assist_user_profile_dev"
    "waf-policy-service" = "rapid_assist_waf_dev"
    "webhook-delivery-service" = "rapid_assist_webhook_delivery_dev"
}

$baseDir = "C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain\shared-infrastructure\Backend\Java"

foreach ($service in $services.Keys) {
    $database = $services[$service]
    $configPath = "$baseDir\$service\src\main\resources\application-dev.yml"

    if (-not (Test-Path $configPath)) {
        Write-Host "Creating config for: $service -> $database"

        # Ensure directory exists
        $dir = Split-Path $configPath
        if (-not (Test-Path $dir)) {
            New-Item -ItemType Directory -Path $dir -Force | Out-Null
        }

        # Extract service name for Spring application.name
        $appName = $service -replace '-', ''

        $config = @"
# =============================================================================
# $service - Development Configuration
# =============================================================================

server:
  port: 8080
  servlet:
    context-path: /api/v1

spring:
  application:
    name: $service

  # MongoDB Configuration
  data:
    mongodb:
      host: localhost
      port: 27017
      database: $database
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

# Supabase Security Configuration (required for dev)
gogidix:
  security:
    supabase:
      issuer-uri: https://dummy-issuer.local
      audience: rapid-assist-dev

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
"@

        Set-Content -Path $configPath -Value $config
        Write-Host "  Created: $configPath" -ForegroundColor Green
    } else {
        Write-Host "Exists: $service" -ForegroundColor Yellow
    }
}

Write-Host ""
Write-Host "=================================================="
Write-Host "Database Configuration Complete"
Write-Host "=================================================="
Write-Host "Total Services Configured: $($services.Count)"
Write-Host "All services now have MongoDB database configuration"
