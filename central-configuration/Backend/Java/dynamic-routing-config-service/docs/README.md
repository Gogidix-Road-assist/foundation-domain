# Dynamic Routing Config Service

## Overview

The Dynamic Routing Config Service is responsible for managing dynamic routing rules for the Rapid Assist SaaS platform. It provides a centralized, tenant-aware routing configuration system that allows for dynamic routing to different services based on patterns, conditions, and strategies.

## Architecture

This service follows **Hexagonal Architecture** (Ports and Adapters) pattern with clear separation of concerns:

- **Domain Layer**: Core business logic (aggregates, entities, events, policies)
- **Application Layer**: Use cases, commands, queries, DTOs, and mappers
- **Infrastructure Layer**: External integrations (MongoDB, Redis, Kafka, REST clients)
- **Adapters Layer**: Web controllers (REST API)

## Features

- **Multi-Tenancy**: Complete tenant isolation for all routing rules
- **Dynamic Routing**: Runtime routing configuration without service restart
- **Pattern Matching**: Support for path prefix, regex, exact path, and wildcard patterns
- **Routing Strategies**: Round-robin, least connections, IP hash, header-based, random
- **Conditions**: Complex routing rules based on headers, query params, path variables
- **Circuit Breaker**: Built-in circuit breaker configuration for resilient routing
- **Event Sourcing**: Domain events published to Kafka for audit and event-driven architecture
- **Caching**: Redis-based caching for high-performance routing lookups
- **Validation**: Comprehensive validation policies for routing rules

## Technology Stack

- **Java 21**
- **Spring Boot 3.3.5**
- **Spring Data MongoDB** - Persistence
- **Spring Data Redis** - Caching
- **Spring Kafka** - Event messaging
- **MapStruct 1.6.0** - DTO mapping
- **OpenAPI 3.0** - API documentation

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- MongoDB 6.0+
- Redis 7.0+
- Kafka 3.0+

### Local Development

1. Clone the repository:
```bash
git clone <repository-url>
cd dynamic-routing-config-service
```

2. Configure environment variables:
```bash
export MONGODB_HOST=localhost
export MONGODB_PORT=27017
export REDIS_HOST=localhost
export REDIS_PORT=6379
export KAFKA_SERVERS=localhost:9092
```

3. Build the project:
```bash
mvn clean install
```

4. Run the service:
```bash
mvn spring-boot:run
```

The service will start on `http://localhost:8080/api/v1`

### Docker

Build and run with Docker:
```bash
docker build -t dynamic-routing-config-service .
docker run -p 8080:8080 \
  -e MONGODB_HOST=host.docker.internal \
  -e REDIS_HOST=host.docker.internal \
  -e KAFKA_SERVERS=host.docker.internal:9092 \
  dynamic-routing-config-service
```

## API Documentation

Once the service is running, access the API documentation at:
- Swagger UI: http://localhost:8080/api/v1/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api/v1/api-docs

## Configuration

### Application Properties

See `application-dev.yml` and `application-prod.yml` for configuration options.

Key configuration properties:
```yaml
app:
  routing:
    tenant:
      enforce-isolation: true
      require-tenant-id: true
    cache:
      enabled: true
      ttl-seconds: 300
    validation:
      enforce-circuit-breaker-for-critical: true
      require-approval-for-protected-routes: true
    events:
      enabled: true
      async-publishing: true
```

## Testing

Run all tests:
```bash
mvn test
```

Run integration tests:
```bash
mvn verify
```

## Monitoring

The service exposes actuator endpoints at:
- Health: http://localhost:8080/api/v1/actuator/health
- Metrics: http://localhost:8080/api/v1/actuator/metrics
- Prometheus: http://localhost:8080/api/v1/actuator/prometheus

## Contributing

Please see [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

## License

Proprietary - Gogidix Platform Team
