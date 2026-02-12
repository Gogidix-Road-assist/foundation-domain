# Reporting Service

## Overview

The Reporting Service is a comprehensive report generation and management system for the Roadside Assistance Platform. Built following hexagonal architecture principles, it provides robust capabilities for creating, scheduling, and managing various types of reports in multiple formats.

## Features

- **Multi-Format Report Generation**: PDF, Excel, CSV, HTML, JSON
- **Report Templates**: Reusable, customizable templates
- **Automated Scheduling**: Cron and interval-based scheduling
- **Report Subscriptions**: User notification system
- **Audit Trail**: Complete history tracking
- **Multi-Tenant Support**: Full tenant isolation
- **Production-Ready**: 70%+ test coverage, monitoring, and observability

## Technology Stack

- **Java 21**
- **Spring Boot 3.3.5**
- **MongoDB** (orchestration_reporting_service_db)
- **Kafka** (event-driven messaging)
- **MapStruct** (DTO mapping)
- **Lombok** (code generation)
- **JUnit 5** (testing)

## Port Configuration

- **Service Port**: 8092
- **Context Path**: /reporting-service
- **Database**: orchestration_reporting_service_db

## Collections

1. **reports**: Generated report metadata
2. **report_templates**: Reusable report templates
3. **report_schedules**: Automated generation schedules
4. **report_history**: Generation audit trail
5. **report_subscriptions**: User subscriptions

## Report Types

- DISPATCH
- FLEET_UTILIZATION
- PERFORMANCE
- COMPLIANCE
- MAINTENANCE
- FINANCIAL
- CUSTOMER_SATISFACTION
- CUSTOM

## Output Formats

- PDF
- EXCEL
- CSV
- HTML
- JSON

## API Endpoints

### Reports

- `POST /api/v1/reports` - Create new report
- `GET /api/v1/reports/{reportId}` - Get report details
- `GET /api/v1/reports?tenantId={id}` - List reports by tenant
- `GET /api/v1/reports/user/{userId}` - Get user reports
- `GET /api/v1/reports/{reportId}/download` - Download report
- `DELETE /api/v1/reports/{reportId}` - Delete report
- `GET /api/v1/reports/statistics` - Get report statistics

### Templates

- `POST /api/v1/templates` - Create template
- `GET /api/v1/templates/{templateId}` - Get template
- `GET /api/v1/templates?tenantId={id}` - List templates
- `PUT /api/v1/templates/{templateId}` - Update template
- `DELETE /api/v1/templates/{templateId}` - Delete template

### Schedules

- `POST /api/v1/schedules` - Create schedule
- `GET /api/v1/schedules/{scheduleId}` - Get schedule
- `GET /api/v1/schedules?tenantId={id}` - List schedules
- `POST /api/v1/schedules/{scheduleId}/trigger` - Trigger schedule
- `PUT /api/v1/schedules/{scheduleId}/pause` - Pause schedule
- `PUT /api/v1/schedules/{scheduleId}/resume` - Resume schedule

## Hexagonal Architecture

```
reporting-service/
├── domain/
│   ├── model/          # Domain entities
│   ├── port/
│   │   ├── in/         # Input ports (use case interfaces)
│   │   └── out/        # Output ports (repository interfaces)
│   ├── event/          # Domain events
│   └── repository/     # Repository interfaces (if needed)
├── application/
│   ├── dto/            # Data transfer objects
│   ├── service/        # Application services (use case implementations)
│   └── mapper/         # MapStruct mappers
├── infrastructure/
│   ├── persistence/    # MongoDB repository implementations
│   ├── reportengine/   # Report generation engine
│   ├── messaging/      # Kafka producers/consumers
│   └── scheduling/     # Scheduled tasks
├── interfaces/
│   └── rest/           # REST controllers
└── shared/
    ├── exception/      # Global exception handling
    └── config/         # Shared configuration
```

## Local Development

### Prerequisites

- Java 21
- Maven 3.9+
- MongoDB 6.0+
- Kafka 3.0+

### Running the Service

```bash
# Build the service
mvn clean install

# Run the service
mvn spring-boot:run

# Or run the JAR
java -jar target/reporting-service-1.0.0.jar
```

### Accessing the Service

- **Service URL**: http://localhost:8092/reporting-service
- **Actuator**: http://localhost:8092/reporting-service/actuator/health
- **API**: http://localhost:8092/reporting-service/api/v1/reports

## Docker Deployment

```bash
# Build Docker image
docker build -t reporting-service:1.0.0 .

# Run container
docker run -p 8092:8092 \
  -e SPRING_DATA_MONGODB_URI=mongodb://host.docker.internal:27017 \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=host.docker.internal:9092 \
  reporting-service:1.0.0
```

## Environment Variables

- `SPRING_DATA_MONGODB_URI` - MongoDB connection string
- `SPRING_KAFKA_BOOTSTRAP_SERVERS` - Kafka bootstrap servers
- `SERVER_PORT` - Service port (default: 8092)
- `SPRING_PROFILES_ACTIVE` - Active profile (dev/test/prod)

## Monitoring

The service exposes metrics via Actuator:

- `/actuator/health` - Health check
- `/actuator/metrics` - Metrics
- `/actuator/prometheus` - Prometheus metrics

## Testing

```bash
# Run all tests
mvn test

# Run with coverage
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

## License

Copyright © 2025 Gogidix. All rights reserved.
