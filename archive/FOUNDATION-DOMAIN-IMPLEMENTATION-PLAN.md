# Foundation Domain Production Readiness - Implementation Plan

**Created**: 2026-01-03
**Project**: Gogidix Road Assist SaaS - Foundation Domain
**Scope**: Complete production readiness for all 93 services

---

## Executive Summary

This plan outlines the step-by-step approach to bring the Foundation Domain to 100% production readiness. The implementation is organized into 5 phases, prioritizing critical infrastructure first, followed by API implementation, documentation, and testing.

**Current Production Readiness**: 60% → **Target**: 100%

---

## Implementation Approach

### Architectural Pattern: Hexagonal/Clean Architecture

All services follow the established pattern used in the 9 fully implemented AI services:

```
┌─────────────────────────────────────────────────┐
│  adapters/in/web/                               │
│  ┌─────────────────────────────────────────┐   │
│  │  *Controller.java                         │   │
│  │  - @RestController                        │   │
│  │  - @RequestMapping                        │   │
│  │  - @Tag, @Operation (Swagger)             │   │
│  │  - @Valid on @RequestBody                │   │
│  └─────────────────────────────────────────┘   │
├─────────────────────────────────────────────────┤
│  application/                                   │
│  ┌─────────────────────────────────────────┐   │
│  │  *Service.java / *UseCase.java            │   │
│  │  - Orchestrates domain logic              │   │
│  │  - Implements Command/Query interfaces    │   │
│  └─────────────────────────────────────────┘   │
├─────────────────────────────────────────────────┤
│  domain/                                        │
│  ┌─────────────────────────────────────────┐   │
│  │  model/ - Domain Entities                 │   │
│  │  port/in/ - Command, Query interfaces     │   │
│  │  port/out/ - Repository, Gateway          │   │
│  └─────────────────────────────────────────┘   │
├─────────────────────────────────────────────────┤
│  infrastructure/adapter/                        │
│  ┌─────────────────────────────────────────┐   │
│  │  persistence/ - Repository implementations │   │
│  │  ai/ - AI provider adapters               │   │
│  │  config/ - Configuration classes          │   │
│  └─────────────────────────────────────────┘   │
└─────────────────────────────────────────────────┘
```

### Key Principles

1. **Zero Assumption** - Verify everything physically, no assumptions
2. **Incremental Progress** - Each service independently deployable
3. **Template Reuse** - Use existing services as templates
4. **Validation First** - DTOs validated before business logic
5. **Swagger Documentation** - Every API endpoint documented

---

## Phase 1: Infrastructure Foundation (Week 1)

### Goal: Establish consistent infrastructure across all services

### 1.1 Global Exception Handler Rollout

**Task**: Copy `GlobalExceptionHandler.java` to all 27 AI services

**Template Location**:
```
/Foundation-Domain/SHARED-INFRASTRUCTURE/templates/GlobalExceptionHandler.java
```

**Target Locations** (update package for each):
```
/{service}/src/main/java/com/gogidix/{service}/infrastructure/adapter/web/GlobalExceptionHandler.java
```

**Services**: All 27 AI services

**Outcome**: Consistent error handling with proper validation error responses

---

### 1.2 Flyway Migrations Rollout

**Task**: Copy migration templates to appropriate services

| Migration | Target Services |
|-----------|-----------------|
| V1__Base_Tables.sql | All 27 AI services |
| V2__AI_Service_Tables.sql | All 27 AI services |
| V3__Chatbot_Tables.sql | ai-chatbot-service |
| V4__Sentiment_Analysis_Tables.sql | ai-sentiment-analysis-service |
| V5__Translation_Tables.sql | ai-translation-service |
| V6__Image_Recognition_Tables.sql | ai-image-recognition-service |
| V7__Data_Prediction_Tables.sql | ai-data-prediction-service |

**Target Location**:
```
/{service}/src/main/resources/db/migration/
```

**Outcome**: Database schema version control and migration capability

---

### 1.3 AI Services Dashboard Production Configuration

**Task**: Create `vercel.json` and environment configuration

**Current State**: React + Vite app running on port 3000
**Required**: Production deployment config for Vercel

**Tasks**:
1. Create `vercel.json` with rewrite rules for API proxy
2. Create `.env.production` template
3. Update `vite.config.ts` for production build

**Outcome**: Dashboard deployable to production

---

## Phase 2: API Implementation (Weeks 2-4)

### Goal: Implement full REST APIs for 15 StatusController-only services

### Service Classification

#### Tier 1: Has Domain + Ports (Just Add API Layer)
| Service | Existing Components | Required |
|---------|-------------------|----------|
| ai-leads-generator-service | 6 models, 5 ports | Controller + DTOs |
| ai-recommendation-engine-service | 2 models | Full API + Service |
| ai-sentiment-analysis-service | Domain exists | API implementation |

#### Tier 2: Empty Services (Full Implementation Required)
| Service | Domain Focus | Priority |
|---------|--------------|----------|
| predictive-maintenance-service | Equipment failure prediction | HIGH |
| fraud-detection-service | Transaction fraud analysis | HIGH |
| intelligent-dispatch-service | Resource assignment | HIGH |
| route-optimization-service | Logistics routing | MEDIUM |
| analytics-service | General analytics | MEDIUM |
| data-analytics-service | Data insights | MEDIUM |
| customer-behaviour-analytics-service | User behavior analysis | MEDIUM |
| ai-training-ml-service | Model training | LOW |
| document-intelligence-service | Document processing | LOW |
| dynamic-pricing-service | Price optimization | LOW |
| customer-support-chatbot-service | Customer service | LOW |
| vendors-product-listing-ai-service | E-commerce | LOW |

---

### 2.1 Implementation Pattern (Template-Based)

For each empty service, follow this pattern:

#### Step 1: Domain Layer
```java
// domain/model/{Entity}.java
public record {Entity}(UUID id, String name, ...) {}

// domain/port/in/{Service}Command.java
public interface {Service}Command {
    record Create{Entity}Command(...) {}
    record Update{Entity}Command(...) {}
    record Delete{Entity}Command(...) {}
}

// domain/port/in/{Service}Query.java
public interface {Service}Query {
    record Get{Entity}Query(...) {}
    record List{Entities}Query(...) {}
}

// domain/port/out/{Entity}Repository.java
public interface {Entity}Repository {
    {Entity} save({Entity} entity);
    Optional<{Entity}> findById(UUID id);
    List<{Entity}> findAll(...);
    void deleteById(UUID id);
}
```

#### Step 2: Application Layer
```java
// application/{Service}Service.java
@Service
public class {Service}Service implements {Service}Command, {Service}Query {
    private final {Entity}Repository repository;
    // Implementation
}
```

#### Step 3: Infrastructure Layer
```java
// infrastructure/adapter/persistence/InMemory{Entity}Repository.java
@Repository
public class InMemory{Entity}Repository implements {Entity}Repository {
    private final Map<UUID, {Entity}> store = new ConcurrentHashMap<>();
    // Implementation
}
```

#### Step 4: Controller Layer
```java
// adapters/in/web/{Service}Controller.java
@RestController
@RequestMapping("/api/v1/{service}")
@Tag(name = "{Service} API", description = "...")
public class {Service}Controller {

    private final {Service}Service service;

    @PostMapping
    @Operation(summary = "Create {entity}")
    public ResponseEntity<ApiResponse<String>> create(
            @Valid @RequestBody Create{Entity}Request request) {
        // Implementation
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get {entity}")
    public ResponseEntity<ApiResponse<{Entity}>> getById(
            @PathVariable UUID id) {
        // Implementation
    }
}
```

---

### 2.2 Service-Specific API Requirements

#### predictive-maintenance-service
**Purpose**: Predict equipment failures before they occur

**Endpoints**:
- `POST /api/v1/predictive-maintenance/predict` - Predict failure probability
- `POST /api/v1/predictive-maintenance/train-model` - Train prediction model
- `GET /api/v1/predictive-maintenance/equipment/{id}/health` - Get equipment health score
- `GET /api/v1/predictive-maintenance/maintenance-schedule` - Get maintenance schedule
- `POST /api/v1/predictive-maintenance/sensor-data` - Ingest sensor data

**Domain Models**:
- `Equipment` - Equipment being monitored
- `SensorReading` - Time-series sensor data
- `MaintenanceSchedule` - Predictive maintenance schedule
- `FailurePrediction` - Prediction result

---

#### fraud-detection-service
**Purpose**: Detect fraudulent transactions in real-time

**Endpoints**:
- `POST /api/v1/fraud-detection/analyze` - Analyze transaction for fraud
- `POST /api/v1/fraud-detection/batch-analyze` - Batch analysis
- `GET /api/v1/fraud-detection/rules` - Get fraud detection rules
- `POST /api/v1/fraud-detection/rules` - Create/update rules
- `GET /api/v1/fraud-detection/alerts` - Get fraud alerts

**Domain Models**:
- `FraudAnalysis` - Analysis result
- `FraudRule` - Detection rule
- `FraudAlert` - Alert for detected fraud
- `Transaction` - Transaction being analyzed

---

#### intelligent-dispatch-service
**Purpose**: Optimize resource assignment for road assistance

**Endpoints**:
- `POST /api/v1/dispatch/assign` - Assign provider to request
- `POST /api/v1/dispatch/auto-assign` - Automatic assignment
- `GET /api/v1/dispatch/providers/available` - Get available providers
- `GET /api/v1/dispatch/requests/pending` - Get pending requests
- `POST /api/v1/dispatch/optimize-routes` - Optimize routes

**Domain Models**:
- `DispatchRequest` - Assistance request
- `Provider` - Service provider
- `DispatchAssignment` - Assignment result
- `RouteOptimization` - Optimized route

---

#### route-optimization-service
**Purpose**: Optimize delivery and service routes

**Endpoints**:
- `POST /api/v1/route-optimization/optimize` - Optimize route
- `POST /api/v1/route-optimization/multi-stop` - Multi-stop optimization
- `GET /api/v1/route-optimization/traffic` - Get traffic data
- `POST /api/v1/route-optimization/recalculate` - Recalculate based on conditions

**Domain Models**:
- `Route` - Optimized route
- `Waypoint` - Route stop
- `TrafficCondition` - Traffic data
- `RouteOptimizationRequest` - Optimization request

---

#### analytics-service
**Purpose**: General-purpose analytics

**Endpoints**:
- `POST /api/v1/analytics/query` - Execute analytics query
- `POST /api/v1/analytics/aggregation` - Aggregate data
- `GET /api/v1/analytics/dashboards/{id}` - Get dashboard
- `POST /api/v1/analytics/reports` - Generate report

**Domain Models**:
- `AnalyticsQuery` - Query definition
- `AnalyticsResult` - Query result
- `Dashboard` - Analytics dashboard
- `Report` - Generated report

---

### 2.3 Implementation Order

1. **Week 2**: predictive-maintenance-service, fraud-detection-service
2. **Week 3**: intelligent-dispatch-service, route-optimization-service
3. **Week 4**: analytics-service, data-analytics-service, customer-behaviour-analytics-service
4. **Week 5**: Remaining 6 lower-priority services

---

## Phase 3: Documentation (Week 5)

### 3.1 OpenAPI Specification Export

**Task**: Export OpenAPI specs from all 87 backend services

**Approach**:
1. Start Spring Boot services with `springdoc-openapi` dependency
2. Access `/v3/api-docs.yaml` endpoint
3. Consolidate into API documentation portal

**Script**:
```bash
#!/bin/bash
# Export OpenAPI specs from all services
SERVICES=(
  "ai-chatbot-service:8081"
  "ai-content-generator-service:8082"
  # ... all 87 services
)

for service in "${SERVICES[@]}"; do
  name="${service%:*}"
  port="${service#*:}"
  curl -s http://localhost:$port/v3/api-docs.yaml > "specs/$name.yaml"
done
```

**Output**: `/Foundation-Domain/API_SPECS/` directory with all OpenAPI specs

---

### 3.2 API Documentation Portal

**Task**: Create centralized API documentation portal

**Options**:
1. **Swagger UI** - Host all specs in a single Swagger UI
2. **Redoc** - Beautiful documentation from OpenAPI specs
3. **Stoplight Studio** - Professional API documentation platform

**Recommended**: Redoc for production

**Location**: `/Foundation-Domain/api-docs/`

---

## Phase 4: Testing (Week 6)

### 4.1 Integration Test Suite

**Task**: Create integration tests for all services

**Framework**: Spring Boot Test + TestContainers

**Coverage Goals**:
- Controller layer: 100%
- Service layer: 80%+
- Critical paths: 100%

**Test Structure**:
```java
@SpringBootTest
@Testcontainers
class {Service}IntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(/* ... */);

    @Autowired
    private {Service}Controller controller;

    @Test
    void testCreate{Entity}() { /* ... */ }
}
```

---

### 4.2 End-to-End Testing

**Task**: Run E2E tests across service boundaries

**Scenarios**:
1. User creates chatbot session → AI processes → Response returned
2. Lead generation request → AI enriches → Lead stored
3. Fraud detection → Transaction analyzed → Alert generated

**Tools**: Postman Collections / Newman

---

## Phase 5: Mock Removal (Week 7)

### 5.1 Mock Audit

**Task**: Identify all mock/stub implementations

**Areas to Audit**:
- AI provider adapters (DeepSeek, OpenAI, etc.)
- Repository implementations (in-memory vs. real DB)
- External service integrations

---

### 5.2 Real Implementation Replacement

**Task**: Replace mocks with real implementations

**Priority**:
1. AI provider integrations
2. Database repositories (MongoDB, PostgreSQL)
3. External API calls

---

## Expected Outcomes

### After Phase 1 (Infrastructure)
- ✅ Consistent error handling across all services
- ✅ Database migrations in place
- ✅ Dashboard deployable to production
- ✅ Production readiness: 70%

### After Phase 2 (API Implementation)
- ✅ All 15 services have full REST APIs
- ✅ All endpoints documented with Swagger
- ✅ DTO validation on all endpoints
- ✅ Production readiness: 85%

### After Phase 3 (Documentation)
- ✅ Complete OpenAPI specs exported
- ✅ API documentation portal live
- ✅ Client SDKs generatable
- ✅ Production readiness: 90%

### After Phase 4 (Testing)
- ✅ Integration tests passing
- ✅ E2E tests passing
- ✅ Performance benchmarks established
- ✅ Production readiness: 95%

### After Phase 5 (Mock Removal)
- ✅ All mocks replaced with real implementations
- ✅ Production database connections
- ✅ Real AI provider integrations
- ✅ Production readiness: 100%

---

## Deployment Readiness Checklist

### Pre-Deployment
- [ ] All services have `application.yml` with environment variables
- [ ] All services have `railway.json` for deployment
- [ ] Database migrations prepared
- [ ] Environment variables documented
- [ ] Health check endpoints verified

### Deployment
- [ ] Deploy 87 backend services to Railway
- [ ] Deploy Dashboard to Vercel
- [ ] Configure API gateway/load balancer
- [ ] Set up monitoring/alerting

### Post-Deployment
- [ ] Smoke tests pass
- [ ] Monitoring dashboards operational
- [ ] Error tracking configured
- [ ] Backup strategy in place

---

## Timeline Summary

| Phase | Duration | Key Deliverables |
|-------|----------|------------------|
| Phase 1: Infrastructure | Week 1 | Exception handlers, migrations, dashboard config |
| Phase 2: API Implementation | Weeks 2-5 | 15 full REST APIs |
| Phase 3: Documentation | Week 5 | OpenAPI specs, API portal |
| Phase 4: Testing | Week 6 | Integration and E2E tests |
| Phase 5: Mock Removal | Week 7 | Real implementations |

**Total Timeline**: 7 weeks to 100% production readiness

---

## Success Metrics

| Metric | Current | Target |
|--------|---------|--------|
| Services with full API | 12/93 (13%) | 93/93 (100%) |
| Services with validation | 9/93 (10%) | 93/93 (100%) |
| Services with error handling | 0/93 (0%) | 93/93 (100%) |
| Services with Swagger docs | 12/93 (13%) | 93/93 (100%) |
| Services with tests | 0/93 (0%) | 93/93 (100%) |
| Production Readiness | 60% | 100% |

---

**Document Version**: 1.0
**Last Updated**: 2026-01-03
