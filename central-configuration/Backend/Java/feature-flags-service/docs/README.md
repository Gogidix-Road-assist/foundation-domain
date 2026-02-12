# Feature Flags Service

## Overview

The Feature Flags Service is a hexagonal architecture-based microservice that provides feature flag management capabilities for the Rapid Assist platform. It implements multi-tenant isolation with comprehensive event-driven architecture.

## Architecture

This service follows the Hexagonal Architecture (Ports and Adapters) pattern:

- **Domain Layer**: Pure business logic with no external dependencies
- **Application Layer**: Use cases, commands, queries, and DTOs
- **Infrastructure Layer**: External adapters (MongoDB, Redis, Kafka, REST clients)
- **Adapters Layer**: Controllers and external interfaces

### Key Components

#### Domain Layer
- `FeatureFlagAggregate`: DDD aggregate root enforcing business rules
- `FeatureFlag`: Domain model with rich behavior
- `FeatureFlagValidationPolicy`: Business rule validation
- Domain Events: `FeatureFlagCreatedEvent`, `FeatureFlagUpdatedEvent`, `FeatureFlagDeletedEvent`

#### Application Layer
- Commands: `CreateFeatureFlagCommand`, `UpdateFeatureFlagCommand`, `DeleteFeatureFlagCommand`
- Queries: `GetFeatureFlagQuery`, `ListFeatureFlagsQuery`
- DTOs: `FeatureFlagResponseDto`, `PagedResponseDto`, `ErrorResponseDto`
- Mappers: MapStruct interfaces for entity-DTO conversion

#### Infrastructure Layer
- MongoDB persistence with tenant-aware repositories
- Redis caching with TTL support
- Kafka event publishing for domain events
- REST client for tenant service integration

## Quick Start

### Prerequisites

- JDK 21
- Maven 3.9+
- MongoDB 6.0+
- Redis 7+
- Kafka 3.x (optional - can be disabled)

### Running Locally

1. **Start dependencies:**
   ```bash
   docker-compose up -d mongodb redis kafka
   ```

2. **Build the service:**
   ```bash
   mvn clean package
   ```

3. **Run the service:**
   ```bash
   java -jar target/feature-flags-service-1.0.0.jar
   ```

4. **Access the API:**
   - API: http://localhost:8080/api/v1
   - Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
   - Health: http://localhost:8080/api/v1/actuator/health

### Configuration

Key configuration properties:

```yaml
app:
  config:
    environment: development
    tenant:
      enforce-isolation: true
      require-tenant-id: true
    cache:
      enabled: true
      ttl-seconds: 300
    events:
      enabled: true
      async-publishing: true
```

## API Usage

### Create a Feature Flag

```bash
curl -X POST http://localhost:8080/api/v1/feature-flags \
  -H "Content-Type: application/json" \
  -H "tenantId: tenant-123" \
  -d '{
    "key": "new-dashboard",
    "name": "New Dashboard",
    "description": "Enable new dashboard UI",
    "type": "BOOLEAN",
    "rolloutStrategy": "ALL_USERS",
    "environment": "production"
  }'
```

### Get Feature Flags

```bash
curl http://localhost:8080/api/v1/feature-flags \
  -H "tenantId: tenant-123"
```

### Evaluate a Feature Flag

```bash
curl http://localhost:8080/api/v1/feature-flags/evaluate \
  -H "tenantId: tenant-123" \
  -d '{
    "key": "new-dashboard",
    "userId": "user-123",
    "context": {}
  }'
```

## Multi-Tenancy

This service enforces strict tenant isolation:
- All queries are filtered by `tenantId`
- Tenant ID must be provided in headers
- Cross-tenant data access is prevented
- See `TenantIsolationTest` for verification

## Events

Domain events are published to Kafka topics:
- `feature-flag.events`: All domain events
- `feature-flag.changes`: State change events
- `feature-flag.evaluations`: Flag evaluation events

## Development

### Running Tests

```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# With coverage
mvn verify jacoco:report
```

### Code Quality

```bash
# Check architecture compliance
mvn test -Dtest=HexArchitectureTest

# Run all quality checks
mvn clean verify
```

## Deployment

### Docker

```bash
docker build -t feature-flags-service:1.0.0 .
docker run -p 8080:8080 feature-flags-service:1.0.0
```

### Kubernetes

```bash
kubectl apply -f k8s/
```

## Monitoring

- **Metrics**: `/actuator/metrics`
- **Health**: `/actuator/health`
- **Prometheus**: `/actuator/prometheus`

## Support

For issues and questions, contact support@gogidix.com
