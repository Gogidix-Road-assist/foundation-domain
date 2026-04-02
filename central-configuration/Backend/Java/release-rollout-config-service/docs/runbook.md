# Release Rollout Configuration Service - Runbook

## Deployment Procedures

### Pre-Deployment Checklist

- [ ] All tests passing (`mvn verify`)
- [ ] Architecture tests passing (`mvn test -Dtest=HexArchitectureTest`)
- [ ] Code coverage threshold met (85%)
- [ ] Documentation updated
- [ ] Environment variables configured
- [ ] Database migrations prepared
- [ ] Rollback plan documented

### Deployment Steps

#### 1. Build the Application

```bash
mvn clean package -DskipTests
```

#### 2. Tag the Release

```bash
git tag -a v1.0.0 -m "Release version 1.0.0"
git push origin v1.0.0
```

#### 3. Deploy to Kubernetes

```bash
kubectl apply -f k8s/
```

#### 4. Verify Deployment

```bash
kubectl rollout status deployment/release-rollout-config-service
kubectl get pods -l app=release-rollout-config-service
```

#### 5. Health Check

```bash
curl http://release-rollout-config-service:8080/api/v1/health
```

Expected response:
```json
{
  "status": "UP"
}
```

## Rollback Procedures

### Kubernetes Rollback

```bash
# View rollout history
kubectl rollout history deployment/release-rollout-config-service

# Rollback to previous version
kubectl rollout undo deployment/release-rollout-config-service

# Rollback to specific revision
kubectl rollout undo deployment/release-rollout-config-service --to-revision=2
```

### Manual Rollback

1. Scale down current deployment:
```bash
kubectl scale deployment/release-rollout-config-service --replicas=0
```

2. Deploy previous version:
```bash
kubectl apply -f k8s/release-rollout-config-service-v1.0.0.yaml
```

3. Verify health:
```bash
curl http://release-rollout-config-service:8080/api/v1/health
```

## Monitoring

### Key Metrics

- Request rate
- Error rate
- Latency (p50, p95, p99)
- Database connection pool usage
- Cache hit ratio
- Kafka message lag

### Prometheus Endpoints

```
http://release-rollout-config-service:8080/actuator/metrics
http://release-rollout-config-service:8080/actuator/prometheus
```

### Logging

View logs:
```bash
kubectl logs -f deployment/release-rollout-config-service
```

Search for errors:
```bash
kubectl logs deployment/release-rollout-config-service | grep ERROR
```

## Troubleshooting

### High Memory Usage

**Symptoms:** Pod OOMKilled

**Solutions:**
1. Check for memory leaks: `kubectl exec -it <pod> -- jcmd <pid> GC.heap_info`
2. Increase memory limits in deployment YAML
3. Review caching configuration

### Database Connection Issues

**Symptoms:** Connection timeout errors

**Solutions:**
1. Check MongoDB connectivity: `kubectl exec -it <pod> -- mongosh mongodb://mongodb:27017`
2. Verify connection pool settings
3. Check database performance metrics

### Kafka Message Backlog

**Symptoms:** High consumer lag

**Solutions:**
1. Check Kafka cluster health
2. Scale consumer pods
3. Review message processing logic

### Tenant Isolation Breach

**Symptoms:** Data from one tenant visible to another

**CRITICAL:** This is a security incident.

**Actions:**
1. Immediately stop accepting traffic
2. Run tenant isolation tests
3. Review recent code changes
4. Check database indexes
5. Escalate to security team

## Data Management

### Backup

```bash
# MongoDB backup
kubectl exec -it mongodb-0 -- mongodump --db release-rollout-config-service-prod --archive=/tmp/backup.gz
kubectl cp mongodb-0:/tmp/backup.gz ./backup-$(date +%Y%m%d).gz
```

### Restore

```bash
kubectl cp ./backup.gz mongodb-0:/tmp/restore.gz
kubectl exec -it mongodb-0 -- mongorestore --db release-rollout-config-service-prod --archive=/tmp/restore.gz
```

### Tenant Data Export

```bash
# Export specific tenant data
kubectl exec -it mongodb-0 -- mongosh --eval '
  db.release_rollouts.find({tenantId: "tenant-123"}).toArray()
' > tenant-123-export.json
```

## Scaling

### Horizontal Scaling

```bash
kubectl scale deployment/release-rollout-config-service --replicas=3
```

### Vertical Scaling

Update deployment YAML with new resource limits:
```yaml
resources:
  requests:
    memory: "1Gi"
    cpu: "500m"
  limits:
    memory: "2Gi"
    cpu: "1000m"
```

## Security

### Rotate Secrets

```bash
# Generate new secret
kubectl create secret generic rollout-secrets \
  --from-literal=mongodb-password=new-password \
  --from-literal=redis-password=new-password \
  --dry-run=client -o yaml | kubectl apply -f -
```

### Update TLS Certificates

```bash
kubectl create secret tls rollout-tls \
  --cert=tls.crt --key=tls.key --dry-run=client -o yaml | kubectl apply -f -
```

## Maintenance Windows

### Scheduled Maintenance

1. Create maintenance notice
2. Scale down to zero replicas
3. Perform maintenance
4. Scale back up
5. Verify health checks

```bash
kubectl scale deployment/release-rollout-config-service --replicas=0
# Perform maintenance
kubectl scale deployment/release-rollout-config-service --replicas=3
kubectl rollout status deployment/release-rollout-config-service
```

## Disaster Recovery

### Recovery Procedure

1. **Assess impact**
   - Check service health
   - Identify affected tenants
   - Estimate data loss

2. **Restore from backup**
   - Select appropriate backup
   - Execute restore procedure
   - Verify data integrity

3. **Restart services**
   - Bring up MongoDB
   - Start application pods
   - Verify health endpoints

4. **Monitor**
   - Check metrics
   - Review logs
   - Validate tenant isolation

5. **Post-incident review**
   - Document root cause
   - Update runbook
   - Implement preventive measures
