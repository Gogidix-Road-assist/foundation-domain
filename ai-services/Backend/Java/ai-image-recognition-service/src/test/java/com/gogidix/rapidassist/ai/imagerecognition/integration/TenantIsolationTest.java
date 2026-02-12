package com.gogidix.rapidassist.ai.imagerecognition.integration;

import com.gogidix.rapidassist.ai.imagerecognition.domain.aggregate.ImageRecognition;
import com.gogidix.rapidassist.ai.imagerecognition.domain.model.RecognitionStatus;
import com.gogidix.rapidassist.ai.imagerecognition.domain.model.RecognitionType;
import com.gogidix.rapidassist.ai.imagerecognition.domain.repository.ImageRecognitionRepositoryPort;
import com.gogidix.rapidassist.ai.imagerecognition.infrastructure.tenant.RequestContextHolder;
import com.gogidix.rapidassist.ai.imagerecognition.infrastructure.tenant.TenantContext;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CRITICAL TEST: Verify tenant isolation.
 * MUST pass for ALL services before production.
 */
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Tenant Isolation Tests")
public class TenantIsolationTest {

    @Autowired
    private ImageRecognitionRepositoryPort repository;

    @AfterEach
    void cleanup() {
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("Tenant A cannot access Tenant B data")
    void whenTenantAQueries_shouldOnlySeeTenantAData() {
        // Given: Tenant A and Tenant B both have entities
        TenantContext tenantA = TenantContext.builder()
                .tenantId("tenant-a")
                .userId("user-a")
                .correlationId(UUID.randomUUID().toString())
                .build();

        TenantContext tenantB = TenantContext.builder()
                .tenantId("tenant-b")
                .userId("user-b")
                .correlationId(UUID.randomUUID().toString())
                .build();

        // When: Create entities for both tenants
        RequestContextHolder.set(tenantA);
        ImageRecognition entityA = repository.save(
                "tenant-a",
                ImageRecognition.initialize("tenant-a", "user-a", "https://example.com/image-a.jpg", RecognitionType.OBJECT_DETECTION)
        );

        RequestContextHolder.set(tenantB);
        ImageRecognition entityB = repository.save(
                "tenant-b",
                ImageRecognition.initialize("tenant-b", "user-b", "https://example.com/image-b.jpg", RecognitionType.OBJECT_DETECTION)
        );

        // Then: Tenant A should NOT see Tenant B's data
        RequestContextHolder.set(tenantA);
        List<ImageRecognition> tenantAResults = repository.findByTenantId("tenant-a");

        assertThat(tenantAResults).hasSize(1);
        assertThat(tenantAResults.get(0).getTenantId()).isEqualTo("tenant-a");
        assertThat(tenantAResults).doesNotContain(entityB);

        // And: Tenant B should NOT see Tenant A's data
        RequestContextHolder.set(tenantB);
        List<ImageRecognition> tenantBResults = repository.findByTenantId("tenant-b");

        assertThat(tenantBResults).hasSize(1);
        assertThat(tenantBResults.get(0).getTenantId()).isEqualTo("tenant-b");
        assertThat(tenantBResults).doesNotContain(entityA);
    }

    @Test
    @DisplayName("findById from different tenant returns empty")
    void findById_whenDifferentTenant_thenReturnEmpty() {
        // Given: Entity exists for Tenant A
        TenantContext tenantA = TenantContext.builder()
                .tenantId("tenant-a")
                .userId("user-a")
                .correlationId(UUID.randomUUID().toString())
                .build();

        RequestContextHolder.set(tenantA);
        ImageRecognition entity = repository.save(
                "tenant-a",
                ImageRecognition.initialize("tenant-a", "user-a", "https://example.com/image.jpg", RecognitionType.OBJECT_DETECTION)
        );
        UUID entityId = entity.getId();

        // When: Tenant B tries to access same entity
        TenantContext tenantB = TenantContext.builder()
                .tenantId("tenant-b")
                .userId("user-b")
                .correlationId(UUID.randomUUID().toString())
                .build();

        RequestContextHolder.set(tenantB);
        var result = repository.findById("tenant-b", entityId);

        // Then: Result should be empty
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByRequestId from different tenant returns empty")
    void findByRequestId_whenDifferentTenant_thenReturnEmpty() {
        // Given: Entity exists for Tenant A
        TenantContext tenantA = TenantContext.builder()
                .tenantId("tenant-a")
                .userId("user-a")
                .correlationId(UUID.randomUUID().toString())
                .build();

        RequestContextHolder.set(tenantA);
        ImageRecognition entity = repository.save(
                "tenant-a",
                ImageRecognition.initialize("tenant-a", "user-a", "https://example.com/image.jpg", RecognitionType.OBJECT_DETECTION)
        );
        String requestId = entity.getRequestId();

        // When: Tenant B tries to access same entity by request ID
        TenantContext tenantB = TenantContext.builder()
                .tenantId("tenant-b")
                .userId("user-b")
                .correlationId(UUID.randomUUID().toString())
                .build();

        RequestContextHolder.set(tenantB);
        var result = repository.findByRequestId("tenant-b", requestId);

        // Then: Result should be empty
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("delete should only affect current tenant")
    void delete_whenDifferentTenant_shouldOnlyDeleteCurrentTenantEntity() {
        // Given: Both tenants have entities with same UUID
        UUID entityId = UUID.randomUUID();

        TenantContext tenantA = TenantContext.builder()
                .tenantId("tenant-a")
                .userId("user-a")
                .correlationId(UUID.randomUUID().toString())
                .build();

        TenantContext tenantB = TenantContext.builder()
                .tenantId("tenant-b")
                .userId("user-b")
                .correlationId(UUID.randomUUID().toString())
                .build();

        // When: Create entities for both tenants
        RequestContextHolder.set(tenantA);
        ImageRecognition entityA = ImageRecognition.builder()
                .id(entityId)
                .tenantId("tenant-a")
                .userId("user-a")
                .requestId(UUID.randomUUID().toString())
                .imageUrl("https://example.com/image-a.jpg")
                .status(RecognitionStatus.PENDING)
                .recognitionType(RecognitionType.OBJECT_DETECTION)
                .build();
        repository.save("tenant-a", entityA);

        RequestContextHolder.set(tenantB);
        ImageRecognition entityB = ImageRecognition.builder()
                .id(entityId)
                .tenantId("tenant-b")
                .userId("user-b")
                .requestId(UUID.randomUUID().toString())
                .imageUrl("https://example.com/image-b.jpg")
                .status(RecognitionStatus.PENDING)
                .recognitionType(RecognitionType.OBJECT_DETECTION)
                .build();
        repository.save("tenant-b", entityB);

        // When: Tenant B deletes their entity
        repository.delete("tenant-b", entityId);

        // Then: Tenant B should not have any entities
        List<ImageRecognition> tenantBResults = repository.findByTenantId("tenant-b");
        assertThat(tenantBResults).isEmpty();

        // And: Tenant A should still have their entity
        RequestContextHolder.set(tenantA);
        List<ImageRecognition> tenantAResults = repository.findByTenantId("tenant-a");
        assertThat(tenantAResults).hasSize(1);
        assertThat(tenantAResults.get(0).getId()).isEqualTo(entityId);
    }

    @Test
    @DisplayName("countByTenantId should only count current tenant")
    void countByTenantId_shouldOnlyCountCurrentTenant() {
        // Given: Multiple tenants have entities
        TenantContext tenantA = TenantContext.builder()
                .tenantId("tenant-a")
                .userId("user-a")
                .correlationId(UUID.randomUUID().toString())
                .build();

        TenantContext tenantB = TenantContext.builder()
                .tenantId("tenant-b")
                .userId("user-b")
                .correlationId(UUID.randomUUID().toString())
                .build();

        // When: Create entities for both tenants
        RequestContextHolder.set(tenantA);
        repository.save(
                "tenant-a",
                ImageRecognition.initialize("tenant-a", "user-a", "https://example.com/image1.jpg", RecognitionType.OBJECT_DETECTION)
        );
        repository.save(
                "tenant-a",
                ImageRecognition.initialize("tenant-a", "user-a", "https://example.com/image2.jpg", RecognitionType.OBJECT_DETECTION)
        );

        RequestContextHolder.set(tenantB);
        repository.save(
                "tenant-b",
                ImageRecognition.initialize("tenant-b", "user-b", "https://example.com/image3.jpg", RecognitionType.OBJECT_DETECTION)
        );

        // Then: Count should be correct for each tenant
        long tenantACount = repository.countByTenantId("tenant-a");
        assertThat(tenantACount).isEqualTo(2);

        long tenantBCount = repository.countByTenantId("tenant-b");
        assertThat(tenantBCount).isEqualTo(1);
    }

    @Test
    @DisplayName("findByUserId should only return entities for current tenant")
    void findByUserId_shouldOnlyReturnEntitiesForCurrentTenant() {
        // Given: Same user ID in different tenants
        TenantContext tenantA = TenantContext.builder()
                .tenantId("tenant-a")
                .userId("shared-user")
                .correlationId(UUID.randomUUID().toString())
                .build();

        TenantContext tenantB = TenantContext.builder()
                .tenantId("tenant-b")
                .userId("shared-user")
                .correlationId(UUID.randomUUID().toString())
                .build();

        // When: Create entities for both tenants with same user ID
        RequestContextHolder.set(tenantA);
        repository.save(
                "tenant-a",
                ImageRecognition.initialize("tenant-a", "shared-user", "https://example.com/image-a.jpg", RecognitionType.OBJECT_DETECTION)
        );

        RequestContextHolder.set(tenantB);
        repository.save(
                "tenant-b",
                ImageRecognition.initialize("tenant-b", "shared-user", "https://example.com/image-b.jpg", RecognitionType.OBJECT_DETECTION)
        );

        // Then: When querying by user ID in tenant A, should only get tenant A entities
        RequestContextHolder.set(tenantA);
        List<ImageRecognition> tenantAResults = repository.findByUserId("tenant-a", "shared-user");
        assertThat(tenantAResults).hasSize(1);
        assertThat(tenantAResults.get(0).getTenantId()).isEqualTo("tenant-a");

        // And: When querying by user ID in tenant B, should only get tenant B entities
        RequestContextHolder.set(tenantB);
        List<ImageRecognition> tenantBResults = repository.findByUserId("tenant-b", "shared-user");
        assertThat(tenantBResults).hasSize(1);
        assertThat(tenantBResults.get(0).getTenantId()).isEqualTo("tenant-b");
    }
}
