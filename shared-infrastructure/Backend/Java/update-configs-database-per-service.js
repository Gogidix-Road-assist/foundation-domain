const fs = require('fs');
const path = require('path');

const baseDir = 'C:\\Users\\HP\\Desktop\\Gogidix-Road-Assist-Saas\\Rapid-Assist\\Foundation-Domain\\shared-infrastructure\\Backend\\Java';

// Service to database mapping (Database Per Service pattern)
const services = {
    'access-control-service': { database: 'access_control_service_db', port: 8080 },
    'alerting-service': { database: 'alerting_service_db', port: 8081 },
    'anti-fraud-rules-service': { database: 'anti_fraud_rules_service_db', port: 8082 },
    'anti-fraud-signals-service': { database: 'anti_fraud_signals_service_db', port: 8083 },
    'api-gateway': { database: 'api_gateway_service_db', port: 8084 },
    'api-keys-service': { database: 'api_keys_service_db', port: 8085 },
    'audit-correlation-service': { database: 'audit_correlation_service_db', port: 8086 },
    'billing-service': { database: 'billing_service_db', port: 8087 },
    'courier-adapter-service': { database: 'courier_adapter_service_db', port: 8088 },
    'currency-converter-service': { database: 'currency_converter_service_db', port: 8089 },
    'database-indexing-service': { database: 'database_indexing_service_db', port: 8090 },
    'database-management-service': { database: 'database_management_service_db', port: 8091 },
    'data-privacy-consent-service': { database: 'data_privacy_consent_service_db', port: 8092 },
    'event-audit-service': { database: 'event_audit_service_db', port: 8093 },
    'geo-location-service': { database: 'geo_location_service_db', port: 8094 },
    'idempotency-service': { database: 'idempotency_service_db', port: 8095 },
    'identity-access-service': { database: 'identity_access_service_db', port: 8096 },
    'identity-service': { database: 'identity_service_db', port: 8097 },
    'insurer-adapter-service': { database: 'insurer_adapter_service_db', port: 8098 },
    'integration-adapters-service': { database: 'integration_adapters_service_db', port: 8099 },
    'logging-aggregation-service': { database: 'logging_aggregation_service_db', port: 8100 },
    'mfa-service': { database: 'mfa_service_db', port: 8101 },
    'notification-service': { database: 'notification_service_db', port: 8102 },
    'onboarding-service': { database: 'onboarding_service_db', port: 8103 },
    'payments-adapter-service': { database: 'payments_adapter_service_db', port: 8104 },
    'payment-service': { database: 'payment_service_db', port: 8105 },
    'policy-engine-service': { database: 'policy_engine_service_db', port: 8106 },
    'pricing-service': { database: 'pricing_service_db', port: 8107 },
    'rate-limiting-service': { database: 'rate_limiting_service_db', port: 8108 },
    'reporting-read-model-service': { database: 'reporting_read_model_service_db', port: 8109 },
    'request-routing-service': { database: 'request_routing_service_db', port: 8110 },
    'rapidassist': { database: 'rapidassist_service_db', port: 8111 },
    'service-health-monitor': { database: 'service_health_monitor_service_db', port: 8112 },
    'service-registry-discovery': { database: 'service_registry_service_db', port: 8113 },
    'session-token-service': { database: 'session_token_service_db', port: 8114 },
    'template-messaging-service': { database: 'template_messaging_service_db', port: 8115 },
    'tenant-org-service': { database: 'tenant_org_service_db', port: 8116 },
    'user-profile-service': { database: 'user_profile_service_db', port: 8117 },
    'waf-policy-service': { database: 'waf_policy_service_db', port: 8118 },
    'webhook-delivery-service': { database: 'webhook_delivery_service_db', port: 8119 }
};

// Common YAML configuration template
function generateYaml(service, database, port) {
    return `server:
  port: ${port}

spring:
  application:
    name: ${service}
  profiles:
    active: dev
  data:
    mongodb:
      host: localhost
      port: 27017
      database: ${database}
      auto-index-creation: true
      username: \${MONGODB_USERNAME:}
      password: \${MONGODB_PASSWORD:}
      authentication-database: \${MONGODB_AUTH_DB:admin}
  redis:
    host: \${REDIS_HOST:localhost}
    port: \${REDIS_PORT:6379}
    password: \${REDIS_PASSWORD:}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
  kafka:
    bootstrap-servers: \${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    consumer:
      group-id: ${service}_group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer

# Database Per Service Architecture
# Each service has its own dedicated database
# Service: ${service}
# Database: ${database}

management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true

logging:
  level:
    root: INFO
    com.gogidix.rapidassist.${service.replace(/-/g, '.')}: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/${service}.log

springdoc:
  api-docs:
    enabled: true
    path: /api-docs
  swagger-ui:
    enabled: true
    path: /swagger-ui.html

---
spring:
  config:
    activate:
      on-profile: dev
  data:
    mongodb:
      uri: mongodb://localhost:27017/${database}
  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true

logging:
  level:
    root: DEBUG
    com.gogidix.rapidassist.${service.replace(/-/g, '.')}: DEBUG
    org.springframework.data.mongodb: DEBUG

---
spring:
  config:
    activate:
      on-profile: prod
  data:
    mongodb:
      uri: \${MONGODB_URI:mongodb://localhost:27017/${database}}

logging:
  level:
    root: INFO
    com.gogidix.rapidassist.${service.replace(/-/g, '.')}: INFO
  file:
    name: /var/log/${service}/application.log
`;
}

let updatedCount = 0;
let createdCount = 0;

console.log('\n╔══════════════════════════════════════════════════════════════════════════════╗');
console.log('║          UPDATING SERVICE CONFIGURATIONS - DATABASE PER SERVICE             ║');
console.log('╚══════════════════════════════════════════════════════════════════════════════╝\n');

for (const [service, config] of Object.entries(services)) {
    const resourceDir = path.join(baseDir, service, 'src', 'main', 'resources');

    if (!fs.existsSync(resourceDir)) {
        fs.mkdirSync(resourceDir, { recursive: true });
    }

    const yamlContent = generateYaml(service, config.database, config.port);
    const configPath = path.join(resourceDir, 'application-dev.yml');

    const exists = fs.existsSync(configPath);
    fs.writeFileSync(configPath, yamlContent);

    if (exists) {
        updatedCount++;
        console.log(`✓ Updated: ${service} → Database: ${config.database} (Port: ${config.port})`);
    } else {
        createdCount++;
        console.log(`✓ Created: ${service} → Database: ${config.database} (Port: ${config.port})`);
    }
}

console.log(`\n╔══════════════════════════════════════════════════════════════════════════════╗`);
console.log(`║              ALL CONFIGURATIONS UPDATED SUCCESSFULLY!                     ║`);
console.log(`╚══════════════════════════════════════════════════════════════════════════════╝\n`);

console.log(`Summary:`);
console.log(`  Architecture Pattern: Database Per Service`);
console.log(`  Total Services: ${Object.keys(services).length}`);
console.log(`  Configurations Updated: ${updatedCount}`);
console.log(`  Configurations Created: ${createdCount}`);
console.log(`  Connection: mongodb://localhost:27017\n`);

console.log(`Service → Database Mapping:\n`);
Object.entries(services).forEach(([service, config], i) => {
    console.log(`  ${(i+1).toString().padStart(2)}. ${service.padEnd(35)} → ${config.database}`);
});

console.log(`\n✓ Each service now has its own dedicated database following microservices best practices.\n`);
