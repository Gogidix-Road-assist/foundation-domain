# Matching Service

Provider matching service for roadside assistance operations using intelligent algorithms.

## Overview

The Matching Service is responsible for:
- Finding and matching service providers to assistance requests
- Multiple matching algorithms (NEAREST, BEST_FIT, LEAST_COST, PRIORITY_BASED)
- Advanced scoring engine with weighted factors
- Geospatial queries for nearby providers
- Real-time provider availability checking
- Multi-tenant provider management

## Port: 8090
## Database: orchestration_matching_service_db

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
- Service: http://localhost:8090/api/v1
- Health: http://localhost:8090/actuator/health
- Swagger UI: http://localhost:8090/swagger-ui.html
- OpenAPI: http://localhost:8090/v3/api-docs

## API Endpoints

### Matching Operations
- `POST /api/v1/matching/requests` - Create matching request
- `GET /api/v1/matching/results/{requestId}` - Get matching result
- `POST /api/v1/matching/requests/{requestId}/rescore` - Re-score with different algorithm
- `POST /api/v1/matching/requests/{requestId}/cancel` - Cancel request
- `GET /api/v1/matching/providers/{providerId}/availability` - Check availability
- `POST /api/v1/matching/requests/batch` - Batch matching

### Provider Management
- `POST /api/v1/providers` - Register provider
- `GET /api/v1/providers/{providerId}` - Get provider
- `PUT /api/v1/providers/{providerId}/location` - Update location
- `PUT /api/v1/providers/{providerId}/status` - Update status
- `GET /api/v1/providers/nearby` - Find nearby providers
- `GET /api/v1/providers/capability/{capability}` - Find by capability
- `POST /api/v1/providers/{providerId}/deactivate` - Deactivate provider

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
docker build -t matching-service .
```

### Run Container
```bash
docker run -p 8090:8090 \
  -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017/ \
  matching-service
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

- `matching_requests` - Provider matching requests
- `matching_results` - Scored provider results
- `matching_criteria` - Matching configuration criteria
- `provider_profiles` - Provider matching profiles with capabilities
- `matching_history` - Historical matching decisions for analytics

## Matching Algorithms

### NEAREST
Finds the geographically closest providers based on distance.

### BEST_FIT (Default)
Uses weighted scoring across multiple factors:
- Distance (30%)
- Capabilities (30%)
- Availability (20%)
- Rating (20%)

### LEAST_COST
Finds the most cost-effective providers based on estimated service cost.

### PRIORITY_BASED
Finds providers based on priority levels and overall score.

## Kafka Topics

- `matching.completed` - Matching process completed
- `provider.assigned` - Provider assigned to request
- `matching.no-providers` - No providers found

## License

Copyright © 2024 Gogidix. All rights reserved.
