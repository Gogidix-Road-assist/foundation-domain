# Access Control Service - Runbook

## Operations Guide

### Health Checks

```bash
# Service health
curl http://localhost:8080/api/v1/status

# Actuator health (if enabled)
curl http://localhost:8080/actuator/health
```

### Metrics

```bash
# Prometheus metrics
curl http://localhost:8080/actuator/prometheus
```

### Monitoring

Key metrics to monitor:
- `access_check_duration_seconds` - Access check latency
- `permission_cache_hit_ratio` - Cache effectiveness
- `kafka_publish_failures_total` - Event publishing failures

### Troubleshooting

#### High Latency

1. Check Redis cache hit ratio
2. Verify MongoDB indexes are created
3. Check network latency to MongoDB

#### Cache Issues

```bash
# Clear entire tenant cache
curl -X DELETE http://localhost:8080/api/v1/cache/tenant/{tenantId}

# Clear subject cache
curl -X DELETE http://localhost:8080/api/v1/cache/subject/{subjectId}
```

#### Tenant Isolation Issues

All queries MUST include tenantId filter. Check logs for:
```
Cross-tenant access attempt: tenantId=...
```

### Deployment

```bash
# Build
mvn clean package -DskipTests

# Docker
docker build -t access-control-service .

# Run
docker run -p 8080:8080 \
  -e MONGODB_URI=... \
  -e REDIS_HOST=... \
  -e KAFKA_SERVERS=... \
  access-control-service
```

### Rollback

```bash
# Railway CLI
railway rollback --service access-control-service

# Or redeploy previous version
railway up --service access-control-service --version <previous-version>
```
