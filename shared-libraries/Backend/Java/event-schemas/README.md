# Event Schemas Library

Domain event definitions for the Gogidix Rapid Assist Platform event-driven architecture.

## Overview

This library provides type-safe domain events used across all services in the platform. Events are serialized to JSON and published to message brokers (Kafka, RabbitMQ) for asynchronous processing.

## Technology Stack

- **Java 21**
- **Spring Boot 3.3.5**
- **Jackson** for JSON serialization
- **JSR-310** for date/time support

## Installation

Add as a dependency in your Maven project:

```xml
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>event-schemas</artifactId>
    <version>1.0.0</version>
</dependency>
```

## Event Architecture

All events extend from the `DomainEvent` base class, which provides:

- `eventId`: Unique event identifier (UUID)
- `eventType`: Type of event (e.g., "ServiceCreated")
- `aggregateId`: ID of the aggregate that generated the event
- `aggregateType`: Type of aggregate (e.g., "Service", "Customer")
- `tenantId`: Multi-tenant identifier
- `organizationId`: Organization identifier
- `correlationId`: Correlates related events
- `causationId`: Links to the event that caused this one
- `occurredAt`: Timestamp when the event occurred
- `userId`: User who triggered the event
- `username`: Username of the user
- `version`: Event schema version

## Available Events

### Service Events

**Package**: `com.gogidix.rapidassist.event.schemas.event.ServiceEvents`

#### ServiceCreated

Emitted when a new service is created in the system.

**Fields**:
- `serviceName`: Name of the service
- `serviceType`: Type of service (TOWING, JUMP_START, etc.)
- `serviceCode`: Unique service code

**Example**:
```java
ServiceEvents.ServiceCreated event = new ServiceEvents.ServiceCreated();
event.setAggregateId(serviceId);
event.setServiceName("Towing Service");
event.setServiceType("TOWING");
event.setServiceCode("TOW-001");
event.setTenantId(tenantId);
```

#### ServiceUpdated

Emitted when a service is updated.

**Fields**:
- `serviceName`: Name of the service
- `updateType`: Type of update (PRICE_CHANGE, AVAILABILITY_CHANGE, etc.)

#### ServiceDeleted

Emitted when a service is deleted.

**Fields**:
- `serviceName`: Name of the service
- `deletionReason`: Reason for deletion

#### ServiceStatusChanged

Emitted when service status changes.

**Fields**:
- `serviceName`: Name of the service
- `oldStatus`: Previous status
- `newStatus`: New status
- `statusReason`: Reason for status change

### User Events

**Package**: `com.gogidix.rapidassist.event.schemas.event.UserEvents`

Events related to user lifecycle:
- UserCreated
- UserUpdated
- UserDeleted
- UserActivated
- UserDeactivated
- UserRoleChanged

### Authentication Events

**Package**: `com.gogidix.rapidassist.event.schemas.event.AuthenticationEvents`

Events related to authentication and authorization:
- UserLoggedIn
- UserLoggedOut
- LoginFailed
- PasswordChanged
- MFADisabled
- MFATokenGenerated

### Audit Events

**Package**: `com.gogidix.rapidassist.event.schemas.event.AuditEvents`

Events for audit trail and compliance:
- DataAccessed
- DataModified
- DataDeleted
- PermissionGranted
- PermissionRevoked
- ConfigurationChanged

### Business Events

**Package**: `com.gogidix.rapidassist.event.schemas.event.BusinessEvents`

Core business events:
- CustomerRegistered
- ServiceRequestCreated
- ServiceRequestAssigned
- ServiceRequestCompleted
- PaymentProcessed
- PaymentFailed

### System Events

**Package**: `com.gogidix.rapidassist.event.schemas.event.SystemEvents`

System-level events:
- HealthCheckCompleted
- MaintenanceStarted
- MaintenanceCompleted
- ErrorOccurred
- WarningIssued

## Event Serialization

All events are serialized to JSON using Jackson.

### Serialization Example

```java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JavaTimeModule());

ServiceEvents.ServiceCreated event = new ServiceEvents.ServiceCreated();
event.setServiceName("Towing Service");
event.setServiceType("TOWING");
event.setTenantId("tenant-123");

String json = mapper.writeValueAsString(event);
```

### Deserialization Example

```java
String json = "...";
ServiceEvents.ServiceCreated event = mapper.readValue(json, ServiceEvents.ServiceCreated.class);
```

## Event Versioning

All events include a `version` field that defaults to "1.0". When evolving events:

1. **Non-breaking changes**: Keep the same version
   - Adding optional fields
   - Adding new event types

2. **Breaking changes**: Increment the version
   - Removing fields
   - Renaming fields
   - Changing field types
   - Required field changes

Example versioned event:
```json
{
  "eventId": "evt-123",
  "eventType": "ServiceCreated",
  "version": "2.0",
  "serviceName": "Towing Service",
  "serviceType": "TOWING",
  "newField": "value"
}
```

## Event Publishing

### Using Spring Kafka

```java
@Service
public class EventPublisher {

    @Autowired
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;

    public void publishServiceCreated(Service service) {
        ServiceEvents.ServiceCreated event = new ServiceEvents.ServiceCreated();
        event.setAggregateId(service.getId());
        event.setServiceName(service.getName());
        event.setServiceType(service.getType());
        event.setTenantId(service.getTenantId());

        kafkaTemplate.send("service-events", event.getEventId(), event);
    }
}
```

### Using Spring Application Events

```java
@Service
public class ServiceService {

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public void createService(Service service) {
        // Save service
        serviceRepository.save(service);

        // Publish event
        ServiceEvents.ServiceCreated event = new ServiceEvents.ServiceCreated();
        event.setAggregateId(service.getId());
        event.setServiceName(service.getName());
        eventPublisher.publishEvent(event);
    }
}
```

## Event Consumption

### Kafka Listener

```java
@Service
public class ServiceEventListener {

    @KafkaListener(topics = "service-events", groupId = "service-consumer-group")
    public void handleServiceCreated(ServiceEvents.ServiceCreated event) {
        log.info("Service created: {}", event.getServiceName());

        // Process event
        notifyUsers(event);
        updateCache(event);
    }
}
```

### Application Event Listener

```java
@Component
public class ServiceEventListener {

    @EventListener
    public void handleServiceCreated(ServiceEvents.ServiceCreated event) {
        log.info("Service created: {}", event.getServiceName());

        // Process event synchronously
    }

    @EventListener
    @Async
    public void handleServiceCreatedAsync(ServiceEvents.ServiceCreated event) {
        // Process event asynchronously
    }
}
```

## Event Correlation

Link related events using correlation and causation IDs:

```java
ServiceEvents.ServiceRequestCreated requestCreated = new ServiceEvents.ServiceRequestCreated();
requestCreated.setCorrelationId("corr-123");
requestCreated.setCausationId(null); // Root cause

ServiceEvents.ServiceRequestAssigned assigned = new ServiceEvents.ServiceRequestAssigned();
assigned.setCorrelationId("corr-123"); // Same correlation
assigned.setCausationId(requestCreated.getEventId()); // Caused by the creation event
```

## Multi-Tenancy

All events include `tenantId` for multi-tenant isolation:

```java
ServiceEvents.ServiceCreated event = new ServiceEvents.ServiceCreated();
event.setTenantId(TenantContext.getTenantId()); // From request context
event.setOrganizationId(TenantContext.getOrganizationId());
```

## Testing

### Unit Tests

```java
@Test
void testEventSerialization() throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());

    ServiceEvents.ServiceCreated event = new ServiceEvents.ServiceCreated();
    event.setServiceName("Towing Service");

    String json = mapper.writeValueAsString(event);
    assertTrue(json.contains("\"serviceName\":\"Towing Service\""));
}
```

### Integration Tests

```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
    "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}"
})
class EventIntegrationTest {

    @Autowired
    private KafkaTemplate<String, DomainEvent> kafkaTemplate;

    @KafkaListener(topics = "test-topic")
    public void handleEvent(DomainEvent event) {
        // Verify event received
    }
}
```

## Best Practices

1. **Immutability**: Events should be immutable. Don't modify events after creation.
2. **Timestamps**: Always use UTC timestamps for consistency.
3. **Versioning**: Increment version for breaking changes.
4. **Correlation**: Use correlation IDs to track request flows.
5. **Idempotency**: Design event handlers to be idempotent (safe to replay).
6. **Error Handling**: Handle serialization failures gracefully.
7. **Schema Validation**: Validate events before publishing.
8. **Documentation**: Document all event fields and their purpose.

## Event Catalog

| Event Type | Aggregate | Description | Version |
|------------|-----------|-------------|---------|
| ServiceCreated | Service | Service created | 1.0 |
| ServiceUpdated | Service | Service updated | 1.0 |
| ServiceDeleted | Service | Service deleted | 1.0 |
| ServiceStatusChanged | Service | Service status changed | 1.0 |
| UserLoggedIn | Authentication | User logged in | 1.0 |
| UserLoggedOut | Authentication | User logged out | 1.0 |
| CustomerRegistered | Customer | Customer registered | 1.0 |
| ServiceRequestCreated | ServiceRequest | Service request created | 1.0 |

## Future Enhancements

- JSON Schema validation for all events
- AsyncAPI specification generation
- Event schema registry integration
- Event version migration utilities
- Event replay support
- Dead letter queue handling

## Contributing

When adding new events:

1. Extend from `DomainEvent`
2. Add descriptive field names
3. Document the event purpose
4. Add serialization tests
5. Update this README
6. Increment library version if breaking

## License

Copyright (c) 2026 Gogidix. All rights reserved.
