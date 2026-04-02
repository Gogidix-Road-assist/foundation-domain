# Configuration Guide for Shared Persistence Library

This guide explains how to configure and use the shared-persistence-library in your microservices.

## 1. Maven Dependency

Add to your service's `pom.xml`:

```xml
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>shared-persistence-library</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## 2. Application Configuration

### application.yml

```yaml
spring:
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: true
        show_sql: false
        default_filters: true  # Enable Hibernate filters by default
        filter:
          tenantFilter:
            param: tenantId
            condition: tenant_id = :tenantId

  datasource:
    url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:rapidassist}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

## 3. Hibernate Filter Configuration

### Option A: Annotation-Based (Recommended)

```java
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@Entity
@Table(name = "service_requests")
@FilterDef(name = "tenantFilter", parameters = {
    @ParamDef(name = "tenantId", type = "long")
})
@Filter(name = "tenantFilter", condition = "tenant_id = :tenantId")
public class ServiceRequest extends TenantAwareEntity {
    // Entity fields
}
```

### Option B: Global Configuration via orm.xml

Create `src/main/resources/META-INF/orm.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="https://jakarta.ee/xml/ns/persistence/orm"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence/orm
                 https://jakarta.ee/xml/ns/persistence/orm_3_0.xsd"
                 version="3.0">

    <!-- Apply tenant filter to all entities extending TenantAwareEntity -->
    <filter-def name="tenantFilter">
        <filter-param name="tenantId" type="long"/>
    </filter-def>

    <entity class="com.gogidix.rapidassist.shared.persistence.domain.TenantAwareEntity">
        <filter name="tenantFilter" condition="tenant_id = :tenantId"/>
    </entity>
</entity-mappings>
```

## 4. Filter Activation Configuration

### Spring Configuration Class

```java
package com.gogidix.rapidassist.yourservice.config;

import com.gogidix.rapidassist.shared.requestcontext.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class TenantFilterConfig {

    @PersistenceContext
    private EntityManager entityManager;

    // This is a simple approach - for production, use an interceptor
    @PostConstruct
    public void setup() {
        // Filters are enabled per-session
        // In a real application, enable this in a request interceptor
    }
}
```

### Request Interceptor for Filter Activation

```java
package com.gogidix.rapidassist.yourservice.interceptor;

import com.gogidix.rapidassist.shared.requestcontext.TenantContext;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.Session;
import org.hibernate.Filter;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class TenantFilterInterceptor implements HandlerInterceptor {

    private final EntityManager entityManager;

    public TenantFilterInterceptor(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {
        Long tenantId = TenantContext.getTenantId();
        if (tenantId != null) {
            Session session = entityManager.unwrap(Session.class);
            Filter filter = session.enableFilter("tenantFilter");
            filter.setParameter("tenantId", tenantId);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler,
                              Exception ex) throws Exception {
        // Filter is automatically disabled when session closes
    }
}
```

Register the interceptor:

```java
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TenantFilterInterceptor tenantFilterInterceptor;

    public WebConfig(TenantFilterInterceptor tenantFilterInterceptor) {
        this.tenantFilterInterceptor = tenantFilterInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantFilterInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/actuator/**", "/error");
    }
}
```

## 5. Entity Listener Registration

### Global Registration (Recommended)

Create `src/main/resources/META-INF/orm.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="https://jakarta.ee/xml/ns/persistence/orm"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence/orm
                 https://jakarta.ee/xml/ns/persistence/orm_3_0.xsd"
                 version="3.0">

    <!-- Apply entity listener to all entities -->
    <entity class="com.gogidix.rapidassist.shared.persistence.domain.TenantAwareEntity">
        <entity-listeners>
            <entity-listener class="com.gogidix.rapidassist.shared.persistence.hibernate.TenantEntityListener"/>
        </entity-listeners>
    </entity>
</entity-mappings>
```

### Per-Entity Registration

```java
@Entity
@EntityListeners(TenantEntityListener.class)
@Table(name = "service_requests")
public class ServiceRequest extends TenantAwareEntity {
    // Entity fields
}
```

## 6. Testing Configuration

### application-test.yml

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:

  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

### Test Configuration

```java
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.yml")
class ServiceRequestRepositoryTest {

    @Autowired
    private ServiceRequestRepository repository;

    @BeforeEach
    void setUp() {
        TenantContext.setTenantId(1L);
    }

    @AfterEach
    void tearDown() {
        TenantContext.clear();
    }

    @Test
    void findAllByTenantId_shouldReturnOnlyTenantData() {
        // Test implementation
    }
}
```

## 7. Database Schema Migration

### Liquibase/Flyway Migration Example

```sql
-- Add tenant_id column to existing table
ALTER TABLE service_requests
ADD COLUMN tenant_id BIGINT NOT NULL;

-- Add index for performance
CREATE INDEX idx_service_requests_tenant_id
ON service_requests(tenant_id);

-- Add foreign key constraint
ALTER TABLE service_requests
ADD CONSTRAINT fk_service_requests_tenant
FOREIGN KEY (tenant_id) REFERENCES tenants(id);

-- Add composite index for common queries
CREATE INDEX idx_service_requests_tenant_status
ON service_requests(tenant_id, status);
```

## 8. Performance Optimization

### Database Indexing

```sql
-- Always index tenant_id columns
CREATE INDEX idx_table_tenant_id ON table_name(tenant_id);

-- Composite indexes for common query patterns
CREATE INDEX idx_table_tenant_created ON table_name(tenant_id, created_at);

-- Covering indexes for frequently queried columns
CREATE INDEX idx_table_tenant_covering ON table_name(tenant_id, status, created_at);
```

### Connection Pool Configuration

```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
```

### Query Optimization

```java
// GOOD: Tenant-scoped query
List<ServiceRequest> requests = repository.findAllByTenantId(tenantId);

// BAD: In-memory filtering
List<ServiceRequest> all = repository.findAll();
List<ServiceRequest> filtered = all.stream()
    .filter(r -> r.getTenantId().equals(tenantId))
    .collect(Collectors.toList());
```

## 9. Security Best Practices

### Validate Tenant Context in Service Layer

```java
@Service
public class ServiceRequestService {

    private final ServiceRequestRepository repository;

    public ServiceRequest getRequest(Long id) {
        Long tenantId = TenantContext.getTenantId();
        return repository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new NotFoundException(
                "ServiceRequest not found or access denied"
            ));
    }
}
```

### Audit Logging

```java
@Aspect
@Component
public class TenantAccessAuditAspect {

    @Around("@annotation(TenantAware)")
    public Object auditTenantAccess(ProceedingJoinPoint joinPoint) throws Throwable {
        Long tenantId = TenantContext.getTenantId();
        String method = joinPoint.getSignature().getName();

        try {
            Object result = joinPoint.proceed();
            log.info("SUCCESS: tenant={}, method={}", tenantId, method);
            return result;
        } catch (Exception e) {
            log.warn("FAILURE: tenant={}, method={}, error={}",
                tenantId, method, e.getMessage());
            throw e;
        }
    }
}
```

## 10. Troubleshooting

### Enable SQL Logging

```yaml
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Verify Filter is Active

```java
Session session = entityManager.unwrap(Session.class);
boolean enabled = session.getEnabledFilter("tenantFilter") != null;
log.debug("Tenant filter enabled: {}", enabled);
```

### Check Tenant Context

```java
Long tenantId = TenantContext.getTenantId();
if (tenantId == null) {
    throw new IllegalStateException("No tenant context set");
}
log.debug("Current tenant: {}", tenantId);
```

## 11. Migration Checklist

- [ ] Add shared-persistence-library dependency
- [ ] Update entities to extend TenantAwareEntity
- [ ] Update repositories to extend TenantAwareRepository
- [ ] Add @EntityListeners(TenantEntityListener.class) to entities
- [ ] Configure Hibernate filters (@FilterDef, @Filter)
- [ ] Create request interceptor for filter activation
- [ ] Add database indexes on tenant_id columns
- [ ] Update integration tests
- [ ] Verify tenant isolation with security tests
- [ ] Update documentation

## 12. Support

For questions or issues:
- Review: /docs/README.md
- Issues: Create ticket in repository
- Email: platform-team@gogidix.com
