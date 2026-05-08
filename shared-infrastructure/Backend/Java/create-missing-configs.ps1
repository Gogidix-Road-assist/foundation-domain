# Script to create application-dev.yml for 12 missing services

$baseDir = "C:\Users\HP\Desktop\Gogidix-Road-Assist-Saas\Rapid-Assist\Foundation-Domain\shared-infrastructure\Backend\Java"

$services = @{
    "alerting-service" = @{
        database = "rapid_assist_alerting_dev"
        port = 8080
    }
    "anti-fraud-rules-service" = @{
        database = "rapid_assist_anti_fraud_rules_dev"
        port = 8081
    }
    "anti-fraud-signals-service" = @{
        database = "rapid_assist_anti_fraud_signals_dev"
        port = 8082
    }
    "api-keys-service" = @{
        database = "rapid_assist_api_keys_dev"
        port = 8083
    }
    "courier-adapter-service" = @{
        database = "rapid_assist_courier_adapter_dev"
        port = 8084
    }
    "currency-converter-service" = @{
        database = "rapid_assist_currency_converter_dev"
        port = 8085
    }
    "event-audit-service" = @{
        database = "rapid_assist_event_audit_dev"
        port = 8086
    }
    "onboarding-service" = @{
        database = "rapid_assist_onboarding_dev"
        port = 8087
    }
    "rate-limiting-service" = @{
        database = "rapid_assist_rate_limiting_dev"
        port = 8088
    }
    "session-token-service" = @{
        database = "rapid_assist_session_token_dev"
        port = 8089
    }
    "template-messaging-service" = @{
        database = "rapid_assist_template_messaging_dev"
        port = 8090
    }
    "webhook-delivery-service" = @{
        database = "rapid_assist_webhook_delivery_dev"
        port = 8091
    }
}

foreach ($service in $services.Keys) {
    $config = $services[$service]
    $database = $config.database
    $port = $config.port
    $configPath = "$baseDir\$service\src\main\resources\application-dev.yml"

    # Create directory if it doesn't exist
    $resourceDir = "$baseDir\$service\src\main\resources"
    if (-not (Test-Path $resourceDir)) {
        New-Item -ItemType Directory -Path $resourceDir -Force | Out-Null
    }

    $yamlContent = @"
server:
  port: $port

spring:
  application:
    name: $service
  profiles:
    active: dev
  data:
    mongodb:
      host: localhost
      port: 27017
      database: $database
      auto-index-creation: true
      username: ${MONGODB_USERNAME:}
      password: ${MONGODB_PASSWORD:}
      authentication-database: ${MONGODB_AUTH_DB:admin}
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0
  kafka:
    bootstrap-servers: ${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    consumer:
      group-id: ${service}_group
      auto-offset-reset: earliest
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.apache.kafka.common.serialization.StringDeserializer
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.apache.kafka.common.serialization.StringSerializer

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
    com.gogidix.rapidassist.$service: DEBUG
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n"
  file:
    name: logs/$service.log

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
      uri: mongodb://localhost:27017/$database
  devtools:
    restart:
      enabled: true
    livereload:
      enabled: true

logging:
  level:
    root: DEBUG
    com.gogidix.rapidassist.$service: DEBUG
    org.springframework.data.mongodb: DEBUG

---
spring:
  config:
    activate:
      on-profile: prod
  data:
    mongodb:
      uri: \${MONGODB_URI:mongodb://localhost:27017/$database}

logging:
  level:
    root: INFO
    com.gogidix.rapidassist.$service: INFO
  file:
    name: /var/log/$service/application.log
"@

    Set-Content -Path $configPath -Value $yamlContent
    Write-Host "✓ Created application-dev.yml for $service (port: $port, db: $database)"
}

Write-Host "`nAll 12 application-dev.yml files created successfully!"
