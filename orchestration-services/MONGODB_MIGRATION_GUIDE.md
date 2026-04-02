# MongoDB Domain Model & Repository Migration Guide

## Purpose

This guide provides step-by-step instructions for migrating domain models and repositories from PostgreSQL/JPA to MongoDB across all 11 Foundation-Domain orchestration services.

---

## Phase 1: Domain Model Migration (@Entity → @Document)

### Step 1: Update Annotations

**Replace JPA annotations with MongoDB annotations:**

```java
// BEFORE (PostgreSQL/JPA)
package com.gogidix.central.xxx.domain.aggregate;

import javax.persistence.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.AuditorAware;

@Entity
@Table(name = "alerts")
public class Alert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "alert_id", unique = true, nullable = false)
    private String alertId;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "severity")
    private String severity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AlertStatus status;

    @Embedded
    private Location location;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}

// AFTER (MongoDB)
package com.gogidix.rapidassist.xxx.domain.aggregate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import lombok.Data;

@Document(collection = "alerts")
@Data
public class Alert {

    @Id
    private String id;  // MongoDB ObjectId as String

    @Indexed(unique = true)
    private String alertId;

    @Indexed
    private String tenantId;

    @Indexed
    private String severity;

    @Indexed
    private String status;

    // Embedded document
    private Location location;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

### Step 2: Update Import Statements

```java
// REMOVE these imports:
import javax.persistence.*;
import org.springframework.data.jpa.domain.*;
import org.springframework.data.jpa.repository.*;
import org.hibernate.annotations.*;

// ADD these imports:
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Embedded;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.repository.Query;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
```

### Step 3: Key Annotation Mappings

| JPA/PostgreSQL | MongoDB | Notes |
|----------------|---------|-------|
| `@Entity` | `@Document(collection = "name")` | Collection name is optional |
| `@Table(name = "xxx")` | `@Document(collection = "xxx")` | Direct mapping |
| `@Id @GeneratedValue` | `@Id` | Use String for ObjectId |
| `@Column(unique = true)` | `@Indexed(unique = true)` | Index creation |
| `@Column(nullable = false)` | Not needed | Schema-less validation |
| `@Enumerated(EnumType.STRING)` | Store as String | Default behavior |
| `@Embedded` | `@Embedded` | Same concept |
| `@ManyToOne @JoinColumn` | `@DBRef` | Lazy loading reference |
| `@OneToMany` | `@DBRef` | Lazy loading reference |
| `@CreationTimestamp` | `@CreatedDate` | Requires auditing |
| `@UpdateTimestamp` | `@LastModifiedDate` | Requires auditing |

### Step 4: Handle Relationships

```java
// BEFORE (JPA)
@ManyToOne
@JoinColumn(name = "provider_id")
private ServiceProvider provider;

@OneToMany(mappedBy = "alert", cascade = CascadeType.ALL)
private List<AlertComment> comments;

// AFTER (MongoDB)
@DBRef
private ServiceProvider provider;

@DBRef
private List<AlertComment> comments;
// OR embed for better performance:
private List<AlertComment> comments;  // Embedded
```

### Step 5: Update Date/Time Types

```java
// BEFORE
import java.sql.Timestamp;
private Timestamp createdAt;

// AFTER
import java.time.LocalDateTime;
private LocalDateTime createdAt;
```

### Step 6: Add Lombok Annotations (Recommended)

```java
@Data                 // Getter/Setter/toString/equals/hashCode
@Builder             // Builder pattern
@AllArgsConstructor   // All args constructor
@NoArgsConstructor    // No args constructor
@Document(collection = "alerts")
public class Alert {
    // fields...
}
```

---

## Phase 2: Repository Migration (JpaRepository → MongoRepository)

### Step 1: Update Repository Interface

```java
// BEFORE (PostgreSQL/JPA)
package com.gogidix.central.xxx.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    Optional<Alert> findByAlertId(String alertId);

    List<Alert> findByTenantIdAndStatus(String tenantId, AlertStatus status);

    @Query("SELECT a FROM Alert a WHERE a.tenantId = :tenantId AND a.severity = :severity")
    List<Alert> findByTenantIdAndSeverity(@Param("tenantId") String tenantId,
                                           @Param("severity") String severity);

    boolean existsByAlertId(String alertId);
}

// AFTER (MongoDB)
package com.gogidix.rapidassist.xxx.domain.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends MongoRepository<Alert, String> {

    Optional<Alert> findByAlertId(String alertId);

    List<Alert> findByTenantIdAndStatus(String tenantId, String status);

    @Query("{ 'tenantId': ?0, 'severity': ?1 }")
    List<Alert> findByTenantIdAndSeverity(String tenantId, String severity);

    @Query(value = "{ 'location.coordinates': { $near: { $geometry: { type: 'Point', coordinates: [ ?0, ?1 ] }, $maxDistance: ?2 } } }",
           count = true)
    long countByLocationNear(double longitude, double latitude, double maxDistance);

    boolean existsByAlertId(String alertId);
}
```

### Step 2: Update Generic Types

| Change | Before | After |
|--------|---------|-------|
| Repository Interface | `JpaRepository<Entity, Long>` | `MongoRepository<Entity, String>` |
| ID Type | `Long id` | `String id` (ObjectId) |

### Step 3: Query Method Mappings

| JPA Query Method | MongoDB Query Method |
|------------------|---------------------|
| `findByTenantIdAndStatus` | `findByTenantIdAndStatus` |
| `countByTenantId` | `countByTenantId` |
| `deleteByAlertId` | `deleteByAlertId` |
| `existsByAlertId` | `existsByAlertId` |

### Step 4: Custom Queries

```java
// BEFORE (JPQL)
@Query("SELECT a FROM Alert a WHERE a.tenantId = :tenantId AND a.createdAt BETWEEN :start AND :end")
List<Alert> findByTenantIdAndDateRange(@Param("tenantId") String tenantId,
                                       @Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end);

// AFTER (MongoDB JSON query)
@Query("{ 'tenantId': ?0, 'createdAt': { $gte: ?1, $lte: ?2 } }")
List<Alert> findByTenantIdAndDateRange(String tenantId, LocalDateTime start, LocalDateTime end);

// OR use MongoDB query syntax
@Query("{ 'tenantId': :tenantId, 'severity': { $in: :severities } }")
List<Alert> findByTenantIdAndSeverities(@Param("tenantId") String tenantId,
                                        @Param("severities") List<String> severities);
```

### Step 5: Geospatial Queries

```java
// MongoDB-specific geospatial queries
@Query("{ 'location.coordinates': { $near: { $geometry: { type: 'Point', coordinates: [ ?0, ?1 ] }, $maxDistance: ?2 } } }")
List<Alert> findByLocationNear(double longitude, double latitude, double maxDistance);

@Query("{ 'location.coordinates': { $geoWithin: { $geometry: { type: 'Polygon', coordinates: [ [ [ ?0, ?1 ], [ ?2, ?3 ], [ ?4, ?5 ], [ ?6, ?7 ], [ ?0, ?1 ] ] ] } } } }")
List<Alert> findByLocationWithinPolygon(double[][] polygon);

// Using repository method naming
List<Alert> findByLocationCoordinatesNear(Point point, Distance distance);
```

---

## Phase 3: MongoDB Configuration

### Enable MongoDB Auditing

```java
@Configuration
@EnableMongoAuditing(auditorAwareRef = "mongoAuditorAware")
public class MongoConfiguration {

    @Bean
    public MongoAuditorAware mongoAuditorAware() {
        return new MongoAuditorAware();
    }

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongoClient(), "rapid_assist_xxx_service");
    }
}

// AuditorAware implementation
public class MongoAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Return current user ID from security context
        return Optional.ofNullable(SecurityContextHolder.getContext()
                .getAuthentication()
                .getName());
    }
}
```

---

## Phase 4: Testing & Verification

### Unit Test Updates

```java
// BEFORE
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AlertRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AlertRepository alertRepository;

    @Test
    void testFindByAlertId() {
        Alert alert = new Alert();
        alert.setAlertId("test-id");
        entityManager.persist(alert);

        Optional<Alert> found = alertRepository.findByAlertId("test-id");
        assertTrue(found.isPresent());
    }
}

// AFTER
@DataMongoTest
class AlertRepositoryTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AlertRepository alertRepository;

    @Test
    void testFindByAlertId() {
        Alert alert = Alert.builder()
                .alertId("test-id")
                .tenantId("default")
                .severity("HIGH")
                .build();
        alertRepository.save(alert);

        Optional<Alert> found = alertRepository.findByAlertId("test-id");
        assertTrue(found.isPresent());
    }
}
```

---

## Phase 5: Service-Specific Migration Checklist

### For Each Service:

1. **Alerting Service**
   - [ ] Alert aggregate → @Document
   - [ ] AlertRule aggregate → @Document
   - [ ] AlertTemplate aggregate → @Document
   - [ ] NotificationHistory aggregate → @Document
   - [ ] Update all repositories

2. **Dispatching Service**
   - [ ] DispatchRequest aggregate → @Document
   - [ ] DispatchAssignment aggregate → @Document
   - [ ] DispatchRoute aggregate → @Document
   - [ ] ServiceProvider aggregate → @Document
   - [ ] Update all repositories

3. **Fleet Assistance Service**
   - [ ] AssistanceRequest aggregate → @Document
   - [ ] AssistanceAssignment aggregate → @Document
   - [ ] FleetAlert aggregate → @Document
   - [ ] Update all repositories

4. **Fleet Organization Service**
   - [ ] Fleet aggregate → @Document
   - [ ] FleetGroup aggregate → @Document
   - [ ] FleetMember aggregate → @Document
   - [ ] OrganizationUnit aggregate → @Document
   - [ ] Update all repositories

5. **Fleet Policy Service**
   - [ ] Policy aggregate → @Document
   - [ ] PolicyRule aggregate → @Document
   - [ ] PolicyViolation aggregate → @Document
   - [ ] PolicyCompliance aggregate → @Document
   - [ ] Update all repositories

6. **Fleet Vehicles Service**
   - [ ] Vehicle aggregate → @Document
   - [ ] VehicleMaintenance aggregate → @Document
   - [ ] VehicleTelemetry aggregate → @Document
   - [ ] VehicleLocation aggregate → @Document
   - [ ] VehicleDocument aggregate → @Document
   - [ ] Update all repositories

7. **Location Service**
   - [ ] Location aggregate → @Document
   - [ ] Geofence aggregate → @Document
   - [ ] LocationHistory aggregate → @Document
   - [ ] POI aggregate → @Document
   - [ ] Update all repositories

8. **Matching Service**
   - [ ] MatchRequest aggregate → @Document
   - [ ] MatchResult aggregate → @Document
   - [ ] MatchingRule aggregate → @Document
   - [ ] ProviderAvailability aggregate → @Document
   - [ ] Update all repositories

9. **Monitoring Service**
   - [ ] MonitoringTarget aggregate → @Document
   - [ ] Metric aggregate → @Document
   - [ ] HealthCheck aggregate → @Document
   - [ ] ThresholdAlert aggregate → @Document
   - [ ] Update all repositories

10. **Reporting Service**
    - [ ] Report aggregate → @Document
    - [ ] ReportTemplate aggregate → @Document
    - [ ] ReportSchedule aggregate → @Document
    - [ ] ExportHistory aggregate → @Document
    - [ ] Update all repositories

11. **Transaction Orchestration Service**
    - [ ] Saga aggregate → @Document
    - [ ] SagaStep aggregate → @Document
    - [ ] TransactionLog aggregate → @Document
    - [ ] CompensationAction aggregate → @Document
    - [ ] Update all repositories

---

## Common Pitfalls & Solutions

### Pitfall 1: ObjectId vs String ID
**Problem:** Using Long instead of String for IDs
**Solution:** Always use `String` for MongoDB ObjectId

### Pitfall 2: Missing @Indexed Annotations
**Problem:** Queries are slow without indexes
**Solution:** Add `@Indexed` to frequently queried fields

### Pitfall 3: Deep @DBRef Chains
**Problem:** Performance issues with multiple references
**Solution:** Consider embedding documents instead

### Pitfall 4: Not Handling Schema Changes
**Problem:** MongoDB schema-less nature causes issues
**Solution:** Use `@Document` validation and consider schema versioning

### Pitfall 5: Date/Time Inconsistency
**Problem:** Timezone issues with dates
**Solution:** Always use `LocalDateTime` and store in UTC

---

## Validation Checklist

Before deploying to production:

- [ ] All domain models updated with @Document
- [ ] All repositories extend MongoRepository
- [ ] All ID types changed from Long to String
- [ ] All @Query annotations updated to MongoDB syntax
- [ ] Indexes defined for all query fields
- [ ] Geospatial indexes configured for location services
- [ ] Auditing enabled for createdAt/updatedAt
- [ ] Unit tests updated and passing
- [ ] Integration tests updated and passing
- [ ] Performance tested with realistic data volumes

---

*MongoDB Migration Guide - Foundation-Domain Orchestration Services*
*Rapid Assist Platform*
