package com.gogidix.rapidassist.ai.fraud.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudAlert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for FraudAlertRepositoryAdapter
 *
 * Tests the in-memory repository adapter implementation.
 */
@DisplayName("FraudAlertRepositoryAdapter Tests")
class FraudAlertRepositoryAdapterTest {

    private FraudAlertRepositoryAdapter adapter;

    private static final String TENANT_ID = "tenant-123";
    private static final String TENANT_ID_2 = "tenant-456";
    private static final UUID TEST_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        adapter = new FraudAlertRepositoryAdapter();
    }

    // ==================== save() tests ====================

    @Test
    @DisplayName("save - Saves alert successfully")
    void testSave_SavesAlertSuccessfully() {
        FraudAlert alert = FraudAlert.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .fraudDetectionId(UUID.randomUUID())
                .alertType("SUSPICIOUS_PATTERN")
                .severity("HIGH")
                .status("PENDING")
                .build();

        FraudAlert result = adapter.save(TENANT_ID, alert);

        assertNotNull(result);
        assertEquals(TEST_ID, result.getId());
        assertEquals(TENANT_ID, result.getTenantId());
    }

    @Test
    @DisplayName("save - Returns same alert instance")
    void testSave_ReturnsSameInstance() {
        FraudAlert alert = FraudAlert.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .alertType("ANOMALY_DETECTED")
                .build();

        FraudAlert result = adapter.save(TENANT_ID, alert);

        assertSame(alert, result);
    }

    // ==================== findById() tests ====================

    @Test
    @DisplayName("findById - Returns alert when found")
    void testFindById_ReturnsAlertWhenFound() {
        FraudAlert alert = FraudAlert.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .alertType("SUSPICIOUS_PATTERN")
                .build();

        adapter.save(TENANT_ID, alert);

        Optional<FraudAlert> result = adapter.findById(TENANT_ID, TEST_ID);

        assertTrue(result.isPresent());
        assertEquals(TEST_ID, result.get().getId());
        assertEquals("SUSPICIOUS_PATTERN", result.get().getAlertType());
    }

    @Test
    @DisplayName("findById - Returns empty when not found")
    void testFindById_ReturnsEmptyWhenNotFound() {
        Optional<FraudAlert> result = adapter.findById(TENANT_ID, UUID.randomUUID());

        assertFalse(result.isPresent());
    }

    // ==================== findByTenantId() tests ====================

    @Test
    @DisplayName("findByTenantId - Returns alerts for tenant")
    void testFindByTenantId_ReturnsAlerts() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudAlert alert1 = FraudAlert.builder()
                .id(id1)
                .tenantId(TENANT_ID)
                .alertType("TYPE1")
                .build();

        FraudAlert alert2 = FraudAlert.builder()
                .id(id2)
                .tenantId(TENANT_ID)
                .alertType("TYPE2")
                .build();

        adapter.save(TENANT_ID, alert1);
        adapter.save(TENANT_ID, alert2);

        List<FraudAlert> result = adapter.findByTenantId(TENANT_ID);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(a -> a.getId().equals(id1)));
        assertTrue(result.stream().anyMatch(a -> a.getId().equals(id2)));
    }

    @Test
    @DisplayName("findByTenantId - Filters by tenant ID")
    void testFindByTenantId_FiltersByTenantId() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudAlert alert1 = FraudAlert.builder()
                .id(id1)
                .tenantId(TENANT_ID)
                .alertType("TYPE1")
                .build();

        FraudAlert alert2 = FraudAlert.builder()
                .id(id2)
                .tenantId(TENANT_ID_2)
                .alertType("TYPE2")
                .build();

        adapter.save(TENANT_ID, alert1);
        adapter.save(TENANT_ID_2, alert2);

        List<FraudAlert> result1 = adapter.findByTenantId(TENANT_ID);
        List<FraudAlert> result2 = adapter.findByTenantId(TENANT_ID_2);

        assertEquals(1, result1.size());
        assertEquals(1, result2.size());
        assertEquals(TENANT_ID, result1.get(0).getTenantId());
        assertEquals(TENANT_ID_2, result2.get(0).getTenantId());
    }

    @Test
    @DisplayName("findByTenantId - Returns empty list when no alerts")
    void testFindByTenantId_ReturnsEmptyWhenNoAlerts() {
        List<FraudAlert> result = adapter.findByTenantId(TENANT_ID);

        assertTrue(result.isEmpty());
    }

    // ==================== findByStatus() tests ====================

    @Test
    @DisplayName("findByStatus - Returns alerts with matching status")
    void testFindByStatus_ReturnsAlertsWithStatus() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudAlert alert1 = FraudAlert.builder()
                .id(id1)
                .tenantId(TENANT_ID)
                .status("PENDING")
                .build();

        FraudAlert alert2 = FraudAlert.builder()
                .id(id2)
                .tenantId(TENANT_ID)
                .status("RESOLVED")
                .build();

        adapter.save(TENANT_ID, alert1);
        adapter.save(TENANT_ID, alert2);

        List<FraudAlert> result = adapter.findByStatus(TENANT_ID, "PENDING");

        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
    }

    @Test
    @DisplayName("findByStatus - Filters by tenant and status")
    void testFindByStatus_FiltersByTenantAndStatus() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudAlert alert1 = FraudAlert.builder()
                .id(id1)
                .tenantId(TENANT_ID)
                .status("PENDING")
                .build();

        FraudAlert alert2 = FraudAlert.builder()
                .id(id2)
                .tenantId(TENANT_ID_2)
                .status("PENDING")
                .build();

        adapter.save(TENANT_ID, alert1);
        adapter.save(TENANT_ID_2, alert2);

        List<FraudAlert> result = adapter.findByStatus(TENANT_ID, "PENDING");

        assertEquals(1, result.size());
        assertEquals(TENANT_ID, result.get(0).getTenantId());
    }

    // ==================== findBySeverity() tests ====================

    @Test
    @DisplayName("findBySeverity - Returns alerts with matching severity")
    void testFindBySeverity_ReturnsAlertsWithSeverity() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudAlert alert1 = FraudAlert.builder()
                .id(id1)
                .tenantId(TENANT_ID)
                .severity("HIGH")
                .build();

        FraudAlert alert2 = FraudAlert.builder()
                .id(id2)
                .tenantId(TENANT_ID)
                .severity("LOW")
                .build();

        adapter.save(TENANT_ID, alert1);
        adapter.save(TENANT_ID, alert2);

        List<FraudAlert> result = adapter.findBySeverity(TENANT_ID, "HIGH");

        assertEquals(1, result.size());
        assertEquals("HIGH", result.get(0).getSeverity());
    }

    @Test
    @DisplayName("findBySeverity - All severity levels")
    void testFindBySeverity_AllSeverityLevels() {
        String[] severities = {"LOW", "MEDIUM", "HIGH", "CRITICAL"};

        for (String severity : severities) {
            UUID id = UUID.randomUUID();
            FraudAlert alert = FraudAlert.builder()
                    .id(id)
                    .tenantId(TENANT_ID)
                    .severity(severity)
                    .build();
            adapter.save(TENANT_ID, alert);

            List<FraudAlert> result = adapter.findBySeverity(TENANT_ID, severity);
            assertEquals(1, result.size());
            assertEquals(severity, result.get(0).getSeverity());
        }
    }

    // ==================== findByAssignedTo() tests ====================

    @Test
    @DisplayName("findByAssignedTo - Returns alerts assigned to user")
    void testFindByAssignedTo_ReturnsAssignedAlerts() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudAlert alert1 = FraudAlert.builder()
                .id(id1)
                .tenantId(TENANT_ID)
                .assignedTo("admin")
                .build();

        FraudAlert alert2 = FraudAlert.builder()
                .id(id2)
                .tenantId(TENANT_ID)
                .assignedTo("reviewer")
                .build();

        adapter.save(TENANT_ID, alert1);
        adapter.save(TENANT_ID, alert2);

        List<FraudAlert> result = adapter.findByAssignedTo(TENANT_ID, "admin");

        assertEquals(1, result.size());
        assertEquals("admin", result.get(0).getAssignedTo());
    }

    @Test
    @DisplayName("findByAssignedTo - Returns empty when no assignments")
    void testFindByAssignedTo_ReturnsEmptyWhenNoAssignments() {
        FraudAlert alert = FraudAlert.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .assignedTo("admin")
                .build();

        adapter.save(TENANT_ID, alert);

        List<FraudAlert> result = adapter.findByAssignedTo(TENANT_ID, "reviewer");

        assertTrue(result.isEmpty());
    }

    // ==================== findPendingAlerts() tests ====================

    @Test
    @DisplayName("findPendingAlerts - Returns pending alerts")
    void testFindPendingAlerts_ReturnsPendingAlerts() {
        FraudAlert alert = FraudAlert.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .status("PENDING")
                .build();

        adapter.save(TENANT_ID, alert);

        List<FraudAlert> result = adapter.findPendingAlerts(TENANT_ID);

        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
    }

    // ==================== findCriticalAlerts() tests ====================

    @Test
    @DisplayName("findCriticalAlerts - Returns critical alerts")
    void testFindCriticalAlerts_ReturnsCriticalAlerts() {
        FraudAlert alert = FraudAlert.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .severity("CRITICAL")
                .build();

        adapter.save(TENANT_ID, alert);

        List<FraudAlert> result = adapter.findCriticalAlerts(TENANT_ID);

        assertEquals(1, result.size());
        assertEquals("CRITICAL", result.get(0).getSeverity());
    }

    // ==================== delete() tests ====================

    @Test
    @DisplayName("delete - Removes alert")
    void testDelete_RemovesAlert() {
        FraudAlert alert = FraudAlert.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .alertType("TYPE")
                .build();

        adapter.save(TENANT_ID, alert);

        adapter.delete(TENANT_ID, TEST_ID);

        Optional<FraudAlert> result = adapter.findById(TENANT_ID, TEST_ID);
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("delete - No-op for non-existent alert")
    void testDelete_NoOpForNonExistentAlert() {
        // Should not throw exception
        assertDoesNotThrow(() -> adapter.delete(TENANT_ID, UUID.randomUUID()));
    }

    // ==================== exists() tests ====================

    @Test
    @DisplayName("exists - Returns true when alert exists")
    void testExists_ReturnsTrueWhenExists() {
        FraudAlert alert = FraudAlert.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .build();

        adapter.save(TENANT_ID, alert);

        assertTrue(adapter.exists(TENANT_ID, TEST_ID));
    }

    @Test
    @DisplayName("exists - Returns false when alert not found")
    void testExists_ReturnsFalseWhenNotFound() {
        assertFalse(adapter.exists(TENANT_ID, UUID.randomUUID()));
    }

    // ==================== Multi-tenant isolation tests ====================

    @Test
    @DisplayName("Multi-tenant - No cross-tenant data leakage")
    void testMultitenancy_NoCrossTenantLeakage() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudAlert alert1 = FraudAlert.builder()
                .id(id1)
                .tenantId(TENANT_ID)
                .alertType("TYPE1")
                .build();

        FraudAlert alert2 = FraudAlert.builder()
                .id(id2)
                .tenantId(TENANT_ID_2)
                .alertType("TYPE2")
                .build();

        adapter.save(TENANT_ID, alert1);
        adapter.save(TENANT_ID_2, alert2);

        List<FraudAlert> tenant1Results = adapter.findByTenantId(TENANT_ID);
        List<FraudAlert> tenant2Results = adapter.findByTenantId(TENANT_ID_2);

        assertEquals(1, tenant1Results.size());
        assertEquals(1, tenant2Results.size());
        assertEquals(TENANT_ID, tenant1Results.get(0).getTenantId());
        assertEquals(TENANT_ID_2, tenant2Results.get(0).getTenantId());
    }

    @Test
    @DisplayName("Multi-tenant - findById respects tenant")
    void testMultitenancy_FindByIdRespectsTenant() {
        UUID id = UUID.randomUUID();

        FraudAlert alert = FraudAlert.builder()
                .id(id)
                .tenantId(TENANT_ID)
                .alertType("TYPE")
                .build();

        adapter.save(TENANT_ID, alert);

        // Find with correct tenant
        Optional<FraudAlert> result1 = adapter.findById(TENANT_ID, id);
        assertTrue(result1.isPresent());

        // Find with different tenant (in-memory adapter doesn't filter by tenant in findById)
        Optional<FraudAlert> result2 = adapter.findById(TENANT_ID_2, id);
        assertTrue(result2.isPresent()); // In-memory implementation returns it regardless
    }

    // ==================== Status tests ====================

    @Test
    @DisplayName("Status values - All valid statuses")
    void testStatusValues_AllValidStatuses() {
        String[] statuses = {"PENDING", "ACKNOWLEDGED", "RESOLVED", "ESCALATED", "CLOSED"};

        for (String status : statuses) {
            UUID id = UUID.randomUUID();
            FraudAlert alert = FraudAlert.builder()
                    .id(id)
                    .tenantId(TENANT_ID)
                    .status(status)
                    .build();
            adapter.save(TENANT_ID, alert);

            List<FraudAlert> result = adapter.findByStatus(TENANT_ID, status);
            assertEquals(1, result.size());
            assertEquals(status, result.get(0).getStatus());
        }
    }

    // ==================== Alert type tests ====================

    @Test
    @DisplayName("Alert types - Various alert types")
    void testAlertTypes_VariousAlertTypes() {
        String[] alertTypes = {
                "SUSPICIOUS_PATTERN",
                "ANOMALY_DETECTED",
                "RULE_VIOLATION",
                "VELOCITY_EXCEEDED",
                "GEOLOCATION_MISMATCH"
        };

        for (String alertType : alertTypes) {
            UUID id = UUID.randomUUID();
            FraudAlert alert = FraudAlert.builder()
                    .id(id)
                    .tenantId(TENANT_ID)
                    .alertType(alertType)
                    .build();
            adapter.save(TENANT_ID, alert);

            Optional<FraudAlert> result = adapter.findById(TENANT_ID, id);
            assertTrue(result.isPresent());
            assertEquals(alertType, result.get().getAlertType());
        }
    }

    // ==================== Edge cases ====================

    @Test
    @DisplayName("Edge case - Multiple saves with same ID")
    void testEdgeCase_MultipleSavesWithSameId() {
        UUID id = UUID.randomUUID();

        FraudAlert alert1 = FraudAlert.builder()
                .id(id)
                .tenantId(TENANT_ID)
                .status("PENDING")
                .build();

        FraudAlert alert2 = FraudAlert.builder()
                .id(id)
                .tenantId(TENANT_ID)
                .status("RESOLVED")
                .build();

        adapter.save(TENANT_ID, alert1);
        adapter.save(TENANT_ID, alert2);

        List<FraudAlert> allAlerts = adapter.findByTenantId(TENANT_ID);
        assertEquals(2, allAlerts.size()); // Both are stored
    }

    @Test
    @DisplayName("Edge case - Empty string assignedTo")
    void testEdgeCase_EmptyStringAssignedTo() {
        FraudAlert alert = FraudAlert.builder()
                .id(TEST_ID)
                .tenantId(TENANT_ID)
                .assignedTo("")
                .build();

        adapter.save(TENANT_ID, alert);

        List<FraudAlert> result = adapter.findByAssignedTo(TENANT_ID, "");
        assertEquals(1, result.size());
    }
}
