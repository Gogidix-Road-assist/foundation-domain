# MongoDB Multi-Tenancy Foundation - Integration Guide

## Overview

This guide provides comprehensive documentation for the MongoDB multi-tenancy infrastructure implemented in the `shared-persistence-library`. This foundation enables automatic tenant data isolation for MongoDB-based microservices in the Gogidix RapidAssist SaaS platform.

## Key Features

- **Automatic Tenant Isolation**: All MongoDB documents are automatically scoped to the current tenant
- **Thread-Safe Context Management**: ThreadLocal-based tenant context with automatic cleanup
- **Soft Delete Support**: Built-in soft delete functionality for audit trails
- **Index Optimization**: Pre-configured compound indexes for efficient tenant-scoped queries
- **Spring Boot Auto-Configuration**: Zero-configuration setup with sensible defaults
- **MongoDB Native**: Uses MongoDB document model, query syntax, and indexing

## Architecture

### Components

```
shared-persistence-library/
└── src/main/java/com/gogidix/rapidassist/shared/persistence/infrastructure/mongodb/
    ├── context/
    │   └── MongoTenantContext.java          # Thread-local tenant storage
    ├── document/
    │   └── TenantAwareDocument.java         # Base class for tenant-aware documents
    ├── repository/
    │   └── TenantAwareMongoRepository.java  # Base interface with tenant filtering
    ├── config/
    │   └── MongoTenantConfiguration.java    # Auto-configuration
    └── index/
        └── MongoIndexCreator.java           # Index management utilities
```

### Data Flow

```
HTTP Request (X-Tenant-Id: 123)
    ↓
TenantRequestFilter extracts tenant ID
    ↓
MongoTenantContext.setTenantId(123L)
    ↓
Service Layer calls repository.save()
    ↓
TenantAwareDocumentListener populates tenant_id
    ↓
MongoDB stores document with tenant_id: 123
    ↓
Response sent, MongoTenantContext.clear()
```

## Quick Start

### 1. Add Dependency

```xml
<dependency>
    <groupId>com.gogidix.rapidassist</groupId>
    <artifactId>shared-persistence-library</artifactId>
    <version>1.0.0</version>
</dependency>
```

### 2. Configure MongoDB Connection

```yaml
# application.yml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/rapidassist
      auto-index-creation: true

mongodb:
  multitenancy:
    enabled: true
    default-tenant-id: 1
```

### 3. Create Tenant-Aware Document

```java
import com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.document.TenantAwareDocument;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "insurance_policies")
public class InsurancePolicy extends TenantAwareDocument {
    private String policyNumber;
    private String coverageType;
    private BigDecimal premiumAmount;

    // Getters and setters
}
```

### 4. Create Repository

```java
import com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb.repository.TenantAwareMongoRepository;

public interface InsurancePolicyRepository extends TenantAwareMongoRepository<InsurancePolicy, String> {
    // All queries automatically filter by tenant_id

    @Query("{ 'tenantId': ?0, 'policyNumber': ?1, 'deleted': false }")
    Optional<InsurancePolicy> findByTenantIdAndPolicyNumber(Long tenantId, String policyNumber);
}
```

### 5. Use in Service

```java
@Service
public class InsurancePolicyService {
    private final InsurancePolicyRepository repository;

    // tenant_id is automatically set from X-Tenant-Id header
    public void createPolicy(CreatePolicyRequest request) {
        InsurancePolicy policy = new InsurancePolicy();
        policy.setPolicyNumber(request.getPolicyNumber());
        // tenantId is auto-populated before save
        repository.save(policy);
    }

    // All queries automatically scoped to current tenant
    public List<InsurancePolicy> getAllPolicies() {
        return repository.findAll(); // Only returns current tenant's policies
    }
}
```

## API Reference

### MongoTenantContext

The thread-local context for storing the current tenant ID.

```java
// Set tenant ID (usually done by filter)
MongoTenantContext.setTenantId(123L);

// Get current tenant ID (returns default if not set)
Long tenantId = MongoTenantContext.getTenantId();

// Get tenant ID or throw exception
Long tenantId = MongoTenantContext.getTenantIdOrThrow();

// Check if tenant is set
boolean isSet = MongoTenantContext.isTenantSet();

// Execute action with specific tenant (for background jobs)
MongoTenantContext.withTenantId(456L, () -> {
    // Code here runs with tenant 456
});

// Clear context (usually done by filter)
MongoTenantContext.clear();
```

### TenantAwareDocument

Base class for all MongoDB documents requiring tenant isolation.

**Auto-Populated Fields:**
- `id`: MongoDB document ID (String/ObjectId)
- `tenantId`: Tenant identifier (Long, auto-populated)
- `createdAt`: Creation timestamp
- `updatedAt`: Last modification timestamp
- `createdBy`: User who created the document
- `updatedBy`: User who last modified the document
- `deleted`: Soft delete flag

**Methods:**
```java
// Check tenant ownership
boolean belongs = document.belongsToTenant(123L);

// Soft delete
document.markAsDeleted();

// Restore soft-deleted document
document.restore();

// Check if active
boolean active = document.isActive();
```

### TenantAwareMongoRepository

Base repository interface with automatic tenant filtering.

**Key Methods:**
```java
// Find by ID (tenant-scoped)
Optional<T> findById(ID id);

// Find all for current tenant
List<T> findAll();

// Find all for specific tenant
List<T> findAllByTenantId(Long tenantId);

// Paginated results
Page<T> findAll(Pageable pageable);

// Count for current tenant
long count();

// Find soft-deleted documents
List<T> findDeleted();

// Find entities created after timestamp
List<T> findByTenantIdAndCreatedAtAfter(Long tenantId, LocalDateTime timestamp);

// Soft delete by ID
void softDeleteById(ID id);

// Restore by ID
void restoreById(ID id);
```

### MongoIndexCreator

Utility for creating MongoDB indexes to support tenant queries.

```java
@Component
public class DataInitializer implements ApplicationRunner {
    private final MongoIndexCreator indexCreator;

    @Override
    public void run(ApplicationArguments args) {
        // Create all tenant indexes for a collection
        indexCreator.createTenantIndexes("insurance_policies");
        indexCreator.createTenantIndexes("claims");

        // Create text index for search
        indexCreator.createTextIndex("policies", "policyNumber", "holderName");

        // Create geospatial index
        indexCreator.createGeospatialIndex("service_requests", "location");
    }
}
```

## MongoDB Query Examples

### Basic Tenant-Scoped Queries

```java
// Repository method with MongoDB query syntax
@Query("{ 'tenantId': ?0, 'status': ?1, 'deleted': false }")
List<Policy> findByTenantIdAndStatus(Long tenantId, String status);

// Using the current tenant (from context)
@Query("{ 'tenantId': ?0, 'deleted': false }")
default List<Policy> findAllActive() {
    return findAllByTenantId(MongoTenantContext.getTenantId());
}
```

### Complex Queries

```java
// Date range query
@Query("{ 'tenantId': ?0, 'deleted': false, 'createdAt': { $gte: ?1, $lte: ?2 } }")
List<Policy> findByTenantIdAndDateRange(Long tenantId, LocalDateTime start, LocalDateTime end);

// Nested field query
@Query("{ 'tenantId': ?0, 'deleted': false, 'holder.address.city': ?1 }")
List<Policy> findByTenantIdAndCity(Long tenantId, String city);

// Array contains query
@Query("{ 'tenantId': ?0, 'deleted': false, 'coverageTypes': { $in: ?1 } }")
List<Policy> findByTenantIdAndCoverageTypes(Long tenantId, List<String> types);

// Regex query (case-insensitive)
@Query("{ 'tenantId': ?0, 'deleted': false, 'policyNumber': { $regex: ?1, $options: 'i' } }")
List<Policy> findByTenantIdAndPolicyNumberRegex(Long tenantId, String pattern);

// Aggregation pipeline
Aggregation aggregation = Aggregation.newAggregation(
    Aggregation.match(Criteria.where("tenantId").is(tenantId).and("deleted").is(false)),
    Aggregation.group("status").count().as("count"),
    Aggregation.project("count").and("status").previousOperation()
);
```

## Index Creation

### Using Annotations

```java
@Document(collection = "policies")
@CompoundIndex(name = "tenant_status_idx", def = "{'tenantId': 1, 'status': 1}", background = true)
public class Policy extends TenantAwareDocument {
    @Indexed
    private String policyNumber;

    @TextIndexed
    private String description;
}
```

### Using MongoIndexCreator (Recommended for Production)

```javascript
// MongoDB Compass - Create indexes manually
db.policies.createIndex(
    { "tenant_id": 1, "deleted": 1 },
    { background: true, name: "tenant_deleted_idx" }
);

db.policies.createIndex(
    { "tenant_id": 1, "created_at": -1 },
    { background: true, name: "tenant_created_idx" }
);

db.policies.createIndex(
    { "tenant_id": 1, "status": 1 },
    { background: true, name: "tenant_status_idx" }
);
```

### Verify Indexes

```javascript
// List all indexes
db.policies.getIndexes();

// Check index usage
db.policies.find({ "tenant_id": 123, "deleted": false }).explain("executionStats");
```

## Configuration Options

```yaml
spring:
  data:
    mongodb:
      # Connection URI
      uri: mongodb://localhost:27017/rapidassist

      # Auto-create indexes from annotations
      auto-index-creation: true

      # Auto-creation timeout
      auto-creation-timeout: 30s

mongodb:
  multitenancy:
    # Enable/disable multi-tenancy
    enabled: true

    # Default tenant ID when header is missing
    default-tenant-id: 1

    # Custom header name (default: X-Tenant-Id)
    header-name: X-Tenant-Id

    # Custom query parameter name (default: tenantId)
    parameter-name: tenantId
```

## Testing

### Unit Test Example

```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/test_db",
    "mongodb.multitenancy.enabled=true"
})
class InsurancePolicyServiceTest {

    @Autowired
    private InsurancePolicyRepository repository;

    @Test
    void shouldIsolateDataByTenant() {
        // Set tenant context
        MongoTenantContext.setTenantId(100L);

        // Create policy for tenant 100
        InsurancePolicy policy1 = new InsurancePolicy();
        policy1.setPolicyNumber("POL-100");
        repository.save(policy1);

        // Switch to tenant 200
        MongoTenantContext.setTenantId(200L);

        // Create policy for tenant 200
        InsurancePolicy policy2 = new InsurancePolicy();
        policy2.setPolicyNumber("POL-200");
        repository.save(policy2);

        // Verify isolation
        MongoTenantContext.setTenantId(100L);
        List<InsurancePolicy> tenant100Policies = repository.findAll();
        assertEquals(1, tenant100Policies.size());
        assertEquals("POL-100", tenant100Policies.get(0).getPolicyNumber());

        MongoTenantContext.setTenantId(200L);
        List<InsurancePolicy> tenant200Policies = repository.findAll();
        assertEquals(1, tenant200Policies.size());
        assertEquals("POL-200", tenant200Policies.get(0).getPolicyNumber());
    }

    @AfterEach
    void cleanup() {
        MongoTenantContext.clear();
    }
}
```

## Security Considerations

### Tenant Isolation

1. **Filter Layer**: Tenant ID is extracted from HTTP header and stored in ThreadLocal
2. **Document Layer**: Tenant ID is auto-populated before save
3. **Query Layer**: All repositories automatically filter by tenant_id
4. **Validation Layer**: Cross-tenant access throws exceptions

### Recommendations

1. **Always use TenantAwareMongoRepository**: Direct MongoTemplate usage bypasses tenant filtering
2. **Validate Tenant Context**: Check `MongoTenantContext.isTenantSet()` in critical operations
3. **Audit Tenant Changes**: Log when tenant_id is modified
4. **Use Soft Delete**: Enables audit trail and data recovery

## Troubleshooting

### Common Issues

**Issue**: Documents from other tenants appear in queries
- **Solution**: Ensure repository extends `TenantAwareMongoRepository`, not `MongoRepository`

**Issue**: tenant_id is null in documents
- **Solution**: Verify `MongoTenantConfiguration` is loaded and filter is registered

**Issue**: Cross-tenant data in tests
- **Solution**: Always clear tenant context in `@AfterEach`: `MongoTenantContext.clear()`

**Issue**: Slow queries on large collections
- **Solution**: Create compound indexes on `(tenantId, deleted)` and verify with explain()

### Logging

Enable debug logging for troubleshooting:

```yaml
logging:
  level:
    com.gogidix.rapidassist.shared.persistence.infrastructure.mongodb: DEBUG
    org.springframework.data.mongodb: DEBUG
```

## Migration Guide

### From JPA/PostgreSQL to MongoDB

1. **Replace Entity with Document**:
   ```java
   // Before (JPA)
   @Entity
   @Table(name = "policies")
   public class Policy extends TenantAwareJpaEntity { }

   // After (MongoDB)
   @Document(collection = "policies")
   public class Policy extends TenantAwareDocument { }
   ```

2. **Update Repository**:
   ```java
   // Before
   public interface PolicyRepository extends TenantAwareJpaRepository<Policy, Long> { }

   // After
   public interface PolicyRepository extends TenantAwareMongoRepository<Policy, String> { }
   ```

3. **Change ID Type**:
   ```java
   // Before: Long id (auto-increment)
   // After: String id (ObjectId)
   ```

4. **Update Queries**:
   ```java
   // Before (JPQL)
   @Query("SELECT p FROM Policy p WHERE p.tenantId = ?1 AND p.status = ?2")
   List<Policy> findByTenantIdAndStatus(Long tenantId, String status);

   // After (MongoDB query syntax)
   @Query("{ 'tenantId': ?0, 'status': ?1, 'deleted': false }")
   List<Policy> findByTenantIdAndStatus(Long tenantId, String status);
   ```

## Insurance Domain Integration

### Insurance-Core Domain

Create tenant-aware documents for:

```java
@Document(collection = "insurance_policies")
public class InsurancePolicy extends TenantAwareDocument {
    private String policyNumber;
    private String policyType;
    private BigDecimal premiumAmount;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private PolicyStatus status;
}

@Document(collection = "policy_holders")
public class PolicyHolder extends TenantAwareDocument {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Address address;
}

@Document(collection = "coverage_types")
public class CoverageType extends TenantAwareDocument {
    private String code;
    private String name;
    private String description;
    private Boolean isActive;
}
```

### Insurance-Claim-Automation Domain

Create tenant-aware documents for:

```java
@Document(collection = "claims")
public class Claim extends TenantAwareDocument {
    private String claimNumber;
    private String policyId;
    private LocalDate incidentDate;
    private BigDecimal claimAmount;
    private ClaimStatus status;
    private String description;
}

@Document(collection = "claim_documents")
public class ClaimDocument extends TenantAwareDocument {
    private String claimId;
    private String documentType;
    private String fileUrl;
    private LocalDate uploadedDate;
}

@Document(collection = "claim_approvals")
public class ClaimApproval extends TenantAwareDocument {
    private String claimId;
    private String approverId;
    private ApprovalDecision decision;
    private String comments;
    private LocalDateTime approvedAt;
}
```

## Performance Best Practices

1. **Use Compound Indexes**: Create indexes on `(tenantId, frequentlyQueriedField)`
2. **Limit Results**: Use pagination with `Pageable` for large collections
3. **Project Fields**: Use `@Query(fields="{'field1': 1, 'field2': 1}")` to limit returned fields
4. **Avoid Large Documents**: Split large documents into smaller, related collections
5. **Monitor Index Usage**: Use `explain()` to verify indexes are being used

## Support

For issues or questions related to MongoDB multi-tenancy:
- Check: `Rapid-Assist/Foundation-Domain/shared-libraries/Backend/Java/shared-persistence-library/docs/`
- Tests: `src/test/java/com/gogidix/rapidassist/shared/persistence/infrastructure/mongodb/`
