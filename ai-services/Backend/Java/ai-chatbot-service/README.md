# ai-chatbot-service

## Overview

Chatbot is part of the RapidAssist AI Services suite. This service provides AI-powered capabilities for the RapidAssist platform.

## Features

- AI-powered Chatbot
- RESTful API for easy integration
- Multi-tenant support
- Real-time processing
- Scalable microservice architecture

## Technology Stack

- Java 21
- Spring Boot 3.2.0
- PostgreSQL
- Maven

## Project Structure

```
ai-chatbot-service/
├── src/
│   ├── main/
│   │   ├── java/com/gogidix/rapidassist/ai/chatbot/
│   │   │   ├── domain/          # Domain models and business logic
│   │   │   ├── application/     # Application services and use cases
│   │   │   ├── infrastructure/  # External integrations and persistence
│   │   │   ├── interfaces/      # REST controllers and external interfaces
│   │   │   └── bootstrap/       # Application bootstrap
│   │   └── resources/           # Configuration files
│   └── test/                    # Test code
├── helm/                        # Helm charts for Kubernetes deployment
├── Dockerfile                   # Container image definition
├── pom.xml                      # Maven configuration
└── README.md                    # This file
```

## Architecture

The service follows Domain-Driven Design (DDD) principles with clean architecture:

- **Domain Layer**: Core business logic and entities
- **Application Layer**: Use cases and application services
- **Infrastructure Layer**: External systems integration
- **Interface Layer**: API endpoints and communication

## Local Development

### Prerequisites

- Java 21+
- Maven 3.8+
- PostgreSQL 14+
- Docker (optional)

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

### Test

```bash
mvn test
```

## Docker

### Build Image

```bash
docker build -t ai-chatbot-service:latest .
```

### Run Container

```bash
docker run -p 8080:8080 ai-chatbot-service:latest
```

## Kubernetes Deployment

```bash
helm install ai-chatbot-service ./helm
```

## API Documentation

Once the service is running, access the API documentation at:
- Swagger UI: http://localhost:8080/swagger-ui.html
- Actuator Health: http://localhost:8080/actuator/health

## Configuration

Configuration is managed through Spring Boot's externalized configuration:
- `application.yml` - Default configuration
- `application-{profile}.yml` - Profile-specific configuration
- Environment variables - Container/Cloud configuration

## Contributing

Please read our contributing guidelines before submitting pull requests.

## License

Copyright © 2024 Gogidix. All rights reserved.
