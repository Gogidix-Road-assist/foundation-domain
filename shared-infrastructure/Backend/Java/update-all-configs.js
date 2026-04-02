const fs = require('fs');
const path = require('path');

const baseDir = 'C:\\Users\\HP\\Desktop\\Gogidix-Road-Assist-Saas\\Rapid-Assist\\Foundation-Domain\\shared-infrastructure\\Backend\\Java';

// All 41 services with their ports
const services = {
    'access-control-service': { port: 8080 },
    'alerting-service': { port: 8081 },
    'anti-fraud-rules-service': { port: 8082 },
    'anti-fraud-signals-service': { port: 8083 },
    'api-gateway': { port: 8084 },
    'api-keys-service': { port: 8085 },
    'audit-correlation-service': { port: 8086 },
    'billing-service': { port: 8087 },
    'courier-adapter-service': { port: 8088 },
    'currency-converter-service': { port: 8089 },
    'database-indexing-service': { port: 8090 },
    'database-management-service': { port: 8091 },
    'data-privacy-consent-service': { port: 8092 },
    'event-audit-service': { port: 8093 },
    'geo-location-service': { port: 8094 },
    'idempotency-service': { port: 8095 },
    'identity-access-service': { port: 8096 },
    'identity-service': { port: 8097 },
    'insurer-adapter-service': { port: 8098 },
    'integration-adapters-service': { port: 8099 },
    'logging-aggregation-service': { port: 8100 },
    'mfa-service': { port: 8101 },
    'notification-service': { port: 8102 },
    'onboarding-service': { port: 8103 },
    'payments-adapter-service': { port: 8104 },
    'payment-service': { port: 8105 },
    'policy-engine-service': { port: 8106 },
    'pricing-service': { port: 8107 },
    'rate-limiting-service': { port: 8108 },
    'reporting-read-model-service': { port: 8109 },
    'request-routing-service': { port: 8110 },
    'rapidassist': { port: 8111 },
    'service-health-monitor': { port: 8112 },
    'service-registry-discovery': { port: 8113 },
    'session-token-service': { port: 8114 },
    'template-messaging-service': { port: 8115 },
    'tenant-org-service': { port: 8116 },
    'user-profile-service': { port: 8117 },
    'waf-policy-service': { port: 8118 },
    'webhook-delivery-service': { port: 8119 }
};

// Common YAML configuration for all services
function generateYaml(service, port) {
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
      database: shared_infrastructure_dev
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

# MongoDB Document DB - Collection Prefixes
# All services use shared_infrastructure_dev database with collection prefixes
# Example: access_control_policies, alerting_alerts, api_keys, etc.

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
      uri: mongodb://localhost:27017/shared_infrastructure_dev
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
      uri: \${MONGODB_URI:mongodb://localhost:27017/shared_infrastructure_dev}

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

for (const [service, config] of Object.entries(services)) {
    const resourceDir = path.join(baseDir, service, 'src', 'main', 'resources');

    if (!fs.existsSync(resourceDir)) {
        fs.mkdirSync(resourceDir, { recursive: true });
    }

    const yamlContent = generateYaml(service, config.port);
    const configPath = path.join(resourceDir, 'application-dev.yml');

    const exists = fs.existsSync(configPath);
    fs.writeFileSync(configPath, yamlContent);

    if (exists) {
        updatedCount++;
        console.log(`✓ Updated: ${service} (port: ${config.port})`);
    } else {
        createdCount++;
        console.log(`✓ Created: ${service} (port: ${config.port})`);
    }
}

console.log(`\n╔══════════════════════════════════════════════════════════════════════════════╗`);
console.log(`║              ALL SERVICE CONFIGURATIONS UPDATED SUCCESSFULLY!               ║`);
console.log(`╚══════════════════════════════════════════════════════════════════════════════╝\n`);

console.log(`Summary:`);
console.log(`  Database: shared_infrastructure_dev`);
console.log(`  Total Services: ${Object.keys(services).length}`);
console.log(`  Configurations Updated: ${updatedCount}`);
console.log(`  Configurations Created: ${createdCount}`);
console.log(`  Connection: mongodb://localhost:27017/shared_infrastructure_dev\n`);

console.log(`All 41 services now use the single shared_infrastructure_dev database`);
console.log(`with collection prefixes for organization (e.g., access_control_, alerting_, etc.)\n`);
