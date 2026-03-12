package com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.entity;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudRiskLevel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FraudDetectionEntity
 */
@DisplayName("FraudDetectionEntity Tests")
class FraudDetectionEntityTest {

    private static final UUID TEST_ID = UUID.randomUUID();
    private static final String TENANT_ID = "tenant-123";

    @Test
    @DisplayName("Builder - All fields")
    void testBuilder_AllFields() {
        LocalDateTime now = LocalDateTime.now();
        List<String> patterns = List.of("PATTERN1", "PATTERN2");
        Map<String, Object> details = Map.of("key", "value");
        Map<String, Object> metadata = Map.of("meta", "data");

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
                .detectedAt(now)
                .reviewedAt(now.plusDays(1))
                .reviewedBy("manager")
                .reviewNotes("Under review")
                .metadata(metadata)
                .createdAt(now)
                .updatedAt(now)
                .build();

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
        assertEquals(now, entity.getDetectedAt());
        assertEquals("manager", entity.getReviewedBy());
        assertEquals("Under review", entity.getReviewNotes());
    }

    @Test
    @DisplayName("Builder - Minimal fields")
    void testBuilder_MinimalFields() {
        FraudDetectionEntity entity = FraudDetectionEntity.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .build();

        assertEquals(TEST_ID, entity.getId());
        assertEquals(TENANT_ID, entity.getTenantId());
        assertNull(entity.getEntityType());
        assertNull(entity.getRiskLevel());
    }

    @Test
    @DisplayName("Getters and Setters")
    void testGettersSetters() {
        FraudDetectionEntity entity = new FraudDetectionEntity();
        entity.setId(TEST_ID);
        entity.setTenantId(TENANT_ID);
        entity.setEntityType("TRANSACTION");
        entity.setEntityId("txn-456");
        entity.setRiskLevel(FraudRiskLevel.LOW);
        entity.setRiskScore(0.25);
        entity.setStatus("REVIEWED");
        entity.setRequiresReview(false);
        entity.setAssignedTo("reviewer");

        assertEquals(TEST_ID, entity.getId());
        assertEquals(TENANT_ID, entity.getTenantId());
        assertEquals("TRANSACTION", entity.getEntityType());
        assertEquals("txn-456", entity.getEntityId());
        assertEquals(FraudRiskLevel.LOW, entity.getRiskLevel());
        assertEquals(0.25, entity.getRiskScore());
        assertEquals("REVIEWED", entity.getStatus());
        assertFalse(entity.getRequiresReview());
        assertEquals("reviewer", entity.getAssignedTo());
    }

    @Test
    @DisplayName("Equals - Same ID")
    void testEquals_SameId() {
        FraudDetectionEntity entity1 = FraudDetectionEntity.builder().id(TEST_ID).build();
        FraudDetectionEntity entity2 = FraudDetectionEntity.builder().id(TEST_ID).build();

        assertEquals(entity1, entity2);
        assertEquals(entity1.hashCode(), entity2.hashCode());
    }

    @Test
    @DisplayName("Equals - Different ID")
    void testEquals_DifferentId() {
        FraudDetectionEntity entity1 = FraudDetectionEntity.builder().id(UUID.randomUUID()).build();
        FraudDetectionEntity entity2 = FraudDetectionEntity.builder().id(UUID.randomUUID()).build();

        assertNotEquals(entity1, entity2);
    }

    @Test
    @DisplayName("Equals - Both null IDs")
    void testEquals_BothNullIds() {
        FraudDetectionEntity entity1 = FraudDetectionEntity.builder().build();
        FraudDetectionEntity entity2 = FraudDetectionEntity.builder().build();

        assertEquals(entity1, entity2);
    }

    @Test
    @DisplayName("Risk level enum values")
    void testRiskLevelEnumValues() {
        FraudDetectionEntity low = FraudDetectionEntity.builder().riskLevel(FraudRiskLevel.LOW).build();
        FraudDetectionEntity medium = FraudDetectionEntity.builder().riskLevel(FraudRiskLevel.MEDIUM).build();
        FraudDetectionEntity high = FraudDetectionEntity.builder().riskLevel(FraudRiskLevel.HIGH).build();
        FraudDetectionEntity critical = FraudDetectionEntity.builder().riskLevel(FraudRiskLevel.CRITICAL).build();

        assertEquals(FraudRiskLevel.LOW, low.getRiskLevel());
        assertEquals(FraudRiskLevel.MEDIUM, medium.getRiskLevel());
        assertEquals(FraudRiskLevel.HIGH, high.getRiskLevel());
        assertEquals(FraudRiskLevel.CRITICAL, critical.getRiskLevel());
    }

    @Test
    @DisplayName("Status values")
    void testStatusValues() {
        String[] statuses = {"PENDING", "UNDER_REVIEW", "REVIEWED", "ESCALATED", "CLOSED"};

        for (String status : statuses) {
            FraudDetectionEntity entity = FraudDetectionEntity.builder().status(status).build();
            assertEquals(status, entity.getStatus());
        }
    }

    @Test
    @DisplayName("Entity types")
    void testEntityTypes() {
        String[] entityTypes = {"CLAIM", "TRANSACTION", "POLICY", "ACCOUNT", "USER"};

        for (String entityType : entityTypes) {
            FraudDetectionEntity entity = FraudDetectionEntity.builder().entityType(entityType).build();
            assertEquals(entityType, entity.getEntityType());
        }
    }

    @Test
    @DisplayName("Detected patterns list")
    void testDetectedPatternsList() {
        List<String> patterns = List.of("GEO_CLUSTERING", "VELOCITY_SPIKE", "ANOMALY_AMOUNT");

        FraudDetectionEntity entity = FraudDetectionEntity.builder()
                .detectedPatterns(patterns)
                .build();

        assertEquals(3, entity.getDetectedPatterns().size());
        assertTrue(entity.getDetectedPatterns().contains("GEO_CLUSTERING"));
        assertTrue(entity.getDetectedPatterns().contains("VELOCITY_SPIKE"));
        assertTrue(entity.getDetectedPatterns().contains("ANOMALY_AMOUNT"));
    }

    @Test
    @DisplayName("Detection details map")
    void testDetectionDetailsMap() {
        Map<String, Object> details = Map.of(
                "confidence", 0.95,
                "model_version", "v1.0",
                "features", List.of("amount", "location", "time")
        );

        FraudDetectionEntity entity = FraudDetectionEntity.builder()
                .detectionDetails(details)
                .build();

        assertEquals(details, entity.getDetectionDetails());
        assertEquals(0.95, entity.getDetectionDetails().get("confidence"));
    }

    @Test
    @DisplayName("toString - Contains ID")
    void testToString_ContainsId() {
        FraudDetectionEntity entity = FraudDetectionEntity.builder().id(TEST_ID).build();
        assertTrue(entity.toString().contains(TEST_ID.toString()));
    }

    @Test
    @DisplayName("Timestamp fields")
    void testTimestampFields() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minusDays(1);
        LocalDateTime future = now.plusDays(1);

        FraudDetectionEntity entity = FraudDetectionEntity.builder()
                .detectedAt(now)
                .createdAt(past)
                .updatedAt(future)
                .reviewedAt(future)
                .build();

        assertEquals(now, entity.getDetectedAt());
        assertEquals(past, entity.getCreatedAt());
        assertEquals(future, entity.getUpdatedAt());
        assertEquals(future, entity.getReviewedAt());
    }

    @Test
    @DisplayName("Metadata map")
    void testMetadataMap() {
        Map<String, Object> metadata = Map.of(
                "source", "API",
                "batch_id", "BATCH-123",
                "processed", true
        );

        FraudDetectionEntity entity = FraudDetectionEntity.builder()
                .metadata(metadata)
                .build();

        assertEquals(metadata, entity.getMetadata());
        assertTrue((Boolean) entity.getMetadata().get("processed"));
    }
}
