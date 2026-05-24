# Health Check Verification Plan
# Core Foundation Services - Sequential Startup & Validation

## Prerequisites
- AWS MongoDB Atlas: Configured and accessible
- AWS ElastiCache Redis: Will be deployed via Terraform
- AWS MSK Kafka: Will be deployed via Terraform
- No local infrastructure - all AWS cloud resources

## Startup Sequence (Order Matters!)

### Phase 1: Infrastructure Base Services
1. **Deploy AWS Infrastructure** (Terraform)
   - ElastiCache Redis
   - MSK Kafka
   - Get connection endpoints

### Phase 2: Core Services (Sequential)

#### 1. Service Registry (Eureka) - PORT 8761
```
Startup Command:
  SPRING_PROFILES_ACTIVE=aws \
  MONGODB_URI=<mongodb-atlas-uri> \
  REDIS_HOST=<aws-redis-endpoint> \
  REDIS_PORT=6379 \
  REDIS_PASSWORD=<redis-password> \
  SPRING_KAFKA_BOOTSTRAP_SERVERS=<aws-msk-brokers> \
  java -jar service-registry-discovery-1.0.0.jar

Health Check:
  curl http://localhost:8761/actuator/health

Expected Response:
  {"status":"UP","groups":["liveness","readiness"]}

Critical Checks:
  ✅ Status: UP
  ✅ Eureka dashboard accessible: http://localhost:8761/
  ✅ No ClassNotFoundException for SharedExceptionAutoConfiguration
  ✅ No ClassNotFoundException for SupabaseSecurityAutoConfiguration
  ✅ Service registers with itself (standalone mode)
```

#### 2. Config Service - PORT 8080
```
Startup Command:
  SPRING_PROFILES_ACTIVE=aws \
  MONGODB_URI=<mongodb-atlas-uri> \
  REDIS_HOST=<aws-redis-endpoint> \
  REDIS_PORT=6379 \
  REDIS_PASSWORD=<redis-password> \
  EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/ \
  java -jar config-service-1.0.0.jar

Health Check:
  curl http://localhost:8080/actuator/health

Expected Response:
  {"status":"UP"}

Critical Checks:
  ✅ Status: UP
  ✅ Registered with Eureka (check Eureka dashboard)
  ✅ MongoDB connection successful
  ✅ Redis connection successful (if used)
```

#### 3. API Gateway - PORT 8304
```
Startup Command:
  SPRING_PROFILES_ACTIVE=aws \
  MONGODB_URI=<mongodb-atlas-uri> \
  REDIS_HOST=<aws-redis-endpoint> \
  REDIS_PORT=6379 \
  REDIS_PASSWORD=<redis-password> \
  EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/ \
  java -jar api-gateway-1.0.0.jar

Health Check:
  curl http://localhost:8304/actuator/health

Expected Response:
  {"status":"UP","components":{"...":{"status":"UP"}}}

Critical Checks:
  ✅ Status: UP
  ✅ Registered with Eureka
  ✅ Gateway routes loaded
  ✅ No WebFlux/Servlet API errors
  ✅ No SharedExceptionAutoConfiguration errors
  ✅ No SupabaseSecurityAutoConfiguration errors
```

#### 4. Monitoring Service - PORT 8091
```
Startup Command:
  SPRING_PROFILES_ACTIVE=aws \
  MONGODB_URI=<mongodb-atlas-uri> \
  SPRING_KAFKA_BOOTSTRAP_SERVERS=<aws-msk-brokers> \
  EUREKA_CLIENT_SERVICEURL_DEFAULTZONE=http://localhost:8761/eureka/ \
  java -jar monitoring-service-1.0.0.jar

Health Check:
  curl http://localhost:8091/actuator/health

Expected Response:
  {"status":"UP"}

Critical Checks:
  ✅ Status: UP
  ✅ Registered with Eureka
  ✅ Kafka connection successful
  ✅ MongoDB connection successful
```

## Smoke Tests (After All Services Running)

### Test 1: Eureka Registry
```bash
curl http://localhost:8761/eureka/apps
Expected: JSON with all registered services
```

### Test 2: Service Discovery via Gateway
```bash
curl http://localhost:8304/actuator/gateway/routes
Expected: JSON with all configured routes
```

### Test 3: Config Service Health
```bash
curl http://localhost:8080/actuator/health
Expected: {"status":"UP"}
```

### Test 4: Monitoring Health
```bash
curl http://localhost:8091/actuator/health
Expected: {"status":"UP"}
```

### Test 5: Cross-Service Communication
```bash
# Gateway can reach Config Service via Eureka
curl http://localhost:8304/config-service/actuator/health
Expected: Config service health response
```

## Failure Criteria (Deployment Must NOT Proceed If Any Fail)

- ❌ Service fails to start within 60 seconds
- ❌ Health check returns status "DOWN"
- ❌ Service not visible in Eureka dashboard
- ❌ ClassNotFoundException related to WebFlux/Servlet incompatibility
- ❌ Connection refused to MongoDB Atlas
- ❌ Connection refused to AWS Redis
- ❌ Connection refused to AWS MSK
- ❌ Service crashes within 30 seconds of startup

## Success Criteria (Before Git Push)

- ✅ All 4 services show {"status":"UP"}
- ✅ All services visible in Eureka dashboard
- ✅ All 5 smoke tests pass
- ✅ Services stay healthy for at least 2 minutes
- ✅ No ERROR logs in application logs

## Environment Variables Required

```bash
export MONGODB_URI="mongodb+srv://gazalgidix_db_user:Ajimmy2907@cluster0.ggksiwg.mongodb.net/<service-db>?appName=Cluster0"
export REDIS_HOST="<aws-elasticache-endpoint>"
export REDIS_PORT="6379"
export REDIS_PASSWORD="<aws-redis-password>"
export SPRING_KAFKA_BOOTSTRAP_SERVERS="<aws-msk-broker-1>:9092,<aws-msk-broker-2>:9092"
export EUREKA_CLIENT_SERVICEURL_DEFAULTZONE="http://localhost:8761/eureka/"
export SPRING_PROFILES_ACTIVE="aws"
```
