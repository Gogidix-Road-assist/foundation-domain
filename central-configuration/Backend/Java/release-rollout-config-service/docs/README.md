# Release Rollout Configuration Service

## Overview

The Release Rollout Configuration Service is responsible for managing deployment rollout configurations with support for multiple deployment strategies including Blue-Green, Canary, Gradual, Big-Bang, and A/B Testing.

## Architecture

This service follows **Hexagonal Architecture** (Ports and Adapters pattern):

- **Domain Layer**: Core business logic (aggregates, entities, policies, events)
- **Application Layer**: Use cases, commands, queries, DTOs, mappers
- **Infrastructure Layer**: External integrations (MongoDB, Redis, Kafka, REST clients)
- **Adapters Layer**: Web controllers, message publishers

## Key Features

- Multi-tenancy with strict tenant isolation
- Multiple deployment strategies:
  - Blue-Green Deployment
  - Canary Deployment
  - Gradual Rollout
  - Big-Bang Deployment
  - A/B Testing
- Domain event publishing via Kafka
- Redis caching for performance
- MongoDB for persistence
- Comprehensive validation policies
- Audit logging support

## Technology Stack

- **Java 21**
- **Spring Boot 3.3.5**
- **MongoDB** - Primary data store
- **Redis** - Caching layer
- **Kafka** - Event streaming
- **MapStruct 1.6.0** - DTO mapping
- **Shared Libraries**:
  - shared-request-context-library
  - shared-security-library
  - shared-exception-library
  - shared-observability-library

## Getting Started

### Prerequisites

- JDK 21
- Maven 3.8+
- MongoDB 4.4+
- Redis 6+
- Kafka 2.8+ (optional, can be disabled)

### Building

```bash
mvn clean compile
```

### Running Tests

```bash
mvn test
```

### Building JAR

```bash
mvn package -DskipTests
```

The JAR will be created at: `target/release-rollout-config-service-1.0.0.jar`

### Running Locally

```bash
# Development mode
java -jar target/release-rollout-config-service-1.0.0.jar --spring.profiles.active=dev

# Production mode
java -jar target/release-rollout-config-service-1.0.0.jar --spring.profiles.active=prod
```

## API Documentation

- Swagger UI: `http://localhost:8082/api/v1/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8082/api/v1/api-docs`

## Configuration

See `src/main/resources/application-dev.yml` and `application-prod.yml` for configuration options.

Key configuration properties:

```yaml
app:
  rollout:
    environment: development
    tenant:
      enforce-isolation: true
    validation:
      require-approval-for-production: true
      validate-batch-configuration: true
    events:
      enabled: true
```

## Deployment

### Kubernetes

See `docs/runbook.md` for deployment procedures.

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVICE_PORT` | Service HTTP port | `8082` |
| `MONGODB_HOST` | MongoDB host | `localhost` |
| `MONGODB_PORT` | MongoDB port | `27017` |
| `MONGODB_DB` | MongoDB database | `release-rollout-config-service-prod` |
| `REDIS_HOST` | Redis host | `localhost` |
| `REDIS_PORT` | Redis port | `6379` |
| `KAFKA_SERVERS` | Kafka bootstrap servers | `localhost:9092` |

## Testing

### Unit Tests

```bash
mvn test
```

### Integration Tests

```bash
mvn verify
```

### Architecture Tests

```bash
mvn test -Dtest=HexArchitectureTest
```

## License

Copyright (c) 2025 Gogidix. All rights reserved.
