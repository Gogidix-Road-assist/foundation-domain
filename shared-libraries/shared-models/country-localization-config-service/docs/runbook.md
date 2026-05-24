# Country Localization Config Service Runbook

## Operational Guide for Country Localization Config Service

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
- Timeouts when saving localizations
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
- Old localization values being returned
- Updates not appearing

**Diagnosis:**
```bash
# Check Redis connectivity
redis-cli ping
redis-cli KEYS "country:*:*"
```

**Resolution:**
1. Clear cache for affected country
2. Check cache TTL settings
3. Verify cache invalidation logic

---

## Common Issues

### Issue: "Country localization not found" for existing country

**Cause:** Wrong country code format

**Resolution:**
1. Verify country code is ISO 3166-1 alpha-2 format (e.g., "IE", "US", "GB")
2. Check case sensitivity (uppercase required)
3. Verify localization exists

### Issue: "Failed to publish event to Kafka"

**Cause:** Kafka is down or misconfigured

**Resolution:**
1. Check Kafka connectivity
2. Verify Kafka configuration in application.yml
3. Check Kafka topic exists: `kafka-topics.sh --list`

### Issue: "Validation failed for country code"

**Cause:** Invalid ISO country code

**Resolution:**
1. Check the country code against ISO 3166-1 alpha-2 standard
2. Verify uppercase format
3. Ensure 2-letter code

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
mongosh country-localization-db --eval "
  db.country_localizations.createIndex(
    { countryCode: 1 },
    { unique: true }
  )
"

# Check index usage
mongosh country-localization-db --eval "db.country_localizations.getIndexes()"
```

### Cache Maintenance

```bash
# Monitor Redis memory
redis-cli INFO memory

# Flush cache for specific country (use with caution)
redis-cli KEYS "country:IE:*" | xargs redis-cli DEL
```

### Backup Procedures

1. **Database Backup** (handled by MongoDB)
   ```bash
   mongodump --uri="mongodb://localhost:27017" --db=country-localization-db
   ```

2. **Configuration Export**
   ```bash
   curl http://localhost:8080/api/v1/country-localizations \
        | jq '.' > country-localizations-backup.json
   ```

---

## Contact

For issues or questions, contact the Platform Team.
