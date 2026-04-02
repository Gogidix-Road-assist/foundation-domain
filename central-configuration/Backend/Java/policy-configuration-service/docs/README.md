# Policy Configuration Service

## Overview

The Policy Configuration Service is a core component of the Rapid Assist platform's Foundation Domain. It provides centralized management of policy configurations including security, privacy, business rules, compliance, rate limiting, and access control policies.

## Architecture

This service follows **Hexagonal Architecture** (Ports and Adapters pattern):

- **Domain Layer**: Core business logic, aggregates, policies, and ports
- **Application Layer**: Use cases, commands, queries, DTOs, and mappers
- **Infrastructure Layer**: Database, messaging, and external service adapters
- **Web Layer**: REST controllers (adapters/in)

### Key Design Principles

1. **Domain-Driven Design (DDD)**: Aggregate roots, domain events, and domain policies
2. **Command Query Responsibility Segregation (CQRS)**: Separate command and query models
3. **Multi-Tenancy**: Complete tenant isolation at all layers
4. **Event-Driven**: Kafka-based domain events for inter-service communication

## Features

- **Policy Management**: Create, read, update, and delete policies
- **Policy Types**: Security, Privacy, Business Rules, Compliance, Rate Limit, Access Control
- **Policy Validation**: Type-specific validation rules and constraints
- **Tenant Isolation**: Complete data isolation between tenants
- **Caching**: Redis-based caching for improved performance
- **Event Publishing**: Kafka-based event publishing for policy changes
- **Audit Trail**: Complete audit trail for all policy changes

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- MongoDB 6.0+
- Redis 7.0+
- Kafka 3.0+

### Building

```bash
mvn clean install
```

### Running Locally

```bash
mvn spring-boot:run
```

The service will start on port 8080 with context path `/api/v1`.

### Configuration

See `application-dev.yml` and `application-prod.yml` for configuration options.

Key configuration properties:
- `app.policy.environment`: Service environment
- `app.policy.tenant.enforce-isolation`: Enable tenant isolation
- `app.policy.cache.enabled`: Enable caching
- `app.policy.events.enabled`: Enable event publishing

## API Documentation

- OpenAPI spec: `src/main/resources/META-INF/openapi/openapi.yaml`
- Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
- API Docs: http://localhost:8080/api/v1/api-docs

## Testing

```bash
# Run unit tests
mvn test

# Run integration tests
mvn verify

# Run with coverage
mvn verify jacoco:report
```

## Deployment

See [runbook.md](runbook.md) for deployment procedures.

## Support

For issues and questions, contact the platform team.
