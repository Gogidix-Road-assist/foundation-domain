package com.gogidix.rapidassist.ai.computervision.integration;

import com.gogidix.rapidassist.ai.computervision.domain.model.AnalysisStatus;
import com.gogidix.rapidassist.ai.computervision.domain.model.ImageAnalysis;
import com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.entity.ImageAnalysisEntity;
import com.gogidix.rapidassist.ai.computervision.infrastructure.persistence.repository.ImageAnalysisMongoRepository;
import com.gogidix.rapidassist.ai.computervision.infrastructure.tenant.RequestContextHolder;
import com.gogidix.rapidassist.ai.computervision.infrastructure.tenant.TenantContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CRITICAL TEST: Verify tenant isolation for AI Computer Vision Service
 * MUST pass for ALL services before production
 */
@SpringBootTest
@ActiveProfiles("test")
public class TenantIsolationTest {

    @Autowired
    private ImageAnalysisMongoRepository repository;

    private TenantContext tenantA;
    private TenantContext tenantB;

    @BeforeEach
    void setUp() {
        // Clean up before each test
        repository.deleteAll();

        // Set up test tenants
        tenantA = TenantContext.builder()
                .tenantId("tenant-a")
                .userId("user-a")
                .correlationId(UUID.randomUUID().toString())
                .build();

        tenantB = TenantContext.builder()
                .tenantId("tenant-b")
                .userId("user-b")
                .correlationId(UUID.randomUUID().toString())
                .build();
    }

    @Test
    @DisplayName("Tenant A cannot access Tenant B data")
    void whenTenantAQueries_shouldOnlySeeTenantAData() {
        // Given: Create entities for both tenants
        RequestContextHolder.set(tenantA);
        ImageAnalysisEntity entityA = createAnalysis("tenant-a", "Analysis A-1");
        entityA = repository.save(entityA);

        RequestContextHolder.set(tenantB);
        ImageAnalysisEntity entityB = createAnalysis("tenant-b", "Analysis B-1");
        entityB = repository.save(entityB);

        // Then: Tenant A should NOT see Tenant B's data
        RequestContextHolder.set(tenantA);
        List<ImageAnalysisEntity> tenantAResults = repository.findByTenantId("tenant-a");

        assertThat(tenantAResults).hasSize(1);
        assertThat(tenantAResults.get(0).getTenantId()).isEqualTo("tenant-a");
        assertThat(tenantAResults.get(0).getAnalysisType()).isEqualTo("Analysis A-1");

        // Verify tenant B's data is not in results
        assertThat(tenantAResults)
                .noneMatch(entity -> "Analysis B-1".equals(entity.getAnalysisType()));

        // Cleanup
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("findById from different tenant returns empty")
    void findById_whenDifferentTenant_thenReturnEmpty() {
        // Given: Entity exists for Tenant A
        RequestContextHolder.set(tenantA);
        ImageAnalysisEntity entity = createAnalysis("tenant-a", "Analysis A-1");
        entity = repository.save(entity);
        UUID entityId = entity.getUuid();

        // When: Tenant B tries to access same entity
        RequestContextHolder.set(tenantB);
        var result = repository.findByTenantIdAndUuid("tenant-b", entityId);

        // Then: Result should be empty
        assertThat(result).isEmpty();

        // Verify the entity exists for tenant A
        RequestContextHolder.set(tenantA);
        var tenantAResult = repository.findByTenantIdAndUuid("tenant-a", entityId);
        assertThat(tenantAResult).isPresent();

        // Cleanup
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("Delete by UUID only affects current tenant")
    void deleteByUuid_whenCalledOnlyAffectsCurrentTenant() {
        // Given: Entities with same UUID for different tenants
        UUID sharedUuid = UUID.randomUUID();

        RequestContextHolder.set(tenantA);
        ImageAnalysisEntity entityA = createAnalysisWithUuid("tenant-a", sharedUuid, "Analysis A");
        repository.save(entityA);

        RequestContextHolder.set(tenantB);
        ImageAnalysisEntity entityB = createAnalysisWithUuid("tenant-b", sharedUuid, "Analysis B");
        repository.save(entityB);

        // When: Delete from tenant A
        RequestContextHolder.set(tenantA);
        repository.deleteByTenantIdAndUuid("tenant-a", sharedUuid);

        // Then: Tenant A's entity should be deleted
        var tenantAResult = repository.findByTenantIdAndUuid("tenant-a", sharedUuid);
        assertThat(tenantAResult).isEmpty();

        // Tenant B's entity should still exist
        RequestContextHolder.set(tenantB);
        var tenantBResult = repository.findByTenantIdAndUuid("tenant-b", sharedUuid);
        assertThat(tenantBResult).isPresent();

        // Cleanup
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("Count operations are tenant-isolated")
    void countOperations_whenCalled_shouldBeTenantIsolated() {
        // Given: Multiple entities for both tenants
        RequestContextHolder.set(tenantA);
        repository.save(createAnalysis("tenant-a", "A-1"));
        repository.save(createAnalysis("tenant-a", "A-2"));
        repository.save(createAnalysis("tenant-a", "A-3"));

        RequestContextHolder.set(tenantB);
        repository.save(createAnalysis("tenant-b", "B-1"));
        repository.save(createAnalysis("tenant-b", "B-2"));

        // When: Count for each tenant
        long countA = repository.countByTenantId("tenant-a");
        long countB = repository.countByTenantId("tenant-b");

        // Then: Counts should be isolated
        assertThat(countA).isEqualTo(3);
        assertThat(countB).isEqualTo(2);

        // Cleanup
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("Exists operations are tenant-isolated")
    void existsOperations_whenCalled_shouldBeTenantIsolated() {
        // Given: Entity exists for Tenant A
        RequestContextHolder.set(tenantA);
        ImageAnalysisEntity entity = createAnalysis("tenant-a", "Analysis A-1");
        entity = repository.save(entity);
        UUID entityId = entity.getUuid();

        // When: Check existence for both tenants
        boolean existsForA = repository.existsByTenantIdAndUuid("tenant-a", entityId);
        boolean existsForB = repository.existsByTenantIdAndUuid("tenant-b", entityId);

        // Then: Should only exist for tenant A
        assertThat(existsForA).isTrue();
        assertThat(existsForB).isFalse();

        // Cleanup
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("Query by status is tenant-isolated")
    void queryByStatus_whenCalled_shouldBeTenantIsolated() {
        // Given: Entities with different statuses for both tenants
        RequestContextHolder.set(tenantA);
        repository.save(createAnalysisWithStatus("tenant-a", AnalysisStatus.COMPLETED));
        repository.save(createAnalysisWithStatus("tenant-a", AnalysisStatus.PENDING));

        RequestContextHolder.set(tenantB);
        repository.save(createAnalysisWithStatus("tenant-b", AnalysisStatus.COMPLETED));
        repository.save(createAnalysisWithStatus("tenant-b", AnalysisStatus.FAILED));

        // When: Query completed analyses for each tenant
        RequestContextHolder.set(tenantA);
        List<ImageAnalysisEntity> completedA = repository.findByTenantIdAndStatus("tenant-a", "COMPLETED");

        RequestContextHolder.set(tenantB);
        List<ImageAnalysisEntity> completedB = repository.findByTenantIdAndStatus("tenant-b", "COMPLETED");

        // Then: Each tenant should only see their completed analyses
        assertThat(completedA).hasSize(1);
        assertThat(completedB).hasSize(1);
        assertThat(completedA)
                .noneMatch(entity -> "tenant-b".equals(entity.getTenantId()));
        assertThat(completedB)
                .noneMatch(entity -> "tenant-a".equals(entity.getTenantId()));

        // Cleanup
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("Query by analysis type is tenant-isolated")
    void queryByAnalysisType_whenCalled_shouldBeTenantIsolated() {
        // Given: Entities with different types for both tenants
        RequestContextHolder.set(tenantA);
        repository.save(createAnalysis("tenant-a", "OBJECT_DETECTION"));
        repository.save(createAnalysis("tenant-a", "FACE_DETECTION"));

        RequestContextHolder.set(tenantB);
        repository.save(createAnalysis("tenant-b", "OBJECT_DETECTION"));
        repository.save(createAnalysis("tenant-b", "TEXT_RECOGNITION"));

        // When: Query by type for each tenant
        RequestContextHolder.set(tenantA);
        List<ImageAnalysisEntity> objectDetectionA =
                repository.findByTenantIdAndAnalysisType("tenant-a", "OBJECT_DETECTION");

        RequestContextHolder.set(tenantB);
        List<ImageAnalysisEntity> objectDetectionB =
                repository.findByTenantIdAndAnalysisType("tenant-b", "OBJECT_DETECTION");

        // Then: Each tenant should only see their object detection analyses
        assertThat(objectDetectionA).hasSize(1);
        assertThat(objectDetectionB).hasSize(1);
        assertThat(objectDetectionA)
                .allMatch(entity -> "tenant-a".equals(entity.getTenantId()));
        assertThat(objectDetectionB)
                .allMatch(entity -> "tenant-b".equals(entity.getTenantId()));

        // Cleanup
        RequestContextHolder.clear();
    }

    // Helper methods

    private ImageAnalysisEntity createAnalysis(String tenantId, String analysisType) {
        return createAnalysisWithUuid(tenantId, UUID.randomUUID(), analysisType);
    }

    private ImageAnalysisEntity createAnalysisWithUuid(String tenantId, UUID uuid, String analysisType) {
        ImageAnalysisEntity entity = new ImageAnalysisEntity();
        entity.setUuid(uuid);
        entity.setTenantId(tenantId);
        entity.setAnalysisType(analysisType);
        entity.setStatus(AnalysisStatus.PENDING);
        entity.setFormat(com.gogidix.rapidassist.ai.computervision.domain.model.ImageFormat.JPEG);
        entity.setCreatedAt(java.time.LocalDateTime.now());
        entity.setUpdatedAt(java.time.LocalDateTime.now());
        entity.setVersion(1L);
        return entity;
    }

    private ImageAnalysisEntity createAnalysisWithStatus(String tenantId, AnalysisStatus status) {
        ImageAnalysisEntity entity = new ImageAnalysisEntity();
        entity.setUuid(UUID.randomUUID());
        entity.setTenantId(tenantId);
        entity.setAnalysisType("TEST_ANALYSIS");
        entity.setStatus(status);
        entity.setFormat(com.gogidix.rapidassist.ai.computervision.domain.model.ImageFormat.JPEG);
        entity.setCreatedAt(java.time.LocalDateTime.now());
        entity.setUpdatedAt(java.time.LocalDateTime.now());
        entity.setVersion(1L);
        return entity;
    }
}
