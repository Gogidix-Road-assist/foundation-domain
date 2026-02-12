# TransactionOrchestration Service

Hexagonal SaaS Multi-Tenant microservice for managing roadside assistance dispatch operations.

## Overview

The TransactionOrchestration Service is responsible for:
- Creating and managing dispatch jobs
- Assigning service providers to requests
- Tracking dispatch status and location
- Managing provider availability and assignments
- Real-time dispatch event publishing

## Port: 8093

## Technology Stack

- **Java 21**
- **Spring Boot 3.3.5**
- **MongoDB** (Database per service pattern)
- **Kafka** (Event streaming)
- **MapStruct** (DTO mapping)
- **JUnit 5** (Testing)
- **Docker** (Containerization)

## Architecture

This service follows **Hexagonal Architecture** with clear separation of concerns:

```
├── domain/              # Core business logic (ZERO framework deps)
│   ├── model/          # Entities
│   ├── aggregate/      # Aggregate roots
│   ├── event/          # Domain events
│   ├── policy/         # Business rules
│   ├── port/           # Input/Output ports
│   └── repository/     # Repository interfaces
│
├── application/        # Use cases and orchestration
│   ├── command/        # Command objects
│   ├── query/          # Query objects
│   ├── service/        # Application services
│   ├── dto/            # Data transfer objects
│   └── mapper/         # MapStruct mappers
│
├── infrastructure/     # External concerns
│   ├── persistence/    # MongoDB, Redis
│   ├── messaging/      # Kafka publishers/consumers
│   ├── security/       # Tenant interceptor
│   ├── adapter/        # External service clients
│   └── config/         # Spring configuration
│
└── interfaces/         # External interfaces
    └── rest/           # REST controllers
```

## Multi-Tenancy

All data is tenant-isolated at the database level:
- Every entity has a `tenantId` field
- All queries filter by `tenantId`
- TenantInterceptor extracts tenant from X-Tenant-ID header
- TenantIsolationTest verifies cross-tenant data protection

## Running Locally

### Prerequisites
- Java 21
- Maven 3.9+
- MongoDB 4.4+
- Kafka 2.8+ (optional - for event publishing)

### Start MongoDB
```bash
mongod --dbpath mongodb-data --port 27017
```

### Build and Run
```bash
mvn clean spring-boot:run
```

### Access API
- Service: http://localhost:8093/api/v1
- Health: http://localhost:8093/actuator/health
- Swagger UI: http://localhost:8093/swagger-ui.html
- OpenAPI: http://localhost:8093/v3/api-docs

## API Endpoints

### Dispatch Operations
- `POST /api/v1/dispatches` - Create new dispatch
- `GET /api/v1/dispatches` - List dispatches (tenant-filtered)
- `GET /api/v1/dispatches/{id}` - Get dispatch by ID
- `PUT /api/v1/dispatches/{id}/status` - Update status
- `POST /api/v1/dispatches/{id}/assignments` - Assign provider

### Provider Operations
- `GET /api/v1/providers/available` - List available providers

## Required Headers

All API requests must include:
```
X-Tenant-ID: your-tenant-id
X-User-ID: user-id (optional)
X-Correlation-ID: correlation-id (optional, auto-generated if not provided)
```

## Testing

### Unit Tests
```bash
mvn test
```

### Integration Tests (requires MongoDB)
```bash
mvn verify -P integration-test
```

### Test Coverage
```bash
mvn jacoco:report
```
Target: 70%+ coverage

## Docker

### Build Image
```bash
docker build -t transaction-orchestration-service .
```

### Run Container
```bash
docker run -p 8093:8093 \
  -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/ \
  transaction-orchestration-service
```

## Deployment

### Railway
```bash
railway up
```

### Kubernetes
```bash
kubectl apply -f k8s/
```

## Monitoring

- Health: `/actuator/health`
- Metrics: `/actuator/metrics`
- Prometheus: `/actuator/prometheus`

## Database Collections

- `dispatches` - Main dispatch records
- `dispatch_assignments` - Provider assignments
- `dispatch_tracking` - Event tracking
- `dispatch_metrics` - Performance metrics
- `dispatch_routes` - Route information
- `dispatch_providers` - Provider status

## Event Topics

- `dispatch-events` - Dispatch lifecycle events
- `assignment-events` - Assignment events

## License

Copyright © 2024 Gogidix. All rights reserved.
