# Foundation-Domain Kubernetes Deployment

This directory contains all Kubernetes manifests for deploying the Foundation-Domain to a Kubernetes cluster.

## Prerequisites

- Kubernetes cluster (v1.28+)
- kubectl configured
- Helm (optional, for additional services)
- NGINX Ingress Controller
- cert-manager (for TLS certificates)

## Quick Start

```bash
# Deploy to Kubernetes
cd Foundation-Domain
chmod +x k8s/deploy.sh
./k8s/deploy.sh dev

# Or deploy manually
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/infrastructure.yaml
kubectl apply -f k8s/core-services.yaml
kubectl apply -f k8s/ai-services.yaml
kubectl apply -f k8s/monitoring.yaml
kubectl apply -f k8s/network-policy.yaml
kubectl apply -f k8s/autoscaler.yaml
kubectl apply -f k8s/ingress.yaml
```

## Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                        Ingress (NGINX)                      │
└─────────────────────────────────────────────────────────────┘
                              │
        ┌─────────────────────┼─────────────────────┐
        ▼                     ▼                     ▼
┌───────────────┐   ┌───────────────┐   ┌───────────────┐
│  API Gateway  │   │  AI Gateway   │   │   Grafana     │
│   (8304)      │   │   (8200)      │   │   (3000)      │
└───────────────┘   └───────────────┘   └───────────────┘
        │                     │
        ▼                     ▼
┌───────────────┐   ┌───────────────┐
│ Core Services │   │ AI Services   │
│ - Identity    │   │ - Inference   │
│ - Notification│   │ - Chatbot     │
│ - Rate Limit  │   │ - NLP         │
└───────────────┘   └───────────────┘
        │                     │
        └──────────┬──────────┘
                   ▼
        ┌───────────────────────┐
        │   Infrastructure      │
        │ - MongoDB (27017)     │
        │ - PostgreSQL (5432)   │
        │ - Redis (6379)        │
        └───────────────────────┘
```

## Components

### Infrastructure (infrastructure.yaml)
- **MongoDB 6.0**: Primary database (StatefulSet)
- **PostgreSQL 15**: Relational database (Deployment)
- **Redis 7**: Caching layer (Deployment)

### Core Services (core-services.yaml)
- **API Gateway**: Entry point (port 8304)
- **Identity Service**: Authentication/authorization (port 8316)
- **Notification Service**: Notifications (port 8323)
- **Service Registry**: Service discovery (port 8761)
- **Rate Limiting**: API rate limiting (port 8080)

### AI Services (ai-services.yaml)
- **AI Gateway**: AI services entry point (port 8200)
- **AI Inference**: Model inference (port 8201)
- **AI Model Management**: Model lifecycle (port 8202)
- **AI Chatbot**: Conversational AI (port 8214)
- **AI NLP**: Natural language processing (port 8210)

### Monitoring (monitoring.yaml)
- **Prometheus**: Metrics collection (port 9090)
- **Grafana**: Visualization (port 3000)
- **Alertmanager**: Alert routing (port 9093)
- **Node Exporter**: Host metrics (port 9100)

### Security (network-policy.yaml)
- Default deny all ingress
- Database access restrictions
- Service-to-service communication rules
- Monitoring scraping access

### Autoscaling (autoscaler.yaml)
- API Gateway: 2-10 replicas
- Identity Service: 2-8 replicas
- AI Inference: 3-20 replicas
- AI Gateway: 2-10 replicas
- Notification: 2-6 replicas
- Rate Limiting: 2-8 replicas

## Access URLs

After deployment, access services via:

| Service | URL | Credentials |
|---------|-----|-------------|
| API Gateway | https://api.gogidix.com | Bearer token |
| AI Gateway | https://ai.gogidix.com | Bearer token |
| Grafana | http://grafana.gogidix.com | admin / foundation123 |
| Prometheus | http://prometheus.gogidix.com | Basic auth |

## Storage Requirements

| Component | Storage |
|-----------|---------|
| MongoDB | 20Gi |
| PostgreSQL | 10Gi |
| Redis | 5Gi |
| Prometheus | 10Gi |
| Grafana | 5Gi |
| Alertmanager | 2Gi |

**Total**: ~52Gi

## Resource Requirements

| Tier | CPU | Memory |
|------|-----|--------|
| Infrastructure | 1.75 | 4Gi |
| Core Services | 2.5 | 5Gi |
| AI Services | 3.5 | 8Gi |
| Monitoring | 1 | 2Gi |
| **Total** | **~9** | **~20Gi** |

## Scaling

Services use HorizontalPodAutoscaler for automatic scaling based on CPU/memory utilization. Scale thresholds:
- CPU: 70%
- Memory: 80%

## Monitoring

Prometheus collects metrics from all services via:
- `/actuator/prometheus` endpoint on Spring Boot services
- Pod annotations for service discovery
- Node Exporter for host metrics

Alerts are configured in `monitoring/prometheus-rules.yml`

## Disaster Recovery

### Backups
```bash
# MongoDB backup
kubectl exec -it mongodb-0 -n foundation -- mongodump --archive=/data/db/backup-$(date +%Y%m%d).gz

# PostgreSQL backup
kubectl exec -it postgres-0 -n foundation -- pg_dump -U foundation foundation > backup.sql

# Restore MongoDB
kubectl exec -it mongodb-0 -n foundation -- mongorestore --archive=/data/db/backup-20240315.gz
```

### High Availability
- MongoDB: StatefulSet with persistent volumes
- Services: Multiple replicas with anti-affinity (recommended)
- Monitoring: Persistent storage for metrics

## Troubleshooting

### Check pod status
```bash
kubectl get pods -n foundation
kubectl describe pod <pod-name> -n foundation
kubectl logs <pod-name> -n foundation
```

### Check services
```bash
kubectl get svc -n foundation
kubectl describe svc <service-name> -n foundation
```

### Check HPA status
```bash
kubectl get hpa -n foundation
kubectl describe hpa <hpa-name> -n foundation
```

### Port forwarding to local
```bash
kubectl port-forward svc/api-gateway 8304:8304 -n foundation
kubectl port-forward svc/grafana 3000:3000 -n foundation
```

### Access service shell
```bash
kubectl exec -it <pod-name> -n foundation -- sh
```

## Upgrading

```bash
# Update image tag
kubectl set image deployment/api-gateway api-gateway=ghcr.io/ggx-insurance-saas/api-gateway:v1.0.1 -n foundation

# Rollback
kubectl rollout undo deployment/api-gateway -n foundation

# Check rollout status
kubectl rollout status deployment/api-gateway -n foundation
```

## Cleanup

```bash
# Delete all resources
kubectl delete namespace foundation

# Or delete individual components
kubectl delete -f k8s/
```
