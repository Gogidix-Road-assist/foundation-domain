package com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudDetection;
import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRiskLevel;
import com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.entity.FraudDetectionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FraudDetectionRepositoryAdapter
 *
 * Tests the repository adapter methods including mapping between domain and entity.
 * Uses Mockito for Spring Data repository - no MongoDB required.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("FraudDetectionRepositoryAdapter Tests")
class FraudDetectionRepositoryAdapterTest {

    @Mock
    private SpringDataFraudDetectionRepository springDataRepository;

    private FraudDetectionRepositoryAdapter adapter;

    private static final String TENANT_ID = "tenant-123";
    private static final UUID TEST_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new FraudDetectionRepositoryAdapter(springDataRepository);
    }

    // ==================== save() tests ====================

    @Test
    @DisplayName("save - Creates new detection with generated ID")
    void testSave_CreatesNewDetection_WithGeneratedId() {
        FraudDetection detection = FraudDetection.builder()
                .tenantId(TENANT_ID)
                .entityType("CLAIM")
                .entityId("claim-123")
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.85)
                .detectionMethod("ML_MODEL")
                .status("PENDING")
                .requiresReview(true)
                .build();

        FraudDetectionEntity savedEntity = FraudDetectionEntity.builder()
                .id(UUID.randomUUID())
                .tenantId(TENANT_ID)
                .entityType("CLAIM")
                .entityId("claim-123")
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.85)
                .detectionMethod("ML_MODEL")
                .status("PENDING")
                .requiresReview(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(springDataRepository.save(any(FraudDetectionEntity.class))).thenReturn(savedEntity);

        FraudDetection result = adapter.save(TENANT_ID, detection);

        assertNotNull(result);
        assertEquals(TENANT_ID, result.getTenantId());
        assertEquals("CLAIM", result.getEntityType());
        assertEquals(FraudRiskLevel.HIGH, result.getRiskLevel());
        verify(springDataRepository).save(any(FraudDetectionEntity.class));
    }

    @Test
    @DisplayName("save - Updates existing detection")
    void testSave_UpdatesExistingDetection() {
        LocalDateTime existingTime = LocalDateTime.now().minusDays(1);
        FraudDetection detection = FraudDetection.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .entityType("CLAIM")
                .entityId("claim-123")
                .status("PENDING")
                .createdAt(existingTime)
                .updatedAt(existingTime)
                .build();

        FraudDetectionEntity savedEntity = FraudDetectionEntity.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .entityType("CLAIM")
                .entityId("claim-123")
                .status("PENDING")
                .createdAt(existingTime)
                .updatedAt(LocalDateTime.now()) // Should be updated
                .build();

        when(springDataRepository.save(any(FraudDetectionEntity.class))).thenReturn(savedEntity);

        FraudDetection result = adapter.save(TENANT_ID, detection);

        assertNotNull(result);
        assertEquals(existingTime, result.getCreatedAt());
        assertNotEquals(existingTime, result.getUpdatedAt());
        verify(springDataRepository).save(any(FraudDetectionEntity.class));
    }

    @Test
    @DisplayName("save - Preserves all fields during mapping")
    void testSave_PreservesAllFields() {
        List<String> patterns = List.of("PATTERN1", "PATTERN2");
        Map<String, Object> details = Map.of("key", "value");
        Map<String, Object> metadata = Map.of("meta", "data");

        FraudDetection detection = FraudDetection.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .entityType("TRANSACTION")
                .entityId("txn-456")
                .riskLevel(FraudRiskLevel.CRITICAL)
                .riskScore(0.95)
                .detectionMethod("NEURAL_NETWORK")
                .detectionDetails(details)
                .detectedPatterns(patterns)
                .status("ESCALATED")
                .requiresReview(true)
                .assignedTo("admin")
                .reviewedBy("manager")
                .reviewNotes("Escalated to management")
                .metadata(metadata)
                .build();

        when(springDataRepository.save(any(FraudDetectionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FraudDetection result = adapter.save(TENANT_ID, detection);

        assertEquals(TEST_ID, result.getId());
        assertEquals(TENANT_ID, result.getTenantId());
        assertEquals("TRANSACTION", result.getEntityType());
        assertEquals("txn-456", result.getEntityId());
        assertEquals(FraudRiskLevel.CRITICAL, result.getRiskLevel());
        assertEquals(0.95, result.getRiskScore());
        assertEquals("NEURAL_NETWORK", result.getDetectionMethod());
        assertEquals(details, result.getDetectionDetails());
        assertEquals(patterns, result.getDetectedPatterns());
        assertEquals("ESCALATED", result.getStatus());
        assertTrue(result.getRequiresReview());
        assertEquals("admin", result.getAssignedTo());
        assertEquals("manager", result.getReviewedBy());
        assertEquals("Escalated to management", result.getReviewNotes());
        assertEquals(metadata, result.getMetadata());
    }

    // ==================== findById() tests ====================

    @Test
    @DisplayName("findById - Returns detection when found")
    void testFindById_ReturnsDetectionWhenFound() {
        FraudDetectionEntity entity = FraudDetectionEntity.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .entityType("CLAIM")
                .riskLevel(FraudRiskLevel.HIGH)
                .build();

        when(springDataRepository.findById(TEST_ID)).thenReturn(Optional.of(entity));

        Optional<FraudDetection> result = adapter.findById(TENANT_ID, TEST_ID);

        assertTrue(result.isPresent());
        assertEquals(TEST_ID, result.get().getId());
        assertEquals(TENANT_ID, result.get().getTenantId());
        assertEquals(FraudRiskLevel.HIGH, result.get().getRiskLevel());
        verify(springDataRepository).findById(TEST_ID);
    }

    @Test
    @DisplayName("findById - Returns empty when not found")
    void testFindById_ReturnsEmptyWhenNotFound() {
        when(springDataRepository.findById(TEST_ID)).thenReturn(Optional.empty());

        Optional<FraudDetection> result = adapter.findById(TENANT_ID, TEST_ID);

        assertFalse(result.isPresent());
        verify(springDataRepository).findById(TEST_ID);
    }

    @Test
    @DisplayName("findById - Returns empty for null ID")
    void testFindById_ReturnsEmptyForNullId() {
        Optional<FraudDetection> result = adapter.findById(TENANT_ID, null);

        assertFalse(result.isPresent());
        verify(springDataRepository, never()).findById(any());
    }

    // ==================== findByTenantId() tests ====================

    @Test
    @DisplayName("findByTenantId - Returns detections for tenant")
    void testFindByTenantId_ReturnsDetections() {
        FraudDetectionEntity entity1 = FraudDetectionEntity.builder()
                .id(UUID.randomUUID())
                .tenantId(TENANT_ID)
                .entityType("CLAIM")
                .build();

        FraudDetectionEntity entity2 = FraudDetectionEntity.builder()
                .id(UUID.randomUUID())
                .tenantId(TENANT_ID)
                .entityType("TRANSACTION")
                .build();

        when(springDataRepository.findByTenantId(TENANT_ID))
                .thenReturn(List.of(entity1, entity2));

        List<FraudDetection> result = adapter.findByTenantId(TENANT_ID);

        assertEquals(2, result.size());
        assertEquals("CLAIM", result.get(0).getEntityType());
        assertEquals("TRANSACTION", result.get(1).getEntityType());
        verify(springDataRepository).findByTenantId(TENANT_ID);
    }

    @Test
    @DisplayName("findByTenantId - Returns empty list for null tenant")
    void testFindByTenantId_ReturnsEmptyForNullTenant() {
        List<FraudDetection> result = adapter.findByTenantId(null);

        assertTrue(result.isEmpty());
        verify(springDataRepository, never()).findByTenantId(any());
    }

    @Test
    @DisplayName("findByTenantId - Returns empty list for empty tenant")
    void testFindByTenantId_ReturnsEmptyForEmptyTenant() {
        List<FraudDetection> result = adapter.findByTenantId("");

        assertTrue(result.isEmpty());
        verify(springDataRepository, never()).findByTenantId(any());
    }

    // ==================== findByEntityId() tests ====================

    @Test
    @DisplayName("findByEntityId - Returns detections for entity")
    void testFindByEntityId_ReturnsDetections() {
        FraudDetectionEntity entity = FraudDetectionEntity.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .entityId("claim-123")
                .build();

        when(springDataRepository.findByTenantIdAndEntityId(TENANT_ID, "claim-123"))
                .thenReturn(List.of(entity));

        List<FraudDetection> result = adapter.findByEntityId(TENANT_ID, "claim-123");

        assertEquals(1, result.size());
        assertEquals("claim-123", result.get(0).getEntityId());
        verify(springDataRepository).findByTenantIdAndEntityId(TENANT_ID, "claim-123");
    }

    // ==================== findByRiskLevel() tests ====================

    @Test
    @DisplayName("findByRiskLevel - Returns detections for valid risk level")
    void testFindByRiskLevel_ReturnsDetectionsForValidLevel() {
        FraudDetectionEntity entity = FraudDetectionEntity.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .riskLevel(FraudRiskLevel.HIGH)
                .build();

        when(springDataRepository.findByTenantIdAndRiskLevel(TENANT_ID, FraudRiskLevel.HIGH))
                .thenReturn(List.of(entity));

        List<FraudDetection> result = adapter.findByRiskLevel(TENANT_ID, "HIGH");

        assertEquals(1, result.size());
        assertEquals(FraudRiskLevel.HIGH, result.get(0).getRiskLevel());
        verify(springDataRepository).findByTenantIdAndRiskLevel(TENANT_ID, FraudRiskLevel.HIGH);
    }

    @Test
    @DisplayName("findByRiskLevel - Returns empty for invalid risk level")
    void testFindByRiskLevel_ReturnsEmptyForInvalidLevel() {
        List<FraudDetection> result = adapter.findByRiskLevel(TENANT_ID, "INVALID");

        assertTrue(result.isEmpty());
        verify(springDataRepository, never()).findByTenantIdAndRiskLevel(any(), any());
    }

    @Test
    @DisplayName("findByRiskLevel - Returns empty for null risk level")
    void testFindByRiskLevel_ReturnsEmptyForNullLevel() {
        List<FraudDetection> result = adapter.findByRiskLevel(TENANT_ID, null);

        assertTrue(result.isEmpty());
        verify(springDataRepository, never()).findByTenantIdAndRiskLevel(any(), any());
    }

    @Test
    @DisplayName("findByRiskLevel - Tests all enum values")
    void testFindByRiskLevel_AllEnumValues() {
        FraudRiskLevel[] levels = {FraudRiskLevel.LOW, FraudRiskLevel.MEDIUM,
                                   FraudRiskLevel.HIGH, FraudRiskLevel.CRITICAL};

        for (FraudRiskLevel level : levels) {
            when(springDataRepository.findByTenantIdAndRiskLevel(TENANT_ID, level))
                    .thenReturn(List.of());

            List<FraudDetection> result = adapter.findByRiskLevel(TENANT_ID, level.name());

            assertNotNull(result);
            verify(springDataRepository).findByTenantIdAndRiskLevel(TENANT_ID, level);
        }
    }

    // ==================== findByStatus() tests ====================

    @Test
    @DisplayName("findByStatus - Returns detections with matching status")
    void testFindByStatus_ReturnsDetections() {
        FraudDetectionEntity entity = FraudDetectionEntity.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .status("PENDING")
                .build();

        when(springDataRepository.findByTenantIdAndStatus(TENANT_ID, "PENDING"))
                .thenReturn(List.of(entity));

        List<FraudDetection> result = adapter.findByStatus(TENANT_ID, "PENDING");

        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
        verify(springDataRepository).findByTenantIdAndStatus(TENANT_ID, "PENDING");
    }

    // ==================== findPendingReview() tests ====================

    @Test
    @DisplayName("findPendingReview - Returns detections requiring review")
    void testFindPendingReview_ReturnsDetections() {
        FraudDetectionEntity entity = FraudDetectionEntity.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .requiresReview(true)
                .build();

        when(springDataRepository.findByTenantIdAndRequiresReviewTrue(TENANT_ID))
                .thenReturn(List.of(entity));

        List<FraudDetection> result = adapter.findPendingReview(TENANT_ID);

        assertEquals(1, result.size());
        assertTrue(result.get(0).getRequiresReview());
        verify(springDataRepository).findByTenantIdAndRequiresReviewTrue(TENANT_ID);
    }

    // ==================== delete() tests ====================

    @Test
    @DisplayName("delete - Deletes detection by ID")
    void testDelete_DeletesDetection() {
        adapter.delete(TENANT_ID, TEST_ID);

        verify(springDataRepository).deleteById(TEST_ID);
    }

    @Test
    @DisplayName("delete - No-op for null ID")
    void testDelete_NoOpForNullId() {
        adapter.delete(TENANT_ID, null);

        verify(springDataRepository, never()).deleteById(any());
    }

    // ==================== exists() tests ====================

    @Test
    @DisplayName("exists - Returns true when detection exists")
    void testExists_ReturnsTrueWhenExists() {
        when(springDataRepository.existsById(TEST_ID)).thenReturn(true);

        boolean result = adapter.exists(TENANT_ID, TEST_ID);

        assertTrue(result);
        verify(springDataRepository).existsById(TEST_ID);
    }

    @Test
    @DisplayName("exists - Returns false when detection not found")
    void testExists_ReturnsFalseWhenNotFound() {
        when(springDataRepository.existsById(TEST_ID)).thenReturn(false);

        boolean result = adapter.exists(TENANT_ID, TEST_ID);

        assertFalse(result);
        verify(springDataRepository).existsById(TEST_ID);
    }

    @Test
    @DisplayName("exists - Returns false for null ID")
    void testExists_ReturnsFalseForNullId() {
        boolean result = adapter.exists(TENANT_ID, null);

        assertFalse(result);
        verify(springDataRepository, never()).existsById(any());
    }

    // ==================== Mapping tests ====================

    @Test
    @DisplayName("toEntity - Maps all domain fields to entity")
    void testToEntity_MapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        List<String> patterns = List.of("PATTERN1");
        Map<String, Object> details = Map.of("confidence", 0.95);

        FraudDetection domain = FraudDetection.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .entityType("CLAIM")
                .entityId("claim-123")
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.85)
                .detectionMethod("ML_MODEL")
                .detectionDetails(details)
                .detectedPatterns(patterns)
                .status("PENDING")
                .requiresReview(true)
                .assignedTo("admin")
                .reviewedBy("manager")
                .reviewNotes("Reviewed")
                .detectedAt(now)
                .reviewedAt(now.plusHours(1))
                .createdAt(now.minusDays(1))
                .updatedAt(now)
                .build();

        // Mock save to return a copy of the entity
        when(springDataRepository.save(any(FraudDetectionEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        adapter.save(TENANT_ID, domain);

        verify(springDataRepository).save(argThat(entity -> {
            assertEquals(TEST_ID, entity.getId());
            assertEquals(TENANT_ID, entity.getTenantId());
            assertEquals("CLAIM", entity.getEntityType());
            assertEquals("claim-123", entity.getEntityId());
            assertEquals(FraudRiskLevel.HIGH, entity.getRiskLevel());
            assertEquals(0.85, entity.getRiskScore());
            assertEquals("ML_MODEL", entity.getDetectionMethod());
            assertEquals(details, entity.getDetectionDetails());
            assertEquals(patterns, entity.getDetectedPatterns());
            assertEquals("PENDING", entity.getStatus());
            assertTrue(entity.getRequiresReview());
            assertEquals("admin", entity.getAssignedTo());
            assertEquals("manager", entity.getReviewedBy());
            assertEquals("Reviewed", entity.getReviewNotes());
            return true;
        }));
    }

    @Test
    @DisplayName("toDomain - Maps all entity fields to domain")
    void testToDomain_MapsAllFields() {
        LocalDateTime now = LocalDateTime.now();
        List<String> patterns = List.of("PATTERN1");
        Map<String, Object> details = Map.of("confidence", 0.95);

        FraudDetectionEntity entity = FraudDetectionEntity.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .entityType("CLAIM")
                .entityId("claim-123")
                .riskLevel(FraudRiskLevel.HIGH)
                .riskScore(0.85)
                .detectionMethod("ML_MODEL")
                .detectionDetails(details)
                .detectedPatterns(patterns)
                .status("PENDING")
                .requiresReview(true)
                .assignedTo("admin")
                .reviewedBy("manager")
                .reviewNotes("Reviewed")
                .detectedAt(now)
                .reviewedAt(now.plusHours(1))
                .createdAt(now.minusDays(1))
                .updatedAt(now)
                .build();

        when(springDataRepository.findById(TEST_ID)).thenReturn(Optional.of(entity));

        Optional<FraudDetection> result = adapter.findById(TENANT_ID, TEST_ID);

        assertTrue(result.isPresent());
        assertEquals(TEST_ID, result.get().getId());
        assertEquals(TENANT_ID, result.get().getTenantId());
        assertEquals("CLAIM", result.get().getEntityType());
        assertEquals("claim-123", result.get().getEntityId());
        assertEquals(FraudRiskLevel.HIGH, result.get().getRiskLevel());
        assertEquals(0.85, result.get().getRiskScore());
        assertEquals("ML_MODEL", result.get().getDetectionMethod());
        assertEquals(details, result.get().getDetectionDetails());
        assertEquals(patterns, result.get().getDetectedPatterns());
        assertEquals("PENDING", result.get().getStatus());
        assertTrue(result.get().getRequiresReview());
        assertEquals("admin", result.get().getAssignedTo());
        assertEquals("manager", result.get().getReviewedBy());
        assertEquals("Reviewed", result.get().getReviewNotes());
    }

    // ==================== Risk level enum tests ====================

    @Test
    @DisplayName("Risk level enum - LOW maps correctly")
    void testRiskLevelEnum_LOW() {
        FraudDetectionEntity entity = FraudDetectionEntity.builder()
                .id(TEST_ID)
                .riskLevel(FraudRiskLevel.LOW)
                .build();

        when(springDataRepository.findById(TEST_ID)).thenReturn(Optional.of(entity));

        Optional<FraudDetection> result = adapter.findById(TENANT_ID, TEST_ID);

        assertTrue(result.isPresent());
        assertEquals(FraudRiskLevel.LOW, result.get().getRiskLevel());
    }

    @Test
    @DisplayName("Risk level enum - MEDIUM maps correctly")
    void testRiskLevelEnum_MEDIUM() {
        FraudDetectionEntity entity = FraudDetectionEntity.builder()
                .id(TEST_ID)
                .riskLevel(FraudRiskLevel.MEDIUM)
                .build();

        when(springDataRepository.findById(TEST_ID)).thenReturn(Optional.of(entity));

        Optional<FraudDetection> result = adapter.findById(TENANT_ID, TEST_ID);

        assertTrue(result.isPresent());
        assertEquals(FraudRiskLevel.MEDIUM, result.get().getRiskLevel());
    }

    @Test
    @DisplayName("Risk level enum - CRITICAL maps correctly")
    void testRiskLevelEnum_CRITICAL() {
        FraudDetectionEntity entity = FraudDetectionEntity.builder()
                .id(TEST_ID)
                .riskLevel(FraudRiskLevel.CRITICAL)
                .build();

        when(springDataRepository.findById(TEST_ID)).thenReturn(Optional.of(entity));

        Optional<FraudDetection> result = adapter.findById(TENANT_ID, TEST_ID);

        assertTrue(result.isPresent());
        assertEquals(FraudRiskLevel.CRITICAL, result.get().getRiskLevel());
    }

    // ==================== Null handling tests ====================

    @Test
    @DisplayName("Null handling - Domain with null optional fields")
    void testNullHandling_DomainWithNullOptionalFields() {
        FraudDetection detection = FraudDetection.builder()
                .tenantId(TENANT_ID)
                .entityType("CLAIM")
                .build();

        when(springDataRepository.save(any(FraudDetectionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        adapter.save(TENANT_ID, detection);

        verify(springDataRepository).save(argThat(entity -> {
            assertEquals(TENANT_ID, entity.getTenantId());
            assertEquals("CLAIM", entity.getEntityType());
            assertNotNull(entity.getId()); // Will be generated by toEntity builder
            return true;
        }));
    }

    // ==================== Collection tests ====================

    @Test
    @DisplayName("Collections - Empty patterns list")
    void testCollections_EmptyPatternsList() {
        FraudDetection detection = FraudDetection.builder()
                .tenantId(TENANT_ID)
                .detectedPatterns(List.of())
                .build();

        when(springDataRepository.save(any(FraudDetectionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        adapter.save(TENANT_ID, detection);

        verify(springDataRepository).save(argThat(entity ->
            entity.getDetectedPatterns() != null && entity.getDetectedPatterns().isEmpty()
        ));
    }

    @Test
    @DisplayName("Collections - Multiple patterns")
    void testCollections_MultiplePatterns() {
        List<String> patterns = List.of("PATTERN1", "PATTERN2", "PATTERN3");
        FraudDetection detection = FraudDetection.builder()
                .tenantId(TENANT_ID)
                .detectedPatterns(patterns)
                .build();

        when(springDataRepository.save(any(FraudDetectionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        adapter.save(TENANT_ID, detection);

        verify(springDataRepository).save(argThat(entity ->
            entity.getDetectedPatterns() != null &&
            entity.getDetectedPatterns().size() == 3
        ));
    }

    // ==================== Timestamp tests ====================

    @Test
    @DisplayName("Timestamps - CreatedAt set for new entities")
    void testTimestamps_CreatedAtSetForNewEntities() {
        FraudDetection detection = FraudDetection.builder()
                .tenantId(TENANT_ID)
                .entityType("CLAIM")
                .build();

        when(springDataRepository.save(any(FraudDetectionEntity.class))).thenAnswer(invocation -> {
            FraudDetectionEntity entity = invocation.getArgument(0);
            assertNotNull(entity.getCreatedAt(), "CreatedAt should be set for new entities");
            return entity;
        });

        adapter.save(TENANT_ID, detection);

        verify(springDataRepository).save(any(FraudDetectionEntity.class));
    }

    @Test
    @DisplayName("Timestamps - UpdatedAt always set")
    void testTimestamps_UpdatedAtAlwaysSet() {
        FraudDetection detection = FraudDetection.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .entityType("CLAIM")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        when(springDataRepository.save(any(FraudDetectionEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        adapter.save(TENANT_ID, detection);

        verify(springDataRepository).save(argThat(entity ->
            entity.getUpdatedAt() != null
        ));
    }
}
