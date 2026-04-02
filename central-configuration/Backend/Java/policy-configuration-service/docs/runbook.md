# Policy Configuration Service Runbook

## Table of Contents

1. [Service Overview](#service-overview)
2. [Pre-deployment Checklist](#pre-deployment-checklist)
3. [Deployment Procedures](#deployment-procedures)
4. [Post-deployment Verification](#post-deployment-verification)
5. [Troubleshooting](#troubleshooting)
6. [Rollback Procedures](#rollback-procedures)
7. [Monitoring and Alerts](#monitoring-and-alerts)

## Service Overview

- **Service Name**: policy-configuration-service
- **Version**: 1.0.0
- **Port**: 8080
- **Context Path**: /api/v1
- **Dependencies**: MongoDB, Redis, Kafka, Tenant Service

## Pre-deployment Checklist

### Infrastructure

- [ ] MongoDB 6.0+ is available and accessible
- [ ] Redis 7.0+ is available and accessible
- [ ] Kafka 3.0+ is available and accessible
- [ ] Tenant Service is accessible
- [ ] Required environment variables are set

### Environment Variables

Required:
- `MONGODB_HOST`, `MONGODB_PORT`, `MONGODB_DB`
- `MONGODB_USER`, `MONGODB_PASSWORD`
- `REDIS_HOST`, `REDIS_PORT`
- `KAFKA_SERVERS`
- `TENANT_SERVICE_URL`, `TENANT_SERVICE_API_KEY`

Optional:
- `SERVICE_PORT` (default: 8080)
- `REDIS_PASSWORD`
- `MONGODB_AUTO_INDEX_CREATION` (production: false)

### Configuration Files

- [ ] `application-prod.yml` is configured correctly
- [ ] Logging levels are appropriate for production
- [ ] Tenant isolation is enforced
- [ ] Event publishing is enabled

## Deployment Procedures

### Docker Deployment

1. **Build Docker Image**
   ```bash
   docker build -t gogidix/policy-configuration-service:1.0.0 .
   ```

2. **Run Container**
   ```bash
   docker run -d \
     --name policy-configuration-service \
     -p 8080:8080 \
     -e MONGODB_HOST=mongodb \
     -e MONGODB_DB=policy-configuration-service \
     -e REDIS_HOST=redis \
     -e KAFKA_SERVERS=kafka:9092 \
     -e TENANT_SERVICE_URL=http://tenant-service:8080 \
     gogidix/policy-configuration-service:1.0.0
   ```

### Kubernetes Deployment

1. **Create ConfigMap**
   ```bash
   kubectl create configmap policy-config \
     --from-file=application-prod.yml
   ```

2. **Create Secrets**
   ```bash
   kubectl create secret generic policy-secrets \
     --from-literal=mongodb-password=*** \
     --from-literal=redis-password=*** \
     --from-literal=tenant-service-api-key=***
   ```

3. **Deploy**
   ```bash
   kubectl apply -f k8s/deployment.yaml
   kubectl apply -f k8s/service.yaml
   ```

4. **Verify Pod Status**
   ```bash
   kubectl get pods -l app=policy-configuration-service
   kubectl logs -f deployment/policy-configuration-service
   ```

## Post-deployment Verification

### Health Checks

1. **Service Health**
   ```bash
   curl http://localhost:8080/api/v1/health
   ```
   Expected: `{"status":"UP"}`

2. **Actuator Endpoints**
   ```bash
   curl http://localhost:8080/actuator/health
   ```

### Smoke Tests

1. **Create Test Policy**
   ```bash
   curl -X POST http://localhost:8080/api/v1/policies \
     -H "X-Tenant-Id: test-tenant" \
     -H "Content-Type: application/json" \
     -d '{
       "policyKey": "test.policy",
       "name": "Test Policy",
       "type": "SECURITY",
       "rules": {"level": "high"},
       "scope": {"type": "GLOBAL"},
       "environment": "development"
     }'
   ```

2. **Retrieve Policy**
   ```bash
   curl http://localhost:8080/api/v1/policies \
     -H "X-Tenant-Id: test-tenant"
   ```

3. **Verify Tenant Isolation**
   ```bash
   # Should return empty (different tenant)
   curl http://localhost:8080/api/v1/policies \
     -H "X-Tenant-Id: different-tenant"
   ```

## Troubleshooting

### Service Won't Start

**Symptoms**: Container exits immediately

**Common Causes**:
1. MongoDB connection failure
2. Redis connection failure
3. Invalid configuration

**Resolution**:
1. Check logs: `kubectl logs deployment/policy-configuration-service`
2. Verify connectivity: `telnet mongodb-host 27017`
3. Check configuration: Verify `application-prod.yml`

### High Memory Usage

**Symptoms**: OOMKilled, frequent restarts

**Common Causes**:
1. Large policy objects in memory
2. Cache size too large

**Resolution**:
1. Reduce cache size: `app.policy.cache.maxSize`
2. Add memory limits to deployment
3. Enable heap dumps for analysis

### Slow Response Times

**Symptoms**: High latency on API calls

**Common Causes**:
1. MongoDB query performance
2. Cache misses
3. Network latency

**Resolution**:
1. Check MongoDB indexes
2. Review cache hit rates
3. Enable query logging

### Kafka Events Not Publishing

**Symptoms**: Events not consumed by other services

**Common Causes**:
1. Kafka connection failure
2. Serialization errors
3. Topic not created

**Resolution**:
1. Verify Kafka connectivity
2. Check event logs for serialization errors
3. Create topics manually if auto-creation disabled

## Rollback Procedures

### Docker Rollback

1. **Stop Current Container**
   ```bash
   docker stop policy-configuration-service
   docker rm policy-configuration-service
   ```

2. **Start Previous Version**
   ```bash
   docker run -d \
     --name policy-configuration-service \
     -p 8080:8080 \
     gogidix/policy-configuration-service:0.9.0
   ```

### Kubernetes Rollback

1. **Rollback Deployment**
   ```bash
   kubectl rollout undo deployment/policy-configuration-service
   ```

2. **Verify Rollback**
   ```bash
   kubectl rollout status deployment/policy-configuration-service
   ```

3. **If Rollback Fails**
   ```bash
   kubectl scale deployment/policy-configuration-service --replicas=0
   kubectl scale deployment/policy-configuration-service --replicas=3
   ```

## Monitoring and Alerts

### Key Metrics

- **JVM Memory**: Heap and non-heap usage
- **Request Rate**: Requests per second
- **Response Time**: P50, P95, P99 latencies
- **Error Rate**: 4xx and 5xx response percentages
- **Cache Hit Rate**: Cache effectiveness
- **Kafka Lag**: Consumer lag for policy events

### Alerting Rules

**Critical Alerts**:
- Service down (health check failing)
- Error rate > 5%
- P99 latency > 1s
- Tenant isolation test failing

**Warning Alerts**:
- High memory usage (> 80%)
- Low cache hit rate (< 70%)
- High database connection pool usage

### Dashboards

Access Grafana dashboards at:
- Service Overview
- JVM Metrics
- API Performance
- Database Performance
