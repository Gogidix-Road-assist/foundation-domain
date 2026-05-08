# Access Control Service

[![Build](https://github.com/gogidix/rapid-assist/actions/workflows/build.yml/badge.svg)](https://github.com/gogidix/rapid-assist/actions/workflows/build.yml)
[![Test](https://github.com/gogidix/rapid-assist/actions/workflows/test.yml/badge.svg)](https://github.com/gogidix/rapid-assist/actions/workflows/test.yml)

**Part of the RapidAssist Foundation Domain - Shared Infrastructure**

## Overview

The Access Control Service provides centralized authorization capabilities for the RapidAssist platform. It implements both Role-Based Access Control (RBAC) and Attribute-Based Access Control (ABAC) in a multi-tenant SaaS architecture.

## Features

- **Multi-Tenant**: Complete tenant isolation with `tenantId` scoping
- **RBAC**: Role-based permission management
- **ABAC**: Attribute-based access control with policy engine
- **Caching**: Redis-backed permission cache for high-performance checks
- **Audit**: Complete audit trail via Kafka event streaming
- **Hexagonal Architecture**: Clean separation of concerns with ports and adapters

## Quick Start

### Prerequisites

- Java 21+
- MongoDB 6+
- Redis 7+
- Kafka 3+

### Running Locally

```bash
# Build the project
mvn clean package

# Run the service
java -jar target/access-control-service-0.0.1-SNAPSHOT.jar
```

### Docker

```bash
docker build -t access-control-service .
docker run -p 8080:8080 \
  -e MONGODB_URI=mongodb://host.docker.internal:27017/rapidassist \
  -e REDIS_HOST=host.docker.internal \
  -e KAFKA_SERVERS=host.docker.internal:9092 \
  access-control-service
```

## API Usage

### Check Access

```bash
curl -X POST http://localhost:8080/api/v1/access/check \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: tenant-123" \
  -d '{
    "subjectId": "user-456",
    "resource": "/api/v1/users",
    "action": "READ"
  }'
```

### Grant Permission

```bash
curl -X POST http://localhost:8080/api/v1/permissions/grant \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: tenant-123" \
  -d '{
    "subjectId": "user-456",
    "subjectType": "USER",
    "resource": "/api/v1/users",
    "action": "READ",
    "effect": "ALLOW"
  }'
```

### Create Role

```bash
curl -X POST http://localhost:8080/api/v1/roles \
  -H "Content-Type: application/json" \
  -H "X-Tenant-ID: tenant-123" \
  -d '{
    "name": "ADMIN",
    "description": "Administrator role"
  }'
```

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                         API GATEWAY                             │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                   TENANT INTERCEPTOR                        ││
│  │  Extracts tenant_id from JWT → Sets RequestContext          ││
│  └─────────────────────────────────────────────────────────────┘│
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                    ACCESS CONTROL SERVICE                       │
│  ┌───────────────┐  ┌───────────────┐  ┌───────────────┐     │
│  │  Controllers  │──│  Command/Query│──│   Domain      │     │
│  │               │  │   Handlers    │  │   Layer       │     │
│  └───────────────┘  └───────────────┘  └───────────────┘     │
│         │                                     │                 │
│         ▼                                     ▼                 │
│  ┌───────────────┐                 ┌───────────────┐          │
│  │ Repositories  │                 │ Policy Engine  │          │
│  │ (MongoDB)     │                 │ (RBAC/ABAC)    │          │
│  └───────────────┘                 └───────────────┘          │
└─────────────────────────────────────────────────────────────────┘
```

## Documentation

- [API Specification](docs/api-specification.md)
- [Runbook](docs/runbook.md)
- [Hexagonal Compliance Report](HEXAGONAL_COMPLIANCE_REPORT.md)

## License

Copyright © 2026 Gogidix. All rights reserved.
