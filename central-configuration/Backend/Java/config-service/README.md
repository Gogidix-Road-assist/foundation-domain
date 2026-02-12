# Config Service

[![Build](https://github.com/gogidix/rapidassist/config-service/workflows/Build/badge.svg)](https://github.com/gogidix/rapidassist/config-service/actions)
[![Test](https://github.com/gogidix/rapidassist/config-service/workflows/Test/badge.svg)](https://github.com/gogidix/rapidassist/config-service/actions)
[![codecov](https://codecov.io/gh/gogidix/rapidassist/config-service/branch/main/graph/badge.svg)](https://codecov.io/gh/gogidix/rapidassist/config-service)

Centralized configuration management service for the Rapid Assist SaaS platform.

## Overview

The Config Service provides a multi-tenant, hexagonal architecture-based solution for managing application configurations with:
- Real-time configuration updates
- Environment-specific configuration management
- Tenant isolation and security
- Configuration versioning and audit trail
- Schema-based configuration validation

## Features

- **Multi-Tenant Support**: Complete tenant isolation with tenant-aware queries
- **Hexagonal Architecture**: Clean separation of concerns with ports and adapters
- **Event-Driven**: Publishes configuration events via Kafka
- **Version Control**: Track all configuration changes with audit trail
- **Validation**: Schema-based configuration validation with custom policies
- **Caching**: Redis-based caching for high-performance reads
- **Security**: JWT-based authentication with tenant context propagation

## Technology Stack

- **Java 21**
- **Spring Boot 3.3.5**
- **MongoDB** - Primary data store
- **Redis** - Caching layer
- **Kafka** - Event streaming
- **MapStruct** - DTO mapping

## Quick Start

### Prerequisites

- JDK 21+
- Maven 3.9+
- MongoDB 4.4+
- Redis 6+
- Kafka 2.8+ (optional)

### Build

```bash
mvn clean install
```

### Run Locally

```bash
mvn spring-boot:run
```

The service will start on port 8080 by default.

### Run Tests

```bash
mvn test
```

### Run with Docker

```bash
docker build -t config-service .
docker run -p 8080:8080 config-service
```

## API Documentation

Once running, access the API documentation at:

- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI JSON: http://localhost:8080/api-docs

### Health Check

```bash
curl http://localhost:8080/actuator/health
```

## Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVICE_PORT` | Service port | `8080` |
| `MONGODB_HOST` | MongoDB host | `localhost` |
| `MONGODB_PORT` | MongoDB port | `27017` |
| `MONGODB_DB` | MongoDB database | `config-service` |
| `REDIS_HOST` | Redis host | `localhost` |
| `REDIS_PORT` | Redis port | `6379` |
| `KAFKA_SERVERS` | Kafka bootstrap servers | `localhost:9092` |

### Profiles

- `dev` - Development environment (default)
- `prod` - Production environment
- `test` - Test environment

## Multi-Tenancy

All operations are scoped to a tenant. The tenant ID is extracted from the JWT token and propagated via the `RequestContext`.

### Tenant Isolation

- Each tenant's configurations are isolated at the database level
- All queries automatically filter by `tenantId`
- Cross-tenant data access is prevented at the repository level

## Development

### Code Structure

```
config-service/
├── domain/              # Business logic
│   ├── model/          # Domain entities
│   ├── aggregate/      # DDD aggregates
│   ├── event/          # Domain events
│   ├── policy/         # Business policies
│   ├── repository/     # Repository interfaces
│   └── port/           # Input/output ports
├── application/         # Application services
│   ├── command/        # Command objects
│   ├── query/          # Query objects
│   ├── service/        # Service implementations
│   ├── dto/            # Data transfer objects
│   └── mapper/         # MapStruct mappers
├── infrastructure/      # External integrations
│   ├── persistence/    # Database adapters
│   ├── messaging/      # Kafka messaging
│   ├── security/       # Security configuration
│   ├── adapter/        # External API clients
│   └── config/         # Spring configuration
└── interfaces/         # REST controllers
    └── rest/           # REST endpoints
```

## Contributing

1. Follow the hexagonal architecture pattern
2. Ensure all tests pass before committing
3. Maintain test coverage above 85%
4. Document API changes in OpenAPI specs

## License

Copyright (c) 2024 Gogidix. All rights reserved.
