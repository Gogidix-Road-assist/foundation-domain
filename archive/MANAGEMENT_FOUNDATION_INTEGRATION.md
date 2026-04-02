# Management-Domain to Foundation-Domain Integration Configuration

## Overview
This document defines the complete integration between Management-Domain services and Foundation-Domain services (80 services total).

---

## Foundation-Domain Services Architecture

### 1. Shared Infrastructure (36 services)

#### Security & Identity Services
| Service | Port | Purpose | Management-Domain Integration |
|---------|------|---------|-------------------------------|
| **api-gateway** | 8080 | Central API Gateway | Routes all Management requests |
| **identity-service** | 8081 | User authentication | JWT token validation |
| **identity-access-service** | 8082 | Access control management | Role-based access control |
| **access-control-service** | 8083 | Permission management | Authorization checks |
| **mfa-service** | 8084 | Multi-factor authentication | Additional security layer |
| **session-token-service** | 8085 | Session management | Token refresh & validation |

#### API Management Services
| Service | Port | Purpose | Management-Domain Integration |
|---------|------|---------|-------------------------------|
| **api-keys-service** | 8086 | API key management | Service-to-service auth |
| **rate-limiting-service** | 8087 | Rate limiting | Request throttling |
| **request-routing-service** | 8088 | Request routing | Service discovery |

#### Monitoring & Observability Services
| Service | Port | Purpose | Management-Domain Integration |
|---------|------|---------|-------------------------------|
| **service-health-monitor-service** | 8089 | Health checks | Service availability |
| **metrics-telemetry-service** | 8090 | Metrics collection | Performance monitoring |
| **logging-aggregation-service** | 8091 | Log aggregation | Centralized logging |
| **audit-correlation-service** | 8092 | Audit correlation | Request tracing |
| **event-audit-service** | 8093 | Event auditing | Compliance logging |

#### Business Services
| Service | Port | Purpose | Management-Domain Integration |
|---------|------|---------|-------------------------------|
| **billing-service** | 8094 | Billing management | Customer billing |
| **payment-service** | 8095 | Payment processing | Payment transactions |
| **pricing-service** | 8096 | Pricing management | Service pricing |
| **payments-adapter-service** | 8097 | Payment gateway adapter | External payments |
| **notification-service** | 8098 | Notifications | Alerts & notifications |
| **onboarding-service** | 8099 | User onboarding | New user setup |
| **tenant-org-service** | 8100 | Tenant management | Multi-tenancy |
| **user-profile-service** | 8101 | User profiles | Profile data |

#### Utility Services
| Service | Port | Purpose | Management-Domain Integration |
|---------|------|---------|-------------------------------|
| **idempotency-service** | 8102 | Idempotency | Duplicate prevention |
| **data-privacy-consent-service** | 8103 | Privacy consent | GDPR compliance |
| **geo-location-service** | 8104 | Geolocation | Location services |
| **currency-converter-service** | 8105 | Currency conversion | Multi-currency |
| **template-messaging-service** | 8106 | Template messaging | Email/SMS templates |
| **webhook-delivery-service** | 8107 | Webhook delivery | External webhooks |
| **anti-fraud-rules-service** | 8108 | Anti-fraud rules | Fraud prevention |
| **anti-fraud-signals-service** | 8109 | Fraud detection | Risk analysis |

#### Integration Adapters
| Service | Port | Purpose | Management-Domain Integration |
|---------|------|---------|-------------------------------|
| **courier-adapter-service** | 8110 | Courier integration | Shipping integration |
| **insurer-adapter-service** | 8111 | Insurance integration | Insurance services |
| **integration-adapters-service** | 8112 | Integration hub | External API integration |
| **maps-geocoding-adapter-service** | 8113 | Maps integration | Location services |
| **database-management-service** | 8114 | Database management | DB operations |
| **reporting-read-model-service** | 8115 | Read models | Reporting data |

---

### 2. Central Configuration (8 services)

| Service | Port | Purpose | Management-Domain Integration |
|---------|------|---------|-------------------------------|
| **config-service** | 8200 | Central configuration | Service configuration |
| **feature-flags-service** | 8201 | Feature flags | Feature toggles |
| **policy-configuration-service** | 8202 | Policy management | Business rules |
| **rate-limit-policy-service** | 8203 | Rate limit policies | Throttling rules |
| **tenancy-configuration-service** | 8204 | Tenant config | Multi-tenant settings |
| **country-localization-config-service** | 8205 | Localization | Country-specific settings |
| **dynamic-routing-config-service** | 8206 | Routing config | Service routing rules |
| **release-rollout-config-service** | 8207 | Release management | Deployment control |

---

### 3. AI Services (27 services)

#### Core AI Services
| Service | Port | Purpose | Management-Domain Integration |
|---------|------|---------|-------------------------------|
| **ai-chatbot-service** | 8300 | AI chatbot | Customer support automation |
| **ai-content-generator-service** | 8301 | Content generation | Automated content |
| **ai-document-analyzer-service** | 8302 | Document analysis | Document processing |
| **ai-image-recognition-service** | 8303 | Image recognition | Image processing |
| **ai-speech-recognition-service** | 8304 | Speech recognition | Voice processing |
| **ai-translation-service** | 8305 | Translation | Multi-language |
| **ai-text-summarization-service** | 8306 | Text summarization | Content summarization |
| **ai-voice-assistant-service** | 8307 | Voice assistant | Voice commands |
| **ai-leads-generator-service** | 8308 | Lead generation | Sales automation |
| **vendors-product-listing-ai-service** | 8309 | Product listing AI | Vendor management |

#### Analytics Services
| Service | Port | Purpose | Management-Domain Integration |
|---------|------|---------|-------------------------------|
| **analytics-service** | 8310 | General analytics | Data insights |
| **data-analytics-service** | 8311 | Data analytics | Advanced analytics |
| **customer-behaviour-analytics-service** | 8312 | Customer behavior | User behavior analysis |
| **customer-support-chatbot-service** | 8313 | Support chatbot | Automated support |
| **document-intelligence-service** | 8314 | Document intelligence | Smart document processing |
| **dynamic-pricing-service** | 8315 | Dynamic pricing | Price optimization |
| **fraud-detection-service** | 8316 | Fraud detection | Risk assessment |
| **intelligent-dispatch-service** | 8317 | Smart dispatch | Resource optimization |
| **predictive-maintenance-service** | 8318 | Predictive maintenance | Maintenance prediction |
| **recommendation-engine-service** | 8319 | Recommendations | Product recommendations |
| **route-optimization-service** | 8320 | Route optimization | Delivery optimization |
| **sentiment-analysis-service** | 8321 | Sentiment analysis | Customer sentiment |

#### Machine Learning Services
| Service | Port | Purpose | Management-Domain Integration |
|---------|------|---------|-------------------------------|
| **ai-training-ml-service** | 8322 | ML model training | Model training |
| **ai-anomaly-detection-service** | 8323 | Anomaly detection | Pattern recognition |
| **ai-data-prediction-service** | 8324 | Data prediction | Forecasting |

---

### 4. Centralized Dashboard (3 services)

| Service | Port | Purpose | Management-Domain Integration |
|---------|------|---------|-------------------------------|
| **dashboard-analytics-service** | 8400 | Dashboard analytics | Reporting dashboards |
| **dashboard-configuration-service** | 8401 | Dashboard config | Dashboard settings |
| **dashboard-reporting-service** | 8402 | Reporting service | Custom reports |

---

## Management-Domain Services

| Service | Port | Foundation Dependencies |
|---------|------|------------------------|
| **customer-support-management** | 8501 | api-gateway, identity-service, config-service, notification-service, ai-chatbot-service, analytics-service |
| **digital-marketing-management** | 8502 | api-gateway, identity-service, config-service, analytics-service, ai-content-generator-service, recommendation-engine-service |
| **global-admin-management** | 8503 | api-gateway, identity-service, access-control-service, audit-correlation-service, event-audit-service, dashboard-analytics-service |
| **shared-services-management** | 8504 | api-gateway, identity-service, config-service, notification-service, metrics-telemetry-service, predictive-maintenance-service |

---

## Integration Architecture

### 1. API Gateway Configuration

All Management-Domain services route requests through **api-gateway (port 8080)**:

```yaml
spring:
  cloud:
    gateway:
      routes:
        # Customer Support Routes
        - id: customer-support-route
          uri: lb://customer-support-management
          predicates:
            - Path=/api/customer-support/**
          filters:
            - StripPrefix=2

        # Digital Marketing Routes
        - id: digital-marketing-route
          uri: lb://digital-marketing-management
          predicates:
            - Path=/api/marketing/**
          filters:
            - StripPrefix=2

        # Global Admin Routes
        - id: global-admin-route
          uri: lb://global-admin-management
          predicates:
            - Path=/api/admin/**
          filters:
            - StripPrefix=2

        # Shared Services Routes
        - id: shared-services-route
          uri: lb://shared-services-management
          predicates:
            - Path=/api/shared-services/**
          filters:
            - StripPrefix=2
```

### 2. Security Integration

All Management-Domain services use **identity-service** for authentication:

```yaml
security:
  oauth2:
    resourceserver:
      jwt:
        issuer-uri: http://localhost:8081/realms/rapidassist
        jwk-set-uri: http://localhost:8081/.well-known/jwks.json
```

### 3. Configuration Integration

All Management-Domain services connect to **config-service**:

```yaml
spring:
  config:
    import: optional:configserver:http://localhost:8200
  cloud:
    config:
      uri: http://localhost:8200
      name: ${spring.application.name}
      profile: ${spring.profiles.active:dev}
```

### 4. Service Discovery Integration

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

## Communication Flow

### Request Flow (Example: Customer Support Request)

```
Client Request
    ↓
API Gateway (8080)
    ↓ (auth validation)
Identity Service (8081)
    ↓ (authorized)
Customer Support Management (8501)
    ↓ (analytics)
Analytics Service (8310)
    ↓ (notification)
Notification Service (8098)
    ↓ (AI automation)
AI Chatbot Service (8300)
```

### Event Flow

```
Management Domain Event
    ↓
Kafka Event Bus
    ↓
Event Audit Service (8093)
    ↓
Analytics Service (8310)
    ↓
Dashboard Analytics (8400)
```

---

## Dependencies to Add

### For All Management-Domain Services

Add these to each `pom.xml`:

```xml
<!-- Foundation-Domain Services -->
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>identity-service-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>config-service-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>notification-service-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>analytics-service-client</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

<!-- Spring Cloud for service discovery -->
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

---

## Startup Order

### Phase 1: Foundation Infrastructure
1. api-gateway (8080)
2. identity-service (8081)
3. config-service (8200)
4. service-discovery (8761)
5. monitoring services (8089-8093)

### Phase 2: Foundation Services
6. All shared-infrastructure services (8082-8115)
7. All central-configuration services (8201-8207)
8. All ai-services (8300-8324)
9. All dashboard services (8400-8402)

### Phase 3: Management Domain
10. customer-support-management (8501)
11. digital-marketing-management (8502)
12. global-admin-management (8503)
13. shared-services-management (8504)

---

## Configuration Files Location

All configuration files should be in:
```
c:/Users/HP/Desktop/Gogidix-Road-Assit-Saas/Rapid-Assist/Foundation-Domain/central-configuration/config-repo/
├── customer-support-management.yml
├── digital-marketing-management.yml
├── global-admin-management.yml
└── shared-services-management.yml
```

---

## Health Check Endpoints

Once integrated, these endpoints will be available:

```
http://localhost:8080/actuator/health          - API Gateway
http://localhost:8081/actuator/health          - Identity Service
http://localhost:8200/actuator/health          - Config Service
http://localhost:8501/actuator/health          - Customer Support
http://localhost:8502/actuator/health          - Digital Marketing
http://localhost:8503/actuator/health          - Global Admin
http://localhost:8504/actuator/health          - Shared Services
```

---

## Next Steps

1. ✅ Compile all Foundation-Domain services
2. ⏳ Install Foundation services to local Maven repository
3. ⏳ Add Foundation dependencies to Management-Domain services
4. ⏳ Configure API Gateway routing
5. ⏳ Set up service discovery
6. ⏳ Test integration end-to-end
7. ⏳ Create production deployment configuration

---

**Status**: Integration configuration defined. Ready for implementation.
