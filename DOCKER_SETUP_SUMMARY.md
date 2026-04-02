# Foundation-Domain Docker Setup Summary

## Overview

The Foundation-Domain is now fully containerized with Docker support for all 98+ services. This document provides a comprehensive guide for building, running, and deploying the services.

## Service Categories & Dockerfile Coverage

| Category | Services | Dockerfiles | Status |
|----------|----------|-------------|--------|
| Core Infrastructure | 38 | 38/38 | ✅ Complete |
| Orchestration Services | 4 | 4/4 | ✅ Complete |
| Configuration Services | 8 | 8/8 | ✅ Complete |
| AI Services | 31 | 31/31 | ✅ Complete |
| **TOTAL** | **81** | **81/81** | **✅ 100%** |

## Docker Compose Files

### 1. `docker-compose.yml` - Core Services
**Purpose**: Local development with essential infrastructure and core services

**Includes**:
- Infrastructure: MongoDB, PostgreSQL, Redis
- Core Services: API Gateway, Identity, Config, Monitoring, Notification
- AI Gateway + Inference + Model Management
- Service Registry, Rate Limiting

**Usage**:
```bash
# Start all core services
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down
```

### 2. `docker-compose.ai.yml` - All AI Services
**Purpose**: Full AI services stack (31 services)

**Usage**:
```bash
# Start core + all AI services
docker-compose -f docker-compose.yml -f docker-compose.ai.yml up -d

# Start only AI services (after core is running)
docker-compose -f docker-compose.ai.yml up -d
```

## Service Port Mapping

### Infrastructure
| Service | Internal Port | External Port |
|---------|---------------|---------------|
| MongoDB | 27017 | 27017 |
| PostgreSQL | 5432 | 5432 |
| Redis | 6379 | 6379 |

### Core Foundation Services
| Service | Internal Port | External Port |
|---------|---------------|---------------|
| API Gateway | 8304 | 8304 |
| Identity Service | 8316 | 8888 |
| Config Service | 8000 | 8000 |
| Monitoring Service | 8091 | 8091 |
| Notification Service | 8323 | 8323 |
| Service Registry | 8761 | 8761 |
| Rate Limiting | 8080 | 8320 |

### AI Services (Port Range 8200-8280)
| Service | External Port |
|---------|---------------|
| AI Gateway | 8200 |
| AI Inference | 8201 |
| AI Model Management | 8202 |
| NLP Processing | 8210 |
| Translation | 8211 |
| Sentiment Analysis | 8212 |
| Summarization | 8213 |
| Chatbot | 8214 |
| Computer Vision | 8220 |
| Image Recognition | 8221 |
| Content Analysis | 8222 |
| Content Moderation | 8223 |
| Anomaly Detection | 8230 |
| Predictive Analytics | 8231 |
| Forecasting | 8232 |
| BI Analytics | 8233 |
| Analytics | 8234 |
| Fraud Detection | 8240 |
| Risk Assessment | 8241 |
| Matching Algorithm | 8242 |
| Recommendation | 8243 |
| Personalization | 8244 |
| Optimization | 8245 |
| Search Optimization | 8246 |
| Automated Tagging | 8250 |
| Categorization | 8251 |
| Data Quality | 8252 |
| Speech Recognition | 8260 |
| Report Generation | 8270 |
| Pricing Engine | 8271 |
| Summization | 8280 |

## Build Scripts

### Quick Build
```bash
# Build and start core services only
./scripts/docker-build-all.sh

# Build and push to registry
./scripts/docker-build-all.sh --push --tag v1.0.0
```

### Individual Service Build
```bash
# Build a specific service
cd ai-services/Backend/Java/ai-gateway-service
docker build -t foundation-ai-gateway:latest .
```

## Environment Variables

### Common Variables
```bash
SPRING_PROFILES_ACTIVE=docker
MONGODB_URL=mongodb://admin:password123@foundation-mongodb:27017
REDIS_URL=redis://foundation-redis:6379
POSTGRES_URL=jdbc:postgresql://admin:password123@foundation-postgres:5432/foundation
```

### Service-Specific Ports
Each service exposes port 8080 internally, mapped to unique external ports.

## Health Checks

All Dockerfiles include health checks:
```dockerfile
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1
```

Check service health:
```bash
# Check all containers
docker ps

# Check specific container health
docker inspect foundation-api-gateway | grep -A 5 Health

# View health status
docker-compose ps
```

## CI/CD Integration

The `.github/workflows/foundation-domain-ci.yml` pipeline includes:
1. Build shared libraries
2. Build core services (parallel)
3. Build AI services (parallel)
4. Build Docker images
5. Security scanning
6. Deploy to staging

## Production Deployment

### Registry Configuration
```bash
# Login to GitHub Container Registry
echo $GITHUB_TOKEN | docker login ghcr.io -u USERNAME --password-stdin

# Build and push
docker build -t ghcr.io/your-org/foundation-domain/api-gateway:latest .
docker push ghcr.io/your-org/foundation-domain/api-gateway:latest
```

### Kubernetes Deployment
See `IMPLEMENTATION_PLAN.md` for Kubernetes manifests.

## Troubleshooting

### Container Won't Start
```bash
# Check logs
docker-compose logs <service-name>

# Check container details
docker inspect <container-id>
```

### Port Conflicts
Edit `docker-compose.yml` to change external port mappings if needed.

### Memory Issues
Increase Docker Desktop memory allocation to at least 8GB for all services.

## Next Steps

1. **Build Verification**: Run `./scripts/build-verify-all.sh`
2. **Local Testing**: Start services with `docker-compose up -d`
3. **Health Checks**: Run `./scripts/health-check.sh`
4. **CI/CD Pipeline**: Push to main branch to trigger builds
5. **Cloud Deployment**: Follow `IMPLEMENTATION_PLAN.md` phases 4-8
