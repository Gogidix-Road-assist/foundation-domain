# Config Service Runbook

## Operational Guide for Config Service

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
- `hikaricp.connections.active` - DB connections
- `cache.gets` - Cache hits
- `kafka.consumer.records.lag` - Consumer lag

---

## Incident Response

### Incident: High Memory Usage

**Symptoms:**
- Slow response times
- OOM errors in logs
- Health check fails

**Diagnosis:**
```bash
# Check JVM memory
curl http://localhost:8080/actuator/metrics/jvm.memory.used
```

**Resolution:**
1. Increase JVM heap: `JAVA_OPTS=-Xmx2g`
2. Check for memory leaks: `jmap -histo:live <pid>`
3. Restart service if needed

### Incident: Database Connection Pool Exhausted

**Symptoms:**
- Timeouts when saving configurations
- "Connection pool exhausted" in logs

**Diagnosis:**
```bash
# Check connection pool metrics
curl http://localhost:8080/actuator/metrics/hikaricp.connections.active
curl http://localhost:8080/actuator/metrics/hikaricp.connections.pending
```

**Resolution:**
1. Increase pool size in application.yml
2. Check for slow queries
3. Restart service

### Incident: Cache Stale Data

**Symptoms:**
- Old configuration values being returned
- Updates not appearing

**Diagnosis:**
```bash
# Check Redis connectivity
redis-cli ping
redis-cli KEYS "tenant:*:config:*"
```

**Resolution:**
1. Clear cache for affected tenant
2. Check cache TTL settings
3. Verify cache invalidation logic

### Incident: Tenant Data Leak

**CRITICAL** - Immediate response required

**Symptoms:**
- Tenant A seeing Tenant B's data
- Cross-tenant access in logs

**Diagnosis:**
```bash
# Run TenantIsolationTest
mvn test -Dtest=TenantIsolationTest

# Check repository queries for tenant filtering
grep -r "findByTenantId" src/
```

**Resolution:**
1. **IMMEDIATELY** stop accepting new requests
2. Investigate the breach
3. Fix the missing tenant filter
4. Run full TenantIsolationTest suite
5. Audit all affected tenant data
6. Do NOT resume until tests pass

---

## Common Issues

### Issue: "Configuration not found" for existing config

**Cause:** Wrong tenant context in JWT token

**Resolution:**
1. Verify JWT contains correct `tenant_id` claim
2. Check `RequestContext.getTenantId()`
3. Verify configuration exists with correct tenant

### Issue: "Failed to publish event to Kafka"

**Cause:** Kafka is down or misconfigured

**Resolution:**
1. Check Kafka connectivity
2. Verify Kafka configuration in application.yml
3. Check Kafka topic exists: `kafka-topics.sh --list`

### Issue: "Validation failed for configuration"

**Cause:** Schema validation rejected the value

**Resolution:**
1. Check the schema definition
2. Verify value matches data type
3. Check allowed values in schema

---

## Maintenance

### Regular Tasks

**Daily:**
- Check health endpoint
- Review error logs
- Monitor key metrics

**Weekly:**
- Review disk usage
- Check cache hit rates
- Review slow queries

**Monthly:**
- Update dependencies
- Review and rotate logs
- Security audit

### Database Maintenance

```bash
# Create MongoDB indexes
mongosh config-service --eval "
  db.configurations.createIndex(
    { tenantId: 1, configKey: 1, environment: 1, namespace: 1 },
    { unique: true }
  )
"

# Check index usage
mongosh config-service --eval "db.configurations.getIndexes()"
```

### Cache Maintenance

```bash
# Monitor Redis memory
redis-cli INFO memory

# Flush cache for specific tenant (use with caution)
redis-cli KEYS "tenant:bad-tenant:*" | xargs redis-cli DEL
```

### Backup Procedures

1. **Database Backup** (handled by MongoDB)
   ```bash
   mongodump --uri="mongodb://localhost:27017" --db=config-service
   ```

2. **Configuration Export**
   ```bash
   curl -H "X-Tenant-ID: tenant-123" \
        http://localhost:8080/api/v1/configurations/tenant/tenant-123 \
        | jq '.' > tenant-123-config-backup.json
   ```

---

## Contact

For issues or questions, contact the Platform Team.
