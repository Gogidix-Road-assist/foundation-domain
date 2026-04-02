# Complete Hexagonal SaaS Multi-Tenant Service Structure
## Foundation Domain - Mandatory Template

**Purpose:** This is the MANDATORY template for ALL Java services in the Foundation Domain.

**Usage:** Copy this structure when creating new services or refactoring existing ones.

**Compliance:** ALL services MUST follow this structure for 95-98% production readiness.

---

## Full Folder Structure

```
service-name/
├── pom.xml
├── Dockerfile
├── railway.json
├── .github/
│   └── workflows/
│       ├── build.yml
│       ├── test.yml
│       └── deploy.yml
├── src/
│   ├── main/
│   │   ├── java/com/gogidix/rapidassist/
│   │   │   └── {service-name}/
│   │   │       ├── {ServiceName}Application.java
│   │   │       │
│   │   │       ├── domain/
│   │   │       │   ├── model/
│   │   │       │   │   ├── {Entity}.java           # Domain entity (ZERO framework deps)
│   │   │       │   │   ├── ValueObject.java         # Value objects
│   │   │       │   │   └── AggregateRoot.java      # DDD Aggregate
│   │   │       │   │
│   │   │       │   ├── aggregate/
│   │   │       │   │   └── {Aggregate}Aggregate.java
│   │   │       │   │
│   │   │       │   ├── event/
│   │   │       │   │   ├── DomainEvent.java
│   │   │       │   │   └── {SpecificEvent}.java
│   │   │       │   │
│   │   │       │   ├── policy/
│   │   │       │   │   └── {BusinessRule}Policy.java
│   │   │       │   │
│   │   │       │   ├── repository/
│   │   │       │   │   └── {Entity}Repository.java     # Interface ONLY
│   │   │       │   │
│   │   │       │   └── port/
│   │   │       │       ├── in/
│   │   │       │       │   ├── {Entity}Command.java     # Input port (commands)
│   │   │       │       │   ├── {Entity}Query.java       # Input port (queries)
│   │   │       │       │   └── UseCase.java            # Use case interface
│   │   │       │       │
│   │   │       │       └── out/
│   │   │       │           ├── {Entity}Repository.java  # Output port
│   │   │       │           ├── {Entity}Store.java
│   │   │       │           └── {External}Adapter.java    # External service port
│   │   │       │
│   │   │       ├── application/
│   │   │       │   ├── command/
│   │   │       │   │   ├── Create{Entity}Command.java
│   │   │       │   │   ├── Update{Entity}Command.java
│   │   │       │   │   └── Delete{Entity}Command.java
│   │   │       │   │
│   │   │       │   ├── query/
│   │   │       │   │   ├── Get{Entity}Query.java
│   │   │       │   │   ├── Search{Entity}Query.java
│   │   │       │   │   └── List{Entity}Query.java
│   │   │       │   │
│   │   │       │   ├── service/
│   │   │       │   │   ├── {Entity}CommandService.java   # Command handler
│   │   │       │   │   ├── {Entity}QueryService.java     # Query handler
│   │   │       │   │   └── {UseCase}Service.java         # Use case impl
│   │   │       │   │
│   │   │       │   ├── dto/
│   │   │       │   │   ├── request/
│   │   │       │   │   │   ├── Create{Entity}RequestDto.java
│   │   │       │   │   │   ├── Update{Entity}RequestDto.java
│   │   │       │   │   │   └── Search{Entity}RequestDto.java
│   │   │       │   │   │
│   │   │       │   │   └── response/
│   │   │       │   │       ├── {Entity}ResponseDto.java
│   │   │       │   │       ├── PagedResponseDto.java
│   │   │       │   │       └── ErrorResponseDto.java
│   │   │       │   │
│   │   │       │   └── mapper/
│   │   │       │       ├── {Entity}Mapper.java           # MapStruct
│   │   │       │       └── DTOConverter.java
│   │   │       │
│   │   │       ├── infrastructure/
│   │   │       │   ├── persistence/
│   │   │       │   │   ├── mongo/
│   │   │       │   │   │   ├── Mongo{Entity}Repository.java    # Impl
│   │   │       │   │   │   ├── {Entity}Document.java           # MongoDB doc
│   │   │       │   │   │   └── {Entity}DocumentConverter.java
│   │   │       │   │   │
│   │   │       │   │   └── redis/
│   │   │       │   │       ├── Redis{Entity}Store.java
│   │   │       │   │       └── {Entity}CacheConfig.java
│   │   │       │   │
│   │   │       │   ├── messaging/
│   │   │       │   │   ├── kafka/
│   │   │       │   │   │   ├── Kafka{Event}Publisher.java
│   │   │       │   │   │   ├── Kafka{Event}Consumer.java
│   │   │       │   │   │   └── KafkaConfig.java
│   │   │       │   │   │
│   │   │       │   │   └── events/
│   │   │       │   │       └── {Event}Publisher.java
│   │   │       │   │
│   │   │       │   ├── security/
│   │   │       │   │   ├── TenantInterceptor.java        # Tenant extraction
│   │   │       │   │   ├── SecurityConfig.java
│   │   │       │   │   └── JwtAuthenticationFilter.java
│   │   │       │   │
│   │   │       │   ├── adapter/
│   │   │       │   │   ├── rest/
│   │   │       │   │   │   ├── {External}ApiClient.java
│   │   │       │   │   │   └── {External}Mapper.java
│   │   │       │   │   │
│   │   │       │   │   └── storage/
│   │   │       │   │       ├── S3StorageAdapter.java
│   │   │       │   │       └── FileStorageAdapter.java
│   │   │       │   │
│   │   │       │   └── config/
│   │   │       │       ├── ApplicationConfig.java
│   │   │       │       ├── MongoDBConfig.java
│   │   │       │       ├── RedisConfig.java
│   │   │       │       └── KafkaConfig.java
│   │   │       │
│   │   │       ├── interfaces/
│   │   │       │   ├── rest/
│   │   │       │   │   ├── {Entity}Controller.java         # REST controller
│   │   │       │   │   ├── HealthController.java
│   │   │       │   │   └── GlobalExceptionHandler.java    # @ControllerAdvice
│   │   │       │   │
│   │   │       │   └── dto/
│   │   │       │       └── ApiErrorResponse.java
│   │   │       │
│   │   │       └── shared/
│   │   │           ├── requestcontext/
│   │   │           │   ├── RequestContext.java            # Tenant, User, CorrelationId
│   │   │           │   ├── RequestContextHolder.java       # ThreadLocal holder
│   │   │           │   └── RequestContextFilter.java       # HTTP filter
│   │   │           │
│   │   │           ├── exception/
│   │   │           │   ├── {Domain}Exception.java
│   │   │           │   ├── NotFoundException.java
│   │   │           │   ├── ValidationException.java
│   │   │           │   └── ConflictException.java
│   │   │           │
│   │   │           └── util/
│   │   │               ├── TenantIdGenerator.java
│   │   │               └── CorrelationIdGenerator.java
│   │   │
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       ├── application-prod.yml
│   │       ├── logback-spring.xml
│   │       └── META-INF/
│   │           └── openapi/
│   │               └── openapi.yaml
│   │
│   └── test/
│       ├── java/com/gogidix/rapidassist/{service-name}/
│       │   ├── unit/
│       │   │   ├── domain/
│       │   │   │   ├── {Entity}Test.java
│       │   │   │   └── {Policy}PolicyTest.java
│       │   │   │
│       │   │   ├── application/
│       │   │   │   ├── {Entity}CommandServiceTest.java
│       │   │   │   └── {Entity}QueryServiceTest.java
│       │   │   │
│       │   │   ├── infrastructure/
│       │   │   │   ├── Mongo{Entity}RepositoryTest.java
│       │   │   │   └── Redis{Entity}StoreTest.java
│       │   │   │
│       │   │   └── interfaces/
│       │   │       └── {Entity}ControllerTest.java
│       │   │
│       │   ├── integration/
│       │   │   ├── {Entity}IntegrationTest.java      # Full flow tests
│       │   │   └── TenantIsolationTest.java         # Multi-tenant tests
│       │   │
│       │   └── architecture/
│       │       └── HexArchitectureTest.java        # ArchUnit test
│       │
│       └── resources/
│           ├── application-test.yml
│           ├── logback-test.xml
│           └── fixtures/
│               └── {entity}-test-data.json
│
├── README.md
└── docs/
    ├── api-specification.md
    └── runbook.md
```

---

## Multi-Tenant SaaS Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           MULTI-TENANT SAAS ARCHITECTURE                        │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                  │
│  ┌───────────────────────────────────────────────────────────────────────────┐ │
│  │                           API GATEWAY / EDGE LAYER                        │ │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐     │ │
│  │  │   CORS      │  │   RATE      │  │   SECURITY  │  │   ROUTING   │     │ │
│  │  │   FILTER    │  │   LIMIT     │  │   FILTER    │  │   /api/v1   │     │ │
│  │  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘     │ │
│  └─────────┼───────────────┼───────────────┼───────────────┼───────────────    │ │
│            │               │               │               │                   │ │
│            ▼               │               │               │                   │ │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │ │
│  │                     TENANT CONTEXT INTERCEPTOR                         │ │ │
│  │  ┌───────────────────────────────────────────────────────────────────┐ │ │ │
│  │  │  1. Extract tenant_id from JWT token                              │ │ │ │
│  │  │  2. Validate tenant exists & is active                             │ │ │ │
│  │  │  3. Set RequestContext (tenantId, userId, correlationId)          │ │ │ │
│  │  │  4. Propagate context to ALL downstream calls                     │ │ │ │
│  │  └───────────────────────────────────────────────────────────────────┘ │ │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │ │
│            │                                                                 │ │
│            ▼                                                                 │ │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │ │
│  │                        CONTROLLER LAYER (interfaces/rest)              │ │ │
│  │  ┌───────────────────────────────────────────────────────────────────┐ │ │ │
│  │  │  @GetMapping("/api/v1/{tenantId}/entities")                     │ │ │ │
│  │  │  public ResponseEntity<List<EntityResponseDto>> getEntities(      │ │ │ │
│  │  │      @PathVariable String tenantId,                                │ │ │ │
│  │  │      @RequestHeader String correlationId                           │ │ │ │
│  │  │  ) {                                                              │ │ │ │
│  │  │      // Tenant validation already done by interceptor             │ │ │ │
│  │  │      // RequestContext already has tenantId                       │ │ │ │
│  │  │      String tenantId = RequestContext.get().tenantId();           │ │ │ │
│  │  │      ...                                                          │ │ │ │
│  │  │  }                                                              │ │ │ │
│  │  └───────────────────────────────────────────────────────────────────┘ │ │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │ │
│            │                                                                 │ │
│            ▼                                                                 │ │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │ │
│  │                    APPLICATION LAYER (application/service)             │ │ │
│  │  ┌───────────────────────────────────────────────────────────────────┐ │ │ │
│  │  │  @Service                                                         │ │ │ │
│  │  │  @Transactional                                                   │ │ │ │
│  │  │  public class EntityCommandService implements EntityCommand {     │ │ │ │
│  │  │                                                                  │ │ │ │
│  │  │      private final EntityRepository repository;                  │ │ │ │
│  │  │      private final RequestContext context;                       │ │ │ │
│  │  │                                                                  │ │ │ │
│  │  │      public void handle(CreateEntityCommand cmd) {               │ │ │ │
│  │  │          // Tenant from context                                   │ │ │ │
│  │  │          String tenantId = context.tenantId();                    │ │ │ │
│  │  │          Entity entity = new Entity(tenantId, cmd...);           │ │ │ │
│  │  │          repository.save(entity);                                 │ │ │ │
│  │  │          publishEvent(new EntityCreatedEvent(tenantId, ...));    │ │ │ │
│  │  │      }                                                           │ │ │ │
│  │  │  }                                                              │ │ │ │
│  │  └───────────────────────────────────────────────────────────────────┘ │ │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │ │
│            │                                                                 │ │
│            ▼                                                                 │ │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │ │
│  │                      DOMAIN LAYER (domain/model)                       │ │ │
│  │  ┌───────────────────────────────────────────────────────────────────┐ │ │ │
│  │  │  @Document(collection = "entities")                               │ │ │ │
│  │  │  @TypeAlias("entity")                                             │ │ │ │
│  │  │  public class Entity {                                           │ │ │ │
│  │  │                                                                  │ │ │ │
│  │  │      @Id private String id;                                       │ │ │ │
│  │  │                                                                  │ │ │ │
│  │  │      @Indexed                                                    │ │ │ │
│  │  │      @Field("tenant_id")                                          │ │ │ │
│  │  │      private String tenantId;  // MANDATORY FOR MULTI-TENANCY    │ │ │ │
│  │  │                                                                  │ │ │ │
│  │  │      // ... other fields                                         │ │ │ │
│  │  │                                                                  │ │ │ │
│  │  │      // Domain logic                                             │ │ │ │
│  │  │      public void update(...) {                                    │ │ │ │
│  │  │          // Business rules here                                  │ │ │ │
│  │  │      }                                                           │ │ │ │
│  │  │  }                                                              │ │ │ │
│  │  └───────────────────────────────────────────────────────────────────┘ │ │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │ │
│            │                                                                 │ │
│            ▼                                                                 │ │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │ │
│  │                  INFRASTRUCTURE LAYER (infrastructure/persistence)     │ │ │
│  │  ┌───────────────────────────────────────────────────────────────────┐ │ │ │
│  │  │  @Repository                                                     │ │ │ │
│  │  │  public interface EntityRepository extends MongoRepository<Entity,│ │ │ │
│  │  │      String> {                                                     │ │ │ │
│  │  │                                                                  │ │ │ │
│  │  │      // All queries MUST filter by tenant                         │ │ │ │
│  │  │      List<Entity> findByTenantId(String tenantId);               │ │ │ │
│  │  │                                                                  │ │ │ │
│  │  │      Optional<Entity> findByIdAndTenantId(String id,              │ │ │ │
│  │  │          String tenantId);                                       │ │ │ │
│  │  │  }                                                              │ │ │ │
│  │  └───────────────────────────────────────────────────────────────────┘ │ │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │ │
│            │                                                                 │ │
│            ▼                                                                 │ │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │ │
│  │                      DATABASE LAYER (Tenant Isolation)                  │ │ │
│  │                                                                          │ │ │
│  │  ┌───────────────────────────────────────────────────────────────────┐ │ │ │
│  │  │  MONGODB: entities collection                                      │ │ │ │
│  │  │  ┌─────────────────────────────────────────────────────────────┐   │ │ │
│  │  │  │  { "_id": "...", "tenant_id": "tenant-1", ... }          │   │ │ │
│  │  │  │  { "_id": "...", "tenant_id": "tenant-1", ... }          │   │ │ │
│  │  │  │  { "_id": "...", "tenant_id": "tenant-2", ... }          │   │ │ │
│  │  │  │  { "_id": "...", "tenant_id": "tenant-2", ... }          │   │ │ │
│  │  │  │                                                            │   │ │ │
│  │  │  │  Index: { "tenant_id": 1, "_id": 1 }  <-- COMPOUND      │   │ │ │
│  │  │  └─────────────────────────────────────────────────────────────┘   │ │ │
│  │  │                                                                    │   │ │ │
│  │  │  REDIS: tenant:{tenantId}:entity:{entityId}  <-- Cache key       │   │ │ │
│  │  │                                                                    │   │ │ │
│  │  │  KAFKA: events carry tenant_id                                   │   │ │ │
│  │  │  { "tenantId": "tenant-1", "eventType": "...", ... }            │   │ │ │
│  │  └───────────────────────────────────────────────────────────────────┘   │ │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │ │
│                                                                                  │
│  ┌─────────────────────────────────────────────────────────────────────────┐ │
│  │                         SHARED COMPONENTS                                │ │
│  │  ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐      │ │
│  │  │ RequestContext   │  │  TenantService   │  │   AuditLog       │      │ │
│  │  │  - tenantId      │  │  - validate      │  │   - tenantId     │      │ │
│  │  │  - userId        │  │  - onboarding    │  │   - userId        │      │ │
│  │  │  - correlationId │  │  - configuration │  │   - action        │      │ │
│  │  └──────────────────┘  └──────────────────┘  └──────────────────┘      │ │
│  └─────────────────────────────────────────────────────────────────────────┘ │ │
│                                                                                  │
└─────────────────────────────────────────────────────────────────────────────────┘
```

---

## 1. RequestContext.java (Shared Component)

```java
package com.gogidix.rapidassist.shared.requestcontext;

/**
 * ThreadLocal holder for tenant context
 * MUST be populated by TenantInterceptor BEFORE any service logic
 */
public final class RequestContext {

    private final String tenantId;
    private final String userId;
    private final String correlationId;
    private final Map<String, Object> metadata;

    private RequestContext(Builder builder) {
        this.tenantId = requireNonNull(builder.tenantId, "tenantId is required");
        this.userId = builder.userId;
        this.correlationId = builder.correlationId;
        this.metadata = builder.metadata != null
            ? Map.copyOf(builder.metadata)
            : Map.of();
    }

    public String tenantId() { return tenantId; }
    public String userId() { return userId; }
    public String correlationId() { return correlationId; }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(RequestContext existing) {
        return new Builder()
            .tenantId(existing.tenantId)
            .userId(existing.userId)
            .correlationId(existing.correlationId)
            .metadata(existing.metadata);
    }

    public static class Builder {
        private String tenantId;
        private String userId;
        private String correlationId;
        private Map<String, Object> metadata;

        public Builder tenantId(String tenantId) {
            this.tenantId = tenantId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder correlationId(String correlationId) {
            this.correlationId = correlationId;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public RequestContext build() {
            return new RequestContext(this);
        }
    }
}
```

---

## 2. RequestContextHolder.java (Shared Component)

```java
package com.gogidix.rapidassist.shared.requestcontext;

/**
 * ThreadLocal holder for RequestContext
 * Context is set by TenantInterceptor and cleared after request completes
 */
public final class RequestContextHolder {

    private static final ThreadLocal<RequestContext> CONTEXT = new ThreadLocal<>();

    private RequestContextHolder() {}

    /**
     * Set the context for the current thread
     * Called by TenantInterceptor
     */
    public static void set(RequestContext context) {
        CONTEXT.set(context);
    }

    /**
     * Get the context for the current thread
     * Returns Optional.empty() if not set (should NOT happen in normal flow)
     */
    public static Optional<RequestContext> get() {
        return Optional.ofNullable(CONTEXT.get());
    }

    /**
     * Get the context or throw if not set
     * Use this when context is required
     */
    public static RequestContext require() {
        return get().orElseThrow(() ->
            new IllegalStateException("RequestContext not set. TenantInterceptor must run first."));
    }

    /**
     * Clear the context for the current thread
     * Called after request completes
     */
    public static void clear() {
        CONTEXT.remove();
    }

    /**
     * Get tenantId from current context
     * Convenience method
     */
    public static String getTenantId() {
        return require().tenantId();
    }

    /**
     * Get userId from current context
     * Convenience method
     */
    public static Optional<String> getUserId() {
        return get().map(RequestContext::userId);
    }

    /**
     * Get correlationId from current context
     * Convenience method
     */
    public static String getCorrelationId() {
        return require().correlationId();
    }
}
```

---

## 3. TenantInterceptor.java (Infrastructure/Security)

```java
package com.gogidix.rapidassist.{service}.infrastructure.security;

/**
 * INTERCEPTS ALL REQUESTS and extracts tenant context from JWT
 * This is the ENTRY POINT for multi-tenancy
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantInterceptor implements HandlerInterceptor {

    private final JwtTokenValidator jwtValidator;
    private final TenantService tenantService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) {

        // 1. Extract JWT from Authorization header
        String token = extractJwt(request);
        if (token == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing JWT token");
        }

        // 2. Validate JWT and extract claims
        JwtClaims claims = jwtValidator.validate(token);

        // 3. Extract tenantId from claims
        String tenantId = claims.getClaim("tenant_id");
        if (tenantId == null || tenantId.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing tenant_id in JWT");
        }

        // 4. Validate tenant exists and is active
        if (!tenantService.isActive(tenantId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Tenant is not active");
        }

        // 5. Extract userId and correlationId
        String userId = claims.getSubject();
        String correlationId = request.getHeader("X-Correlation-ID");
        if (correlationId == null) {
            correlationId = UUID.randomUUID().toString();
        }

        // 6. Build and set RequestContext
        RequestContext context = RequestContext.builder()
            .tenantId(tenantId)
            .userId(userId)
            .correlationId(correlationId)
            .build();

        RequestContextHolder.set(context);

        // 7. Add correlationId to response for tracing
        response.setHeader("X-Correlation-ID", correlationId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler,
                               Exception ex) {
        // ALWAYS clear context to prevent memory leaks
        RequestContextHolder.clear();
    }

    private String extractJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

---

## 4. MongoEntityRepository.java (Tenant Isolation)

```java
package com.gogidix.rapidassist.{service}.infrastructure.persistence.mongo;

/**
 * Repository implementation with MANDATORY tenant filtering
 * ALL queries MUST filter by tenantId
 */
@Repository
public class MongoEntityRepository implements EntityRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public Entity save(Entity entity) {
        // TenantId is already set in the entity by the service layer
        // from RequestContext
        return mongoTemplate.save(entity);
    }

    @Override
    public Optional<Entity> findById(String id) {
        // MUST filter by tenantId from context
        String tenantId = RequestContextHolder.getTenantId();

        Query query = Query.query(
            Criteria.where("id").is(id)
                .and("tenantId").is(tenantId)
        );

        return Optional.ofNullable(mongoTemplate.findOne(query, Entity.class));
    }

    @Override
    public List<Entity> findAll() {
        // MUST filter by tenantId from context
        String tenantId = RequestContextHolder.getTenantId();

        Query query = Query.query(
            Criteria.where("tenantId").is(tenantId)
        );

        return mongoTemplate.find(query, Entity.class);
    }

    @Override
    public void deleteById(String id) {
        // MUST filter by tenantId - prevents cross-tenant deletion
        String tenantId = RequestContextHolder.getTenantId();

        Query query = Query.query(
            Criteria.where("id").is(id)
                .and("tenantId").is(tenantId)
        );

        mongoTemplate.remove(query, Entity.class);
    }
}
```

---

## 5. TenantIsolationTest.java (Test)

```java
package com.gogidix.rapidassist.{service}.integration;

/**
 * CRITICAL TEST: Verify tenant isolation
 * MUST pass for ALL services before production
 */
@SpringBootTest
@TestInstance(TestInstance.PerClass)
public class TenantIsolationTest {

    @Autowired
    private EntityRepository repository;

    @Autowired
    private EntityCommandService service;

    @Test
    @DisplayName("Tenant A cannot access Tenant B data")
    void whenTenantAQueries_shouldOnlySeeTenantAData() {
        // Given: Tenant A and Tenant B both have entities
        RequestContext tenantA = RequestContext.builder()
            .tenantId("tenant-a")
            .userId("user-a")
            .correlationId(UUID.randomUUID().toString())
            .build();

        RequestContext tenantB = RequestContext.builder()
            .tenantId("tenant-b")
            .userId("user-b")
            .correlationId(UUID.randomUUID().toString())
            .build();

        // When: Create entities for both tenants
        RequestContextHolder.set(tenantA);
        Entity entityA = repository.save(new Entity("tenant-a", "A-1"));

        RequestContextHolder.set(tenantB);
        Entity entityB = repository.save(new Entity("tenant-b", "B-1"));

        // Then: Tenant A should NOT see Tenant B's data
        RequestContextHolder.set(tenantA);
        List<Entity> tenantAResults = repository.findAll();

        assertThat(tenantAResults).hasSize(1);
        assertThat(tenantAResults.get(0).getTenantId()).isEqualTo("tenant-a");
        assertThat(tenantAResults).doesNotContain(entityB);

        // Cleanup
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("findById from different tenant returns empty")
    void findById_whenDifferentTenant_thenReturnEmpty() {
        // Given: Entity exists for Tenant A
        RequestContext tenantA = RequestContext.builder()
            .tenantId("tenant-a")
            .userId("user-a")
            .correlationId(UUID.randomUUID().toString())
            .build();

        RequestContextHolder.set(tenantA);
        Entity entity = repository.save(new Entity("tenant-a", "A-1"));
        String entityId = entity.getId();

        // When: Tenant B tries to access same entity
        RequestContext tenantB = RequestContext.builder()
            .tenantId("tenant-b")
            .userId("user-b")
            .correlationId(UUID.randomUUID().toString())
            .build();

        RequestContextHolder.set(tenantB);
        Optional<Entity> result = repository.findById(entityId);

        // Then: Result should be empty
        assertThat(result).isEmpty();

        // Cleanup
        RequestContextHolder.clear();
    }
}
```

---

## 6. application.yml Template

```yaml
# =============================================================================
# Service Configuration - {Service Name}
# =============================================================================

server:
  port: ${SERVICE_PORT:8080}
  servlet:
    context-path: /api/v1

spring:
  application:
    name: {service-name}

  # MongoDB Configuration
  data:
    mongodb:
      host: ${MONGODB_HOST:localhost}
      port: ${MONGODB_PORT:27017}
      database: ${MONGODB_DB:{service-name}}
      username: ${MONGODB_USER}
      password: ${MONGODB_PASSWORD}
      auto-index-creation: true

  # Redis Configuration
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0

  # Kafka Configuration
  kafka:
    bootstrap-servers: ${KAFKA_SERVERS:localhost:9092}
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializers.JsonSerializer
      properties:
        acks: all
    consumer:
      group-id: ${spring.application.name}
      key-deserializer: org.apache.kafka.common.serialization.StringDeserializer
      value-deserializer: org.springframework.kafka.support.serializers.JsonDeserializer
      properties:
        spring.json.trusted.packages: "*"

# Actuator Configuration
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true

# OpenAPI Configuration
springdoc:
  api-docs:
    path: /api-docs
  swagger-ui:
    path: /swagger-ui.html
  show-actuator: true

# Logging Configuration
logging:
  level:
    com.gogidix.rapidassist: ${LOG_LEVEL:DEBUG}
    org.springframework.data.mongodb: ${MONGO_LOG_LEVEL:INFO}
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} - %msg [tenantId=%X{tenantId}][%X{traceId},%X{spanId}]%n"
```

---

## 7. pom.xml Template

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>
    <parent>
        <groupId>com.gogidix.rapidassist</groupId>
        <artifactId>foundation-services</artifactId>
        <version>1.0.0</version>
    </parent>

    <artifactId>{service-name}</artifactId>
    <name>{Service Name}</name>
    <description>Hexagonal SaaS service for {purpose}</description>

    <properties>
        <java.version>21</java.version>
        <spring-boot.version>3.3.5</spring-boot.version>
    </properties>

    <dependencies>
        <!-- Spring Boot Starters -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-mongodb</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>

        <!-- Kafka -->
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka</artifactId>
        </dependency>

        <!-- OpenAPI -->
        <dependency>
            <groupId>org.springdoc</groupId>
            <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
            <version>2.3.0</version>
        </dependency>

        <!-- MapStruct -->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>1.5.5.Final</version>
        </dependency>

        <!-- Shared Libraries -->
        <dependency>
            <groupId>com.gogidix.rapidassist</groupId>
            <artifactId>shared-request-context-library</artifactId>
        </dependency>
        <dependency>
            <groupId>com.gogidix.rapidassist</groupId>
            <artifactId>shared-security-library</artifactId>
        </dependency>
        <dependency>
            <groupId>com.gogidix.rapidassist</groupId>
            <artifactId>shared-exception-library</artifactId>
        </dependency>
        <dependency>
            <groupId>com.gogidix.rapidassist</groupId>
            <artifactId>shared-observability-library</artifactId>
        </dependency>

        <!-- Test Dependencies -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.kafka</groupId>
            <artifactId>spring-kafka-test</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>mongodb</artifactId>
            <scope>test</scope>
        </dependency>
        <dependency>
            <groupId>com.tngtech.archunit</groupId>
            <artifactId>archunit</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <!-- JaCoCo for Code Coverage -->
            <plugin>
                <groupId>org.jacoco</groupId>
                <artifactId>jacoco-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

---

## 8. Dockerfile Template

```dockerfile
# =============================================================================
# Multi-stage Dockerfile for {Service Name}
# =============================================================================

# Stage 1: Builder
FROM maven:3.9-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml and download dependencies (cached layer)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Add non-root user
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copy jar from builder
COPY --from=builder /app/target/{service-name}-*.jar app.jar

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Entry point
ENTRYPOINT ["java", "-jar", "app.jar"]
```

---

## 9. GlobalExceptionHandler.java

```java
package com.gogidix.rapidassist.{service}.interfaces.rest;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFound(
            NotFoundException ex,
            HttpServletRequest request) {

        log.warn("Not found: {} - {}", ex.getMessage(), request.getRequestURI());

        ErrorResponseDto error = ErrorResponseDto.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.NOT_FOUND.value())
            .error("Not Found")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .correlationId(RequestContextHolder.getCorrelationId())
            .tenantId(RequestContextHolder.getTenantId())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponseDto> handleValidation(
            ValidationException ex,
            HttpServletRequest request) {

        log.warn("Validation failed: {}", ex.getMessage());

        ErrorResponseDto error = ErrorResponseDto.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.BAD_REQUEST.value())
            .error("Validation Failed")
            .message(ex.getMessage())
            .path(request.getRequestURI())
            .correlationId(RequestContextHolder.getCorrelationId())
            .tenantId(RequestContextHolder.getTenantId())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGeneric(
            Exception ex,
            HttpServletRequest request) {

        log.error("Unexpected error: ", ex);

        ErrorResponseDto error = ErrorResponseDto.builder()
            .timestamp(Instant.now())
            .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Internal Server Error")
            .message("An unexpected error occurred")
            .path(request.getRequestURI())
            .correlationId(RequestContextHolder.getCorrelationId())
            .tenantId(RequestContextHolder.getTenantId())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

---

## 10. Domain Entity Template (with tenant_id)

```java
package com.gogidix.rapidassist.{service}.domain.model;

/**
 * Domain Entity - MUST have tenantId field for multi-tenancy
 * ZERO framework dependencies - pure domain logic
 */
@Document(collection = "entities")
@TypeAlias("entity")
public class Entity {

    @Id
    private String id;

    /**
     * MANDATORY: Tenant ID for multi-tenancy
     * All queries MUST filter by this field
     */
    @Indexed
    @Field("tenant_id")
    private String tenantId;

    // Business fields
    private String name;
    private String description;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Create new entity with tenant context
     */
    public Entity(String tenantId, String name) {
        this.id = UUID.randomUUID().toString();
        this.tenantId = requireNonNull(tenantId, "tenantId is required");
        this.name = name;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    /**
     * Domain logic: update entity
     */
    public void update(String name, String description) {
        this.name = name;
        this.description = description;
        this.updatedAt = Instant.now();
    }

    /**
     * Domain logic: business rule validation
     */
    public void validate() {
        if (name == null || name.isBlank()) {
            throw new ValidationException("Name cannot be blank");
        }
    }

    // Getters
    public String getId() { return id; }
    public String getTenantId() { return tenantId; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
}
```

---

## 11. Controller Template (with OpenAPI)

```java
package com.gogidix.rapidassist.{service}.interfaces.rest;

@RestController
@RequestMapping("/api/v1/entities")
@Tag(name = "Entity Management", description = "CRUD operations for entities")
@SecurityRequirement(name = "bearerAuth")
@CrossOrigin(origins = "${ALLOWED_ORIGINS:http://localhost:3000}")
@Slf4j
public class EntityController {

    private final EntityQueryService queryService;
    private final EntityCommandService commandService;

    @GetMapping
    @Operation(summary = "List all entities", description = "Returns paginated list of entities for current tenant")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved entities"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden")
    })
    public CompletableFuture<ResponseEntity<PagedResponseDto<EntityResponseDto>>> listEntities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String sort,
            RequestContextHolder context) {

        String tenantId = context.tenantId();
        log.info("Listing entities for tenant: {}", tenantId);

        return queryService.list(tenantId, page, size, sort)
            .thenApply(result -> ResponseEntity.ok(result));
    }

    @PostMapping
    @Operation(summary = "Create new entity", description = "Creates a new entity for current tenant")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Entity created successfully"),
        @ApiResponse(responseCode = "400", description = "Validation failed"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    public CompletableFuture<ResponseEntity<EntityResponseDto>> createEntity(
            @RequestBody @Valid CreateEntityRequestDto request,
            RequestContextHolder context) {

        String tenantId = context.tenantId();
        log.info("Creating entity for tenant: {}", tenantId);

        CreateEntityCommand command = new CreateEntityCommand(
            tenantId,
            request.name(),
            request.description()
        );

        return commandService.handle(command)
            .thenApply(result -> ResponseEntity.status(HttpStatus.CREATED).body(result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get entity by ID", description = "Returns a single entity for current tenant")
    public CompletableFuture<ResponseEntity<EntityResponseDto>> getEntity(
            @PathVariable String id,
            RequestContextHolder context) {

        String tenantId = context.tenantId();
        log.info("Getting entity {} for tenant: {}", id, tenantId);

        return queryService.getById(tenantId, id)
            .thenApply(result -> ResponseEntity.ok(result));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update entity", description = "Updates an existing entity")
    public CompletableFuture<ResponseEntity<EntityResponseDto>> updateEntity(
            @PathVariable String id,
            @RequestBody @Valid UpdateEntityRequestDto request,
            RequestContextHolder context) {

        String tenantId = context.tenantId();
        log.info("Updating entity {} for tenant: {}", id, tenantId);

        UpdateEntityCommand command = new UpdateEntityCommand(
            tenantId,
            id,
            request.name(),
            request.description()
        );

        return commandService.handle(command)
            .thenApply(result -> ResponseEntity.ok(result));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete entity", description = "Deletes an entity")
    @ApiResponse(responseCode = "204", description = "Entity deleted successfully")
    public CompletableFuture<ResponseEntity<Void>> deleteEntity(
            @PathVariable String id,
            RequestContextHolder context) {

        String tenantId = context.tenantId();
        log.info("Deleting entity {} for tenant: {}", id, tenantId);

        DeleteEntityCommand command = new DeleteEntityCommand(tenantId, id);

        return commandService.handle(command)
            .thenApply(result -> ResponseEntity.noContent().build());
    }
}
```

---

## 12. Railway.json Template

```json
{
  "$schema": "https://railway.app/railway.schema.json",
  "build": {
    "builder": "NIXPACKS",
    "buildCommand": "mvn clean package -DskipTests",
    "watchPatterns": [
      "src/**",
      "pom.xml"
    ]
  },
  "deploy": {
    "startCommand": "java -jar target/*.jar",
    "healthcheckPath": "/actuator/health",
    "healthcheckTimeout": 300,
    "restartPolicyType": "ON_FAILURE",
    "restartPolicyMaxRetries": 10
  }
}
```

---

## CHECKLIST: Verify Service Compliance

Before marking a service as production-ready, verify:

### Structure Compliance
- [ ] domain/ folder exists with model/, repository/, port/, event/, policy/
- [ ] application/ folder exists with command/, query/, service/, dto/, mapper/
- [ ] infrastructure/ folder exists with persistence/, messaging/, security/, adapter/, config/
- [ ] interfaces/ folder exists with rest/, dto/
- [ ] shared/requestcontext/ folder exists with RequestContext, RequestContextHolder

### Multi-Tenancy Compliance
- [ ] TenantInterceptor.java exists and is registered
- [ ] All domain models have tenantId field
- [ ] All repositories filter by tenantId
- [ ] All controllers use RequestContext
- [ ] TenantIsolationTest.java exists and passes

### Documentation Compliance
- [ ] OpenAPI annotations on all controllers
- [ ] Swagger UI accessible at /swagger-ui.html
- [ ] API docs at /api-docs

### Testing Compliance
- [ ] Unit tests exist for domain, application, infrastructure layers
- [ ] Integration tests exist
- [ ] TenantIsolationTest exists and passes
- [ ] HexArchitectureTest exists and passes
- [ ] JaCoCo coverage > 80%

### Security Compliance
- [ ] GlobalExceptionHandler exists
- [ ] CORS configured (not origins="*")
- [ ] JWT validation implemented
- [ ] Rate limiting configured

### Deployment Compliance
- [ ] Dockerfile exists
- [ ] railway.json exists
- [ ] CI/CD pipeline exists
- [ ] Environment profiles (dev/prod) exist

---

**Template Version:** 1.0
**Last Updated:** 2026-01-20
**Status:** MANDATORY FOR ALL FOUNDATION DOMAIN SERVICES
