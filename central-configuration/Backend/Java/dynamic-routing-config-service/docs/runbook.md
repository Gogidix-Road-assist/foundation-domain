# Dynamic Routing Config Service - Runbook

## Table of Contents

1. [Service Overview](#service-overview)
2. [Common Issues and Solutions](#common-issues-and-solutions)
3. [Monitoring and Alerts](#monitoring-and-alerts)
4. [Maintenance Procedures](#maintenance-procedures)
5. [Disaster Recovery](#disaster-recovery)
6. [Performance Tuning](#performance-tuning)

## Service Overview

### Purpose
Manages dynamic routing rules for the Rapid Assist platform, enabling runtime routing configuration without service restarts.

### Dependencies
- MongoDB (persistence)
- Redis (caching)
- Kafka (event streaming)
- Tenant Service (tenant validation)

### Key Metrics
- Request latency (p50, p95, p99)
- Error rate
- Cache hit/miss ratio
- Event publishing latency
- Active routing rules count

## Common Issues and Solutions

### High Latency

**Symptoms:**
- API response times > 1s
- Increased request queue depth

**Diagnosis:**
```bash
# Check cache hit ratio
curl http://localhost:8080/api/v1/actuator/metrics/cache.ratio

# Check MongoDB query performance
curl http://localhost:8080/api/v1/actuator/mongodb/query.stats

# Check thread pool usage
curl http://localhost:8080/api/v1/actuator/metrics/jvm.threads.live
```

**Solutions:**
1. Verify Redis is healthy
2. Check MongoDB indexing
3. Review circuit breaker configuration
4. Scale horizontally if needed

### Routing Rules Not Taking Effect

**Symptoms:**
- New rules not working
- Old rules still active after update

**Diagnosis:**
```bash
# Check event publishing status
curl http://localhost:8080/api/v1/actuator/health/kafka

# Verify cache invalidation
redis-cli FLUSHDB
```

**Solutions:**
1. Clear Redis cache
2. Restart service
3. Verify Kafka event delivery
4. Check rule priority conflicts

### Tenant Data Leakage

**CRITICAL** - Must address immediately

**Symptoms:**
- Tenant A sees Tenant B's routing rules
- Cross-tenant data access

**Diagnosis:**
```bash
# Run tenant isolation test
mvn test -Dtest=TenantIsolationTest

# Check MongoDB queries
db.routing_rules.find({tenantId: "tenant-a"})
```

**Solutions:**
1. Immediately verify tenant ID filtering
2. Review all database queries
3. Check application-level filtering
4. Run integration tests

### High Memory Usage

**Symptoms:**
- JVM heap usage > 80%
- Frequent GC pauses

**Diagnosis:**
```bash
# Check heap usage
curl http://localhost:8080/api/v1/actuator/metrics/jvm.memory.used

# Check GC metrics
curl http://localhost:8080/api/v1/actuator/metrics/jvm.gc.pause
```

**Solutions:**
1. Review cache configuration (reduce TTL/size)
2. Check for memory leaks
3. Increase heap size
4. Profile application

## Monitoring and Alerts

### Key Metrics to Monitor

#### Application Metrics
- `http.server.requests` - Request count and latency
- `routing.rules.active` - Count of active rules
- `routing.rules.created` - Rules created rate
- `routing.rules.updated` - Rules updated rate
- `routing.rules.deleted` - Rules deleted rate

#### Infrastructure Metrics
- `jvm.memory.used` - Heap usage
- `jvm.gc.pause` - GC pause time
- `process.cpu.usage` - CPU usage
- `cache.hits` / `cache.misses` - Cache performance

#### Database Metrics
- `mongodb.connections` - Connection pool usage
- `mongodb.query.time` - Query latency
- `mongodb.operations.count` - Operation counts

#### Message Broker Metrics
- `kafka.producer.record.send.rate` - Message rate
- `kafka.producer.record.error.rate` - Error rate

### Alert Thresholds

| Metric | Warning | Critical |
|--------|---------|----------|
| Error rate | > 1% | > 5% |
| Latency (p95) | > 500ms | > 1s |
| Cache hit ratio | < 80% | < 50% |
| Kafka publishing lag | > 1000 | > 10000 |
| Heap usage | > 75% | > 90% |
| DB connections | > 80% | > 95% |

## Maintenance Procedures

### Regular Maintenance

#### Daily
- Check health endpoints
- Review error logs
- Verify event publishing

#### Weekly
- Review performance metrics
- Check cache efficiency
- Analyze query performance

#### Monthly
- Review and optimize indexes
- Clean up old events from event store
- Capacity planning

### Deploying Updates

#### Zero-Downtime Deployment

1. Pre-deployment checks:
```bash
mvn test
mvn verify
```

2. Deploy to one instance:
```bash
kubectl patch deployment dynamic-routing-config-service -p '{"spec":{"template":{"spec":{"containers":[{"name":"app","env":[{"name":"VERSION","value":"new-version"}]}]}}}}'
```

3. Verify health:
```bash
kubectl rollout status deployment/dynamic-routing-config-service
```

4. Roll out to remaining instances

#### Rollback Procedure

If issues detected:
```bash
kubectl rollout undo deployment/dynamic-routing-config-service
```

### Database Maintenance

#### Index Optimization
```javascript
// MongoDB
db.routing_rules.createIndex({ "tenantId": 1, "environment": 1, "ruleName": 1 })
db.routing_rules.createIndex({ "tenantId": 1, "priority": 1, "active": 1 })
```

#### Event Store Cleanup
```javascript
// Delete events older than 90 days
db.routing_rule_events.deleteMany({
  occurredAt: { $lt: new Date(Date.now() - 90 * 24 * 60 * 60 * 1000) }
})
```

## Disaster Recovery

### Backup Strategy

#### MongoDB Backups
- Daily automated backups
- Retention: 30 days
- Store in secure offsite location

#### Redis Backups
- RDB snapshots every hour
- AOF enabled for durability
- Retention: 7 days

### Recovery Procedures

#### Restore from Backup

1. Stop the service
2. Restore MongoDB from backup
3. Flush Redis cache
4. Start the service
5. Verify data integrity

#### Event Replay

If event store is corrupted:
```bash
# Identify last consistent state
curl http://localhost:8080/api/v1/actuator/events/last-consistent

# Replay events from that point
curl -X POST http://localhost:8080/api/v1/admin/events/replay \
  -d '{"sinceEventId": "event-123"}'
```

## Performance Tuning

### JVM Tuning

```
-Xms2g -Xmx4g
-XX:+UseG1GC
-XX:MaxGCPauseMillis=200
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=/logs/
```

### Connection Pool Tuning

```yaml
spring:
  data:
    mongodb:
      # Maximum pool size
      max-connections-per-host: 50
      # Connection timeout
      connect-timeout: 5000
      # Socket timeout
      socket-timeout: 30000
```

### Cache Tuning

```yaml
app:
  routing:
    cache:
      enabled: true
      ttl-seconds: 600
      max-size: 10000
```

### Kafka Tuning

```yaml
spring:
  kafka:
    producer:
      # Batch size
      batch-size: 16384
      # Linger time
      linger-ms: 10
      # Buffer size
      buffer-memory: 33554432
```

## Security Procedures

### Incident Response

1. **Identify** - Determine scope of security issue
2. **Contain** - Isolate affected systems
3. **Eradicate** - Remove threat
4. **Recover** - Restore normal operations
5. **Lessons Learned** - Document and improve

### Access Control

- Regular audit of API access logs
- Review of tenant isolation tests
- Validation of JWT token expiration
- Monitoring of failed authentication attempts

## Escalation Contacts

| Role | Name | Contact |
|------|------|---------|
| Service Owner | Platform Team | platform@gogidix.com |
| On-Call Engineer | DevOps Team | oncall@gogidix.com |
| Security Team | Security | security@gogidix.com |
