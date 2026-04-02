# Rate Limit Policy Service Runbook

## Operational Guide for Rate Limit Policy Service

## Table of Contents
1. [Deployment](#deployment)
2. [Health Monitoring](#health-monitoring)
3. [Incident Response](#incident-response)
4. [Common Issues](#common-issues)
5. [Maintenance](#maintenance)

---

## Deployment

### Prerequisites Checklist

- [ ] MongoDB is accessible
- [ ] Redis is accessible
- [ ] Kafka is running (if events enabled)
- [ ] Environment variables are set
- [ ] JWT issuer URI is configured

### Deployment Steps

1. **Build the service**
   ```bash
   mvn clean package -DskipTests
   ```

2. **Verify the JAR was created**
   ```bash
   ls -lh target/*.jar
   ```

3. **Deploy to Railway** (or your platform)
   ```bash
   railway up
   ```

4. **Verify deployment**
   ```bash
   curl https://your-service.railway.app/actuator/health
   ```

### Rollback Procedure

1. Identify the previous stable version
2. Update railway.json or deployment config
3. Redeploy:
   ```bash
   railway up
   ```
4. Verify health endpoint

---

## Health Monitoring

### Health Indicators

| Component | Health Check | Recovery |
|-----------|--------------|----------|
| Database | `/actuator/health/db` | Restart service, check MongoDB |
| Redis | `/actuator/health/redis` | Check Redis, restart service |
| Kafka | `/actuator/health/kafka` | Check Kafka, restart service |

### Monitoring Commands

```bash
# Check overall health
curl http://localhost:8080/actuator/health

# Check metrics
curl http://localhost:8080/actuator/metrics

# Check info
curl http://localhost:8080/actuator/info

# Check Prometheus metrics
curl http://localhost:8080/actuator/prometheus
```

### Key Metrics to Monitor

- `jvm.memory.used` - Memory usage
- `http.server.requests` - Request count
- `mongodb.driver.commands` - DB operations
- `cache.gets` - Cache hits
- `kafka.producer.record.send` - Event publishing

---

## Incident Response

### Incident: High Memory Usage

**Symptoms:**
- Slow response times
- OOM errors in logs

**Diagnosis:**
```bash
# Check memory metrics
curl http://localhost:8080/actuator/metrics/jvm.memory.used

# Check heap dump
jcmd <pid> GC.heap_info
```

**Resolution:**
1. Check for memory leaks in policy caching
2. Adjust Redis cache size in application.yml
3. Restart service if necessary
4. Consider horizontal scaling

### Incident: Tenant Data Leak

**CRITICAL: Immediate Action Required**

**Symptoms:**
- Tenant A can see Tenant B's policies
- Test failures in TenantIsolationTest

**Diagnosis:**
```bash
# Run tenant isolation test
mvn test -Dtest=TenantIsolationTest
```

**Resolution:**
1. Immediately disable affected endpoints
2. Review MongoDB queries for proper tenant filtering
3. Check all repository methods include tenant filtering
4. Verify security filters are properly configured
5. Do NOT restore service until tests pass

### Incident: Cache Stale Data

**Symptoms:**
- Policies not reflecting recent changes
- Inconsistent behavior across instances

**Resolution:**
```bash
# Clear Redis cache
redis-cli FLUSHDB

# Or clear specific keys
redis-cli --scan --pattern 'rate-limit-policy:*' | xargs redis-cli DEL
```

### Incident: Kafka Events Not Publishing

**Symptoms:**
- Downstream services not receiving events
- No errors in logs

**Diagnosis:**
```bash
# Check Kafka connectivity
curl http://localhost:8080/actuator/health/kafka

# Check Kafka topics
kafka-topics.sh --list --bootstrap-server localhost:9092
```

**Resolution:**
1. Verify Kafka broker is running
2. Check bootstrap-servers configuration
3. Verify topic exists: `rate-limit-policy.events`
4. Check network connectivity to Kafka
5. Review event publishing logs

---

## Common Issues

### Issue: Policy Not Found

**Symptoms:** 404 when querying by policy key

**Possible Causes:**
1. Incorrect tenant ID
2. Policy key doesn't exist
3. Wrong environment

**Resolution:**
```bash
# Verify policy exists in MongoDB
mongosh
use rate-limit-policy-service-prod
db.rate_limit_policies.findOne({ policyKey: "your-key", tenantId: "your-tenant" })
```

### Issue: Duplicate Policy Key

**Symptoms:** 409 Conflict when creating policy

**Resolution:**
1. Check if policy already exists
2. Use different policy key
3. Or update existing policy instead of creating new

### Issue: Slow Query Performance

**Diagnosis:**
```bash
# Check MongoDB slow queries
mongosh
db.setProfilingLevel(2)
db.system.profile.find().sort({ millis: -1 }).limit(10)
```

**Resolution:**
1. Add indexes on tenantId + policyKey
2. Add indexes on environment
3. Review query patterns
4. Consider cache warming

---

## Maintenance

### Regular Maintenance Tasks

#### Daily
- Monitor health endpoints
- Check error rates
- Review cache hit rates

#### Weekly
- Review tenant isolation test results
- Check disk usage on MongoDB
- Monitor Kafka consumer lag

#### Monthly
- Review and rotate logs
- Update dependencies
- Performance testing
- Security audit

### Database Maintenance

```bash
# Create indexes
db.rate_limit_policies.createIndex({ tenantId: 1, policyKey: 1 }, { unique: true })
db.rate_limit_policies.createIndex({ tenantId: 1, environment: 1 })
db.rate_limit_policies.createIndex({ tenantId: 1, enabled: 1 })

# Compact database
db.runCommand({ compact: "rate_limit_policies" })

# Check stats
db.stats()
db.rate_limit_policies.stats()
```

### Cache Warming Strategy

```bash
# Preload commonly accessed policies
curl -H "X-Tenant-ID: tenant-123" \
  http://localhost:8080/api/v1/rate-limit-policies?page=0&size=100
```

---

## Backup and Recovery

### MongoDB Backup

```bash
# Create backup
mongodump --host localhost --port 27017 \
  --db rate-limit-policy-service-prod \
  --out /backup/$(date +%Y%m%d)

# Restore backup
mongorestore --host localhost --port 27017 \
  --db rate-limit-policy-service-prod \
  /backup/20240120/rate-limit-policy-service-prod
```

### Configuration Backup

```bash
# Backup application config
cp application-prod.yml backup/
cp application-dev.yml backup/

# Backup environment variables
env | grep RATE_LIMIT > backup/env.txt
```

---

## Security Considerations

### Tenant Isolation Validation

**CRITICAL:** Always verify tenant isolation after deployment:

```bash
mvn test -Dtest=TenantIsolationTest
```

### Rate Limit Abuse

Monitor for:
- Unusual policy creation patterns
- Rapid policy updates
- Attempts to create global policies

### Audit Trail

All policy changes are logged with:
- User who made the change
- Timestamp
- Reason for change
- Previous and new values

---

## Contact and Escalation

| Issue Type | Contact |
|------------|---------|
| Critical Outage | on-call@company.com |
| Security Issue | security@company.com |
| Performance Issue | performance@company.com |
| General Support | support@company.com |
