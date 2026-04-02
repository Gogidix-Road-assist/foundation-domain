# Shared Audit Library

A comprehensive audit logging library for Spring Boot 3.3.5+ applications. Provides configurable audit event publishers (Database, Kafka, NoOp) with automatic retention policy support.

## Features

- **Multiple Publisher Implementations**
  - **Database Publisher**: Persists audit logs to relational databases using JPA
  - **Kafka Publisher**: Publishes audit events to Kafka topics for distributed processing
  - **NoOp Publisher**: Default no-op implementation for development/testing

- **Rich Query API**: Query audit logs by tenant, actor, entity, date range, event type, and more

- **Automatic Retention Policy**: Scheduled cleanup of old audit logs

- **Multi-Tenant Support**: Built-in tenant isolation for SaaS applications

- **Flexible Configuration**: Property-based configuration with sensible defaults

## Installation

Add the dependency to your project:

```xml
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>shared-audit-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Configuration

### Basic Configuration

```yaml
audit:
  enabled: true
  publisher: database  # Options: database, kafka, noop
```

### Database Publisher Configuration

```yaml
audit:
  publisher: database

  # Retention policy
  retention:
    enabled: true
    period: 90d  # Retain audit logs for 90 days
    cron: "0 0 0 * * ?"  # Run cleanup daily at midnight

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/audit_db
    username: audit_user
    password: audit_password
  jpa:
    hibernate:
      ddl-auto: update
```

### Kafka Publisher Configuration

```yaml
audit:
  publisher: kafka
  kafka:
    topic: audit-events

spring:
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
```

## Usage

### Publishing Audit Events

Inject the `AuditPublisher` interface and publish audit events:

```java
import com.gogidix.rapidassist.shared.audit.library.domain.port.out.AuditPublisher;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEventEnvelope;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditActor;
import com.gogidix.rapidassist.shared.audit.library.domain.model.AuditEntityRef;

@Service
public class CustomerService {

    private final AuditPublisher auditPublisher;

    public CustomerService(AuditPublisher auditPublisher) {
        this.auditPublisher = auditPublisher;
    }

    public void createCustomer(CreateCustomerRequest request) {
        // Business logic
        Customer customer = repository.save(request);

        // Publish audit event
        AuditActor actor = new AuditActor(
            getCurrentUserId(),
            "USER",
            getCurrentUserName()
        );

        AuditEntityRef entity = new AuditEntityRef(
            "Customer",
            customer.getId(),
            customer.getName()
        );

        AuditEventEnvelope event = new AuditEventEnvelope(
            "1.0",
            UUID.randomUUID().toString(),
            "CUSTOMER_CREATED",
            Instant.now(),
            MDC.get("correlationId"),
            "US",
            getCurrentTenantId(),
            null,  // subTenantId
            actor,
            entity,
            Map.of("ipAddress", request.getIpAddress()),
            Map.of(
                "customerId", customer.getId(),
                "customerName", customer.getName(),
                "customerEmail", customer.getEmail()
            )
        );

        auditPublisher.publish(event);
    }
}
```

### Querying Audit Logs

Inject the `AuditQueryService` to query audit logs:

```java
import com.gogidix.rapidassist.shared.audit.library.application.AuditQueryService;
import com.gogidix.rapidassist.shared.audit.library.infrastructure.database.AuditLogEntity;

@Service
public class AuditReportService {

    private final AuditQueryService auditQueryService;

    public AuditReportService(AuditQueryService auditQueryService) {
        this.auditQueryService = auditQueryService;
    }

    public List<AuditLogEntity> getCustomerAuditHistory(String customerId) {
        return auditQueryService.findByEntity("Customer", customerId);
    }

    public List<AuditLogEntity> getUserActivity(String userId, Instant from, Instant to) {
        return auditQueryService.findByTenantAndActorAndDateRange(
            getCurrentTenantId(),
            userId,
            from,
            to
        );
    }

    public Page<AuditLogEntity> getTenantAuditLogs(String tenantId, int page, int size) {
        return auditQueryService.findByTenant(
            tenantId,
            PageRequest.of(page, size, Sort.by("occurredAt").descending())
        );
    }
}
```

## Audit Event Structure

### AuditEventEnvelope

The `AuditEventEnvelope` record contains the following fields:

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| specVersion | String | Yes | Audit spec version (e.g., "1.0") |
| eventId | String | Yes | Unique event identifier |
| eventType | String | Yes | Type of event (e.g., "USER_CREATED") |
| occurredAt | Instant | Yes | When the event occurred |
| correlationId | String | Yes | Correlation ID for request tracing |
| country | String | Yes | Country code |
| tenantId | String | Yes | Tenant identifier |
| subTenantId | String | No | Sub-tenant identifier |
| actor | AuditActor | Yes | Who performed the action |
| entity | AuditEntityRef | Yes | What entity was affected |
| attributes | Map<String, Object> | No | Additional context attributes |
| payload | Object | Yes | Event payload data |

### AuditActor

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| id | String | Yes | Actor identifier (user ID, system ID) |
| type | String | Yes | Actor type (USER, SYSTEM, API) |
| name | String | No | Actor name/display name |

### AuditEntityRef

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| type | String | Yes | Entity type (Customer, Provider, etc.) |
| id | String | Yes | Entity identifier |
| name | String | No | Entity name/display name |

## Database Schema

The library creates the following table when using the database publisher:

```sql
CREATE TABLE audit_logs (
    id VARCHAR(255) PRIMARY KEY,
    spec_version VARCHAR(50) NOT NULL,
    event_id VARCHAR(100) NOT NULL UNIQUE,
    event_type VARCHAR(255) NOT NULL,
    occurred_at TIMESTAMP NOT NULL,
    correlation_id VARCHAR(100) NOT NULL,
    country VARCHAR(10) NOT NULL,
    tenant_id VARCHAR(100) NOT NULL,
    sub_tenant_id VARCHAR(100),
    actor_id VARCHAR(100) NOT NULL,
    actor_type VARCHAR(50) NOT NULL,
    actor_name VARCHAR(255),
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    entity_name VARCHAR(255),
    attributes TEXT,
    payload TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    INDEX idx_audit_tenant_id (tenant_id),
    INDEX idx_audit_actor_id (actor_id),
    INDEX idx_audit_entity_type (entity_type),
    INDEX idx_audit_entity_id (entity_id),
    INDEX idx_audit_occurred_at (occurred_at),
    INDEX idx_audit_event_type (event_type)
);
```

## Best Practices

### 1. Choose the Right Publisher

- **Development/Testing**: Use `noop` publisher
- **Production**: Use `database` or `kafka` based on your needs
- **High Volume**: Use `kafka` for async processing

### 2. Event Naming Conventions

Use past-tense, descriptive event names:

```
- USER_CREATED
- USER_UPDATED
- USER_DELETED
- PASSWORD_CHANGED
- LOGIN_SUCCESS
- LOGIN_FAILED
- SERVICE_REQUEST_CREATED
- PROVIDER_APPROVED
```

### 3. Include Relevant Context

Add useful attributes to help with auditing:

```java
Map<String, Object> attributes = Map.of(
    "ipAddress", request.getRemoteAddr(),
    "userAgent", request.getHeader("User-Agent"),
    "sessionId", session.getId()
);
```

### 4. Use Correlation IDs

Ensure correlation IDs are propagated across services:

```java
MDC.put("correlationId", UUID.randomUUID().toString());
```

### 5. Set Appropriate Retention

Configure retention based on compliance requirements:

```yaml
audit:
  retention:
    enabled: true
    period: 365d  # 1 year for financial compliance
```

## Monitoring

The library includes metrics for monitoring:

- `audit.publisher.success`: Number of successfully published events
- `audit.publisher.failure`: Number of failed publish attempts
- `audit.retention.deleted`: Number of logs deleted by retention job

## Troubleshooting

### Audit Logs Not Appearing

1. Check the publisher configuration: `audit.publisher`
2. Verify database connection for database publisher
3. Check Kafka connectivity for kafka publisher
4. Review application logs for errors

### Retention Job Not Running

1. Ensure retention is enabled: `audit.retention.enabled=true`
2. Verify scheduling is working
3. Check the cron expression: `audit.retention.cron`

## License

Copyright (c) 2026 Gogidix. All rights reserved.

## Support

For issues and questions, please open an issue in the repository.
