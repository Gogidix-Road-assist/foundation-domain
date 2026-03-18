# Foundation-Domain Implementation Plan

**Version:** 1.0.0
**Created:** 2026-03-14
**Target:** Production Deployment
**Estimated Duration:** 30-50 hours
**Team Size:** 2-3 developers

---

## Executive Summary

This document outlines the complete implementation plan for deploying the **Foundation-Domain** to production. The Foundation-Domain provides pure infrastructure services (98 services) that are plug-and-play for any project.

### Objectives

1. ✅ Verify all services build successfully
2. ✅ Create container images for all services
3. ✅ Set up CI/CD pipeline
4. ✅ Deploy to cloud infrastructure
5. ✅ Configure monitoring and observability
6. ✅ Perform end-to-end testing

### Success Criteria

- All 98 services build and deploy successfully
- CI/CD pipeline passes all checks
- Services are accessible via API Gateway
- Monitoring dashboards are operational
- End-to-end tests pass with 90%+ success rate

---

## Table of Contents

1. [Current State Assessment](#1-current-state-assessment)
2. [Architecture Overview](#2-architecture-overview)
3. [Phase 1: Build Verification](#3-phase-1-build-verification)
4. [Phase 2: Containerization](#4-phase-2-containerization)
5. [Phase 3: CI/CD Pipeline](#5-phase-3-cicd-pipeline)
6. [Phase 4: Infrastructure Setup](#6-phase-4-infrastructure-setup)
7. [Phase 5: Deployment](#7-phase-5-deployment)
8. [Phase 6: Monitoring & Observability](#8-phase-6-monitoring--observability)
9. [Phase 7: Testing](#9-phase-7-testing)
10. [Phase 8: Go-Live](#10-phase-8-go-live)
11. [Risk Management](#11-risk-management)
12. [Resource Requirements](#12-resource-requirements)

---

## 1. Current State Assessment

### 1.1 Service Inventory

| Domain | Services | Technology | Status |
|--------|----------|------------|--------|
| AI Services | 31 | Java 21, Spring Boot | Code exists |
| Central Configuration | 9 | Java 21, Spring Boot | Code exists |
| Centralized Dashboard | 4 | Java/Node.js | Code exists |
| Orchestration Services | 7 | Java 21, Spring Boot | Code exists |
| Shared Infrastructure | 38 | Java 21, Spring Boot | Code exists |
| Shared Libraries | 15 | Java | Code exists |
| **Total** | **98** | **Mixed** | **Code exists** |

### 1.2 Existing Infrastructure

| Component | Status | Notes |
|-----------|--------|-------|
| GitHub Actions CI | ✅ Exists | Needs updates for new structure |
| Dockerfiles | ⚠️ Partial | Need to create for all services |
| Kubernetes manifests | ❌ Missing | Need to create |
| Cloud deployment | ❌ Not deployed | Target TBD |
| Monitoring | ❌ Not configured | Need setup |

### 1.3 Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Java | 21 | Runtime |
| Spring Boot | 3.x | Framework |
| MongoDB | 6.0+ | Database |
| PostgreSQL | 15+ | Database |
| Redis | 7.0+ | Cache/Message Queue |
| Kafka | 3.x | Event Streaming |

---

## 2. Architecture Overview

### 2.1 Deployment Architecture

```
┌─────────────────────────────────────────────────────────────────────────┐
│                              CLOUD (AWS/GCP/Azure)                      │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│  ┌───────────────────────────────────────────────────────────────────┐  │
│  │                        Load Balancer                                │  │
│  │                   (ALB / Cloud Load Balancer)                      │  │
│  └───────────────────────────────┬───────────────────────────────────┘  │
│                                  │                                       │
│  ┌───────────────────────────────▼───────────────────────────────────┐  │
│  │                      Kubernetes Cluster                            │  │
│  │  ┌─────────────────────────────────────────────────────────────┐ │  │
│  │  │                  API Gateway (8304)                          │ │  │
│  │  │                   Ingress Controller                          │ │  │
│  │  └─────────────────────────────────────────────────────────────┘ │  │
│  │                                                                   │  │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐              │  │
│  │  │ AI Services  │ │   Config     │ │ Identity &   │              │  │
│  │  │  (31 pods)    │ │ Services     │ │   Access     │              │  │
│  │  └──────────────┘ └──────────────┘ └──────────────┘              │  │
│  │                                                                   │  │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐              │  │
│  │  │ Monitoring   │ │ Notification │ │   Storage    │              │  │
│  │  │ Services     │ │  Services    │ │  Services    │              │  │
│  │  └──────────────┘ └──────────────┘ └──────────────┘              │  │
│  └───────────────────────────────────────────────────────────────────┘  │
│                                  │                                       │
│  ┌───────────────────────────────▼───────────────────────────────────┐  │
│  │                        Data Layer                                  │  │
│  │  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐              │  │
│  │  │   MongoDB    │ │  PostgreSQL  │ │    Redis     │              │  │
│  │  │  (Replica Set)│ │   (HA Pair)   │ │   (Cluster)   │              │  │
│  │  └──────────────┘ └──────────────┘ └──────────────┘              │  │
│  └───────────────────────────────────────────────────────────────────┘  │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### 2.2 Service Port Allocation

| Service Type | Port Range | Protocol |
|--------------|------------|----------|
| API Gateway | 8304 | HTTP/HTTPS |
| Identity Services | 8888-8899 | HTTP |
| Configuration Services | 8000-8010 | HTTP |
| AI Services | 8100-8130 | HTTP |
| Monitoring Services | 8091-8099 | HTTP |
| Infrastructure Services | 8310-8400 | HTTP |

---

## 3. Phase 1: Build Verification

**Duration:** 4-6 hours
**Goal:** Ensure all services compile successfully

### 3.1 Build Script Creation

**File:** `Foundation-Domain/scripts/build-all-services.sh`

```bash
#!/bin/bash
set -e

FOUNDATION_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
FAILED_SERVICES=()
SUCCESS_COUNT=0
TOTAL_COUNT=0

echo "======================================"
echo "Foundation-Domain Build Verification"
echo "======================================"
echo ""

# Build shared libraries first
echo "Phase 1: Building Shared Libraries"
echo "-----------------------------------"
for lib in "$FOUNDATION_DIR/shared-libraries/Backend/Java"/*; do
    if [ -d "$lib" ] && [ -f "$lib/pom.xml" ]; then
        lib_name=$(basename "$lib")
        echo "Building $lib_name..."
        cd "$lib"
        if mvn clean install -DskipTests -q; then
            echo "✓ $lib_name built successfully"
            ((SUCCESS_COUNT++))
        else
            echo "✗ $lib_name build failed"
            FAILED_SERVICES+=("$lib_name")
        fi
        ((TOTAL_COUNT++))
    fi
done

echo ""
echo "Phase 2: Building AI Services"
echo "------------------------------"
for service in "$FOUNDATION_DIR/ai-services/Backend/Java"/*; do
    if [ -d "$service" ] && [ -f "$service/pom.xml" ]; then
        service_name=$(basename "$service")
        echo "Building $service_name..."
        cd "$service"
        if mvn clean compile -DskipTests -q; then
            echo "✓ $service_name built successfully"
            ((SUCCESS_COUNT++))
        else
            echo "✗ $service_name build failed"
            FAILED_SERVICES+=("$service_name")
        fi
        ((TOTAL_COUNT++))
    fi
done

echo ""
echo "Phase 3: Building Configuration Services"
echo "-----------------------------------------"
for service in "$FOUNDATION_DIR/central-configuration/Backend/Java"/*; do
    if [ -d "$service" ] && [ -f "$service/pom.xml" ]; then
        service_name=$(basename "$service")
        echo "Building $service_name..."
        cd "$service"
        if mvn clean compile -DskipTests -q; then
            echo "✓ $service_name built successfully"
            ((SUCCESS_COUNT++))
        else
            echo "✗ $service_name build failed"
            FAILED_SERVICES+=("$service_name")
        fi
        ((TOTAL_COUNT++))
    fi
done

echo ""
echo "Phase 4: Building Infrastructure Services"
echo "------------------------------------------"
for service in "$FOUNDATION_DIR/shared-infrastructure/Backend/Java"/*; do
    if [ -d "$service" ] && [ -f "$service/pom.xml" ]; then
        service_name=$(basename "$service")
        echo "Building $service_name..."
        cd "$service"
        if mvn clean compile -DskipTests -q; then
            echo "✓ $service_name built successfully"
            ((SUCCESS_COUNT++))
        else
            echo "✗ $service_name build failed"
            FAILED_SERVICES+=("$service_name")
        fi
        ((TOTAL_COUNT++))
    fi
done

# Summary
echo ""
echo "======================================"
echo "Build Summary"
echo "======================================"
echo "Total: $TOTAL_COUNT"
echo "Success: $SUCCESS_COUNT"
echo "Failed: ${#FAILED_SERVICES[@]}"

if [ ${#FAILED_SERVICES[@]} -gt 0 ]; then
    echo ""
    echo "Failed services:"
    for service in "${FAILED_SERVICES[@]}"; do
        echo "  - $service"
    done
    exit 1
fi

echo ""
echo "✓ All services built successfully!"
exit 0
```

### 3.2 Build Verification Checklist

| Step | Command | Expected Result |
|------|---------|-----------------|
| 1. Prerequisites check | `java -version` | Java 21 installed |
| 2. Maven check | `mvn -version` | Maven 3.9+ installed |
| 3. Shared libraries build | `./scripts/build-all-services.sh` | All libraries install |
| 4. AI services build | `./scripts/build-ai-services.sh` | All services compile |
| 5. Config services build | `./scripts/build-config-services.sh` | All services compile |
| 6. Infra services build | `./scripts/build-infra-services.sh` | All services compile |

### 3.3 Common Build Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| Missing dependency | Parent pom not installed | Build shared-libraries first |
| Version conflict | Dependency version mismatch | Check dependency tree |
| Compilation error | Syntax or type error | Fix in source code |
| Test failure | Unit test broken | Skip tests with -DskipTests |

---

## 4. Phase 2: Containerization

**Duration:** 6-8 hours
**Goal:** Create Docker images for all services

### 4.1 Base Dockerfile

**File:** `Foundation-Domain/Dockerfile.template`

```dockerfile
# Multi-stage build for Foundation services
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests -o

# Runtime image
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", \
           "-jar", \
           "/app/app.jar", \
           "--spring.profiles.active=${SPRING_PROFILES_ACTIVE:production}"]
```

### 4.2 Service-Specific Dockerfiles

Each service will have its own Dockerfile that extends the base:

```dockerfile
# ai-gateway-service/Dockerfile
FROM eclipse-temurin:21-jre-alpine

LABEL service="ai-gateway-service" \
      domain="ai-services" \
      version="1.0.0"

WORKDIR /app
COPY target/ai-gateway-service-1.0.0.jar app.jar

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 4.3 Docker Compose for Local Development

**File:** `Foundation-Domain/docker-compose.yml`

```yaml
version: '3.8'

services:
  # Infrastructure
  mongodb:
    image: mongo:6.0
    ports:
      - "27017:27017"
    environment:
      MONGO_INITDB_ROOT_USERNAME: admin
      MONGO_INITDB_ROOT_PASSWORD: password
    volumes:
      - mongodb_data:/data/db

  postgres:
    image: postgres:15-alpine
    ports:
      - "5432:5432"
    environment:
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: password
      POSTGRES_DB: foundation
    volumes:
      - postgres_data:/var/lib/postgresql/data

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data

  kafka:
    image: confluentinc/cp-kafka:7.5.0
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://localhost:9092
    depends_on:
      - zookeeper

  zookeeper:
    image: confluentinc/cp-zookeeper:7.5.0
    ports:
      - "2181:2181"
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181

  # Core Services
  api-gateway:
    build:
      context: ./shared-infrastructure/Backend/Java/api-gateway
      dockerfile: Dockerfile
    ports:
      - "8304:8304"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      MONGODB_URL: mongodb://admin:password@mongodb:27017
      REDIS_URL: redis://redis:6379
    depends_on:
      - mongodb
      - redis

  identity-service:
    build:
      context: ./shared-infrastructure/Backend/Java/identity-service
      dockerfile: Dockerfile
    ports:
      - "8888:8888"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      MONGODB_URL: mongodb://admin:password@mongodb:27017
    depends_on:
      - mongodb

  config-service:
    build:
      context: ./central-configuration/Backend/Java/config-service
      dockerfile: Dockerfile
    ports:
      - "8000:8000"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      MONGODB_URL: mongodb://admin:password@mongodb:27017
    depends_on:
      - mongodb

volumes:
  mongodb_data:
  postgres_data:
  redis_data:
```

### 4.4 Container Registry Setup

### AWS ECR

```bash
# Create repository
aws ecr create-repository --repository-name foundation/api-gateway

# Login
aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin <account-id>.dkr.ecr.us-east-1.amazonaws.com

# Push image
docker tag api-gateway:latest <account-id>.dkr.ecr.us-east-1.amazonaws.com/foundation/api-gateway:latest
docker push <account-id>.dkr.ecr.us-east-1.amazonaws.com/foundation/api-gateway:latest
```

### Google GCR

```bash
# Tag for GCR
docker tag api-gateway:latest gcr.io/<project-id>/foundation/api-gateway:latest

# Push to GCR
docker push gcr.io/<project-id>/foundation/api-gateway:latest
```

---

## 5. Phase 3: CI/CD Pipeline

**Duration:** 4-6 hours
**Goal:** Automated build, test, and deployment

### 5.1 Updated GitHub Actions Workflow

**File:** `.github/workflows/foundation-domain-ci.yml`

```yaml
name: Foundation-Domain CI/CD

on:
  push:
    branches: [main, develop]
    paths:
      - 'Foundation-Domain/**'
  pull_request:
    branches: [main, develop]
    paths:
      - 'Foundation-Domain/**'
  workflow_dispatch:

env:
  JAVA_VERSION: '21'
  REGISTRY: ghcr.io
  IMAGE_PREFIX: foundation-domain

jobs:
  build-and-test:
    name: Build and Test
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: 'maven'

      - name: Build Shared Libraries
        run: |
          cd Foundation-Domain
          ./scripts/build-shared-libraries.sh

      - name: Build All Services
        run: |
          cd Foundation-Domain
          ./scripts/build-all-services.sh

      - name: Run Tests
        run: |
          cd Foundation-Domain
          ./scripts/run-all-tests.sh

  security-scan:
    name: Security Scan
    needs: build-and-test
    runs-on: ubuntu-latest

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Run Trivy
        uses: aquasecurity/trivy-action@master
        with:
          scan-type: 'fs'
          scan-ref: 'Foundation-Domain'
          format: 'sarif'
          output: 'trivy-results.sarif'

      - name: Upload Results
        uses: github/codeql-action/upload-sarif@v3
        with:
          sarif_file: 'trivy-results.sarif'

  build-images:
    name: Build Docker Images
    needs: [build-and-test, security-scan]
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'

    strategy:
      matrix:
        service:
          - api-gateway
          - identity-service
          - config-service
          - monitoring-service
          - notification-service

    steps:
      - name: Checkout
        uses: actions/checkout@v4

      - name: Set up Docker Buildx
        uses: docker/setup-buildx-action@v3

      - name: Login to GHCR
        uses: docker/login-action@v3
        with:
          registry: ghcr.io
          username: ${{ github.actor }}
          password: ${{ secrets.GITHUB_TOKEN }}

      - name: Build Image
        uses: docker/build-push-action@v5
        with:
          context: ./Foundation-Domain
          file: ./Foundation-Domain/${{ matrix.service }}/Dockerfile
          push: true
          tags: |
            ghcr.io/${{ github.repository }}/${{ matrix.service }}:latest
            ghcr.io/${{ github.repository }}/${{ matrix.service }}:${{ github.sha }}
          labels: |
            service=${{ matrix.service }}
            version=${{ github.sha }}

  deploy-staging:
    name: Deploy to Staging
    needs: build-images
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/develop'
    environment: staging

    steps:
      - name: Deploy to Kubernetes
        run: |
          kubectl set image deployment/api-gateway \
            api-gateway=ghcr.io/${{ github.repository }}/api-gateway:${{ github.sha }} \
            -n foundation-staging

  deploy-production:
    name: Deploy to Production
    needs: build-images
    runs-on: ubuntu-latest
    if: github.ref == 'refs/heads/main'
    environment: production

    steps:
      - name: Deploy to Kubernetes
        run: |
          kubectl set image deployment/api-gateway \
            api-gateway=ghcr.io/${{ github.repository }}/api-gateway:${{ github.sha }} \
            -n foundation-production

      - name: Verify Deployment
        run: |
          kubectl rollout status deployment/api-gateway -n foundation-production
```

### 5.2 Build Scripts

**File:** `Foundation-Domain/scripts/build-shared-libraries.sh`

```bash
#!/bin/bash
set -e

SHARED_LIBS_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/../shared-libraries/Backend/Java" && pwd)"

for lib in "$SHARED_LIBS_DIR"/*; do
    if [ -d "$lib" ] && [ -f "$lib/pom.xml" ]; then
        lib_name=$(basename "$lib")
        echo "Building $lib_name..."
        cd "$lib"
        mvn clean install -DskipTests -q
    fi
done

echo "All shared libraries built successfully"
```

**File:** `Foundation-Domain/scripts/run-all-tests.sh`

```bash
#!/bin/bash
set -e

FOUNDATION_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
TOTAL_TESTS=0
PASSED_TESTS=0

for service_dir in "$FOUNDATION_DIR"/*/Backend/Java/*; do
    if [ -d "$service_dir" ] && [ -f "$service_dir/pom.xml" ]; then
        service_name=$(basename "$service_dir")
        echo "Testing $service_name..."
        cd "$service_dir"

        if mvn test -q; then
            ((PASSED_TESTS++))
        fi
        ((TOTAL_TESTS++))
    fi
done

echo ""
echo "Test Results: $PASSED_TESTS/$TOTAL_TESTS passed"

if [ $PASSED_TESTS -lt $TOTAL_TESTS ]; then
    exit 1
fi
```

---

## 6. Phase 4: Infrastructure Setup

**Duration:** 6-8 hours
**Goal:** Set up cloud infrastructure

### 6.1 Cloud Provider Comparison

| Feature | AWS | GCP | Azure |
|---------|-----|-----|-------|
| Kubernetes (EKS/GKE/AKS) | ✅ Mature | ✅ Excellent | ✅ Good |
| Managed MongoDB | DocumentDB | Cosmos DB | Cosmos DB |
| Managed PostgreSQL | RDS | Cloud SQL | Azure SQL |
| CI/CD Integration | CodePipeline | Cloud Build | Azure DevOps |
| Cost | $$ | $$$ | $$ |

**Recommended:** AWS (best balance of features, cost, and maturity)

### 6.2 AWS Infrastructure

#### EKS Cluster

```yaml
# infrastructure/terraform/eks-cluster.tf
resource "aws_eks_cluster" "foundation" {
  name     = "foundation-domain"
  role_arn = aws_iam_role.eks_cluster.arn
  version  = "1.28"

  vpc_config {
    subnet_ids = aws_subnet.foundation[*].id
  }

  enabled_cluster_log_types = ["api", "audit", "authenticator"]
}

resource "aws_eks_node_group" "foundation" {
  cluster_name    = aws_eks_cluster.foundation.name
  node_group_name = "foundation-nodes"
  node_role_arn   = aws_iam_role.eks_nodes.arn
  subnet_ids      = aws_subnet.foundation[*].id

  scaling_config {
    desired_size = 3
    max_size     = 6
    min_size     = 2
  }

  instance_types = ["t3.large"]

  labels = {
    domain = "foundation"
  }
}
```

#### RDS PostgreSQL

```yaml
resource "aws_db_instance" "foundation_postgres" {
  identifier     = "foundation-postgres"
  engine         = "postgres"
  engine_version = "15.4"
  instance_class = "db.r6g.xlarge"

  allocated_storage     = 100
  max_allocated_storage = 1000
  storage_encrypted     = true

  db_name  = "foundation"
  username = var.db_username
  password = var.db_password

  vpc_security_group_ids = [aws_security_group.db.id]
  db_subnet_group_name   = aws_db_subnet_group.foundation.name

  backup_retention_period = 30
  skip_final_snapshot    = false

  tags = {
    Domain = "foundation"
  }
}
```

#### DocumentDB (MongoDB)

```yaml
resource "aws_docdb_cluster" "foundation_mongo" {
  cluster_identifier      = "foundation-mongo"
  engine                  = "docdb"
  master_username         = var.mongo_username
  master_password         = var.mongo_password
  backup_retention_period = 30
  preferred_backup_window = "07:00-09:00"

  skip_final_snapshot = false

  vpc_security_group_ids = [aws_security_group.db.id]

  tags = {
    Domain = "foundation"
  }
}

resource "aws_docdb_cluster_instance" "foundation_instances" {
  count              = 3
  cluster_identifier = aws_docdb_cluster.foundation_mongo.id
  instance_class     = "db.r6g.large"

  tags = {
    Domain = "foundation"
  }
}
```

#### ElastiCache (Redis)

```yaml
resource "aws_elasticache_replication_group" "foundation_redis" {
  replication_group_id          = "foundation-redis"
  replication_group_description = "Foundation Domain Redis"

  node_type            = "cache.r6g.large"
  number_cache_clusters = 2
  port                 = 6379

  automatic_failover_enabled = true

  auth_token = var.redis_auth_token

  snapshot_retention_limit = 30

  tags = {
    Domain = "foundation"
  }
}
```

### 6.3 Kubernetes Namespaces

```yaml
# infrastructure/k8s/namespaces.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: foundation-production
  labels:
    domain: foundation
    env: production

---
apiVersion: v1
kind: Namespace
metadata:
  name: foundation-staging
  labels:
    domain: foundation
    env: staging
```

---

## 7. Phase 5: Deployment

**Duration:** 8-12 hours
**Goal:** Deploy all services to Kubernetes

### 7.1 Kubernetes Deployment Template

```yaml
# infrastructure/k8s/service-template.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${SERVICE_NAME}
  namespace: foundation-production
  labels:
    app: ${SERVICE_NAME}
    domain: foundation
spec:
  replicas: 3
  selector:
    matchLabels:
      app: ${SERVICE_NAME}
  template:
    metadata:
      labels:
        app: ${SERVICE_NAME}
        domain: foundation
    spec:
      containers:
      - name: ${SERVICE_NAME}
        image: ${IMAGE}:${TAG}
        ports:
        - name: http
          containerPort: 8080
          protocol: TCP
        env:
        - name: SPRING_PROFILES_ACTIVE
          value: "production"
        - name: MONGODB_URL
          valueFrom:
            configMapKeyRef:
              name: foundation-config
              key: mongodb.url
        - name: REDIS_URL
          valueFrom:
            configMapKeyRef:
              name: foundation-config
              key: redis.url
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "1000m"
        livenessProbe:
          httpGet:
            path: /actuator/health
            port: 8080
          initialDelaySeconds: 60
          periodSeconds: 30
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
          initialDelaySeconds: 30
          periodSeconds: 10
```

### 7.2 Service Templates

```yaml
apiVersion: v1
kind: Service
metadata:
  name: ${SERVICE_NAME}
  namespace: foundation-production
  labels:
    app: ${SERVICE_NAME}
spec:
  type: ClusterIP
  ports:
  - port: 8080
    targetPort: 8080
    protocol: TCP
    name: http
  selector:
    app: ${SERVICE_NAME}
```

### 7.3 Ingress Configuration

```yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: foundation-gateway
  namespace: foundation-production
  annotations:
    kubernetes.io/ingress.class: alb
    alb.ingress.kubernetes.io/scheme: internet-facing
    alb.ingress.kubernetes.io/target-type: ip
    alb.ingress.kubernetes.io/healthcheck-path: /health
    alb.ingress.kubernetes.io/healthcheck-interval-seconds: '30'
    cert-manager.io/cluster-issuer: letsencrypt-prod
spec:
  ingressClassName: alb
  tls:
  - hosts:
    - api.foundation.rapidassist.com
    secretName: foundation-tls
  rules:
  - host: api.foundation.rapidassist.com
    http:
      paths:
      - path: /
        pathType: Prefix
        backend:
          service:
            name: api-gateway
            port:
              number: 8304
```

### 7.4 ConfigMaps

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: foundation-config
  namespace: foundation-production
data:
  mongodb.url: "mongodb://admin:password@foundation-mongo.cluster-xyz.us-east-1.docdb.amazonaws.com:27017"
  postgres.url: "jdbc:postgresql://foundation-postgres.xyz.us-east-1.rds.amazonaws.com:5432/foundation"
  redis.url: "redis://foundation-redis.xxyz.cache.amazonaws.com:6379"
  kafka.bootstrap: "foundation-kafka.xxyz.us-east-1.kafka.amazonaws.com:9092"
```

### 7.5 Secrets

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: foundation-secrets
  namespace: foundation-production
type: Opaque
stringData:
  mongodb.password: ${MONGODB_PASSWORD}
  postgres.password: ${POSTGRES_PASSWORD}
  redis.auth-token: ${REDIS_AUTH_TOKEN}
  jwt.secret: ${JWT_SECRET}
```

---

## 8. Phase 6: Monitoring & Observability

**Duration:** 4-6 hours
**Goal:** Set up monitoring, logging, and alerting

### 8.1 Prometheus Stack

```yaml
# infrastructure/monitoring/prometheus-stack.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: prometheus-config
  namespace: monitoring
data:
  prometheus.yml: |
    global:
      scrape_interval: 15s
      evaluation_interval: 15s

    scrape_configs:
    - job_name: 'foundation-services'
      kubernetes_sd_configs:
      - role: pod
        namespaces:
          names:
          - foundation-production
      relabel_configs:
      - source_labels: [__meta_kubernetes_pod_label_domain]
        action: keep
        regex: foundation
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
      - source_labels: [__meta_kubernetes_pod_name]
        target_label: pod
```

### 8.2 Grafana Dashboards

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: grafana-dashboards
  namespace: monitoring
data:
  foundation-services.json: |
    {
      "dashboard": {
        "title": "Foundation Services",
        "panels": [
          {
            "title": "Service Health",
            "targets": [
              {
                "expr": "up{job=~\"foundation.*\"}"
              }
            ]
          },
          {
            "title": "Request Rate",
            "targets": [
              {
                "expr": "rate(http_requests_total{domain=\"foundation\"}[5m])"
              }
            ]
          },
          {
            "title": "Error Rate",
            "targets": [
              {
                "expr": "rate(http_requests_total{domain=\"foundation\",status=~\"5..\"}[5m])"
              }
            ]
          },
          {
            "title": "Response Time",
            "targets": [
              {
                "expr": "histogram_quantile(0.95, http_request_duration_seconds{domain=\"foundation\"})"
              }
            ]
          }
        ]
      }
    }
```

### 8.3 Alerting Rules

```yaml
# infrastructure/monitoring/alerts.yaml
apiVersion: monitoring.coreos.com/v1
kind: PrometheusRule
metadata:
  name: foundation-alerts
  namespace: foundation-production
spec:
  groups:
  - name: foundation-services
    rules:
    - alert: ServiceDown
      expr: up{job=~"foundation.*"} == 0
      for: 2m
      labels:
        severity: critical
      annotations:
        summary: "Service {{ $labels.instance }} is down"
        description: "{{ $labels.instance }} has been down for more than 2 minutes"

    - alert: HighErrorRate
      expr: |
        rate(http_requests_total{domain="foundation",status=~"5.."}[5m])
        / rate(http_requests_total{domain="foundation"}[5m]) > 0.05
      for: 5m
      labels:
        severity: warning
      annotations:
        summary: "High error rate on {{ $labels.service }}"
        description: "Error rate is {{ $value | humanizePercentage }} for {{ $labels.service }}"

    - alert: HighLatency
      expr: |
        histogram_quantile(0.95,
          rate(http_request_duration_seconds_bucket{domain="foundation"}[5m])
        ) > 1
      for: 10m
      labels:
        severity: warning
      annotations:
        summary: "High latency on {{ $labels.service }}"
        description: "P95 latency is {{ $value }}s for {{ $labels.service }}"
```

### 8.4 Logging Stack

```yaml
# infrastructure/logging/elasticsearch.yaml
apiVersion: v1
kind: StatefulSet
metadata:
  name: elasticsearch
  namespace: logging
spec:
  serviceName: elasticsearch
  replicas: 3
  template:
    spec:
      containers:
      - name: elasticsearch
        image: docker.elastic.co/elasticsearch/elasticsearch:8.12.0
        ports:
        - containerPort: 9200
          name: http
        - containerPort: 9300
          name: transport
        env:
        - name: discovery.type
          value: "single-node"
        - name: ES_JAVA_OPTS
          value: "-Xms2g -Xmx2g"
        resources:
          requests:
            memory: "4Gi"
            cpu: "1000m"
          limits:
            memory: "4Gi"
            cpu: "2000m"
        volumeMounts:
        - name: data
          mountPath: /usr/share/elasticsearch/data
  volumeClaimTemplates:
  - metadata:
      name: data
    spec:
      accessModes: ["ReadWriteOnce"]
      storageClassName: gp2
      resources:
        requests:
          storage: 100Gi
```

---

## 9. Phase 7: Testing

**Duration:** 8-12 hours
**Goal:** Comprehensive testing of all services

### 9.1 Test Categories

| Test Type | Purpose | Tool |
|-----------|---------|------|
| Unit Tests | Test individual components | JUnit |
| Integration Tests | Test service interactions | TestContainers |
| Contract Tests | Test API contracts | Pact |
| Load Tests | Test performance under load | Gatling |
| E2E Tests | Test complete workflows | Cypress/Selenium |

### 9.2 Integration Test Template

```java
// shared-libraries/Backend/Java/shared-persistence-library/src/test/java/com/gogidix/rapidassist/persistence/IntegrationTest.java
@SpringBootTest(classes = TestApplication.class)
@Testcontainers
public abstract class IntegrationTest {

    @Container
    static MongoDBContainer mongoDB = new MongoDBContainer("mongo:6.0")
            .withReuse(true);

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withReuse(true);

    @Container
    static GenericContainer<?> redis = new GenericContainer<>("redis:7-alpine")
            .withExposedPorts(6379)
            .withReuse(true);

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDB::getConnectionString);
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
    }
}
```

### 9.3 Contract Test Example

```java
// api-gateway/src/test/java/com/gogidix/rapidassist/gateway/ContractTest.java
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ExtendWith(PactVerificationTest.class)
@PactTestFor(providerName = "api-gateway", port = "8304")
public class ContractTest {

    @Test
    public void testAuthContract(PactVerificationContext context) {
        context.verifyInteraction();
    }

    @State("valid auth request")
    public void toValidAuthState() {
        // Setup test state
    }
}
```

### 9.4 Load Test Script

```scala
// infrastructure/load-tests/src/test/scala/com/gogidix/rapidassist/load/ApiGatewayLoadTest.scala
import io.gatling.core.Predef._
import io.gatling.http.Predef._

class ApiGatewayLoadTest extends Simulation {

  val httpProtocol = http
    .baseUrl("http://api.foundation.rapidassist.com")
    .acceptHeader("application/json")

  val scn = scenario("API Gateway Load Test")
    .exec(
      http("Health Check")
        .get("/health")
        .check(status.is(200))
    )
    .pause(1)
    .exec(
      http("Auth Login")
        .post("/api/v1/auth/login")
        .body(StringBody("""{"username":"test","password":"test","tenantId":"test"}"""))
        .check(status.is(200))
        .check(jsonPath("$.token").saveAs("token"))
    )
    .pause(1)
    .exec(
      http("Get Config")
        .get("/api/v1/config")
        .header("Authorization", "Bearer ${token}")
        .check(status.is(200))
    )

  setUp(
    scn.inject(
      rampUsers(100).during(60.seconds),
      constantUsersPerSec(50).during(5.minutes)
    )
  ).protocols(httpProtocol)
}
```

### 9.5 E2E Test Scenarios

| Scenario | Description | Success Criteria |
|----------|-------------|------------------|
| User Authentication | Login and get JWT | Token returned |
| Configuration Access | Get config values | Config retrieved |
| Multi-tenancy | Access tenant-specific data | Data isolation verified |
| Service Discovery | Service registry lookup | Service found |
| Health Check | All services healthy | All UP |

---

## 10. Phase 8: Go-Live

**Duration:** 4-6 hours
**Goal:** Production deployment and verification

### 10.1 Pre-Go-Live Checklist

| Check | Status | Notes |
|-------|--------|-------|
| All services build | ⬜ | |
| All tests pass | ⬜ | |
| Infrastructure provisioned | ⬜ | |
| Monitoring configured | ⬜ | |
| Alerts configured | ⬜ | |
| Backups configured | ⬜ | |
| Documentation complete | ⬜ | |
| Runbook created | ⬜ | |

### 10.2 Deployment Steps

```bash
# 1. Create production namespace
kubectl create namespace foundation-production

# 2. Create secrets
kubectl apply -f infrastructure/k8s/secrets.yaml

# 3. Create configmaps
kubectl apply -f infrastructure/k8s/configmaps.yaml

# 4. Deploy infrastructure services
kubectl apply -f infrastructure/k8s/mongodb.yaml
kubectl apply -f infrastructure/k8s/redis.yaml
kubectl apply -f infrastructure/k8s/kafka.yaml

# 5. Wait for infrastructure
kubectl wait --for=condition=ready pod -l app=mongodb -n foundation-production --timeout=300s

# 6. Deploy shared libraries
./scripts/deploy-shared-libraries.sh

# 7. Deploy core services
kubectl apply -f infrastructure/k8s/api-gateway.yaml
kubectl apply -f infrastructure/k8s/identity-service.yaml
kubectl apply -f infrastructure/k8s/config-service.yaml

# 8. Deploy dependent services
./scripts/deploy-all-services.sh

# 9. Verify deployment
./scripts/verify-deployment.sh

# 10. Run smoke tests
./scripts/run-smoke-tests.sh
```

### 10.3 Verification Script

```bash
#!/bin/bash
# scripts/verify-deployment.sh

SERVICES=(
    "api-gateway:8304"
    "identity-service:8888"
    "config-service:8000"
    "monitoring-service:8091"
)

for service in "${SERVICES[@]}"; do
    IFS=':' read -r name port <<< "$service"

    echo "Checking $name..."

    if kubectl get pod -l app=$name -n foundation-production | grep -q "Running"; then
        echo "✓ $name is running"
    else
        echo "✗ $name is not running"
        exit 1
    fi

    if curl -f http://localhost:$port/health > /dev/null 2>&1; then
        echo "✓ $name health check passed"
    else
        echo "✗ $name health check failed"
        exit 1
    fi
done

echo ""
echo "All services deployed and healthy!"
```

### 10.4 Smoke Tests

```bash
#!/bin/bash
# scripts/run-smoke-tests.sh

# Test API Gateway
echo "Testing API Gateway..."
curl -f http://api.foundation.rapidassist.com/health || exit 1

# Test Authentication
echo "Testing Authentication..."
TOKEN=$(curl -X POST http://api.foundation.rapidassist.com/api/v1/auth/login \
    -H "Content-Type: application/json" \
    -d '{"username":"test","password":"test","tenantId":"test"}' \
    | jq -r '.token')

if [ -z "$TOKEN" ]; then
    echo "Authentication failed"
    exit 1
fi

# Test Configuration
echo "Testing Configuration..."
curl -f http://api.foundation.rapidassist.com/api/v1/config/ \
    -H "Authorization: Bearer $TOKEN" || exit 1

# Test Monitoring
echo "Testing Monitoring..."
curl -f http://api.foundation.rapidassist.com/api/v1/monitoring/health || exit 1

echo ""
echo "All smoke tests passed!"
```

### 10.5 Rollback Plan

```bash
#!/bin/bash
# scripts/rollback.sh

DEPLOYMENT_VERSION=$1

if [ -z "$DEPLOYMENT_VERSION" ]; then
    echo "Usage: ./rollback.sh <version>"
    exit 1
fi

kubectl set image deployment/api-gateway \
    api-gateway=ghcr.io/repository/api-gateway:$DEPLOYMENT_VERSION \
    -n foundation-production

kubectl rollout undo deployment/api-gateway -n foundation-production
```

---

## 11. Risk Management

### 11.1 Risks and Mitigations

| Risk | Probability | Impact | Mitigation |
|------|-------------|--------|------------|
| Service compilation failures | Medium | High | Fix dependency issues early |
| Infrastructure delays | Low | High | Use managed services |
| Performance issues | Medium | Medium | Load testing before go-live |
| Security vulnerabilities | Low | Critical | Security scanning |
| Data loss | Low | Critical | Backups and replication |
| Downtime during deployment | Medium | Medium | Rolling updates |

### 11.2 Disaster Recovery

| Component | RTO | RPO | Backup Strategy |
|-----------|-----|-----|----------------|
| API Services | 1 hour | 15 min | Multi-AZ deployment |
| MongoDB | 4 hours | 1 hour | Daily snapshots + PITR |
| PostgreSQL | 4 hours | 1 hour | Daily snapshots + WAL archiving |
| Redis | 1 hour | 15 min | AOF persistence + replication |
| Kafka | 4 hours | 1 hour | Replication + log retention |

---

## 12. Resource Requirements

### 12.1 Team

| Role | Count | Responsibilities |
|------|-------|-----------------|
| DevOps Engineer | 1 | Infrastructure, CI/CD, deployment |
| Backend Developer | 2 | Build fixes, testing, verification |
| QA Engineer | 1 | Testing, quality assurance |

### 12.2 Infrastructure Costs (Monthly - AWS)

| Component | Spec | Cost (USD) |
|-----------|------|------------|
| EKS Cluster | Control plane | $72/month |
| EKS Nodes | 3 x t3.large | $90/month |
| DocumentDB | 3 x db.r6g.large | $600/month |
| RDS PostgreSQL | db.r6g.xlarge | $200/month |
| ElastiCache | cache.r6g.large | $90/month |
| ALB | 1 unit | $20/month |
| CloudWatch | Metrics + Logs | $50/month |
| Data Transfer | 1 TB | $80/month |
| **Total** | | **~$1,200/month** |

### 12.3 Timeline

| Phase | Duration | Start Date | End Date |
|-------|----------|------------|----------|
| Phase 1: Build Verification | 4-6 hours | Week 1, Day 1 | Week 1, Day 1 |
| Phase 2: Containerization | 6-8 hours | Week 1, Day 2 | Week 1, Day 3 |
| Phase 3: CI/CD Pipeline | 4-6 hours | Week 1, Day 3 | Week 1, Day 4 |
| Phase 4: Infrastructure Setup | 6-8 hours | Week 1, Day 4 | Week 2, Day 1 |
| Phase 5: Deployment | 8-12 hours | Week 2, Day 1 | Week 2, Day 3 |
| Phase 6: Monitoring | 4-6 hours | Week 2, Day 3 | Week 2, Day 4 |
| Phase 7: Testing | 8-12 hours | Week 2, Day 4 | Week 2, Day 5 |
| Phase 8: Go-Live | 4-6 hours | Week 2, Day 5 | Week 2, Day 5 |
| **Total** | **44-64 hours** | | **~2 weeks** |

---

## Appendices

### A. Service Port Allocation

| Service | Port | Protocol | Notes |
|---------|------|----------|-------|
| API Gateway | 8304 | HTTP | Main entry point |
| Identity Service | 8888 | HTTP | Authentication |
| Config Service | 8000 | HTTP | Configuration |
| Feature Flags | 8001 | HTTP | Feature toggles |
| Monitoring Service | 8091 | HTTP | Monitoring |
| Alerting Service | 8083 | HTTP | Alerting |
| AI Services | 8100-8130 | HTTP | AI/ML services |
| Infrastructure Services | 8310-8400 | HTTP | Various infrastructure |

### B. Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| SPRING_PROFILES_ACTIVE | Spring profile | dev |
| MONGODB_URL | MongoDB connection | localhost:27017 |
| POSTGRES_URL | PostgreSQL connection | localhost:5432 |
| REDIS_URL | Redis connection | localhost:6379 |
| KAFKA_BOOTSTRAP | Kafka brokers | localhost:9092 |
| JWT_SECRET | JWT signing | - |
| TENANT_ID | Default tenant | - |

### C. Useful Commands

```bash
# Build all services
./scripts/build-all-services.sh

# Run all tests
./scripts/run-all-tests.sh

# Deploy to Kubernetes
kubectl apply -f infrastructure/k8s/

# Check service health
curl http://localhost:8304/health

# View logs
kubectl logs -f deployment/api-gateway -n foundation-production

# Scale deployment
kubectl scale deployment/api-gateway --replicas=5 -n foundation-production

# Rollback
kubectl rollout undo deployment/api-gateway -n foundation-production
```

---

**Document Version:** 1.0.0
**Last Updated:** 2026-03-14
**Next Review:** After Phase 1 completion
