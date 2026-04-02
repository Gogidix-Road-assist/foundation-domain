# GitHub Secrets Setup Guide - Development Environment

**Repository:** ggx-insurance-saas/Insurance-company-Saas
**Environment:** Development (Railway + Vercel)
**Date:** 2026-02-09

---

## 🚨 Development Environment Notice

This configuration is for **DEVELOPMENT/TESTING ONLY**. All credentials must be rotated before production deployment.

---

## 📋 Quick Setup Instructions

### 1. Navigate to GitHub Secrets Settings

1. Go to: https://github.com/ggx-insurance-saas/Insurance-company-Saas/settings/secrets/actions
2. Click **"New repository secret"**
3. Add each secret below with its corresponding value

---

## 🔑 Required Secrets - Railway (Development)

| Secret Name | Value | Purpose |
|-------------|-------|---------|
| `RAILWAY_API_TOKEN` | `<your-railway-token>` | Railway CLI authentication (get from railway.app/settings) |
| `RAILWAY_PROJECT_ID_DEV` | `<your-railway-project-id>` | Dev project identifier (get from Railway project settings) |

---

## 🌐 Required Secrets - Vercel (Frontend)

| Secret Name | Value | Purpose |
|-------------|-------|---------|
| `VERCEL_TOKEN` | `<your-vercel-token>` | Vercel CLI authentication (get from vercel.com/tokens) |
| `VERCEL_ORG_ID` | `<your-vercel-org-id>` | Vercel organization ID (get from Vercel settings) |
| `VERCEL_PROJECT_ID` | `<your-vercel-project-id>` | Vercel project ID (get from project settings) |

---

## 🔴 Required Secrets - Redis Cloud

| Secret Name | Value | Purpose |
|-------------|-------|---------|
| `REDIS_USERNAME` | `default` | Redis authentication username |
| `REDIS_PASSWORD` | `<your-redis-password>` | Redis authentication password |
| `REDIS_HOST` | `<your-redis-host>` | Redis Cloud host endpoint |
| `REDIS_PORT` | `6379` | Redis port (default) |

---

## 🔐 Required Secrets - Auth0 / JWT

| Secret Name | Value | Purpose |
|-------------|-------|---------|
| `AUTH0_DOMAIN` | `<your-auth0-domain>` | Auth0 tenant domain |
| `AUTH0_AUDIENCE` | `<your-auth0-audience>` | Auth0 API audience |
| `AUTH0_CLIENT_ID` | `<your-auth0-client-id>` | Auth0 application client ID |
| `AUTH0_CLIENT_SECRET` | `<your-auth0-client-secret>` | Auth0 application secret |
| `JWT_SECRET_DEV` | `<your-jwt-secret>` | Development JWT signing secret |

---

## 📮 Required Secrets - Postman

| Secret Name | Value | Purpose |
|-------------|-------|---------|
| `POSTMAN_API_KEY` | `<your-postman-api-key>` | Postman API automation |
| `POSTMAN_WORKSPACE_ID` | `<your-postman-workspace-id>` | Postman workspace identifier |

---

## ✅ Setup Verification Checklist

After adding all secrets, verify:

- [ ] All Railway secrets added (`RAILWAY_API_TOKEN`, `RAILWAY_PROJECT_ID_DEV`)
- [ ] All Vercel secrets added (`VERCEL_TOKEN`, `VERCEL_ORG_ID`, `VERCEL_PROJECT_ID`)
- [ ] Redis secrets added (`REDIS_USERNAME`, `REDIS_PASSWORD`)
- [ ] Auth0/JWT secrets added (`AUTH0_DOMAIN`, `JWT_SECRET_DEV`)
- [ ] Postman secrets added (`POSTMAN_API_KEY`, `POSTMAN_WORKSPACE_ID`)

---

## 🧪 Testing the Pipeline

After secrets are configured:

1. Go to: https://github.com/ggx-insurance-saas/Insurance-company-Saas/actions
2. Select **"Deploy to Development"** workflow
3. Click **"Run workflow"** → Select `dev` branch
4. Monitor the deployment execution

---

## 📁 Related Configuration Files

| File | Purpose |
|------|---------|
| `.github/workflows/deploy-dev.yml` | Development deployment pipeline |
| `.github/workflows/deploy-all-78-services.yml` | Full Foundation services deployment |
| `.github/workflows/multi-env-deployment.yml` | Multi-environment deployment |
| `railway.toml` | Railway project configuration |
| `vercel.json` | Vercel project configuration |

---

## 🔄 Credential Rotation Plan (Pre-Production)

Before moving to production, rotate ALL credentials:

| Credential | Action | Timeline |
|------------|--------|----------|
| Railway Token | Regenerate via railway.app/settings | Before staging |
| Vercel Token | Regenerate via vercel.com/tokens | Before staging |
| Redis Password | Rotate via Redis Cloud console | Before staging |
| JWT Secret | Generate new secret (32+ chars) | Before staging |
| GitHub PAT | Regenerate via GitHub settings | Before staging |
| Postman Key | Regenerate via Postman settings | Before staging |

---

## 🔗 Useful Links

| Service | Dashboard |
|---------|-----------|
| Railway | https://railway.app/dashboard |
| Vercel | https://vercel.com/ggx-tech |
| GitHub Actions | https://github.com/ggx-insurance-saas/Insurance-company-Saas/actions |
| Auth0 | https://manage.auth0.com/dashboard/us/dev-xebb447lu1yuyuv4 |
| Postman | https://gazal-gidix-227419.postman.co |

---

**Last Updated:** 2026-02-09
**Status:** Ready for setup
**Next Step:** Add secrets to GitHub and test deployment
