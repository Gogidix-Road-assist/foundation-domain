# Foundation Domain - Operations Runbook

**Version:** 1.0.0
**Last Updated:** 2025-12-30
**Environment:** Production

---

## Table of Contents
1. [Service Overview](#service-overview)
2. [Common Operations](#common-operations)
3. [Incident Response](#incident-response)
4. [Troubleshooting Guide](#troubleshooting-guide)
5. [Maintenance Procedures](#maintenance-procedures)

---

## Service Overview

### Architecture Components

| Component | Count | Purpose |
|------------|-------|---------|
| API Gateway | 1 | Entry point, routing, auth |
| AI Services | 29 | ML/AI capabilities |
| Configuration Services | 8 | Config management |
| Dashboard Services | 3 | Analytics & reporting |
| Infrastructure Services | 40 | Core platform services |
| Shared Libraries | 8 | Common code |
| **Total** | **88** | **Foundation services** |

### Critical Services (Single Point of Failure)

- api-gateway
- identity-service
- tenant-org-service
- config-service
- MongoDB cluster
- Redis cluster
- Kafka cluster

---

## Common Operations

### Health Checks

```bash
# Check all services
curl http://api-gateway:8080/actuator/health

# Check specific service
curl http://identity-service:8080/actuator/health

# Liveness probe
curl http://<service>:8080/actuator/health/liveness

# Readiness probe
curl http://<service>:8080/actuator/health/readiness
```

### View Logs

```bash
# Kubernetes
kubectl logs -f deployment/<service-name> -n foundation

# Docker Compose
docker-compose logs -f <service-name>

# Loki log query
logcli query "{namespace=\"foundation\"}"
```

### Scaling Services

```bash
# Horizontal scaling
kubectl scale deployment/<service> --replicas=5 -n foundation

# Autoscale based on CPU
kubectl autoscale deployment/<service> --min=2 --max=10 --cpu-percent=70 -n foundation

# Docker Compose
docker-compose up -d --scale <service>=5
```

---

## Incident Response

### Severity Levels

| Severity | Response Time | Examples |
|----------|---------------|----------|
| P0 - Critical | 15 minutes | Complete outage, data loss |
| P1 - High | 1 hour | Major feature down |
| P2 - Medium | 4 hours | Degraded performance |
| P3 - Low | 24 hours | Minor issues |

### Incident Commands

```bash
# Restart failing service
kubectl rollout restart deployment/<service> -n foundation

# Rollback to previous version
kubectl rollout undo deployment/<service> -n foundation

# Check pod status
kubectl get pods -n foundation -o wide

# Describe pod for events
kubectl describe pod <pod-name> -n foundation

# Enter container for debugging
kubectl exec -it <pod-name> -n foundation -- bash

# Port forward to local machine
kubectl port-forward deployment/<service> 8080:8080 -n foundation
```

### Common Issues & Solutions

#### 1. High Memory Usage
```bash
# Check memory usage
kubectl top pods -n foundation --containers

# Solution: Increase limits
kubectl set resources deployment <service> --requests=memory=2Gi --limits=memory=4Gi -n foundation
```

#### 2. Database Connection Pool Exhausted
```bash
# Symptoms: "Timeout waiting for connection from pool"
# Solution: Scale up MongoDB or increase pool size
```

#### 3. Redis Connection Failures
```bash
# Check Redis status
kubectl exec -it redis-0 -n foundation -- redis-cli PING

# Check Redis info
kubectl exec -it redis-0 -n foundation -- redis-cli INFO
```

#### 4. Kafka Lag
```bash
# Check consumer lag
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --describe-group --group <group-id>

# Reset offsets (emergency only)
kafka-consumer-groups.sh --bootstrap-server localhost:9092 --group <group-id> --reset-offsets --to-earliest --topic <topic>
```

---

## Troubleshooting Guide

### Service Won't Start

1. **Check pod status**
   ```bash
   kubectl get pods -n foundation
   ```

2. **Check logs**
   ```bash
   kubectl logs <pod-name> -n foundation
   ```

3. **Check events**
   ```bash
   kubectl get events -n foundation --sort-by='.lastTimestamp'
   ```

4. **Common fixes**
   - Image pull error: Check image name/tag and registry credentials
   - CrashLoopBackOff: Check application logs for startup errors
   - OOMKilled: Increase memory limits
   - CreateContainerConfigError: Invalid configuration

### High Latency

1. **Check metrics**
   ```bash
   curl http://<service>:8080/actuator/metrics/http.server.requests
   ```

2. **Check database queries**
   - Enable slow query logging
   - Check MongoDB profiler
   - Review indexes

3. **Solutions**
   - Scale up service replicas
   - Add database indexes
   - Enable caching
   - Optimize queries

### Database Issues

```bash
# MongoDB connection test
kubectl exec -it mongodb-0 -n foundation -- mongosh --eval "db.adminCommand('ping')"

# Check MongoDB replication status
kubectl exec -it mongodb-0 -n foundation -- mongosh --eval "rs.status()"

# Redis connection test
kubectl exec -it redis-0 -n foundation -- redis-cli PING

# Check Redis memory
kubectl exec -it redis-0 -n foundation -- redis-cli INFO memory
```

---

## Maintenance Procedures

### Deployment

```bash
# Deploy new version
kubectl set image deployment/<service> <service>=<new-image>:<tag> -n foundation

# Watch rollout status
kubectl rollout status deployment/<service> -n foundation

# Deploy with zero downtime
kubectl apply -f k8s/<service>-deployment.yaml
```

### Backup

```bash
# MongoDB backup
kubectl exec mongodb-0 -n foundation -- mongodump --archive=/backup/backup-$(date +%Y%m%d).gz
kubectl cp mongodb-0:/backup/backup-$(date +%Y%m%d).gz ./backup-$(date +%Y%m%d).gz -n foundation

# Redis backup
kubectl exec redis-0 -n foundation -- redis-cli SAVE
kubectl cp redis-0:/data/dump.rdb ./redis-backup.rdb -n foundation
```

### Restore

```bash
# MongoDB restore
kubectl cp ./backup.gz mongodb-0:/backup/backup.gz -n foundation
kubectl exec mongodb-0 -n foundation -- mongorestore --archive=/backup/backup.gz

# Redis restore
kubectl cp ./redis-backup.rdb redis-0:/data/dump.rdb -n foundation
kubectl exec redis-0 -n foundation -- redis-cli SHUTDOWN NOSAVE
kubectl scale statefulset redis -n foundation --replicas=0
kubectl scale statefulset redis -n foundation --replicas=1
```

### Configuration Updates

```bash
# Update ConfigMap
kubectl create configmap app-config --from-file=config/application-prod.yml --dry-run=client -o yaml | kubectl apply -f - -n foundation

# Restart services to pick up config
kubectl rollout restart deployment -l app=foundation -n foundation
```

---

## Monitoring Dashboards

### Grafana Dashboards

| Dashboard | Purpose | URL |
|-----------|---------|-----|
| Foundation Overview | All services health | /d/foundation-overview |
| Service Performance | Response times, throughput | /d/service-performance |
| Database Metrics | MongoDB, Redis stats | /d/database-metrics |
| Kafka Metrics | Producer/Consumer stats | /d/kafka-metrics |
| JVM Metrics | Memory, GC, threads | /d/jvm-metrics |

### Key Metrics to Monitor

| Metric | Threshold | Alert |
|--------|-----------|-------|
| Service availability | < 99.9% | P1 |
| Response time P95 | > 500ms | P2 |
| Error rate | > 1% | P2 |
| CPU usage | > 80% | P3 |
| Memory usage | > 85% | P2 |
| Disk usage | > 80% | P2 |
| MongoDB connections | > 90% pool | P2 |
| Kafka lag | > 10000 | P2 |

---

## Contact

| Role | Name | Contact |
|------|------|---------|
| SRE Lead | | sre@gogidix.com |
| DevOps Lead | | ops@gogidix.com |
| Architect | | architecture@gogidix.com |

---

**Document End**
