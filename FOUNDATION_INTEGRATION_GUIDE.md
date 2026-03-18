# Foundation-Domain Integration Guide

**Version:** 1.0.0
**Last Updated:** 2026-03-14
**Target Audience:** Architects, Developers integrating with Foundation-Domain

---

## Overview

The **Foundation-Domain** provides pure infrastructure services that are **plug-and-play** for any project or domain. All services are generic infrastructure with NO business logic.

### What Foundation-Domain Provides

| Category | Services | Purpose |
|----------|----------|---------|
| **AI/ML** | 31 services | Artificial intelligence and machine learning |
| **Configuration** | 9 services | Central configuration management |
| **Monitoring** | 4 services | Dashboards and analytics |
| **Orchestration** | 7 services | Alerting, monitoring, reporting, transactions |
| **Infrastructure** | 38 services | Identity, auth, logging, messaging, etc. |
| **Libraries** | 15 libraries | Shared code and utilities |

### What Foundation-Domain Does NOT Provide

- ❌ Business logic (moved to shared-business-core)
- ❌ Industry-specific operations
- ❌ Domain-specific workflows

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         FOUNDATION-DOMAIN                               │
│                    (Pure Infrastructure Layer)                          │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  API Gateway (8304) - Single Entry Point                        │   │
│  │  /api/v1/auth/*         → Identity/Auth Services                │   │
│  │  /api/v1/config/*       → Configuration Services                │   │
│  │  /api/v1/ai/*           → AI Services                           │   │
│  │  /api/v1/monitoring/*   → Monitoring Services                   │   │
│  │  /api/v1/infra/*        → Infrastructure Services               │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
│  ┌─────────────────────────────────────────────────────────────────┐   │
│  │  Service Registry (8500) - Service Discovery                    │   │
│  └─────────────────────────────────────────────────────────────────┘   │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
                    ┌───────────────┼───────────────┐
                    ▼               ▼               ▼
        ┌───────────────┐ ┌───────────────┐ ┌───────────────┐
        │    Other      │ │   shared-     │ │   Business-   │
        │   Domains     │ │ business-core │ │    Domain     │
        └───────────────┘ └───────────────┘ └───────────────┘
```

---

## Quick Start

### 1. Prerequisites

Before integrating, ensure:

```bash
# Foundation-Domain is running
curl http://localhost:8304/health

# Expected response:
{"status":"UP","services":{"api-gateway":"UP","identity-service":"UP",...}}
```

### 2. Add Dependency

```xml
<!-- pom.xml -->
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>shared-persistence-library</artifactId>
    <version>1.0.0</version>
</dependency>

<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>shared-security-library</artifactId>
    <version>1.0.0</version>
</dependency>

<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>shared-request-context-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 3. Configure Application

```yaml
# application.yml
foundation:
  gateway:
    url: http://localhost:8304
    timeout: 5000
  services:
    identity:
      url: http://localhost:8888
    config:
      url: http://localhost:8888
    monitoring:
      url: http://localhost:8091
```

### 4. Authenticate

```bash
# Get JWT token from Foundation Identity Service
curl -X POST http://localhost:8304/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"your-user","password":"your-password","tenantId":"your-tenant"}'

# Response:
{
  "token":"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken":"...",
  "expiresIn":3600
}
```

### 5. Make API Calls

```bash
# Use the token for subsequent requests
curl http://localhost:8304/api/v1/config/ \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "X-Tenant-ID: your-tenant"
```

---

## Authentication & Authorization

### JWT Token Flow

```
┌─────────┐                ┌──────────────┐                ┌──────────┐
│ Client  │────login──────▶│ API Gateway  │────validate────▶│ Identity │
└─────────┘                └──────────────┘                │ Service │
     │                           │                        └──────────┘
     │                           │                           │
     │───────────token────────────▶                           │
     │                           │                           │
     │◀───────JWT + Refresh───────┼───────────────────────────│
     │                           │                           │
     Store token                 │                           │
     for subsequent requests     │                           │
```

### Token Validation

```java
// Your service can validate tokens via Foundation Identity Service
@Service
public class FoundationAuthService {

    private final RestTemplate restTemplate;

    @Value("${foundation.services.identity.url}")
    private String identityServiceUrl;

    public boolean validateToken(String token) {
        try {
            ResponseEntity<ValidationResponse> response = restTemplate.getForEntity(
                identityServiceUrl + "/api/v1/auth/validate?token=" + token,
                ValidationResponse.class
            );
            return response.getBody().isValid();
        } catch (Exception e) {
            return false;
        }
    }
}
```

### Multi-Tenancy

All Foundation services support multi-tenancy via the `X-Tenant-ID` header:

```bash
curl http://localhost:8304/api/v1/config/ \
  -H "Authorization: Bearer {token}" \
  -H "X-Tenant-ID: tenant-001"
```

---

## Service Integration Points

### 1. Identity & Access Control

| Service | Port | Base URL | Purpose |
|---------|------|----------|---------|
| identity-service | 8888 | /api/v1/identity | User identity |
| identity-access-service | 8315 | /api/v1/access | Access control |
| access-control-service | - | /api/v1/acl | RBAC |
| mfa-service | - | /api/v1/mfa | Multi-factor auth |
| session-token-service | - | /api/v1/sessions | Session management |

**Key Endpoints:**
```bash
# Login
POST /api/v1/auth/login

# Validate token
GET /api/v1/auth/validate?token={token}

# Refresh token
POST /api/v1/auth/refresh

# Logout
POST /api/v1/auth/logout

# Get user profile
GET /api/v1/identity/users/{userId}

# Check permissions
GET /api/v1/acl/permissions/check?userId={userId}&resource={resource}&action={action}
```

---

### 2. Configuration Management

| Service | Port | Base URL | Purpose |
|---------|------|----------|---------|
| config-service | 8888 | /api/v1/config | Central config |
| feature-flags-service | - | /api/v1/features | Feature toggles |
| dynamic-routing-config-service | - | /api/v1/routing | Dynamic routing |
| policy-configuration-service | - | /api/v1/policies | Policy config |
| rate-limit-policy-service | - | /api/v1/rate-limits | Rate limiting rules |

**Key Endpoints:**
```bash
# Get configuration
GET /api/v1/config/{configKey}?environment=production&tenantId={tenantId}

# Set configuration
PUT /api/v1/config/{configKey}

# Check feature flag
GET /api/v1/features/{featureKey}?tenantId={tenantId}

# Get routing rules
GET /api/v1/routing?service={serviceName}
```

---

### 3. Monitoring & Observability

| Service | Port | Base URL | Purpose |
|---------|------|----------|---------|
| monitoring-service | 8091 | /api/v1/monitoring | System monitoring |
| alerting-service | 8083 | /api/v1/alerting | Alert management |
| logging-aggregation-service | 8319 | /api/v1/logs | Log aggregation |
| metrics-telemetry-service | 8321 | /api/v1/metrics | Metrics collection |

**Key Endpoints:**
```bash
# Health check
GET /api/v1/monitoring/health

# Get metrics
GET /api/v1/metrics?service={serviceName}&from={timestamp}&to={timestamp}

# Create alert
POST /api/v1/alerting/rules

# Query logs
GET /api/v1/logs?service={serviceName}&level={level}&from={timestamp}
```

---

### 4. Notification Services

| Service | Port | Base URL | Purpose |
|---------|------|----------|---------|
| notification-service | 8323 | /api/v1/notifications | Notifications |
| template-messaging-service | - | /api/v1/templates | Message templates |
| email-service | - | /api/v1/email | Email (if available) |
| sms-service | - | /api/v1/sms | SMS (if available) |

**Key Endpoints:**
```bash
# Send notification
POST /api/v1/notifications/send
{
  "tenantId": "tenant-001",
  "type": "EMAIL",
  "recipients": ["user@example.com"],
  "templateKey": "welcome-email",
  "data": {"name": "John Doe"}
}

# Get message templates
GET /api/v1/templates?tenantId={tenantId}
```

---

### 5. AI Services

All AI services are available under `/api/v1/ai/`:

| Category | Services | Base URL |
|----------|----------|----------|
| NLP | nlp-processing, translation, sentiment-analysis | /api/v1/ai/nlp/ |
| Vision | computer-vision, image-recognition | /api/v1/ai/vision/ |
| Analytics | predictive-analytics, bi-analytics | /api/v1/ai/analytics/ |
| ML Operations | model-management, inference | /api/v1/ai/mlops/ |

**Example:**
```bash
# Text analysis
POST /api/v1/ai/nlp/analyze
{
  "text": "Your text here",
  "operations": ["sentiment", "entities", "keywords"]
}

# Image recognition
POST /api/v1/ai/vision/recognize
Content-Type: multipart/form-data
file: {image}
```

---

## Data Persistence Integration

### MongoDB Integration

Foundation provides `TenantAwareMongoRepository` for multi-tenant data access:

```java
// Your repository
@Repository
public interface YourRepository extends TenantAwareMongoRepository<YourEntity, String> {
    // Tenant-aware queries
    List<YourEntity> findByTenantId(String tenantId);

    // Foundation automatically filters by tenant from context
    @Query("{ 'name': ?0 }")
    List<YourEntity> findByName(String name);
}
```

### Tenant Context

```java
// Set tenant context (usually in a filter)
@Component
public class TenantFilter implements OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        String tenantId = request.getHeader("X-Tenant-ID");
        if (tenantId != null) {
            MongoTenantContext.setTenantId(tenantId);
        }
        try {
            filterChain.doFilter(request, response);
        } finally {
            MongoTenantContext.clear();
        }
    }
}
```

---

## Event-Driven Integration

### Publishing Events

```java
// Publish events to Foundation's event bus
@Service
public class YourService {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    public void publishEvent(DomainEvent event) {
        kafkaTemplate.send("foundation.events", event);
    }
}
```

### Consuming Foundation Events

```java
// Subscribe to Foundation events
@KafkaListener(topics = "foundation.events")
public void handleFoundationEvent(DomainEvent event) {
    // Handle event from Foundation
}
```

**Available Event Topics:**
- `foundation.events.auth` - Authentication events
- `foundation.events.config` - Configuration changes
- `foundation.events.monitoring` - Monitoring alerts
- `foundation.events.tenant` - Tenant lifecycle events

---

## Error Handling

### Standard Error Format

All Foundation services return errors in consistent format:

```json
{
  "timestamp": "2026-03-14T10:30:00Z",
  "status": 400,
  "error": "BAD_REQUEST",
  "message": "Invalid request parameter",
  "path": "/api/v1/config",
  "details": {
    "field": "configKey",
    "reason": "Cannot be empty"
  },
  "correlationId": "abc-123-def"
}
```

### Error Codes

| Code | Description |
|------|-------------|
| 400 | Bad Request |
| 401 | Unauthorized |
| 403 | Forbidden |
| 404 | Not Found |
| 409 | Conflict |
| 429 | Rate Limit Exceeded |
| 500 | Internal Server Error |
| 503 | Service Unavailable |

---

## Rate Limiting

Foundation enforces rate limiting via the API Gateway:

```bash
# Rate limit headers are included in responses
HTTP/1.1 200 OK
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1647260400

# When limit exceeded
HTTP/1.1 429 Too Many Requests
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1647260400
Retry-After: 60
```

---

## Health Checks

### Service Health

```bash
# Foundation Gateway health
GET http://localhost:8304/health

# Individual service health
GET http://localhost:8888/actuator/health  # Identity service
GET http://localhost:8091/health           # Monitoring service
```

### Health Check Response Format

```json
{
  "status": "UP",
  "components": {
    "api-gateway": {"status": "UP"},
    "identity-service": {"status": "UP"},
    "config-service": {"status": "UP"},
    "mongodb": {"status": "UP"},
    "redis": {"status": "UP"}
  }
}
```

---

## Deployment Integration

### Docker Compose

Include Foundation services in your docker-compose:

```yaml
services:
  your-service:
    depends_on:
      - foundation-gateway
      - mongodb
      - redis
    environment:
      - FOUNDATION_GATEWAY_URL=http://foundation-gateway:8304
      - FOUNDATION_IDENTITY_SERVICE_URL=http://identity-service:8888

  foundation-gateway:
    image: rapidassist/foundation-gateway:latest
    ports:
      - "8304:8304"

  identity-service:
    image: rapidassist/identity-service:latest
    ports:
      - "8888:8888"
```

### Kubernetes

```yaml
apiVersion: v1
kind: Service
metadata:
  name: foundation-gateway
spec:
  ports:
  - port: 8304
    targetPort: 8304
  selector:
    app: foundation-gateway
```

---

## SDK Integration

### Java Client Library

```xml
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>foundation-client-java</artifactId>
    <version>1.0.0</version>
</dependency>
```

```java
// Use Foundation client
FoundationClient client = FoundationClient.builder()
    .gatewayUrl("http://localhost:8304")
    .tenantId("tenant-001")
    .build();

// Get configuration
String config = client.config().get("my.config.key");

// Send notification
client.notifications().send(Notification.builder()
    .type(NotificationType.EMAIL)
    .recipient("user@example.com")
    .template("welcome")
    .build());
```

### Node.js Client Library

```bash
npm install @rapidassist/foundation-client
```

```javascript
const { FoundationClient } = require('@rapidassist/foundation-client');

const client = new FoundationClient({
  gatewayUrl: 'http://localhost:8304',
  tenantId: 'tenant-001'
});

// Get configuration
const config = await client.config.get('my.config.key');

// Send notification
await client.notifications.send({
  type: 'EMAIL',
  recipient: 'user@example.com',
  template: 'welcome'
});
```

---

## Best Practices

### 1. Always Include Tenant Context

```java
// GOOD - Tenant ID from request header
@GetMapping("/data")
public Response getData(@RequestHeader("X-Tenant-ID") String tenantId) {
    MongoTenantContext.setTenantId(tenantId);
    return service.getData();
}

// BAD - No tenant context
@GetMapping("/data")
public Response getData() {
    return service.getData(); // Security risk!
}
```

### 2. Use Correlation IDs

```java
// Include correlation ID for distributed tracing
String correlationId = UUID.randomUUID().toString();
 HttpHeaders headers = new HttpHeaders();
headers.set("X-Correlation-ID", correlationId);
```

### 3. Handle Tokens Securely

```java
// Store tokens securely, never log them
// Use refresh tokens to extend sessions
// Implement proper token revocation on logout
```

### 4. Use Circuit Breakers

```yaml
# Configure resilience
resilience4j:
  circuitbreaker:
    instances:
      foundation-service:
        failure-rate-threshold: 50
        wait-duration-in-open-state: 5s
```

### 5. Monitor Integration Health

```java
// Actively check Foundation services health
@Scheduled(fixedRate = 30000)
public void checkFoundationHealth() {
    if (!foundationClient.health().isUp()) {
        alertService.notify("Foundation services down!");
    }
}
```

---

## Troubleshooting

### Common Issues

**1. Connection Refused**
```
Error: connect ECONNREFUSED 127.0.0.1:8304
```
**Solution:** Ensure Foundation API Gateway is running

**2. Token Expired**
```json
{"error":"UNAUTHORIZED","message":"Token expired"}
```
**Solution:** Use refresh token or re-authenticate

**3. Tenant Not Found**
```json
{"error":"NOT_FOUND","message":"Tenant not registered"}
```
**Solution:** Register tenant via Foundation tenant management

**4. Rate Limit Exceeded**
```json
{"error":"TOO_MANY_REQUESTS","message":"Rate limit exceeded"}
```
**Solution:** Implement exponential backoff and retry

---

## Support & Documentation

### Additional Documentation

- [Foundation-Domain README](./README.md)
- [Production Readiness Certificate](./PRODUCTION_READINESS_CERTIFICATE.md)
- [API Documentation](http://localhost:8304/swagger-ui.html)

### Support Contacts

- Foundation Support: foundation-support@rapidassist.mt
- DevOps: devops@rapidassist.mt

### Changelog

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | 2026-03-14 | Initial release - Pure infrastructure domain |

---

*This document is maintained by the Foundation-Domain team. For updates, contact foundation-support@rapidassist.mt*
