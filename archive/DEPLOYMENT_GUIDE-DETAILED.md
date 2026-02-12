# 🚀 Complete Deployment Guide - Foundation Domain to Cloud

**Environment**: Development (Cloud)
**Target**: Railway (Backend) + Vercel (Frontend)
**Branch**: `dev`

---

## 📋 Prerequisites Checklist

Before starting, ensure you have:

### Accounts Needed:
- [ ] **GitHub Account** - Source code repository
- [ ] **Railway Account** - https://railway.app (Free tier available)
- [ ] **MongoDB Atlas Account** - https://www.mongodb.com/cloud/atlas (Free tier M0)
- [ ] **Supabase Account** - https://supabase.com (Free tier available)
- [ ] **Vercel Account** - https://vercel.com (Free tier available)

### Local Tools:
- [ ] Git
- [ ] Node.js 18+
- [ ] Railway CLI: `npm install -g @railway/cli`
- [ ] Vercel CLI: `npm install -g vercel`

---

## 🎯 Phase 1: Database Setup

### Step 1.1: Set up MongoDB Atlas

```bash
# 1. Go to https://www.mongodb.com/cloud/atlas
# 2. Create a free account
# 3. Create a new cluster (M0 Free Tier)
# 4. Choose region closest to you (e.g., AWS eu-west-1)
# 5. Cluster Name: rapid-assist-foundation

# 6. Create Database User:
#    - Username: rapidassist
#    - Password: [Generate strong password]
#    - Database User Privileges: Read and write to any database

# 7. Network Access:
#    - Add IP: 0.0.0.0/0 (allows all - for Railway)

# 8. Get Connection String:
#    Click "Connect" -> "Drivers" -> Select Java
#    Connection string format:
mongodb+srv://rapidassist:<password>@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority
```

**Database Name**: `rapid_assist_foundation`

### Step 1.2: Set up Supabase PostgreSQL

```bash
# 1. Go to https://supabase.com
# 2. Create a new project
# 3. Project Name: rapid-assist-auth
# 4. Database Password: [Generate strong password]
# 5. Region: Choose same region as MongoDB (if possible)

# 6. Get Connection Details:
#    - Go to Project Settings -> Database
#    - Connection String (URI format):
postgresql://postgres:[YOUR-PASSWORD]@db.xxxxx.supabase.co:5432/postgres

# 7. Get API Keys:
#    - Go to Project Settings -> API
#    - Copy: project_url (SUPABASE_URL)
#    - Copy: anon public (SUPABASE_ANON_KEY)
#    - Copy: service_role (SUPABASE_SERVICE_ROLE_KEY)
```

**SQL to Run in Supabase SQL Editor**:
```sql
-- Enable extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

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

-- Audit logs table
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

-- Indexes
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_created_at ON audit_logs(created_at);
CREATE INDEX idx_sessions_user_id ON sessions(user_id);
CREATE INDEX idx_sessions_token ON sessions(token);
```

---

## 🎯 Phase 2: GitHub Secrets Setup

### Step 2.1: Add Secrets to GitHub Repository

Go to: `https://github.com/<username>/<repo>/settings/secrets/actions`

Add the following secrets:

```bash
# Railway
RAILWAY_TOKEN=<your-railway-token>
# Get token from: https://railway.app/account

# Vercel
VERCEL_TOKEN=<your-vercel-token>
# Get token from: https://vercel.com/account/tokens
VERCEL_ORG_ID=<your-org-id>
VERCEL_PROJECT_ID=<your-project-id>

# MongoDB
MONGODB_URI=mongodb+srv://rapidassist:<password>@cluster0.xxxxx.mongodb.net/rapid_assist_foundation?retryWrites=true&w=majority

# Supabase
SUPABASE_DB_URL=postgresql://postgres:<password>@db.xxxxx.supabase.co:5432/postgres
SUPABASE_URL=https://xxxxx.supabase.co
SUPABASE_ANON_KEY=<your-anon-key>
SUPABASE_SERVICE_ROLE_KEY=<your-service-role-key>

# JWT (Generate a strong secret)
JWT_SECRET=<generate-strong-secret-here>
```

---

## 🎯 Phase 3: Railway Setup

### Step 3.1: Initialize Railway Project

```bash
# Install Railway CLI
npm install -g @railway/cli

# Login to Railway
railway login

# Initialize project
cd Rapid-Assist
railway init

# Set project name
railway variables set RAILWAY_PROJECT_NAME=rapid-assist-foundation
```

### Step 3.2: Add MongoDB Plugin to Railway

```bash
# From Railway UI:
# 1. Go to https://railway.app/new
# 2. Click "New Project" -> "Deploy from GitHub repo"
# 3. Select your repository
# 4. Click "Add New Service" -> "Database" -> "Add MongoDB"

# OR via CLI:
railway add mongodb

# Get MongoDB connection string from Railway
railway variables get MONGODB_URI
```

### Step 3.3: Add Environment Variables to Railway

```bash
# Via Railway UI or CLI:

# Database
railway variables set MONGODB_URI=<your-mongodb-uri>
railway variables set SUPABASE_DB_URL=<your-supabase-db-url>
railway variables set SUPABASE_ANON_KEY=<your-anon-key>
railway variables set SUPABASE_SERVICE_ROLE_KEY=<your-service-role-key>

# Spring Configuration
railway variables set SPRING_PROFILES_ACTIVE=production
railway variables set LOGGING_LEVEL_COM_GOGIDIX=INFO

# JWT
railway variables set JWT_SECRET=<your-jwt-secret>
railway variables set JWT_EXPIRATION=86400000
```

---

## 🎯 Phase 4: Deploy Services to Railway

### Option A: Deploy via Railway UI (Easiest)

```bash
# 1. Go to https://railway.app/new
# 2. Click "New Project" -> "Deploy from GitHub repo"
# 3. Select your repository and branch (dev)
# 4. For each service:
#    - Click "New Service" -> "Deploy from GitHub repo"
#    - Select the service directory (e.g., feature-flags-service)
#    - Railway will auto-detect the Dockerfile
#    - Add environment variables
#    - Click "Deploy"
```

### Option B: Deploy via Railway CLI

```bash
# Deploy individual services
cd Rapid-Assist/Foundation-Domain/central-configuration/Backend/Java/feature-flags-service
railway up --service feature-flags-service

cd ../country-localization-config-service
railway up --service country-localization-config-service

# ... repeat for all services
```

### Option C: Deploy via GitHub Actions (Automatic)

```bash
# 1. Push to dev branch
git checkout dev
git add .
git commit -m "Enable Railway deployment"
git push origin dev

# 2. GitHub Actions will automatically:
#    - Build all services
#    - Deploy to Railway
#    - Run health checks
```

---

## 🎯 Phase 5: Vercel Frontend Deployment

### Step 5.1: Connect Vercel to GitHub

```bash
# 1. Go to https://vercel.com/new
# 2. Click "Import Project"
# 3. Connect GitHub repository
# 4. Select root directory or specific frontend:
#    Rapid-Assist/Foundation-Domain/centralized-dashboard/Frontend/Web/centralized-dashboard-web

# 5. Configure:
#    Framework Preset: Vite
#    Build Command: npm run build
#    Output Directory: dist
#    Install Command: npm install

# 6. Add Environment Variables:
VITE_API_BASE_URL=https://rapid-assist-foundation.railway.app
VITE_WS_URL=wss://rapid-assist-foundation.railway.app
VITE_SUPABASE_URL=<your-supabase-url>
VITE_SUPABASE_ANON_KEY=<your-anon-key>

# 7. Click "Deploy"
```

### Step 5.2: Configure Custom Domain (Optional)

```bash
# 1. In Vercel project, go to Settings -> Domains
# 2. Add your custom domain (e.g., dashboard.rapidassist.ie)
# 3. Configure DNS records as instructed by Vercel
```

---

## 🎯 Phase 6: Verify Deployment

### Step 6.1: Check Service Health

```bash
# Test individual services
curl https://feature-flags-service.railway.app/actuator/health
curl https://dashboard-configuration-service.railway.app/actuator/health
curl https://dashboard-analytics-service.railway.app/actuator/health

# Check Railway Dashboard
https://railway.app/dashboard

# Check service logs
railway logs --service feature-flags-service
```

### Step 6.2: Test Database Connections

```bash
# Test MongoDB Connection
# From Railway service logs, look for:
# "MongoDB connection established"

# Test Supabase Connection
# Check logs for successful connection messages
```

### Step 6.3: Test Frontend-Backend Integration

```bash
# 1. Open Vercel deployment URL
# 2. Check browser console for API calls
# 3. Test API endpoints directly:
curl https://rapid-assist-foundation.railway.app/api/dashboards/health
```

---

## 🎯 Phase 7: Continuous Deployment

### Automatic Deployment Setup

Now everything is set up for **automatic deployment**:

```bash
# When you push to dev branch:
git checkout dev
git add .
git commit -m "Update service"
git push origin dev

# GitHub Actions will:
# 1. Build all services
# 2. Run tests
# 3. Deploy to Railway
# 4. Deploy to Vercel
# 5. Run health checks
```

---

## 📊 Deployment Architecture Overview

```
┌─────────────────────────────────────────────────────────┐
│                    GitHub (dev branch)                  │
│                    Source Code Repository                │
└────────────────────┬────────────────────────────────────┘
                     │
                     ▼
        ┌────────────────────────┐
        │  GitHub Actions CI/CD  │
        │  - Build               │
        │  - Test                │
        │  - Package             │
        └────────┬───────────────┘
                 │
        ┌────────┴────────┐
        ▼                 ▼
┌──────────────┐  ┌──────────────┐
│   Railway    │  │   Vercel     │
│              │  │              │
│ [39 Services]│  │ [Frontends]  │
│              │  │              │
│ - Java 21    │  │ - React      │
│ - Node.js    │  │ - Vite       │
└──────┬───────┘  └──────────────┘
       │
  ┌────┴────┐
  ▼         ▼
┌────┐  ┌────────┐
│Mongo│  │Postgres│
│DB   │  │Supabase│
└────┘  └────────┘
```

---

## 🔍 Troubleshooting

### Common Issues:

#### 1. Service Not Starting
```bash
# Check logs:
railway logs --service <service-name>

# Common causes:
# - Missing environment variables
# - Database connection issues
# - Port conflicts
```

#### 2. Database Connection Failed
```bash
# Verify MongoDB URI format:
mongodb+srv://username:password@cluster0.xxxxx.mongodb.net/database?retryWrites=true&w=majority

# Verify IP whitelist in MongoDB Atlas includes:
# 0.0.0.0/0 (for Railway) or specific Railway IPs
```

#### 3. Build Failures
```bash
# Check if Java 21 is used
# Check if Maven dependencies are resolved
# Review GitHub Actions logs
```

---

## 📝 Next Steps After Deployment

1. **Monitor Services**: Set up Railway & Vercel alerts
2. **Test APIs**: Use Postman/Insomnia to test endpoints
3. **Load Testing**: Test service performance
4. **Domain Setup**: Configure custom domains
5. **SSL Certificates**: Auto-provided by Railway/Vercel
6. **Monitoring**: Set up application monitoring (Sentry, LogRocket)
7. **Analytics**: Configure usage analytics

---

## 🎉 Success Checklist

After deployment, verify:

- [ ] All Railway services show "Healthy" status
- [ ] Service health endpoints return 200 OK
- [ ] MongoDB connected (check logs)
- [ ] Supabase connected (check logs)
- [ ] Frontend loads on Vercel URL
- [ ] Frontend can call backend APIs
- [ ] GitHub Actions runs successfully on push
- [ ] New deployments are automatic on dev branch push

---

**Congratulations!** Your Foundation Domain is now live in the cloud! 🚀

For issues or questions, check logs in Railway Dashboard and GitHub Actions.
