# Central-Configuration Domain - Production Readiness Task List

**Generated**: January 11, 2026
**Domain**: Central-Configuration
**Services**: 8 microservices

---

## CRITICAL Priority Tasks

### DTO Layer - CRITICAL-001
**Task**: Refactor inline request/response records to dedicated DTO package
**Effort**: 3 days
**Files**:
- Create: `/Backend/Java/*/src/main/java/com/gogidix/rapidassist/*/adapters/in/web/dto/`
  - `request/CreateConfigurationRequest.java`
  - `request/UpdateConfigurationRequest.java`
  - `request/BulkUpdateRequest.java`
  - `response/ConfigurationResponse.java`
  - `response/ConfigurationListResponse.java`
  - `mapper/ConfigurationMapper.java` (MapStruct or manual)
- Update: All controllers to use new DTOs
- Services affected: All 8 services
**Acceptance Criteria**:
- [ ] DTOs in separate package structure
- [ ] Request/Response DTOs separated
- [ ] Mappers between domain models and DTOs
- [ ] Validation annotations preserved
- [ ] Controllers updated to use DTOs

### API Documentation - CRITICAL-002
**Task**: Add OpenAPI/Swagger documentation
**Effort**: 2 days
**Files**:
- Update: All 8 `pom.xml` files
  ```xml
  <dependency>
      <groupId>org.springdoc</groupId>
      <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
      <version>2.3.0</version>
  </dependency>
  ```
- Create: `OpenApiConfig.java` in each service
- Update: All controllers with annotations:
  - `@Tag(name = "...", description = "...")`
  - `@Operation(summary = "...", description = "...")`
  - `@ApiResponse(responseCode = "...", description = "...")`
**Services affected**: All 8 services
**Acceptance Criteria**:
- [ ] Springdoc dependency added to all pom.xml
- [ ] OpenAPI configuration class created
- [ ] All controllers annotated with @Tag
- [ ] All endpoints annotated with @Operation
- [ ] @ApiResponse for all response codes
- [ ] Swagger UI accessible at /swagger-ui.html
- [ ] OpenAPI spec available at /v3/api-docs

### Test Coverage - CRITICAL-003
**Task**: Implement comprehensive unit and integration tests
**Effort**: 10 days
**Files**:
- Create: Test files for each service
  - `/test/java/.../application/service/*ServiceTest.java`
  - `/test/java/.../adapters/in/web/*ControllerTest.java`
  - `/test/java/.../infrastructure/persistence/*RepositoryTest.java`
  - `/test/java/.../domain/model/*ModelTest.java`
- Update: All `pom.xml` files
  ```xml
  <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-test</artifactId>
      <scope>test</scope>
  </dependency>
  <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>mongodb</artifactId>
      <scope>test</scope>
  </dependency>
  <dependency>
      <groupId>org.testcontainers</groupId>
      <artifactId>junit-jupiter</artifactId>
      <scope>test</scope>
  </dependency>
  ```
- Add JaCoCo plugin for coverage:
  ```xml
  <plugin>
      <groupId>org.jacoco</groupId>
      <artifactId>jacoco-maven-plugin</artifactId>
  </plugin>
  ```
**Services affected**: All 8 services
**Target Coverage**: 85-95%
**Acceptance Criteria**:
- [ ] Unit tests for all service methods
- [ ] Integration tests with Testcontainers
- [ ] Repository tests with MongoDB
- [ ] Controller tests with MockMvc
- [ ] Validation tests
- [ ] Error handling tests
- [ ] JaCoCo coverage report generated
- [ ] Coverage ≥ 85% for all services

### Exception Handling - CRITICAL-004
**Task**: Implement global exception handler
**Effort**: 2 days
**Files**:
- Create: `/Backend/Java/*/src/main/java/com/gogidix/rapidassist/*/adapters/in/web/exception/`
  - `GlobalExceptionHandler.java`
  - `ErrorResponse.java`
  - `NotFoundException.java`
  - `ValidationException.java`
  - `ConflictException.java`
  - `BadRequestException.java`
- Update: All controllers to remove `.exceptionally()` blocks
**Services affected**: All 8 services
**Acceptance Criteria**:
- [ ] @ControllerAdvice class created
- [ ] Standardized ErrorResponse format
- [ ] Exception handlers for all exception types
- [ ] Proper HTTP status codes (404, 400, 409, 500)
- [ ] Error responses include correlation ID
- [ ] Controllers simplified (no exception handling)

### Security Configuration - CRITICAL-005
**Task**: Add SecurityConfig to all services
**Effort**: 3 days
**Files**:
- Create: `/Backend/Java/*/src/main/java/com/gogidix/rapidassist/*/infrastructure/security/SecurityConfig.java`
  - (missing in 7 services)
- Update: All `pom.xml` to add Spring Security dependencies
- Update: `application.yml` files for security properties
- Remove: Hardcoded credentials from config-service SecurityConfig
**Services affected**: 7 services (feature-flags, policy-configuration, dynamic-routing, rate-limit-policy, release-rollout, tenancy-configuration, country-localization)
**Acceptance Criteria**:
- [ ] SecurityConfig.java in all 8 services
- [ ] OAuth2 JWT configuration
- [ ] Role-based authorization
- [ ] Environment-based credentials (no hardcoding)
- [ ] Actuator security enabled
- [ ] Default passwords removed

### Rate Limiting - CRITICAL-006
**Task**: Implement API rate limiting
**Effort**: 3 days
**Files**:
- Create: `/Backend/Java/*/src/main/java/com/gogidix/rapidassist/*/infrastructure/security/RateLimitConfig.java`
- Update: All `pom.xml` files
  ```xml
  <dependency>
      <groupId>com.bucket4j</groupId>
      <artifactId>bucket4j-core</artifactId>
      <version>8.7.0</version>
  </dependency>
  <dependency>
      <groupId>com.bucket4j</groupId>
      <artifactId>bucket4j-jcache</artifactId>
      <version>8.7.0</version>
  </dependency>
  ```
- Update: All controllers with `@RateLimit` annotations
**Services affected**: All 8 services
**Acceptance Criteria**:
- [ ] Rate limit configuration
- [ ] Per-IP rate limiting
- [ ] Per-user rate limiting
- [ ] Redis-backed rate limiting
- [ ] Custom rate limit exceptions
- [ ] Rate limit headers in responses

### CI/CD Pipeline - CRITICAL-007
**Task**: Create GitHub Actions workflow
**Effort**: 3 days
**Files**:
- Create: `/.github/workflows/build.yml`
- Create: `/.github/workflows/test.yml`
- Create: `/.github/workflows/deploy-staging.yml`
- Create: `/.github/workflows/deploy-production.yml`
- Create: `/.github/workflows/security-scan.yml`
**Pipeline Stages**:
1. Build: Compile all services
2. Test: Run unit and integration tests
3. Quality: SonarQube analysis
4. Security: SAST (CodeQL), SCA (Dependabot)
5. Package: Build Docker images
6. Deploy: Deploy to Railway
**Acceptance Criteria**:
- [ ] Build workflow compiles all 8 services
- [ ] Test workflow runs all tests with coverage
- [ ] Quality workflow integrates SonarQube
- [ ] Security workflow runs CodeQL and SCA scans
- [ ] Deploy workflow pushes to Railway
- [ ] Workflow status badges in README
- [ ] Automated deployment on merge to main

### Distributed Tracing - CRITICAL-008
**Task**: Add OpenTelemetry distributed tracing
**Effort**: 2 days
**Files**:
- Update: All `pom.xml` files
  ```xml
  <dependency>
      <groupId>io.opentelemetry</groupId>
      <artifactId>opentelemetry-api</artifactId>
  </dependency>
  <dependency>
      <groupId>io.opentelemetry</groupId>
      <artifactId>opentelemetry-instrumentation-spring-boot-starter</artifactId>
  </dependency>
  ```
- Update: All `application.yml` files with OTel configuration
**Services affected**: All 8 services
**Acceptance Criteria**:
- [ ] OpenTelemetry dependencies added
- [ ] Automatic tracing configured
- [ ] Trace export to Jaeger/Zipkin/OTel Collector
- [ ] Correlation IDs propagated
- [ ] Spans for service calls
- [ ] Spans for database queries
- [ ] Spans for Redis operations

### Database Migrations - CRITICAL-009
**Task**: Implement MongoDB migrations with Mongock
**Effort**: 3 days
**Files**:
- Update: All `pom.xml` files
  ```xml
  <dependency>
      <groupId>io.mongock</groupId>
      <artifactId>mongock-springboot-v3</artifactId>
      <version>5.4.0</version>
  </dependency>
  ```
- Create: `/Backend/Java/*/src/main/java/com/gogidix/rapidassist/*/migration/`
  - `V001__initial_setup.java`
  - `V002__create_indexes.java`
- Update: All `application.yml` with Mongock configuration
**Services affected**: All 8 services
**Acceptance Criteria**:
- [ ] Mongock dependency added
- [ ] Initial migration changeunits created
- [ ] Index creation migrations
- [ ] Migration history tracking
- [ ] Rollback capability
- [ ] Migration lock handling

### CORS Security - CRITICAL-010
**Task**: Fix overly permissive CORS configuration
**Effort**: 1 day
**Files**:
- Update: All controllers to remove `@CrossOrigin(origins = "*")`
- Update: All `SecurityConfig.java` to configure allowed origins
- Create: `CorsConfig.java` in each service
**Services affected**: All 8 services
**Acceptance Criteria**:
- [ ] Remove `@CrossOrigin(origins = "*")` from all controllers
- [ ] Configure allowed origins from environment variable
- [ ] Configure allowed methods
- [ ] Configure allowed headers
- [ ] Configure credentials support
- [ ] Preflight requests handled

---

## HIGH Priority Tasks

### DTO Mappers - HIGH-001
**Task**: Implement MapStruct mappers between domain models and DTOs
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] MapStruct dependency added
- [ ] Mapper interfaces created
- [ ] Bidirectional mapping
- [ ] Collection mapping
- [ ] Null value handling

### Transaction Management - HIGH-002
**Task**: Add @Transactional to all service classes
**Effort**: 1 day
**Files**: All `application/*Service.java` classes
**Acceptance Criteria**:
- [ ] @Transactional on all service classes
- [ ] Transaction manager configured
- [ ] Rollback rules defined
- [ ] Isolation levels specified

### API Versioning - HIGH-003
**Task**: Implement consistent API versioning strategy
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Version-specific controllers (/api/v1/, /api/v2/)
- [ ] Content negotiation support
- [ ] Deprecation headers
- [ ] Version transition documentation

### Test Coverage Measurement - HIGH-004
**Task**: Set up JaCoCo for coverage measurement
**Effort**: 1 day
**Acceptance Criteria**:
- [ ] JaCoCo plugin configured
- [ ] Coverage reports generated
- [ ] Minimum coverage thresholds enforced
- [ ] Coverage reports published

### Integration Tests - HIGH-005
**Task**: Create integration tests with Testcontainers
**Effort**: 4 days
**Acceptance Criteria**:
- [ ] MongoDB Testcontainers
- [ ] Redis Testcontainers
- [ ] Integration test suite
- [ ] Test data fixtures
- [ ] Test isolation

### Mock Tests - HIGH-006
**Task**: Create mock-based unit tests
**Effort**: 3 days
**Acceptance Criteria**:
- [ ] Mockito for service dependencies
- [ ] MockMvc for controller tests
- [ ] Mock repositories
- [ ] Mock external services

### Tenant Context Propagation - HIGH-007
**Task**: Implement tenant context propagation
**Effort**: 2 days
**Files**:
- Create: `TenantContext.java` (ThreadLocal)
- Create: `TenantInterceptor.java`
- Update: All controllers to extract tenant from JWT
**Acceptance Criteria**:
- [ ] Tenant context holder created
- [ ] Tenant extracted from JWT claims
- [ ] Context propagated to service layer
- [ ] Context cleared after request

### Tenant Isolation Tests - HIGH-008
**Task**: Verify tenant data isolation
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Tests for cross-tenant query prevention
- [ ] Tests for tenant ID validation
- [ ] Tests for tenant-based filtering
- [ ] Security tests for tenant bypass

### Spring Cloud Config - HIGH-009
**Task**: Integrate Spring Cloud Config
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Config server integration
- [ ] Configuration refresh
- [ ] Encryption support
- [ ] Environment-specific configs

### Docker Compose - HIGH-010
**Task**: Create docker-compose.yml for local development
**Effort**: 1 day
**Files**:
- Create: `/docker-compose.yml`
- Create: `/docker-compose.dev.yml`
- Create: `/docker-compose.test.yml`
**Acceptance Criteria**:
- [ ] All 8 services defined
- [ ] MongoDB included
- [ ] Redis included
- [ ] Networking configured
- [ ] Environment variables set
- [ ] Health checks enabled

### Container Resource Limits - HIGH-011
**Task**: Add resource limits to Dockerfiles
**Effort**: 1 day
**Files**: All Dockerfiles
**Acceptance Criteria**:
- [ ] Memory limits specified
- [ ] CPU limits specified
- [ ] JVM heap configured
- [ ] Container tuning applied

### Docker Port Fix - HIGH-012
**Task**: Fix Dockerfile port mismatch (8000 vs 8080)
**Effort**: 0.5 day
**Files**: All Dockerfiles
**Acceptance Criteria**:
- [ ] EXPOSE matches application.yml
- [ ] HEALTHCHECK uses correct port
- [ ] Documentation updated

### Security Scanning in CI - HIGH-013
**Task**: Add security scanning to GitHub Actions
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] CodeQL scanning
- [ ] Dependabot alerts
- [ ] SCA scanning
- [ ] Container scanning (Trivy)
- [ ] Secret scanning

### Code Quality Gates - HIGH-014
**Task**: Set up SonarQube quality gates
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] SonarQube server configured
- [ ] Quality gate criteria defined
- [ ] Build fails on gate failure
- [ ] Code coverage measured
- [ ] Code smells detected
- [ ] Security hotspots detected

### Environment Documentation - HIGH-015
**Task**: Document Railway environment variables
**Effort**: 1 day
**Files**:
- Create: `/deployment/railway/env-vars.md`
- Update: All `railway.json` files
**Acceptance Criteria**:
- [ ] All environment variables documented
- [ ] Default values specified
- [ ] Sensitive variables marked
- [ ] Configuration examples provided

### JWT Validation Enhancement - HIGH-016
**Task**: Implement comprehensive JWT validation
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Token expiration checked
- [ ] Token signature verified
- [ ] Claims validated
- [ ] Audience verification
- [ ] Issuer verification
- [ ] Refresh token support

### Correlation IDs - HIGH-017
**Task**: Add correlation ID tracking
**Effort**: 1 day
**Acceptance Criteria**:
- [ ] Correlation ID generated
- [ ] Correlation ID in headers
- [ ] Correlation ID in logs
- [ ] Correlation ID propagated to services

### Custom Metrics - HIGH-018
**Task**: Implement custom business metrics
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Metrics with Micrometer
- [ ] Business operation metrics
- [ ] Custom timers
- [ ] Custom counters
- [ ] Custom gauges
- [ ] Prometheus scrape endpoint

### Database Backup - HIGH-019
**Task**: Implement MongoDB backup strategy
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Automated backup schedule
- [ ] Backup retention policy
- [ ] Restore procedures
- [ ] Backup monitoring
- [ ] Disaster recovery plan

### Connection Pool Configuration - HIGH-020
**Task**: Configure MongoDB connection pool
**Effort**: 1 day
**Files**: All `application.yml`
**Acceptance Criteria**:
- [ ] Max pool size configured
- [ ] Min pool size configured
- [ ] Connection timeout set
- [ ] Pool metrics enabled

---

## MEDIUM Priority Tasks

### DTO Versioning - MEDIUM-001
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Versioned DTOs (v1, v2)
- [ ] Backward compatibility
- [ ] Deprecation strategy

### Transaction Rollback Rules - MEDIUM-002
**Effort**: 1 day
**Acceptance Criteria**:
- [ ] Rollback for specific exceptions
- [ ] Rollback for custom exceptions
- [ ] Transaction boundaries defined

### API Documentation Examples - MEDIUM-003
**Effort**: 1 day
**Acceptance Criteria**:
- [ ] @Operation with examples
- [ ] @Schema with examples
- [ ] Request/response examples
- [ ] Example values in annotations

### Performance Tests - MEDIUM-004
**Effort**: 3 days
**Acceptance Criteria**:
- [ ] JMeter/Gatling tests
- [ ] Load testing scenarios
- [ ] Stress tests
- [ ] Performance benchmarks

### Contract Testing - MEDIUM-005
**Effort**: 3 days
**Acceptance Criteria**:
- [ ] Pact contract tests
- [ ] Consumer contracts
- [ ] Provider verification
- [ ] Contract publishing

### Profile-Specific Configs - MEDIUM-006
**Effort**: 1 day
**Acceptance Criteria**:
- [ ] application-dev.yml
- [ ] application-staging.yml
- [ ] application-prod.yml
- [ ] Profile activation

### Configuration Encryption - MEDIUM-007
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Jasypt integration
- [ ] Encrypted values
- [ ] Key management
- [ ] Password encryption

### Docker Security Scanning - MEDIUM-008
**Effort**: 1 day
**Acceptance Criteria**:
- [ ] Trivy scanning
- [ ] Snyk scanning
- [ ] Vulnerability reports
- [ ] CI integration

### Railway Orchestration - MEDIUM-009
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Multi-service deployment
- [ ] Service dependencies
- [ ] Health check orchestration
- [ ] Rolling updates

### Health Check Customization - MEDIUM-010
**Effort**: 1 day
**Acceptance Criteria**:
- [ ] Custom health indicators
- [ ] Database health checks
- [ ] Redis health checks
- [ ] Dependency health checks

### Security Audit Logging - MEDIUM-011
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Authentication events logged
- [ ] Authorization failures logged
- [ ] Admin actions logged
- [ ] Audit log retention

### Centralized Logging - MEDIUM-012
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] ELK stack integration
- [ ] Loki integration
- [ ] Log aggregation
- [ ] Log search capability

### Metrics Dashboards - MEDIUM-013
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Grafana dashboards
- [ ] Service metrics
- [ ] Business metrics
- [ ] Alert thresholds

### Query Performance Monitoring - MEDIUM-014
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Slow query logging
- [ ] Query metrics
- [ ] Index usage monitoring
- [ ] N+1 query detection

### Data Consistency Checks - MEDIUM-015
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Consistency validators
- [ ] Scheduled checks
- [ ] Alerting on violations
- [ ] Repair procedures

---

## LOW Priority Tasks

### Async Port Interfaces - LOW-001
**Effort**: 3 days
**Acceptance Criteria**:
- [ ] Reactive port interfaces
- [ ] Non-blocking operations
- [ ] Backpressure handling

### Business Validation Layer - LOW-002
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Separate validation layer
- [ ] Business rule validators
- [ ] Validation framework

### HAL/HATEOAS Links - LOW-003
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Hypermedia links
- [ ] Resource linking
- [ ] Navigation support

### Tenant Metadata Integration - LOW-004
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] Tenant registry integration
- [ ] Tenant metadata cache
- [ ] Tenant lookup service

### Configuration Documentation - LOW-005
**Effort**: 1 day
**Acceptance Criteria**:
- [ ] All properties documented
- [ ] Default values listed
- [ ] Examples provided
- [ ] Schema definitions

### Container Security Scanning - LOW-006
**Effort**: 1 day
**Acceptance Criteria**:
- [ ] Trivy in CI
- [ ] Snyk in CI
- [ ] Vulnerability thresholds
- [ ] Scan reports

### Log Retention Policies - LOW-007
**Effort**: 1 day
**Acceptance Criteria**:
- [ ] Retention periods defined
- [ ] Log rotation configured
- [ ] Archive policies
- [ ] Cleanup jobs

### Database Documentation - LOW-008
**Effort**: 2 days
**Acceptance Criteria**:
- [ ] ER diagrams
- [ ] Collection schemas
- [ ] Index documentation
- [ ] Relationship diagrams

---

## Task Statistics

- **Total Tasks**: 65
- **CRITICAL**: 10 tasks (~35 days)
- **HIGH**: 20 tasks (~45 days)
- **MEDIUM**: 15 tasks (~32 days)
- **LOW**: 8 tasks (~14 days)

**Total Estimated Effort**: 126 days (approximately 6 months with 1 developer, or 2-3 months with a small team)

---

## Recommended Implementation Order

### Phase 1 (Weeks 1-4): Foundation
1. CRITICAL-003: Test Coverage
2. CRITICAL-004: Exception Handling
3. CRITICAL-005: Security Configuration
4. CRITICAL-007: CI/CD Pipeline

### Phase 2 (Weeks 5-8): Production Readiness
5. CRITICAL-002: API Documentation
6. CRITICAL-001: DTO Layer
7. CRITICAL-006: Rate Limiting
8. CRITICAL-010: CORS Security

### Phase 3 (Weeks 9-12): Observability & Reliability
9. CRITICAL-008: Distributed Tracing
10. CRITICAL-009: Database Migrations
11. HIGH-017: Correlation IDs
12. HIGH-018: Custom Metrics

### Phase 4 (Weeks 13+): Optimization
13. Remaining HIGH priority tasks
14. MEDIUM priority tasks
15. LOW priority tasks

---

**Task List Generated**: January 11, 2026
**Last Updated**: January 11, 2026
