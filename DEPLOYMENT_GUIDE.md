# Foundation-Domain Deployment Guide

**Last Updated:** 2026-02-12
**Domain:** Foundation-Domain
**Total Services:** 97 deployable services + 14 shared libraries

---

## Quick Reference

| Environment | Backend | Frontend | Branch |
|-------------|---------|----------|--------|
| **Development** | Railway | Vercel | `dev` |
| **Staging** | Railway | Vercel | `staging` |
| **Production** | AWS/GCP | Vercel | `main` |

---

## Prerequisites

### Local Tools
- Java 21 (Temurin JDK)
- Maven 3.9+
- Node.js 18+
- Railway CLI: `npm install -g @railway/cli`
- Vercel CLI: `npm install -g vercel`

### Required Secrets
```bash
# Railway
RAILWAY_API_TOKEN          # From railway.app/account
RAILWAY_PROJECT_ID_DEV     # Dev project ID
RAILWAY_PROJECT_ID_STAGING # Staging project ID

# Vercel
VERCEL_TOKEN               # From vercel.com/account/tokens
VERCEL_ORG_ID              # Vercel organization ID
VERCEL_PROJECT_ID          # Vercel project ID

# Authentication
AUTH0_DOMAIN               # Auth0 domain
JWT_SECRET_DEV             # JWT signing secret

# Database
REDIS_PASSWORD             # Redis password
REDIS_PORT                 # Redis port
REDIS_USERNAME             # Redis username
```

---

## Deployment Architecture

```
Foundation-Domain (111 components)
├── AI-Services (32)
├── Central-Configuration (8)
├── Centralized-Dashboard (4) - 3 Java + 1 Node.js
├── Orchestration-Services (11)
├── Shared-Infrastructure (42)
└── Shared-Libraries (14)
```

---

## Deployment Order

**Deploy in this order to avoid dependency issues:**

### Phase 1: Infrastructure First
1. service-registry-discovery
2. config-service
3. api-gateway

### Phase 2: Shared Libraries
```bash
cd shared-libraries/Backend/Java
mvn clean install -DskipTests
```

### Phase 3: Shared Infrastructure (42 services)
Deploy in batches of 5-10

### Phase 4: Central Configuration (8 services)
All can be deployed in parallel

### Phase 5: Orchestration Services (11 services)

### Phase 6: AI Services (32 services)
Deploy in batches

### Phase 7: Dashboard Services (4 services)

---

## Development Deployment (Railway + Vercel)

### Backend to Railway
```bash
railway login --token $RAILWAY_API_TOKEN
railway link --project $RAILWAY_PROJECT_ID_DEV

# Deploy individual service
cd ai-services/Backend/Java/ai-chatbot-service
railway up --service ai-chatbot-service --detach
```

### Frontend to Vercel
```bash
vercel login --token $VERCEL_TOKEN
vercel pull --yes --environment=development
vercel build
vercel deploy --prebuilt
```

---

## Health Check Verification

```bash
# API Gateway
curl https://insurance-api-gateway-dev.up.railway.app/actuator/health

# Config Service
curl https://config-service-dev.up.railway.app/actuator/health

# Individual service
curl https://ai-chatbot-service-dev.up.railway.app/actuator/health
```

Expected response: `{"status":"UP"}`

---

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Service won't start | Check Railway logs, verify MongoDB/Redis connection |
| Build failures | Ensure Java 21, check Maven dependencies |
| Runtime errors | Check tenant headers (X-Tenant-ID), JWT tokens |

### Rollback
```bash
# Railway
railway rollback --service <service-name>

# Vercel
vercel rollback --token=$VERCEL_TOKEN
```

---

## CI/CD Pipeline

| Trigger | Action |
|---------|--------|
| Push to `dev` | Deploy to Development |
| Push to `staging` | Deploy to Staging |
| Push to `main` | Deploy to Production (manual approval) |

**Pipeline file:** `.github/workflows/foundation-domain-ci.yml`

---

## Package Naming

**DO NOT CHANGE** - Current naming is consistent:
- `com.gogidix.rapidassist.{service-type}.{service-name}`
- Examples: `ai.anomaly.*`, `config.service.*`, `api.gateway.*`

---

## Monitoring Endpoints

Each service exposes:
- `/actuator/health` - Health status
- `/actuator/metrics` - Metrics
- `/actuator/info` - Build info

---

*For detailed deployment procedures, see `archive/DEPLOYMENT_GUIDE-DETAILED.md`*
*For production readiness status, see `PRODUCTION_READINESS_CERTIFICATE.md`*
