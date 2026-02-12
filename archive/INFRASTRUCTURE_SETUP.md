# Foundation Domain - Cloud Infrastructure Setup

**Last Updated**: December 25, 2024
**Environment**: Development (Cloud)
**Status**: In Progress

---

## 🎯 Overview

This document outlines the complete cloud infrastructure setup for the **Foundation Domain** microservices.

### Services to Deploy (78 Total)

| Domain | Services | Tech Stack | Port |
|--------|----------|------------|------|
| **shared-infrastructure** | 39 services | Java/Spring Boot | 8300-8338 |
| **ai-services** | 27 services | Java/Spring Boot | 8100-8126 |
| **central-configuration** | 8 services | Java/Spring Boot | 8000-8007 |
| **centralized-dashboard** | 3 Java + 1 Node.js | Java/Spring Boot, Node.js | 8200-8202, 3000 |
| **shared-libraries** | 0 services (libraries) | N/A | N/A |

---

## 🏗️ Infrastructure Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    GitHub Repository                         │
│                  (Source Code - dev branch)                  │
└────────────────────┬────────────────────────────────────────┘
                     │
                     ▼
         ┌───────────────────────┐
         │  GitHub Actions CI/CD │
         │  (Build & Test)       │
         └───────────┬───────────┘
                     │
        ┌────────────┴────────────┐
        ▼                         ▼
┌──────────────────┐    ┌──────────────────┐
│  Railway         │    │  Vercel          │
│  (Backend APIs)  │    │  (Frontends)     │
│  - Java Services │    │  - React Apps    │
│  - Node.js       │    │  - Web Apps      │
└────────┬─────────┘    └──────────────────┘
         │
    ┌────┴────┐
    ▼         ▼
┌────────┐ ┌──────────┐
│ MongoDB│ │PostgreSQL│
│  Atlas │ │ Supabase │
└────────┘ └──────────┘
```

---

## 🗄️ Database Setup

### 1. MongoDB Atlas (Primary Database)

**Purpose**: All microservices data storage

**Services using MongoDB**:
- All 77 Java Spring Boot services
- 1 Node.js dashboard aggregation service

**Setup Steps**:
1. Create MongoDB Atlas account: https://www.mongodb.com/cloud/atlas
2. Create a new cluster (Free tier: M0)
3. Database Name: `rapid_assist_foundation`
4. Create collections for each service:

**Shared-Infrastructure (39 collections)**:
- `access_controls`, `alerts`, `anti_fraud_rules`, `anti_fraud_signals`
- `api_keys`, `audit_correlations`, `billing_records`, `courier_adapters`
- `currency_rates`, `database_configs`, `data_privacy_consents`, `event_audits`
- `geo_locations`, `idempotency_keys`, `identities`, `insurer_adapters`
- `integration_adapters`, `logs`, `maps_geocoding`, `metrics_telemetry`
- `mfa_configs`, `notifications`, `onboarding_flows`, `payment_adapters`
- `payments`, `policies`, `pricing_rules`, `rate_limits`
- `read_models`, `request_routing`, `service_health`, `service_registry`
- `session_tokens`, `message_templates`, `tenant_orgs`, `user_profiles`
- `webhook_deliveries`

**Central-Configuration (8 collections)**:
- `feature_flags`, `country_configs`, `routing_rules`
- `policies`, `rate_limit_policies`, `release_rollouts`, `tenant_configs`
- `configurations`

**AI-Services (27 collections)**:
- `ai_anomaly_detection`, `ai_chatbot_conversations`, `ai_content_generation`
- `ai_data_predictions`, `ai_document_analysis`, `ai_image_recognition`
- `ai_leads_generation`, `ai_recommendations`, `ai_sentiment_analysis`
- `ai_speech_recognition`, `ai_text_summaries`, `ai_ml_training`
- `ai_translations`, `ai_voice_assistant`, `analytics_data`
- `customer_behaviour`, `customer_support_chats`, `data_analytics`
- `document_intelligence`, `dynamic_pricing`, `fraud_detection`
- `intelligent_dispatch`, `predictive_maintenance`, `recommendations`
- `route_optimization`, `sentiment_analysis`, `vendor_products`

**Centralized-Dashboard (4 collections)**:
- `dashboard_configs`, `analytics_events`, `report_definitions`, `report_executions`

**Connection String**:
```
mongodb+srv://<username>:<password>@cluster0.xxxxx.mongodb.net/rapid_assist_foundation?retryWrites=true&w=majority
```

**Environment Variable**:
```bash
MONGODB_URI=mongodb+srv://<username>:<password>@cluster0.xxxxx.mongodb.net/rapid_assist_foundation?retryWrites=true&w=majority
```

---

### 2. Supabase PostgreSQL (Relational Data)

**Purpose**: User authentication, relational data, audit logs

**Setup Steps**:
1. Create Supabase account: https://supabase.com
2. Create a new project
3. Database Name: `rapid_assist_auth`
4. Enable required extensions:
   - `pgcrypto` (for encryption)
   - `uuid-ossp` (for UUID generation)
   - `pgjwt` (for JWT tokens)

**Connection String**:
```
postgresql://postgres:<password>@db.xxxxx.supabase.co:5432/postgres
```

**Environment Variable**:
```bash
SUPABASE_DB_URL=postgresql://postgres:<password>@db.xxxxx.supabase.co:5432/postgres
SUPABASE_ANON_KEY=<your-anon-key>
SUPABASE_SERVICE_ROLE_KEY=<your-service-role-key>
```

**Tables to Create**:
```sql
-- Users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    role VARCHAR(50) DEFAULT 'USER',
    tenant_id VARCHAR(100),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Audit log table
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    resource_type VARCHAR(100),
    resource_id VARCHAR(100),
    old_data JSONB,
    new_data JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT NOW()
);

-- Sessions table
CREATE TABLE sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID REFERENCES users(id),
    token VARCHAR(500) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT NOW()
);
```

---

## 🚂 Railway Deployment Setup

### Account Setup
1. Create Railway account: https://railway.app
2. Install Railway CLI: `npm install -g @railway/cli`
3. Authenticate: `railway login`
4. Initialize project: `railway init`

### Project Structure on Railway

```
railway.app/rapid-assist-foundation/
│
├── Shared-Infrastructure Services (39 services)
│   ├── access-control-service (Port 8300)
│   ├── alerting-service (Port 8301)
│   ├── anti-fraud-rules-service (Port 8302)
│   ├── anti-fraud-signals-service (Port 8303)
│   ├── api-gateway (Port 8304)
│   ├── api-keys-service (Port 8305)
│   ├── audit-correlation-service (Port 8306)
│   ├── billing-service (Port 8307)
│   ├── courier-adapter-service (Port 8308)
│   ├── currency-converter-service (Port 8309)
│   ├── database-management-service (Port 8310)
│   ├── data-privacy-consent-service (Port 8311)
│   ├── event-audit-service (Port 8312)
│   ├── geo-location-service (Port 8313)
│   ├── idempotency-service (Port 8314)
│   ├── identity-access-service (Port 8315)
│   ├── identity-service (Port 8316)
│   ├── insurer-adapter-service (Port 8317)
│   ├── integration-adapters-service (Port 8318)
│   ├── logging-aggregation-service (Port 8319)
│   ├── maps-geocoding-adapter-service (Port 8320)
│   ├── metrics-telemetry-service (Port 8321)
│   ├── mfa-service (Port 8322)
│   ├── notification-service (Port 8323)
│   ├── onboarding-service (Port 8324)
│   ├── payments-adapter-service (Port 8325)
│   ├── payment-service (Port 8326)
│   ├── policy-engine-service (Port 8327)
│   ├── pricing-service (Port 8328)
│   ├── rate-limiting-service (Port 8329)
│   ├── reporting-read-model-service (Port 8330)
│   ├── request-routing-service (Port 8331)
│   ├── service-health-monitor-service (Port 8332)
│   ├── service-registry-discovery (Port 8333)
│   ├── session-token-service (Port 8334)
│   ├── template-messaging-service (Port 8335)
│   ├── tenant-org-service (Port 8336)
│   ├── user-profile-service (Port 8337)
│   └── webhook-delivery-service (Port 8338)
│
├── AI Services (27 services)
│   ├── ai-anomaly-detection-service (Port 8100)
│   ├── ai-chatbot-service (Port 8101)
│   ├── ai-content-generator-service (Port 8102)
│   ├── ai-data-prediction-service (Port 8103)
│   ├── ai-document-analyzer-service (Port 8104)
│   ├── ai-image-recognition-service (Port 8105)
│   ├── ai-leads-generator-service (Port 8106)
│   ├── ai-recommendation-engine-service (Port 8107)
│   ├── ai-sentiment-analysis-service (Port 8108)
│   ├── ai-speech-recognition-service (Port 8109)
│   ├── ai-text-summarization-service (Port 8110)
│   ├── ai-training-ml-service (Port 8111)
│   ├── ai-translation-service (Port 8112)
│   ├── ai-voice-assistant-service (Port 8113)
│   ├── analytics-service (Port 8114)
│   ├── customer-behaviour-analytics-service (Port 8115)
│   ├── customer-support-chatbot-service (Port 8116)
│   ├── data-analytics-service (Port 8117)
│   ├── document-intelligence-service (Port 8118)
│   ├── dynamic-pricing-service (Port 8119)
│   ├── fraud-detection-service (Port 8120)
│   ├── intelligent-dispatch-service (Port 8121)
│   ├── predictive-maintenance-service (Port 8122)
│   ├── recommendation-engine-service (Port 8123)
│   ├── route-optimization-service (Port 8124)
│   ├── sentiment-analysis-service (Port 8125)
│   └── vendors-product-listing-ai-service (Port 8126)
│
├── Central-Configuration Services (8 services)
│   ├── config-service (Port 8000)
│   ├── feature-flags-service (Port 8001)
│   ├── country-localization-config-service (Port 8002)
│   ├── dynamic-routing-config-service (Port 8003)
│   ├── policy-configuration-service (Port 8004)
│   ├── rate-limit-policy-service (Port 8005)
│   ├── release-rollout-config-service (Port 8006)
│   └── tenancy-configuration-service (Port 8007)
│
├── Centralized-Dashboard Services (4 services)
│   ├── dashboard-configuration-service (Port 8200) - Java
│   ├── dashboard-analytics-service (Port 8201) - Java
│   ├── dashboard-reporting-service (Port 8202) - Java
│   └── dashboard-aggregation-service (Port 3000) - Node.js
│
└── MongoDB Plugin (Database)
```

### Railway Configuration Files

Each service will have a `railway.toml` file:

```toml
[build]
builder = "DOCKERFILE"
dockerfilePath = "Dockerfile"

[deploy]
startCommand = "java -jar target/*.jar"
healthcheckPath = "/actuator/health"
healthcheckTimeout = 300
restartPolicyType = "ON_FAILURE"

[[services]]
name = "feature-flags-service"
```

---

## 🔄 GitHub Actions CI/CD

### Workflow: `.github/workflows/deploy-dev.yml`

```yaml
name: Deploy to Development (Railway + Vercel)

on:
  push:
    branches: [dev]
  workflow_dispatch:

env:
  RAILWAY_TOKEN: ${{ secrets.RAILWAY_TOKEN }}
  VERCEL_TOKEN: ${{ secrets.VERCEL_TOKEN }}
  MONGODB_URI: ${{ secrets.MONGODB_URI }}
  SUPABASE_DB_URL: ${{ secrets.SUPABASE_DB_URL }}

jobs:
  # Backend Services Deployment
  deploy-backend:
    name: Deploy Backend Services to Railway
    runs-on: ubuntu-latest
    strategy:
      matrix:
        service:
          - feature-flags-service
          - country-localization-service
          - dynamic-routing-service
          - policy-configuration-service
          - rate-limit-policy-service
          - release-rollout-service
          - tenancy-configuration-service
          - config-service

    steps:
      - name: Checkout Code
        uses: actions/checkout@v3

      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Build Service
        run: |
          cd Foundation-Domain/central-configuration/Backend/Java/${{ matrix.service }}
          mvn clean package -DskipTests

      - name: Deploy to Railway
        run: |
          railway up --service ${{ matrix.service }} --detach
```

---

## ⚡ Vercel Deployment Setup

### Frontend Projects

1. **Centralized Dashboard Web**
   - Repository: `Foundation-Domain/centralized-dashboard/Frontend/Web/centralized-dashboard-web`
   - Framework: React + Vite
   - Build Command: `npm run build`
   - Output Directory: `dist`

### Vercel Configuration: `vercel.json`

```json
{
  "version": 2,
  "builds": [
    {
      "src": "package.json",
      "use": "@vercel/static-build",
      "config": {
        "distDir": "dist"
      }
    }
  ],
  "routes": [
    {
      "src": "/(.*)",
      "dest": "/index.html"
    }
  ],
  "env": {
    "VITE_API_BASE_URL": "https://rapid-assist-foundation.railway.app"
  }
}
```

---

## 🔐 Environment Variables Template

### Backend Services (Railway)

```env
# Database Configuration
MONGODB_URI=mongodb+srv://<username>:<password>@cluster0.xxxxx.mongodb.net/rapid_assist_foundation?retryWrites=true&w=majority
SUPABASE_DB_URL=postgresql://postgres:<password>@db.xxxxx.supabase.co:5432/postgres
SUPABASE_ANON_KEY=<your-anon-key>
SUPABASE_SERVICE_ROLE_KEY=<your-service-role-key>

# Redis Configuration (Optional - for caching)
REDIS_HOST=redis.railway.app
REDIS_PORT=6379
REDIS_PASSWORD=<your-redis-password>

# Service Configuration
SPRING_PROFILES_ACTIVE=production
SERVER_PORT=<service-specific-port>
LOGGING_LEVEL_COM_GOGIDIX=INFO

# JWT Configuration
JWT_SECRET=<your-jwt-secret>
JWT_EXPIRATION=86400000

# AI Service Configuration (if using external AI)
DEEPSEEK_API_KEY=<your-deepseek-key>
Z_AI_API_KEY=<your-z-ai-key>
```

### Frontend (Vercel)

```env
VITE_API_BASE_URL=https://rapid-assist-foundation.railway.app
VITE_WS_URL=wss://rapid-assist-foundation.railway.app
VITE_SUPABASE_URL=<your-supabase-url>
VITE_SUPABASE_ANON_KEY=<your-anon-key>
```

---

## 📋 Deployment Checklist

### Phase 1: Database Setup ✅
- [ ] Create MongoDB Atlas account
- [ ] Set up cluster and database
- [ ] Create database user and password
- [ ] Configure IP whitelist (0.0.0.0/0 for Railway)
- [ ] Create Supabase account
- [ ] Create project and database
- [ ] Run SQL schema creation scripts
- [ ] Save connection strings to GitHub Secrets

### Phase 2: Railway Setup ✅
- [ ] Create Railway account
- [ ] Create new project
- [ ] Add MongoDB plugin to project
- [ ] Add Redis plugin to project (optional)
- [ ] Create environment variables template
- [ ] Add environment variables to Railway project

### Phase 3: Backend Deployment ✅
- [ ] Create Dockerfile for each service
- [ ] Create railway.toml for each service
- [ ] Push services to Railway (start with 1-2 test services)
- [ ] Verify health endpoints
- [ ] Test database connectivity
- [ ] Deploy all 39 backend services

### Phase 4: GitHub Actions Setup ✅
- [ ] Create `.github/workflows/deploy-dev.yml`
- [ ] Add secrets to GitHub repository:
  - `RAILWAY_TOKEN`
  - `VERCEL_TOKEN`
  - `MONGODB_URI`
  - `SUPABASE_DB_URL`
  - `SUPABASE_ANON_KEY`
  - `SUPABASE_SERVICE_ROLE_KEY`
- [ ] Test workflow on dev branch push
- [ ] Verify automatic deployments

### Phase 5: Frontend Deployment ✅
- [ ] Connect Vercel to GitHub repository
- [ ] Import frontend projects
- [ ] Configure environment variables
- [ ] Deploy frontends
- [ ] Test API connectivity

### Phase 6: Integration Testing ✅
- [ ] Test all service health endpoints
- [ ] Test database connections
- [ ] Test service-to-service communication
- [ ] Test frontend-backend integration
- [ ] Load testing
- [ ] Monitor logs and metrics

---

## 🔗 Useful Links

- **Railway Dashboard**: https://railway.app/dashboard
- **MongoDB Atlas**: https://cloud.mongodb.com
- **Supabase Dashboard**: https://supabase.com/dashboard
- **Vercel Dashboard**: https://vercel.com/dashboard
- **GitHub Actions**: https://github.com/<username>/<repo>/actions

---

## 📝 Notes

- All services use MongoDB for primary data storage
- PostgreSQL/Supabase is used for authentication and audit logs
- Redis is optional but recommended for caching (available on Railway)
- Each service will have its own subdomain: `service-name.railway.app`
- API Gateway can be set up later using Spring Cloud Gateway or Kong

---

**Next Steps**: Start with Phase 1 (Database Setup) and work through each phase systematically.
