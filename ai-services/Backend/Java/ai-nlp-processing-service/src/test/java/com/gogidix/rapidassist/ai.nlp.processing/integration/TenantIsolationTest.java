package com.gogidix.rapidassist.ai.nlp.processing.integration;

import com.gogidix.rapidassist.ai.nlp.processing.application.service.NLPProcessingService;
import com.gogidix.rapidassist.ai.nlp.processing.domain.model.TextProcessing;
import com.gogidix.rapidassist.ai.nlp.processing.domain.port.out.TextProcessingRepositoryPort;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * CRITICAL TEST: Verify tenant isolation
 * MUST pass for ALL services before production
 */
@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Tenant Isolation Tests")
public class TenantIsolationTest {

    @Autowired
    private TextProcessingRepositoryPort textProcessingRepository;

    @Autowired
    private NLPProcessingService nlpProcessingService;

    private RequestContext tenantA;
    private RequestContext tenantB;

    @BeforeEach
    void setUp() {
        // Create test tenant contexts
        tenantA = RequestContext.builder()
            .tenantId("tenant-a-test")
            .userId("user-a")
            .correlationId(UUID.randomUUID().toString())
            .requestId(UUID.randomUUID().toString())
            .build();

        tenantB = RequestContext.builder()
            .tenantId("tenant-b-test")
            .userId("user-b")
            .correlationId(UUID.randomUUID().toString())
            .requestId(UUID.randomUUID().toString())
            .build();

        // Clean up before each test
        cleanupTestData();
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.clear();
        cleanupTestData();
    }

    @Test
    @DisplayName("Tenant A cannot access Tenant B data")
    void whenTenantAQueries_shouldOnlySeeTenantAData() {
        // Given: Tenant A and Tenant B both have text processing records
        RequestContextHolder.set(tenantA);
        TextProcessing processingA = createTestTextProcessing("Test text from Tenant A");
        textProcessingRepository.save(processingA);

        RequestContextHolder.set(tenantB);
        TextProcessing processingB = createTestTextProcessing("Test text from Tenant B");
        textProcessingRepository.save(processingB);

        // When: Tenant A queries for all records
        RequestContextHolder.set(tenantA);
        List<TextProcessing> tenantAResults = textProcessingRepository.findByTenantId("tenant-a-test");

        // Then: Tenant A should ONLY see Tenant A's data
        assertThat(tenantAResults).hasSize(1);
        assertThat(tenantAResults.get(0).getTenantId()).isEqualTo("tenant-a-test");
        assertThat(tenantAResults).doesNotContain(processingB);

        // Cleanup
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("findById from different tenant returns empty")
    void findById_whenDifferentTenant_thenReturnEmpty() {
        // Given: Entity exists for Tenant A
        RequestContextHolder.set(tenantA);
        TextProcessing processing = createTestTextProcessing("Test text for findById");
        TextProcessing saved = textProcessingRepository.save(processing);
        UUID entityId = saved.getId();

        // When: Tenant B tries to access same entity
        RequestContextHolder.set(tenantB);
        var result = textProcessingRepository.findById("tenant-b-test", entityId);

        // Then: Result should be empty
        assertThat(result).isEmpty();

        // Verify Tenant A can still access
        RequestContextHolder.set(tenantA);
        var tenantAResult = textProcessingRepository.findById("tenant-a-test", entityId);
        assertThat(tenantAResult).isPresent();
        assertThat(tenantAResult.get().getId()).isEqualTo(entityId);

        // Cleanup
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("findByStatus respects tenant isolation")
    void findByStatus_shouldOnlyReturnMatchingTenantRecords() {
        // Given: Both tenants have records with same status
        RequestContextHolder.set(tenantA);
        TextProcessing processingA1 = createTestTextProcessing("Tenant A completed 1");
        processingA1.setStatus(TextProcessing.ProcessingStatus.COMPLETED);
        textProcessingRepository.save(processingA1);

        TextProcessing processingA2 = createTestTextProcessing("Tenant A completed 2");
        processingA2.setStatus(TextProcessing.ProcessingStatus.COMPLETED);
        textProcessingRepository.save(processingA2);

        RequestContextHolder.set(tenantB);
        TextProcessing processingB = createTestTextProcessing("Tenant B completed");
        processingB.setStatus(TextProcessing.ProcessingStatus.COMPLETED);
        textProcessingRepository.save(processingB);

        // When: Tenant A queries for completed records
        RequestContextHolder.set(tenantA);
        List<TextProcessing> tenantACompleted = textProcessingRepository.findByTenantIdAndStatus(
            "tenant-a-test",
            TextProcessing.ProcessingStatus.COMPLETED
        );

        // Then: Only Tenant A's completed records should be returned
        assertThat(tenantACompleted).hasSize(2);
        assertThat(tenantACompleted)
            .allMatch(p -> p.getTenantId().equals("tenant-a-test"));

        // Cleanup
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("Delete respects tenant isolation")
    void delete_shouldOnlyDeleteFromCorrectTenant() {
        // Given: Same entity ID in both tenants
        UUID entityId = UUID.randomUUID();

        RequestContextHolder.set(tenantA);
        TextProcessing processingA = createTestTextProcessingWithId(entityId, "Tenant A delete test");
        textProcessingRepository.save(processingA);

        RequestContextHolder.set(tenantB);
        TextProcessing processingB = createTestTextProcessingWithId(entityId, "Tenant B delete test");
        textProcessingRepository.save(processingB);

        // When: Tenant A deletes entity
        RequestContextHolder.set(tenantA);
        textProcessingRepository.deleteById("tenant-a-test", entityId);

        // Then: Tenant A's entity should be deleted, Tenant B's should remain
        RequestContextHolder.set(tenantA);
        var tenantAResult = textProcessingRepository.findById("tenant-a-test", entityId);
        assertThat(tenantAResult).isEmpty();

        RequestContextHolder.set(tenantB);
        var tenantBResult = textProcessingRepository.findById("tenant-b-test", entityId);
        assertThat(tenantBResult).isPresent();

        // Cleanup
        RequestContextHolder.clear();
    }

    @Test
    @DisplayName("Update cannot affect different tenant data")
    void update_whenDifferentTenant_shouldNotAffectOtherTenant() {
        // Given: Both tenants have entities
        UUID entityId = UUID.randomUUID();
        String originalText = "Original text";

        RequestContextHolder.set(tenantA);
        TextProcessing processingA = createTestTextProcessingWithId(entityId, originalText);
        textProcessingRepository.save(processingA);

        RequestContextHolder.set(tenantB);
        TextProcessing processingB = createTestTextProcessingWithId(entityId, originalText);
        textProcessingRepository.save(processingB);

        // When: Tenant A updates entity
        RequestContextHolder.set(tenantA);
        var updateResult = textProcessingRepository.findById("tenant-a-test", entityId);
        assertThat(updateResult).isPresent();

        TextProcessing updated = updateResult.get();
        updated.setText("Updated by Tenant A");
        textProcessingRepository.save(updated);

        // Then: Tenant B's data should remain unchanged
        RequestContextHolder.set(tenantB);
        var tenantBResult = textProcessingRepository.findById("tenant-b-test", entityId);
        assertThat(tenantBResult).isPresent();
        assertThat(tenantBResult.get().getText()).isEqualTo(originalText);

        // Cleanup
        RequestContextHolder.clear();
    }

    // Helper methods

    private TextProcessing createTestTextProcessing(String text) {
        return createTestTextProcessingWithId(UUID.randomUUID(), text);
    }

    private TextProcessing createTestTextProcessingWithId(UUID id, String text) {
        return TextProcessing.builder()
            .id(id)
            .tenantId(RequestContextHolder.get().get().tenantId())
            .text(text)
            .processingType(TextProcessing.ProcessingType.TOKENIZATION)
            .language("en")
            .status(TextProcessing.ProcessingStatus.PENDING)
            .createdAt(java.time.LocalDateTime.now())
            .updatedAt(java.time.LocalDateTime.now())
            .createdBy("test-user")
            .build();
    }

    private void cleanupTestData() {
        try {
            RequestContextHolder.set(tenantA);
            textProcessingRepository.deleteAllByTenantId("tenant-a-test");

            RequestContextHolder.set(tenantB);
            textProcessingRepository.deleteAllByTenantId("tenant-b-test");

            RequestContextHolder.clear();
        } catch (Exception e) {
            // Ignore cleanup errors
        }
    }
}
