# Feature Flags Service - Runbook

## Table of Contents
1. [Startup](#startup)
2. [Health Checks](#health-checks)
3. [Common Issues](#common-issues)
4. [Monitoring](#monitoring)
5. [Maintenance](#maintenance)
6. [Disaster Recovery](#disaster-recovery)

## Startup

### Normal Startup Procedure

1. **Verify Prerequisites**
   ```bash
   # Check MongoDB
   mongosh --eval "db.adminCommand('ping')"

   # Check Redis
   redis-cli ping

   # Check Kafka (optional)
   kafka-topics.sh --bootstrap-server localhost:9092 --list
   ```

2. **Start the Service**
   ```bash
   java -jar feature-flags-service-1.0.0.jar \
     --spring.profiles.active=prod \
     --spring.data.mongodb.uri=mongodb://user:pass@localhost:27017/feature-flags \
     --spring.redis.host=localhost \
     --spring.kafka.bootstrap-servers=localhost:9092
   ```

3. **Verify Startup**
   ```bash
   # Check health endpoint
   curl http://localhost:8080/api/v1/actuator/health

   # Expected response: {"status":"UP"}
   ```

## Health Checks

### Service Health

```bash
curl http://localhost:8080/api/v1/actuator/health
```

**Indicators:**
- `status: UP` - All systems healthy
- `status: DOWN` - One or more components failing

### Component Health

```bash
# Detailed health breakdown
curl http://localhost:8080/api/v1/actuator/health | jq '.components'
```

**Components to Monitor:**
- `db` - MongoDB connection
- `redis` - Redis connection
- `kafka` - Kafka connection (if enabled)

## Common Issues

### Issue: MongoDB Connection Failed

**Symptoms:**
- Health check shows `db.status: DOWN`
- Errors in logs: "Timeout connecting to MongoDB"

**Resolution:**
1. Check MongoDB is running:
   ```bash
   mongosh --eval "db.adminCommand('ping')"
   ```
2. Verify connection string in application.yml
3. Check network connectivity
4. Verify credentials

### Issue: High Memory Usage

**Symptoms:**
- JVM using > 80% of heap
- Garbage collection warnings in logs

**Resolution:**
1. Check heap configuration:
   ```bash
   jinfo -flag HeapSizeMetrics <pid>
   ```
2. Analyze heap dump:
   ```bash
   jmap -dump:format=b,file=heap.hprof <pid>
   ```
3. Increase heap if needed:
   ```bash
   java -Xmx2g -Xms1g -jar feature-flags-service-1.0.0.jar
   ```

### Issue: Cache Misses

**Symptoms:**
- High MongoDB query rate
- Slow API response times

**Resolution:**
1. Check Redis connectivity:
   ```bash
   redis-cli ping
   ```
2. Verify cache is enabled:
   ```bash
   curl http://localhost:8080/api/v1/actuator/configprops | jq '.contexts.application.beans.cacheConfig'
   ```
3. Check cache hit rates in logs

### Issue: Kafka Events Not Publishing

**Symptoms:**
- Events not reaching consumers
- "Failed to publish event" errors in logs

**Resolution:**
1. Check Kafka connectivity:
   ```bash
   kafka-topics.sh --bootstrap-server localhost:9092 --list
   ```
2. Verify topic exists:
   ```bash
   kafka-topics.sh --bootstrap-server localhost:9092 --topic feature-flag.events --describe
   ```
3. Check producer configuration
4. Verify Kafka is not disabled via profile

## Monitoring

### Key Metrics

Monitor these metrics via Actuator:

```bash
# JVM Memory
curl http://localhost:8080/api/v1/actuator/metrics/jvm.memory.used

# HTTP Request Traffic
curl http://localhost:8080/api/v1/actuator/metrics/http.server.requests

# Cache Metrics
curl http://localhost:8080/api/v1/actuator/metrics/cache.gets

# MongoDB Metrics
curl http://localhost:8080/api/v1/actuator/metrics/mongodb.driver.commands
```

### Prometheus Setup

The service exposes metrics at `/actuator/prometheus`.

**Prometheus configuration:**
```yaml
scrape_configs:
  - job_name: 'feature-flags-service'
    metrics_path: '/api/v1/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

### Alerting Rules

**Recommended alerts:**

```yaml
groups:
  - name: feature_flags_service
    rules:
      - alert: HighErrorRate
        expr: rate(http_server_requests_seconds_count{status=~"5.."}[5m]) > 0.05
        for: 5m
        annotations:
          summary: "High error rate detected"

      - alert: HighLatency
        expr: histogram_quantile(0.95, http_server_requests_seconds_bucket) > 1
        for: 5m
        annotations:
          summary: "95th percentile latency > 1s"

      - alert: ServiceDown
        expr: up{job="feature-flags-service"} == 0
        for: 2m
        annotations:
          summary: "Feature Flags Service is down"
```

## Maintenance

### Rolling Deployment

1. **Deploy to first instance**
   ```bash
   kubectl rollout restart deployment/feature-flags-service -n production
   ```

2. **Wait for health check**
   ```bash
   kubectl wait --for=condition=ready pod -l app=feature-flags-service -n production --timeout=60s
   ```

3. **Verify traffic**
   ```bash
   kubectl logs -f deployment/feature-flags-service -n production
   ```

### Database Migration

**Note:** This service uses MongoDB with no migrations currently planned.

**For schema changes:**
1. Deploy new service version with backward-compatible changes
2. Run data migration scripts if needed
3. Monitor for errors
4. Remove old code paths in next deployment

### Cache Clearing

To clear all caches:

```bash
redis-cli FLUSHDB
```

To clear specific cache keys:

```bash
redis-cli --scan --pattern "feature-flag:*" | xargs redis-cli DEL
```

## Disaster Recovery

### Backup Strategy

**MongoDB Backup:**
```bash
# Create backup
mongodump --uri="mongodb://localhost:27017/feature-flags" --out=/backup/$(date +%Y%m%d)

# Restore from backup
mongorestore --uri="mongodb://localhost:27017/feature-flags" --dir=/backup/20240101
```

**Redis Backup:**
```bash
# Create snapshot
redis-cli BGSAVE

# Copy RDB file
cp /var/lib/redis/dump.rdb /backup/redis_$(date +%Y%m%d).rdb
```

### Recovery Procedure

1. **Restore MongoDB**
   ```bash
   mongorestore --uri="mongodb://localhost:27017/feature-flags" --dir=/backup/latest
   ```

2. **Start Service**
   ```bash
   java -jar feature-flags-service-1.0.0.jar
   ```

3. **Verify Data**
   ```bash
   curl -H "tenantId: test-tenant" http://localhost:8080/api/v1/feature-flags
   ```

### Failure Scenarios

#### Scenario: Total Data Loss

**Recovery Steps:**
1. Restore from latest backup
2. Replay Kafka events from last backup timestamp
3. Verify data consistency
4. Resume normal operations

#### Scenario: Region Failure

**Recovery Steps:**
1. Switch DNS to failover region
2. Verify health checks
3. Monitor replication lag when primary region recovers

## Emergency Contacts

- **On-Call Engineer**: +1-XXX-XXX-XXXX
- **Engineering Lead**: +1-XXX-XXX-XXXX
- **Infrastructure Team**: infrastructure@gogidix.com
