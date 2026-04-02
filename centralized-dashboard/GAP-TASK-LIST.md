# Centralized Dashboard Domain - Gap Task List

**Generated**: 2026-01-11
**Agent**: Agent 2 of 4 (Centralized-Dashboard Domain)
**Total Gaps**: 92 tasks across 6 services

---

## Task Summary by Severity

| Severity | Count | Estimated Hours |
|----------|-------|-----------------|
| CRITICAL | 48 | 160-200 hours |
| HIGH | 28 | 60-80 hours |
| MEDIUM | 12 | 15-25 hours |
| LOW | 4 | 5-15 hours |
| **TOTAL** | **92** | **240-320 hours** |

---

## CRITICAL Tasks (48 tasks)

### C1. DTO Layer & Validation (12 tasks)

#### Dashboard Analytics Service
- [ ] **C1.1** Create package: `com.gogidix.rapidassist.dashboard.analytics.service.adapters.in.web.dto`
  - File: `/Backend/Java/dashboard-analytics-service/src/main/java/com/gogidix/rapidassist/dashboard/analytics/service/adapters/in/web/dto/`
  - Effort: 2 hours

- [ ] **C1.2** Create `RecordEventRequestDto.java` with validation annotations
  - Add: @NotNull, @NotBlank, @Size validations
  - File: `dto/RecordEventRequestDto.java`
  - Effort: 1 hour

- [ ] **C1.3** Create `BatchEventsRequestDto.java` with validation
  - Add: @NotEmpty, @Size max for batch
  - File: `dto/BatchEventsRequestDto.java`
  - Effort: 1 hour

- [ ] **C1.4** Create response DTOs for all endpoints
  - Create: `DashboardAnalyticsResponseDto.java`
  - Create: `UsageReportResponseDto.java`
  - Create: `EventListResponseDto.java`
  - File: `dto/*ResponseDto.java`
  - Effort: 2 hours

#### Dashboard Configuration Service
- [ ] **C1.5** Create package: `com.gogidix.rapidassist.dashboard.configuration.service.adapters.in.web.dto`
  - File: `/Backend/Java/dashboard-configuration-service/src/main/java/.../dto/`
  - Effort: 2 hours

- [ ] **C1.6** Create request DTOs with validation
  - Create: `CreateDashboardRequestDto.java`
  - Create: `UpdateDashboardRequestDto.java`
  - Add: @NotNull, @NotBlank, @Size validations
  - File: `dto/*RequestDto.java`
  - Effort: 2 hours

- [ ] **C1.7** Create response DTOs
  - Create: `DashboardConfigurationResponseDto.java`
  - Create: `DashboardListResponseDto.java`
  - File: `dto/*ResponseDto.java`
  - Effort: 2 hours

#### Dashboard Reporting Service
- [ ] **C1.8** Create package: `com.gogidix.rapidassist.dashboard.reporting.service.adapters.in.web.dto`
  - File: `/Backend/Java/dashboard-reporting-service/src/main/java/.../dto/`
  - Effort: 2 hours

- [ ] **C1.9** Create request DTOs with validation
  - Create: `CreateDefinitionRequestDto.java`
  - Create: `GenerateReportRequestDto.java`
  - File: `dto/*RequestDto.java`
  - Effort: 2 hours

- [ ] **C1.10** Create response DTOs
  - Create: `ReportDefinitionResponseDto.java`
  - Create: `ReportExecutionResponseDto.java`
  - File: `dto/*ResponseDto.java`
  - Effort: 2 hours

#### Dashboard Aggregation Service (Node.js)
- [ ] **C1.11** Create DTO directory and validation schemas
  - Directory: `/src/adapters/in/http/dto/`
  - Create: Joi validation schemas for all requests
  - File: `dto/*.js`
  - Effort: 3 hours

- [ ] **C1.12** Add request validation middleware
  - Enhance existing validation middleware
  - Add schema validation for all endpoints
  - File: `src/adapters/in/http/middleware/validation.js`
  - Effort: 2 hours

---

### C2. Mapper Layer (6 tasks)

#### Dashboard Analytics Service
- [ ] **C2.1** Create mapper package
  - Package: `com.gogidix.rapidassist.dashboard.analytics.service.adapters.in.web.mapper`
  - Effort: 1 hour

- [ ] **C2.2** Create `DashboardAnalyticsMapper.java`
  - Map: DTO → Domain model
  - Map: Domain model → Response DTO
  - Use: MapStruct or manual mapping
  - File: `mapper/DashboardAnalyticsMapper.java`
  - Effort: 2 hours

#### Dashboard Configuration Service
- [ ] **C2.3** Create `DashboardConfigMapper.java`
  - Map: DTO ↔ Domain model
  - Handle nested records (widgets, layout, theme)
  - File: `mapper/DashboardConfigMapper.java`
  - Effort: 2 hours

#### Dashboard Reporting Service
- [ ] **C2.4** Create `ReportingMapper.java`
  - Map: DTO ↔ Domain model
  - File: `mapper/ReportingMapper.java`
  - Effort: 2 hours

#### Dashboard Aggregation Service
- [ ] **C2.5** Create mapper utilities
  - Directory: `/src/adapters/in/http/mappers/`
  - Create mapping functions for all entities
  - File: `mappers/*.js`
  - Effort: 2 hours

- [ ] **C2.6** Update controllers to use mappers
  - Replace inline mapping with mapper calls
  - All 3 Java services
  - Effort: 2 hours

---

### C3. Exception Handling (8 tasks)

#### All Java Services
- [ ] **C3.1** Create exception package
  - Package: `...service.adapters.in.web.exception`
  - All 3 services
  - Effort: 1 hour

- [ ] **C3.2** Create custom exceptions
  - Create: `ResourceNotFoundException.java`
  - Create: `ValidationException.java`
  - Create: `BusinessRuleException.java`
  - Create: `TenantNotFoundException.java`
  - File: `exception/*.java`
  - Effort: 2 hours

- [ ] **C3.3** Create `GlobalExceptionHandler.java` for Analytics Service
  - Add: @ControllerAdvice
  - Handle: all custom exceptions
  - Handle: IllegalArgumentException, IllegalStateException
  - Return: standardized ErrorResponse
  - File: `exception/GlobalExceptionHandler.java`
  - Effort: 2 hours

- [ ] **C3.4** Create `GlobalExceptionHandler.java` for Configuration Service
  - Same as C3.3 for Configuration service
  - File: `exception/GlobalExceptionHandler.java`
  - Effort: 2 hours

- [ ] **C3.5** Create `GlobalExceptionHandler.java` for Reporting Service
  - Same as C3.3 for Reporting service
  - File: `exception/GlobalExceptionHandler.java`
  - Effort: 2 hours

- [ ] **C3.6** Create `ErrorResponse.java` class
  - Fields: timestamp, status, error, message, path, requestId
  - File: `exception/ErrorResponse.java`
  - Effort: 1 hour

- [ ] **C3.7** Update service methods to throw exceptions
  - Replace: `Optional.empty()` returns with exceptions
  - Add: proper error messages
  - All 3 services
  - Effort: 3 hours

- [ ] **C3.8** Enhance Node.js error handler
  - File: `src/adapters/in/http/middleware/errorHandler.js`
  - Add: error types/classes
  - Add: standardized error response format
  - Effort: 2 hours

---

### C4. Transaction Management (4 tasks)

#### All Java Services
- [ ] **C4.1** Add `@Transactional` to command methods
  - Service: DashboardAnalyticsService
  - Methods: recordEvent, batchRecordEvents, deleteEventsBefore
  - Add: rollback rules
  - File: `application/DashboardAnalyticsService.java`
  - Effort: 1 hour

- [ ] **C4.2** Add `@Transactional` to configuration service
  - Service: DashboardConfigService
  - Methods: all update operations
  - Add: rollback for RuntimeException
  - File: `application/DashboardConfigService.java`
  - Effort: 1 hour

- [ ] **C4.3** Add `@Transactional` to reporting service
  - Service: DashboardReportingService
  - Methods: create, update, generate operations
  - File: `application/DashboardReportingService.java`
  - Effort: 1 hour

- [ ] **C4.4** Configure transaction manager
  - Add: `@EnableTransactionManagement`
  - Configure: MongoDB transaction manager
  - File: `*/DashboardAnalyticsServiceApplication.java` (all 3)
  - Effort: 1 hour

---

### C5. Testing - Unit Tests (12 tasks)

#### Dashboard Analytics Service
- [ ] **C5.1** Create test package structure
  - Package: `...service.application`
  - Package: `...service.adapters.in.web`
  - Package: `...service.adapters.infrastructure`
  - Effort: 1 hour

- [ ] **C5.2** Write tests for `DashboardAnalyticsService`
  - Test: recordEvent - happy path
  - Test: recordEvent - validation errors
  - Test: batchRecordEvents
  - Test: getEventsByDashboard
  - Test: generateUsageReport
  - Use: @Mock for repository
  - File: `application/DashboardAnalyticsServiceTest.java`
  - Effort: 4 hours

- [ ] **C5.3** Write tests for `AnalyticsController`
  - Test: all endpoints with valid requests
  - Test: all endpoints with invalid requests
  - Test: error handling
  - Use: MockMvc
  - File: `adapters/in/web/AnalyticsControllerTest.java`
  - Effort: 3 hours

- [ ] **C5.4** Write tests for `MongoAnalyticsRepository`
  - Test: save, findBy*, count*, delete
  - Use: @DataMongoTest
  - File: `adapters/infrastructure/MongoAnalyticsRepositoryTest.java`
  - Effort: 2 hours

#### Dashboard Configuration Service
- [ ] **C5.5** Write tests for `DashboardConfigService`
  - Test: CRUD operations
  - Test: widget management
  - Test: permission checks
  - File: `application/DashboardConfigServiceTest.java`
  - Effort: 4 hours

- [ ] **C5.6** Write tests for `DashboardConfigController`
  - Test: all REST endpoints
  - File: `adapters/in/web/DashboardConfigControllerTest.java`
  - Effort: 3 hours

- [ ] **C5.7** Write tests for `MongoDashboardConfigRepository`
  - File: `adapters/infrastructure/MongoDashboardConfigRepositoryTest.java`
  - Effort: 2 hours

#### Dashboard Reporting Service
- [ ] **C5.8** Write tests for `DashboardReportingService`
  - Test: report definition CRUD
  - Test: report generation
  - File: `application/DashboardReportingServiceTest.java`
  - Effort: 3 hours

- [ ] **C5.9** Write tests for `ReportingController`
  - File: `adapters/in/web/ReportingControllerTest.java`
  - Effort: 2 hours

- [ ] **C5.10** Write tests for repositories
  - File: `adapters/infrastructure/*RepositoryTest.java`
  - Effort: 2 hours

#### Dashboard Aggregation Service
- [ ] **C5.11** Create test structure and write tests
  - Test: controllers, use cases, infrastructure
  - Framework: Supertest + Jest
  - File: `**/*.test.js`
  - Effort: 6 hours

- [ ] **C5.12** Add JaCoCo plugin for coverage
  - Add: jacoco-maven-plugin to all pom.xml
  - Configure: 85% minimum coverage
  - File: `pom.xml` (all 3 services)
  - Effort: 1 hour

---

### C6. API Documentation (6 tasks)

#### All Java Services
- [ ] **C6.1** Add springdoc-openapi dependency
  - Add to pom.xml (all 3 services)
  - Dependency: org.springdoc:springdoc-openapi-starter-webmvc-ui
  - File: `pom.xml`
  - Effort: 1 hour

- [ ] **C6.2** Add OpenAPI configuration
  - Create: `OpenApiConfig.java`
  - Configure: API info, servers, security
  - File: `config/OpenApiConfig.java`
  - Effort: 1 hour

- [ ] **C6.3** Add @Operation annotations to Analytics Controller
  - Annotate: all endpoints
  - Add: descriptions, summaries, tags
  - Add: @ApiResponse for success/error
  - File: `adapters/in/web/AnalyticsController.java`
  - Effort: 2 hours

- [ ] **C6.4** Add @Operation annotations to Configuration Controller
  - Same as C6.3
  - File: `adapters/in/web/DashboardConfigController.java`
  - Effort: 2 hours

- [ ] **C6.5** Add @Operation annotations to Reporting Controller
  - Same as C6.3
  - File: `adapters/in/web/ReportingController.java`
  - Effort: 2 hours

- [ ] **C6.6** Add Swagger UI for Node.js service
  - Add: swagger-ui-express
  - Configure: OpenAPI spec
  - File: `src/bootstrap/server.js`
  - Effort: 2 hours

---

## HIGH Priority Tasks (28 tasks)

### H1. CI/CD Pipeline (10 tasks)

- [ ] **H1.1** Create `.github/workflows` directory
  - Location: Root of each service
  - Effort: 1 hour

- [ ] **H1.2** Create CI workflow for Analytics Service
  - File: `.github/workflows/ci-analytics.yml`
  - Steps: checkout, setup JDK, cache Maven, test, build
  - Effort: 2 hours

- [ ] **H1.3** Create CI workflow for Configuration Service
  - File: `.github/workflows/ci-config.yml`
  - Same as H1.2
  - Effort: 2 hours

- [ ] **H1.4** Create CI workflow for Reporting Service
  - File: `.github/workflows/ci-reporting.yml`
  - Same as H1.2
  - Effort: 2 hours

- [ ] **H1.5** Create CI workflow for Aggregation Service
  - File: `.github/workflows/ci-aggregation.yml`
  - Steps: checkout, setup Node, install, test, lint
  - Effort: 2 hours

- [ ] **H1.6** Create Docker build workflow
  - File: `.github/workflows/docker-build.yml`
  - Build and push images
  - Effort: 2 hours

- [ ] **H1.7** Add code quality gate (SonarQube)
  - Add: Sonar Cloud scan
  - Configure: quality thresholds
  - Effort: 2 hours

- [ ] **H1.8** Add security scanning
  - Add: OWASP dependency check
  - Add: Trivy vulnerability scan
  - Effort: 2 hours

- [ ] **H1.9** Create deployment workflow
  - File: `.github/workflows/deploy.yml`
  - Deploy to: Railway/staging
  - Effort: 2 hours

- [ ] **H1.10** Add workflow for production deployment
  - Require: manual approval
  - Deploy to: Railway/production
  - Effort: 1 hour

---

### H2. Multi-Tenancy Enhancement (8 tasks)

#### All Java Services
- [ ] **H2.1** Create tenant context package
  - Package: `...security.context`
  - Effort: 1 hour

- [ ] **H2.2** Create `TenantContext.java`
  - ThreadLocal storage for tenantId
  - Methods: set, get, clear
  - File: `security/context/TenantContext.java`
  - Effort: 1 hour

- [ ] **H2.3** Create `TenantInterceptor.java`
  - Extract: tenantId from JWT/header
  - Set: TenantContext
  - File: `security/interceptor/TenantInterceptor.java`
  - Effort: 2 hours

- [ ] **H2.4** Register interceptor in MVC config
  - Add: WebMvcConfigurer
  - File: `config/WebMvcConfig.java`
  - Effort: 1 hour

- [ ] **H2.5** Update repositories to use TenantContext
  - Remove: tenantId parameters
  - Read: from TenantContext
  - All 3 services
  - Effort: 3 hours

- [ ] **H2.6** Update services to use TenantContext
  - Remove: tenantId parameters
  - All 3 services
  - Effort: 2 hours

- [ ] **H2.7** Update controllers to remove tenantId
  - Remove: tenantId from method signatures
  - All 3 services
  - Effort: 2 hours

- [ ] **H2.8** Add tenant-aware caching
  - Cache key includes tenantId
  - File: `config/CacheConfig.java`
  - Effort: 2 hours

---

### H3. Security Implementation (6 tasks)

- [ ] **H3.1** Add Spring Security dependency
  - Add to pom.xml (all 3 services)
  - Dependency: spring-boot-starter-security
  - File: `pom.xml`
  - Effort: 1 hour

- [ ] **H3.2** Create JWT utility classes
  - Create: `JwtTokenProvider.java`
  - Create: `JwtAuthenticationFilter.java`
  - File: `security/jwt/*.java`
  - Effort: 3 hours

- [ ] **H3.3** Create SecurityConfig
  - Configure: JWT authentication
  - Configure: endpoint permissions
  - File: `config/SecurityConfig.java`
  - Effort: 2 hours

- [ ] **H3.4** Fix CORS configuration
  - Remove: `@CrossOrigin(origins = "*")`
  - Add: proper CORS configuration in SecurityConfig
  - File: `config/SecurityConfig.java`
  - Effort: 1 hour

- [ ] **H3.5** Add rate limiting for Java services
  - Add: spring-boot-starter-data-redis-rate-limit
  - Configure: rate limits per endpoint
  - File: `config/RateLimitConfig.java`
  - Effort: 2 hours

- [ ] **H3.6** Implement authentication in Node.js service
  - Add: JWT middleware
  - Add: passport.js or custom auth
  - File: `src/adapters/in/http/middleware/auth.js`
  - Effort: 3 hours

---

### H4. Database Migrations (4 tasks)

- [ ] **H4.1** Add Mongock dependency
  - Add to pom.xml (all 3 services)
  - Dependency: mongock-springboot
  - File: `pom.xml`
  - Effort: 1 hour

- [ ] **H4.2** Create initial changeunits for Analytics
  - Create: indexes for tenantId, dashboardId, timestamp
  - File: `db/changeunit/*.java`
  - Effort: 2 hours

- [ ] **H4.3** Create initial changeunits for Configuration
  - Create: indexes for tenantId, dashboardId
  - File: `db/changeunit/*.java`
  - Effort: 2 hours

- [ ] **H4.4** Create initial changeunits for Reporting
  - Create: indexes for tenantId, reportId
  - File: `db/changeunit/*.java`
  - Effort: 2 hours

---

## MEDIUM Priority Tasks (12 tasks)

### M1. Configuration Management (4 tasks)

- [ ] **M1.1** Create application-dev.yml
  - Override: MongoDB, Redis URLs for dev
  - File: `src/main/resources/application-dev.yml`
  - Effort: 1 hour

- [ ] **M1.2** Create application-prod.yml
  - Externalize: all configuration
  - Use: environment variables
  - File: `src/main/resources/application-prod.yml`
  - Effort: 1 hour

- [ ] **M1.3** Create application-test.yml
  - Configure: H2 in-memory or test containers
  - File: `src/main/resources/application-test.yml`
  - Effort: 1 hour

- [ ] **M1.4** Update Dockerfile to use profiles
  - Add: SPRING_PROFILES_ACTIVE environment variable
  - File: `Dockerfile`
  - Effort: 1 hour

---

### M2. Observability Enhancement (4 tasks)

- [ ] **M2.1** Add OpenTelemetry dependency
  - Add to pom.xml (all 3 services)
  - Dependency: opentelemetry-spring-boot-starter
  - File: `pom.xml`
  - Effort: 1 hour

- [ ] **M2.2** Configure distributed tracing
  - Add: correlation IDs
  - Export: traces to Jaeger/OTLP
  - File: `application.yml`
  - Effort: 2 hours

- [ ] **M2.3** Add custom metrics
  - Add: @Timed metrics
  - Add: business metrics (dashboard views, report generations)
  - File: `*.java` (all services)
  - Effort: 2 hours

- [ ] **M2.4** Configure structured logging
  - Add: logstash-logback-encoder
  - Format: JSON logs
  - File: `logback-spring.xml`
  - Effort: 2 hours

---

### M3. Performance Optimization (2 tasks)

- [ ] **M3.1** Implement pagination
  - Add: Pagination parameters to all list endpoints
  - Use: Spring Data Pageable
  - All services
  - Effort: 3 hours

- [ ] **M3.2** Implement caching with Redis
  - Add: @Cacheable to query methods
  - Add: @CacheEvict to update/delete methods
  - Configure: cache TTLs
  - File: `application/DashboardAnalyticsService.java` etc.
  - Effort: 3 hours

---

### M4. Frontend Docker (2 tasks)

- [ ] **M4.1** Create Dockerfile for Web
  - Multi-stage build: build → nginx
  - File: `/Frontend/Web/centralized-dashboard-web/Dockerfile`
  - Effort: 2 hours

- [ ] **M4.2** Create Dockerfile for Mobile
  - Multi-stage build for Expo
  - File: `/Frontend/Mobile/foundation-dashboard-mobile/Dockerfile`
  - Effort: 2 hours

---

## LOW Priority Tasks (4 tasks)

### L1. API Versioning (2 tasks)

- [ ] **L1.1** Add API versioning to Java services
  - Configure: URL path versioning (/api/v1/analytics)
  - File: `config/WebMvcConfig.java`
  - Effort: 2 hours

- [ ] **L1.2** Add API versioning to Node.js service
  - Update: route paths
  - File: `src/adapters/in/http/routes.js`
  - Effort: 1 hour

---

### L2. Additional Documentation (2 tasks)

- [ ] **L2.1** Create README.md for each service
  - Include: setup, run, test, deploy instructions
  - File: `README.md` (all services)
  - Effort: 2 hours

- [ ] **L2.2** Create architecture diagrams
  - Draw: component diagrams
  - Draw: sequence diagrams for key flows
  - File: `docs/architecture.md`
  - Effort: 3 hours

---

## Task Breakdown by Service

### Dashboard Analytics Service (Java)

| Severity | Tasks | Hours |
|----------|-------|-------|
| CRITICAL | 15 | 35-45 |
| HIGH | 10 | 20-25 |
| MEDIUM | 5 | 10-15 |
| LOW | 1 | 2-3 |
| **TOTAL** | **31** | **67-88** |

### Dashboard Configuration Service (Java)

| Severity | Tasks | Hours |
|----------|-------|-------|
| CRITICAL | 14 | 30-40 |
| HIGH | 8 | 15-20 |
| MEDIUM | 4 | 8-12 |
| LOW | 1 | 2-3 |
| **TOTAL** | **27** | **55-75** |

### Dashboard Reporting Service (Java)

| Severity | Tasks | Hours |
|----------|-------|-------|
| CRITICAL | 12 | 25-35 |
| HIGH | 6 | 12-16 |
| MEDIUM | 3 | 6-10 |
| LOW | 1 | 2-3 |
| **TOTAL** | **22** | **45-64** |

### Dashboard Aggregation Service (Node.js)

| Severity | Tasks | Hours |
|----------|-------|-------|
| CRITICAL | 7 | 15-20 |
| HIGH | 4 | 10-14 |
| MEDIUM | 0 | 0 |
| LOW | 0 | 0 |
| **TOTAL** | **11** | **25-34** |

### Frontend (Web + Mobile)

| Severity | Tasks | Hours |
|----------|-------|-------|
| CRITICAL | 0 | 0 |
| HIGH | 0 | 0 |
| MEDIUM | 2 | 4-6 |
| LOW | 1 | 2-3 |
| **TOTAL** | **3** | **6-9** |

---

## Execution Plan

### Sprint 1 (Week 1-2): Foundation
**Focus**: DTOs, Validation, Exception Handling
- Tasks: C1.1-C1.12 (DTOs)
- Tasks: C3.1-C3.8 (Exception Handling)
- Effort: 40-50 hours

### Sprint 2 (Week 3-4): Security & Transactions
**Focus**: Security, Transactions, Mappers
- Tasks: C2.1-C2.6 (Mappers)
- Tasks: C4.1-C4.4 (Transactions)
- Tasks: H3.1-H3.6 (Security)
- Effort: 40-50 hours

### Sprint 3 (Week 5-6): Testing & Documentation
**Focus**: Unit Tests, API Documentation
- Tasks: C5.1-C5.12 (Unit Tests)
- Tasks: C6.1-C6.6 (API Docs)
- Effort: 60-80 hours

### Sprint 4 (Week 7-8): CI/CD & Multi-tenancy
**Focus**: CI/CD, Multi-tenancy, Migrations
- Tasks: H1.1-H1.10 (CI/CD)
- Tasks: H2.1-H2.8 (Multi-tenancy)
- Tasks: H4.1-H4.4 (Migrations)
- Effort: 40-50 hours

### Sprint 5 (Week 9+): Polish
**Focus**: Performance, Observability, Frontend
- Tasks: M1.1-M4.2 (Medium priority)
- Tasks: L1.1-L2.2 (Low priority)
- Effort: 20-30 hours

---

## Dependencies

### Critical Path
```
DTOs → Mappers → Exception Handling → Validation → Controllers → Tests
                ↓
            Security → Transactions → Documentation
```

### Parallel Work Streams
1. **Backend Core**: DTOs, Mappers, Exception Handling, Validation
2. **Security**: Auth, CORS, Rate Limiting
3. **Quality**: Tests, Documentation, CI/CD
4. **Infrastructure**: Config, Migrations, Observability
5. **Frontend**: Docker, Environment Config

---

## Risk Mitigation

### High-Risk Tasks
1. **C5 - Testing**: Large effort, may underestimate
   - **Mitigation**: Start with happy path tests, add edge cases incrementally

2. **H3 - Security**: Complex, requires careful implementation
   - **Mitigation**: Use proven libraries, follow Spring Security best practices

3. **H2 - Multi-tenancy**: Requires refactoring all methods
   - **Mitigation**: Implement one service first, replicate pattern

### Time Management
- **Buffer**: Add 20% buffer to estimates
- **MVP**: Focus on CRITICAL tasks first
- **Parallel**: Work on independent tasks in parallel

---

## Success Criteria

### Phase 1 Completion Criteria
- [ ] All CRITICAL tasks completed
- [ ] Test coverage ≥ 60%
- [ ] All services have CI/CD pipeline
- [ ] Security implemented (JWT)

### Production Readiness Criteria
- [ ] Test coverage ≥ 85%
- [ ] All HIGH tasks completed
- [ ] API documentation complete
- [ ] Multi-tenancy automatic
- [ ] Database migrations in place
- [ ] Observability implemented

---

**End of Task List**

Total: **92 tasks** | **240-320 hours** | **6-8 weeks** (1 developer)
