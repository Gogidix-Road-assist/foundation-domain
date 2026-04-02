# ✅ Complete Deployment Checklist - 78 Services

**Target Environment**: Development Cloud (Railway + Vercel)
**Total Services**: 78 (77 Java + 1 Node.js)
**Branch**: `dev`

---

## 📋 Pre-Deployment Checklist (Before You Start)

### 1. Account Setup ✅
- [ ] GitHub account with repository access
- [ ] Railway account created (https://railway.app)
- [ ] MongoDB Atlas account created (https://www.mongodb.com/cloud/atlas)
- [ ] Supabase account created (https://supabase.com)
- [ ] Vercel account created (https://vercel.com)

### 2. Database Setup ✅
- [ ] MongoDB Atlas cluster created (M0 free tier or higher)
- [ ] MongoDB database user created
- [ ] MongoDB IP whitelist: `0.0.0.0/0` (for Railway)
- [ ] MongoDB connection string saved
- [ ] Supabase project created
- [ ] Supabase SQL schema executed
- [ ] Supabase connection strings saved

### 3. GitHub Secrets Configuration ✅
Go to: `https://github.com/<username>/<repo>/settings/secrets/actions`

Add these secrets:
- [ ] `RAILWAY_TOKEN` - From Railway account settings
- [ ] `VERCEL_TOKEN` - From Vercel account settings
- [ ] `VERCEL_ORG_ID` - From Vercel project settings
- [ ] `VERCEL_PROJECT_ID` - From Vercel project settings
- [ ] `MONGODB_URI` - MongoDB connection string
- [ ] `SUPABASE_DB_URL` - Supabase PostgreSQL connection string
- [ ] `SUPABASE_URL` - Supabase project URL
- [ ] `SUPABASE_ANON_KEY` - Supabase anonymous key
- [ ] `SUPABASE_SERVICE_ROLE_KEY` - Supabase service role key
- [ ] `JWT_SECRET` - Strong random secret (generate one)

### 4. Local Environment Setup ✅
- [ ] Git installed and configured
- [ ] Node.js 18+ installed
- [ ] Railway CLI installed: `npm install -g @railway/cli`
- [ ] Vercel CLI installed: `npm install -g vercel`
- [ ] Docker installed (for local testing)

---

## 🚀 Deployment Steps

### Step 1: Generate Deployment Files

```bash
cd Rapid-Assist/Foundation-Domain
chmod +x generate-dockerfiles.sh
./generate-dockerfiles.sh
```

This creates for each service:
- `Dockerfile` - Container configuration
- `railway.toml` - Railway deployment config
- `.railway.env.template` - Environment variables template

**Result**: 234 files created (78 services × 3 files)

---

### Step 2: Update Environment Variables

For each domain, update the `.railway.env.template` files:

```bash
# MongoDB URI (replace with your actual values)
MONGODB_URI=mongodb+srv://rapidassist:YOUR_PASSWORD@cluster0.xxxxx.mongodb.net/rapid_assist_foundation?retryWrites=true&w=majority

# Supabase (replace with your actual values)
SUPABASE_DB_URL=postgresql://postgres:YOUR_PASSWORD@db.xxxxx.supabase.co:5432/postgres
SUPABASE_URL=https://xxxxx.supabase.co
SUPABASE_ANON_KEY=your-anon-key
SUPABASE_SERVICE_ROLE_KEY=your-service-role-key

# JWT (generate a strong secret)
JWT_SECRET=your-generated-secret-here
```

**Tip**: Use `find` to update all at once:
```bash
find . -name ".railway.env.template" -exec sed -i 's/mongodb+srv:\/\/<username>:<password>@/mongodb+srv:\/\/rapidassist:YOUR_PASS@/g' {} \;
```

---

### Step 3: Create Railway Projects

Railway free tier allows **5 services per project**. For 78 services, you need **16 projects**.

#### Railway Project Structure:

**Project 1: Infrastructure-1** (Ports 8300-8304)
- access-control-service
- alerting-service
- anti-fraud-rules-service
- anti-fraud-signals-service
- api-gateway

**Project 2: Infrastructure-2** (Ports 8305-8309)
- api-keys-service
- audit-correlation-service
- billing-service
- courier-adapter-service
- currency-converter-service

... (continue pattern)

**Project 8: Infrastructure-8** (Ports 8334-8338)
- session-token-service
- template-messaging-service
- tenant-org-service
- user-profile-service
- webhook-delivery-service

**Project 9: AI-Services-1** (Ports 8100-8104)
... (6 projects for 27 AI services)

**Project 15: Central-Config** (Ports 8000-8007)
... (2 projects for 8 config services)

**Project 16: Dashboard** (Ports 8200-8202 + 3000)
... (1 project for 4 dashboard services)

---

### Step 4: Deploy via GitHub Actions

Push to dev branch:

```bash
git checkout dev
git add .
git commit -m "Enable deployment for all 78 services"
git push origin dev
```

GitHub Actions will:
1. ✅ Build all 78 services
2. ✅ Deploy to Railway in phases
3. ✅ Deploy to Vercel
4. ✅ Run health checks

**Estimated time**: 20-40 minutes

---

### Step 5: Monitor Deployment

Watch the deployment:

```bash
# GitHub Actions
https://github.com/<username>/<repo>/actions

# Railway Dashboard
https://railway.app/dashboard

# Check service logs (for a specific service)
railway logs --service feature-flags-service
```

---

## ✅ Post-Deployment Verification

### Health Check All Services

```bash
# Shared-Infrastructure (39 services)
for port in {8300..8338}; do
    curl -f "https://service-$port.railway.app/actuator/health" || echo "Service on port $port not healthy"
done

# Central-Configuration (8 services)
for port in {8000..8007}; do
    curl -f "https://service-$port.railway.app/actuator/health" || echo "Service on port $port not healthy"
done

# AI-Services (27 services)
for port in {8100..8126}; do
    curl -f "https://service-$port.railway.app/actuator/health" || echo "Service on port $port not healthy"
done

# Dashboard (4 services)
curl "https://dashboard-configuration-service.railway.app/actuator/health"
curl "https://dashboard-analytics-service.railway.app/actuator/health"
curl "https://dashboard-reporting-service.railway.app/actuator/health"
curl "https://dashboard-aggregation-service.railway.app/health"
```

### Test Database Connectivity

Check service logs for successful MongoDB connection:
```
MongoDB connection established: rapid_assist_foundation
```

### Test Frontend

Open Vercel URL and verify:
- [ ] Page loads successfully
- [ ] No console errors
- [ ] API calls succeed
- [ ] Dashboard displays data

---

## 🎯 Service Summary by Domain

| Domain | Services | Ports | Railway Projects |
|--------|----------|-------|------------------|
| Shared-Infrastructure | 39 | 8300-8338 | 8 projects |
| AI-Services | 27 | 8100-8126 | 6 projects |
| Central-Configuration | 8 | 8000-8007 | 2 projects |
| Centralized-Dashboard | 4 | 8200-8202, 3000 | 1 project |
| **Total** | **78** | **3000, 8000-8338** | **17 projects** |

---

## 💰 Cost Estimation

### Railway Pricing (as of 2024)

**Free Tier**:
- 5 services per project
- $0/month
- 512 MB RAM per service
- 1 GB storage per service

**Paid Tier** (if needed):
- $5/month per service
- 512 MB RAM → 1 GB RAM
- Better performance

**For 78 services**:
- Free tier: 16 projects × 5 services = 80 service slots (sufficient!)
- Cost: **$0/month** (using free tier strategically)

### MongoDB Atlas Pricing

- **M0 Free Tier**: 512 MB storage
- **M2**: $9/month (2 GB storage)
- **M5**: $19/month (5 GB storage)

**Recommendation**: Start with M0, upgrade to M2 if needed.

### Vercel Pricing

- **Hobby**: Free (sufficient for development)
- **Pro**: $20/month (for production)

---

## 🔧 Troubleshooting

### Issue: Service Not Starting
**Solution**:
```bash
railway logs --service <service-name>
# Check for errors in logs
# Common issues: missing env vars, database connection failure
```

### Issue: MongoDB Connection Failed
**Solution**:
- Verify IP whitelist includes `0.0.0.0/0`
- Check connection string format
- Verify database user credentials

### Issue: Out of Memory
**Solution**:
- Railway free tier: 512 MB RAM per service
- Upgrade service to paid tier ($5/month) for more RAM
- Or reduce number of deployed services

### Issue: GitHub Actions Timeout
**Solution**:
- Deploy in smaller batches
- Increase timeout in workflow file

---

## 📞 Support Resources

- **Railway Docs**: https://docs.railway.app
- **MongoDB Atlas Docs**: https://docs.atlas.mongodb.com
- **Supabase Docs**: https://supabase.com/docs
- **Vercel Docs**: https://vercel.com/docs
- **GitHub Actions Docs**: https://docs.github.com/actions

---

## 🎉 Success Criteria

Deployment is successful when:

- [x] All 78 services show "Healthy" in Railway
- [x] All service health endpoints return 200 OK
- [x] MongoDB connected (check logs)
- [x] Supabase connected (check logs)
- [x] Frontend loads on Vercel
- [x] Frontend can call backend APIs
- [x] GitHub Actions workflow completes without errors
- [x] Service logs show no critical errors

---

**Ready to deploy?** Follow the steps above and you'll have all 78 services running in the cloud! 🚀
