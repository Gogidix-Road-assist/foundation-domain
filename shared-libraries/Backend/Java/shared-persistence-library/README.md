# Shared Persistence Library

Tenant-aware persistence components for multi-tenant data isolation in the Rapid Assist SaaS platform.

## Overview

This library provides automatic tenant isolation at the database/ORM level, ensuring that each tenant's data remains completely isolated from other tenants. It offers base classes, repository interfaces, and Hibernate filters that automatically inject `tenant_id` conditions into all database operations.

## Features

- **TenantAwareEntity**: Base class for entities with automatic tenant_id management
- **TenantAwareRepository**: Repository interface with tenant-scoped CRUD operations
- **TenantFilter**: Hibernate filter for automatic query filtering
- **TenantEntityListener**: JPA lifecycle listener for tenant validation

## Architecture

```
shared-persistence-library/
├── src/main/java/com/gogidix/rapidassist/shared/persistence/
│   ├── domain/
│   │   └── TenantAwareEntity.java          # Base entity class with tenant_id
│   ├── repository/
│   │   └── TenantAwareRepository.java      # Base repository interface
│   └── hibernate/
│       ├── TenantFilter.java               # Hibernate filter for query injection
│       └── TenantEntityListener.java       # JPA entity listener for validation
└── pom.xml
```

## Usage

### 1. Create Tenant-Aware Entities

Extend `TenantAwareEntity` in your domain entities:

```java
import com.gogidix.rapidassist.shared.persistence.domain.TenantAwareEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "service_requests")
@EntityListeners(TenantEntityListener.class)
public class ServiceRequest extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private RequestStatus status;

    // Constructors, getters, setters...
}
```

### 2. Create Tenant-Aware Repositories

Extend `TenantAwareRepository` instead of `JpaRepository`:

```java
import com.gogidix.rapidassist.shared.persistence.repository.TenantAwareRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceRequestRepository
    extends TenantAwareRepository<ServiceRequest, Long> {

    // Custom tenant-scoped queries
    List<ServiceRequest> findByTenantIdAndStatus(Long tenantId, RequestStatus status);

    // Spring Data JPA automatically adds tenant filtering
    Optional<ServiceRequest> findByIdAndTenantId(Long id, Long tenantId);
}
```

### 3. Enable Hibernate Filters (Optional)

For automatic query filtering at the ORM level:

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
    // ... entity fields
}
```

Enable the filter in your service or configuration:

```java
import org.hibernate.Session;
import jakarta.persistence.EntityManager;

@Service
public class ServiceRequestService {

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    public void setup() {
        Session session = entityManager.unwrap(Session.class);
        Filter filter = session.enableFilter("tenantFilter");
        filter.setParameter("tenantId", TenantContext.getTenantId());
    }
}
```

### 4. Automatic Tenant Validation

The `TenantEntityListener` automatically:
- Validates tenant_id is present before persisting entities
- Auto-populates tenant_id from TenantContext if available
- Prevents cross-tenant updates and deletions
- Logs security violations

## Integration with TenantContext

This library integrates with `shared-request-context-library` for automatic tenant resolution:

```java
// In your request filter or interceptor
@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) {
        String tenantHeader = request.getHeader("X-Tenant-ID");
        Long tenantId = Long.parseLong(tenantHeader);
        TenantContext.setTenantId(tenantId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                              HttpServletResponse response,
                              Object handler,
                              Exception ex) {
        TenantContext.clear();
    }
}
```

## Security Features

1. **PrePersist Validation**: Ensures tenant_id is populated before saving
2. **PreUpdate Protection**: Prevents tenant_id modification and cross-tenant updates
3. **PreRemove Validation**: Blocks deletion of other tenants' data
4. **Hibernate Filter**: Automatic tenant_id injection in SQL queries
5. **Repository Scoping**: All repository methods are tenant-aware

## Maven Dependency

```xml
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>shared-persistence-library</artifactId>
    <version>1.0.0-SNAPSHOT</version>
</dependency>
```

## Requirements

- Java 17+
- Jakarta Persistence 3.1+
- Hibernate 6.2+
- Spring Data JPA 2023.0+
- SLF4J 2.0+

## Testing

The library includes unit tests for all components:

```bash
mvn test
```

## Best Practices

1. **Always extend TenantAwareEntity** for all domain entities
2. **Use TenantAwareRepository** for all repositories
3. **Enable Hibernate filters** for additional safety
4. **Log security violations** in production
5. **Test tenant isolation** thoroughly
6. **Never bypass tenant context** in production code
7. **Use @Transactional** for repository operations

## Migration Guide

### From Standard JPA Entities

**Before:**
```java
@Entity
public class ServiceRequest {
    @Id
    private Long id;
    private String description;
}
```

**After:**
```java
@Entity
@EntityListeners(TenantEntityListener.class)
public class ServiceRequest extends TenantAwareEntity {
    @Id
    private Long id;
    private String description;
}
```

### From Standard Repositories

**Before:**
```java
public interface ServiceRequestRepository extends JpaRepository<ServiceRequest, Long> {
    List<ServiceRequest> findAll();
}
```

**After:**
```java
public interface ServiceRequestRepository extends TenantAwareRepository<ServiceRequest, Long> {
    List<ServiceRequest> findAllByTenantId(Long tenantId);
}
```

## Performance Considerations

- **Indexing**: Ensure `tenant_id` columns are indexed in all tables
- **Partitioning**: Consider table partitioning by tenant_id for large datasets
- **Connection Pooling**: Use tenant-specific connection pools for strict isolation
- **Caching**: Configure second-level caching with tenant-aware keys
- **Query Optimization**: Use `findAllByTenantId()` instead of filtering in-memory

## Troubleshooting

### Issue: Entities saved without tenant_id

**Solution**: Ensure `TenantEntityListener` is registered:
```java
@EntityListeners(TenantEntityListener.class)
```

### Issue: Cross-tenant data access

**Solution**: Enable Hibernate filters and verify TenantContext is set:
```java
Session session = entityManager.unwrap(Session.class);
session.enableFilter("tenantFilter")
       .setParameter("tenantId", TenantContext.getTenantId());
```

### Issue: Repository methods not filtered

**Solution**: Extend `TenantAwareRepository` instead of `JpaRepository`

## License

Copyright © 2026 Gogidix. All rights reserved.

## Support

For issues and questions:
- Create an issue in the repository
- Contact: platform-team@gogidix.com
