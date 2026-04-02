# Rate Limit Policy Service

## Overview

The Rate Limit Policy Service is a hexagonal architecture-based microservice for managing API rate limiting policies in a multi-tenant SaaS environment. It provides CRUD operations for rate limit policies with complete tenant isolation.

## Architecture

The service follows **Hexagonal Architecture** (Ports and Adapters):

```
rate-limit-policy-service/
├── domain/              # Core business logic
│   ├── aggregate/       # Aggregate roots
│   ├── event/           # Domain events
│   ├── model/           # Domain models
│   ├── policy/          # Business policies
│   ├── port/            # Ports (in/out)
│   └── repository/      # Repository interfaces
├── application/         # Application services
│   ├── command/         # CQRS commands
│   ├── query/           # CQRS queries
│   ├── dto/             # Data transfer objects
│   └── mapper/          # MapStruct mappers
├── infrastructure/      # Technical implementations
│   ├── config/          # Configuration
│   ├── persistence/     # Database adapters
│   ├── messaging/       # Kafka messaging
│   └── adapter/         # External API clients
└── adapters/in/web/     # REST controllers
```

## Technology Stack

- **Java 21**
- **Spring Boot 3.3.5**
- **MongoDB** - Primary data store
- **Redis** - Caching layer
- **Kafka** - Event streaming
- **MapStruct 1.6.0** - Bean mapping
- **JUnit 5** - Testing
- **Testcontainers** - Integration testing

## Features

- **Multi-tenancy**: Complete tenant isolation at all layers
- **Event Sourcing**: Domain events published to Kafka
- **CQRS**: Separate command and query operations
- **Caching**: Redis-based policy caching
- **Audit Trail**: Complete tracking of policy changes
- **Health Monitoring**: Spring Boot Actuator endpoints

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9+
- MongoDB 6.0+
- Redis 7.0+
- Kafka 3.0+ (optional)

### Running Locally

```bash
# Clone repository
git clone <repository-url>
cd rate-limit-policy-service

# Build
mvn clean install

# Run
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Environment Variables

```bash
MONGODB_HOST=localhost
MONGODB_PORT=27017
MONGODB_DB=rate-limit-policy-service-dev
REDIS_HOST=localhost
REDIS_PORT=6379
KAFKA_SERVERS=localhost:9092
TENANT_SERVICE_URL=http://localhost:8081
```

## API Documentation

- **Swagger UI**: http://localhost:8080/api/v1/swagger-ui.html
- **OpenAPI Spec**: http://localhost:8080/api/v1/api-docs
- **Health Check**: http://localhost:8080/api/v1/actuator/health

## Testing

```bash
# Unit tests
mvn test

# Integration tests
mvn verify

# Architecture tests
mvn test -Dtest=HexArchitectureTest

# Coverage report
mvn jacoco:report
```

## Deployment

The service is containerized and can be deployed to any container orchestration platform.

```bash
# Build
mvn clean package -DskipTests

# Docker
docker build -t rate-limit-policy-service:1.0.0 .

# Railway (automatic via railway.json)
railway up
```

## License

Proprietary - All rights reserved
