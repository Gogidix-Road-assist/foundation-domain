package com.gogidix.rapidassist.ai.fraud.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FraudAlert Domain Model Tests")
class FraudAlertTest {

    private static final String TEST_TENANT_ID = "tenant-123";

    @Test
    @DisplayName("Builder should create valid FraudAlert instance")
    void testBuilder_ValidConstruction() {
        UUID id = UUID.randomUUID();
        UUID detectionId = UUID.randomUUID();

        FraudAlert alert = FraudAlert.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .fraudDetectionId(detectionId)
                .alertType("FRAUD_DETECTED")
                .severity("HIGH")
                .title("Suspicious activity detected")
                .description("Potential fraud pattern identified")
                .status("PENDING")
                .escalationLevel(0)
                .caseCreated(false)
                .createdAt(LocalDateTime.now())
                .build();

        assertNotNull(alert);
        assertEquals(id, alert.getId());
        assertEquals(TEST_TENANT_ID, alert.getTenantId());
        assertEquals(detectionId, alert.getFraudDetectionId());
        assertEquals("FRAUD_DETECTED", alert.getAlertType());
        assertEquals("HIGH", alert.getSeverity());
        assertEquals("PENDING", alert.getStatus());
        assertEquals(0, alert.getEscalationLevel());
        assertFalse(alert.getCaseCreated());
    }

    @Test
    @DisplayName("acknowledge should update status and set acknowledged fields")
    void testAcknowledge_UpdatesStatus() {
        String acknowledgedBy = "admin-user";
        String notes = "Under review";

        FraudAlert alert = FraudAlert.builder()
                .status("PENDING")
                .build();

        alert.acknowledge(acknowledgedBy, notes);

        assertEquals("ACKNOWLEDGED", alert.getStatus());
        assertEquals(acknowledgedBy, alert.getAcknowledgedBy());
        assertEquals(notes, alert.getAcknowledgmentNotes());
        assertNotNull(alert.getAcknowledgedAt());
        assertNotNull(alert.getUpdatedAt());
    }

    @Test
    @DisplayName("resolve should update status and set resolved fields")
    void testResolve_UpdatesStatus() {
        String resolvedBy = "investigator";
        String notes = "False positive confirmed";

        FraudAlert alert = FraudAlert.builder()
                .status("ACKNOWLEDGED")
                .build();

        alert.resolve(resolvedBy, notes);

        assertEquals("RESOLVED", alert.getStatus());
        assertEquals(resolvedBy, alert.getResolvedBy());
        assertEquals(notes, alert.getResolutionNotes());
        assertNotNull(alert.getResolvedAt());
        assertNotNull(alert.getUpdatedAt());
    }

    @Test
    @DisplayName("escalate should increment escalation level")
    void testEscalate_IncrementsLevel() {
        FraudAlert alert = FraudAlert.builder()
                .escalationLevel(0)
                .build();

        alert.escalate();
        assertEquals(1, alert.getEscalationLevel());

        alert.escalate();
        assertEquals(2, alert.getEscalationLevel());
    }

    @Test
    @DisplayName("escalate from null should start at level 1")
    void testEscalate_FromNull() {
        FraudAlert alert = FraudAlert.builder()
                .escalationLevel(null)
                .build();

        alert.escalate();
        assertEquals(1, alert.getEscalationLevel());
    }

    @Test
    @DisplayName("linkToCase should set fraudCaseId and caseCreated flag")
    void testLinkToCase_AddsCaseId() {
        UUID caseId = UUID.randomUUID();

        FraudAlert alert = FraudAlert.builder()
                .fraudCaseId(null)
                .caseCreated(false)
                .build();

        alert.linkToCase(caseId);

        assertEquals(caseId, alert.getFraudCaseId());
        assertTrue(alert.getCaseCreated());
    }

    @Test
    @DisplayName("isCritical should return true for CRITICAL severity")
    void testIsCritical_CriticalSeverity() {
        FraudAlert alert = FraudAlert.builder()
                .severity("CRITICAL")
                .escalationLevel(0)
                .build();

        assertTrue(alert.isCritical());
    }

    @Test
    @DisplayName("isCritical should return true when escalation level >= 3")
    void testIsCritical_HighEscalationLevel() {
        FraudAlert alert = FraudAlert.builder()
                .severity("MEDIUM")
                .escalationLevel(3)
                .build();

        assertTrue(alert.isCritical());
    }

    @Test
    @DisplayName("isCritical should return false for non-critical with low escalation")
    void testIsCritical_NotCritical() {
        FraudAlert alert = FraudAlert.builder()
                .severity("LOW")
                .escalationLevel(1)
                .build();

        assertFalse(alert.isCritical());
    }

    @Test
    @DisplayName("isPending should return true for PENDING status")
    void testIsPending_PendingStatus() {
        FraudAlert alert = FraudAlert.builder()
                .status("PENDING")
                .build();

        assertTrue(alert.isPending());
    }

    @Test
    @DisplayName("isPending should return true for OPEN status")
    void testIsPending_OpenStatus() {
        FraudAlert alert = FraudAlert.builder()
                .status("OPEN")
                .build();

        assertTrue(alert.isPending());
    }

    @Test
    @DisplayName("isPending should return false for other statuses")
    void testIsPending_NotPending() {
        FraudAlert alert = FraudAlert.builder()
                .status("RESOLVED")
                .build();

        assertFalse(alert.isPending());
    }

    @Test
    @DisplayName("getAgeInHours should calculate correct age")
    void testGetAgeInHours_CalculatesCorrectly() {
        LocalDateTime createdAt = LocalDateTime.now().minusHours(3);

        FraudAlert alert = FraudAlert.builder()
                .createdAt(createdAt)
                .build();

        long age = alert.getAgeInHours();

        assertTrue(age >= 2 && age <= 4, "Age should be approximately 3 hours");
    }

    @Test
    @DisplayName("getAgeInHours should return 0 when createdAt is null")
    void testGetAgeInHours_NullCreatedAt() {
        FraudAlert alert = FraudAlert.builder()
                .createdAt(null)
                .build();

        assertEquals(0, alert.getAgeInHours());
    }

    @Test
    @DisplayName("tenantId field should be present for multi-tenancy")
    void testTenantId_Isolation() {
        FraudAlert alert = FraudAlert.builder()
                .tenantId(TEST_TENANT_ID)
                .build();

        assertEquals(TEST_TENANT_ID, alert.getTenantId());
    }

    @Test
    @DisplayName("NoArgsConstructor should create empty instance")
    void testNoArgsConstructor() {
        FraudAlert alert = new FraudAlert();

        assertNotNull(alert);
        assertNull(alert.getId());
        assertNull(alert.getTenantId());
    }

    @Test
    @DisplayName("AllArgsConstructor should create complete instance")
    void testAllArgsConstructor() {
        UUID id = UUID.randomUUID();
        UUID detectionId = UUID.randomUUID();
        Map<String, Object> details = new HashMap<>();

        FraudAlert alert = new FraudAlert(
                id, TEST_TENANT_ID, detectionId, "FRAUD", "HIGH",
                "Test", "Desc", details, "PENDING", "user",
                LocalDateTime.now(), "user", "notes",
                LocalDateTime.now(), "user", "notes",
                null, false, 0, details,
                LocalDateTime.now(), LocalDateTime.now()
        );

        assertEquals(id, alert.getId());
        assertEquals(TEST_TENANT_ID, alert.getTenantId());
    }

    // ========== Mutation-Killing Tests: Getter Verification ==========

    @Test
    @DisplayName("All getters should return values set via builder")
    void testAllGetters_ReturnBuilderValues() {
        UUID id = UUID.randomUUID();
        UUID detectionId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        Map<String, Object> details = Map.of("key", "value");
        Map<String, Object> metadata = Map.of("meta", "value");

        FraudAlert alert = FraudAlert.builder()
                .id(id)
                .tenantId(TEST_TENANT_ID)
                .fraudDetectionId(detectionId)
                .alertType("FRAUD_DETECTED")
                .severity("HIGH")
                .title("Test Title")
                .description("Test Description")
                .alertDetails(details)
                .status("PENDING")
                .assignedTo("investigator")
                .acknowledgedAt(now)
                .acknowledgedBy("admin")
                .acknowledgmentNotes("notes")
                .resolvedAt(now)
                .resolvedBy("resolver")
                .resolutionNotes("resolution")
                .fraudCaseId(caseId)
                .caseCreated(true)
                .escalationLevel(2)
                .metadata(metadata)
                .createdAt(now)
                .updatedAt(now)
                .build();

        assertEquals(id, alert.getId());
        assertEquals(TEST_TENANT_ID, alert.getTenantId());
        assertEquals(detectionId, alert.getFraudDetectionId());
        assertEquals("FRAUD_DETECTED", alert.getAlertType());
        assertEquals("HIGH", alert.getSeverity());
        assertEquals("Test Title", alert.getTitle());
        assertEquals("Test Description", alert.getDescription());
        assertEquals(details, alert.getAlertDetails());
        assertEquals("PENDING", alert.getStatus());
        assertEquals("investigator", alert.getAssignedTo());
        assertEquals(now, alert.getAcknowledgedAt());
        assertEquals("admin", alert.getAcknowledgedBy());
        assertEquals("notes", alert.getAcknowledgmentNotes());
        assertEquals(now, alert.getResolvedAt());
        assertEquals("resolver", alert.getResolvedBy());
        assertEquals("resolution", alert.getResolutionNotes());
        assertEquals(caseId, alert.getFraudCaseId());
        assertTrue(alert.getCaseCreated());
        assertEquals(2, alert.getEscalationLevel());
        assertEquals(metadata, alert.getMetadata());
        assertEquals(now, alert.getCreatedAt());
        assertEquals(now, alert.getUpdatedAt());
    }

    // ========== Mutation-Killing Tests: Edge Cases ==========

    @Test
    @DisplayName("isCritical should return false when severity is null")
    void testIsCritical_NullSeverity() {
        FraudAlert alert = FraudAlert.builder()
                .severity(null)
                .escalationLevel(0)
                .build();

        assertFalse(alert.isCritical());
    }

    @Test
    @DisplayName("isCritical should return false when escalation level is null")
    void testIsCritical_NullEscalationLevel() {
        FraudAlert alert = FraudAlert.builder()
                .severity("MEDIUM")
                .escalationLevel(null)
                .build();

        assertFalse(alert.isCritical());
    }

    @Test
    @DisplayName("isCritical should return true for CRITICAL with null escalation")
    void testIsCritical_CriticalSeverityNullEscalation() {
        FraudAlert alert = FraudAlert.builder()
                .severity("CRITICAL")
                .escalationLevel(null)
                .build();

        assertTrue(alert.isCritical());
    }

    @Test
    @DisplayName("isCritical should return true for escalation level 3 with null severity")
    void testIsCritical_Escalation3NullSeverity() {
        FraudAlert alert = FraudAlert.builder()
                .severity(null)
                .escalationLevel(3)
                .build();

        assertTrue(alert.isCritical());
    }

    @Test
    @DisplayName("isCritical should return true for escalation level 4")
    void testIsCritical_Escalation4() {
        FraudAlert alert = FraudAlert.builder()
                .severity("LOW")
                .escalationLevel(4)
                .build();

        assertTrue(alert.isCritical());
    }

    @Test
    @DisplayName("isPending should return false for null status")
    void testIsPending_NullStatus() {
        FraudAlert alert = FraudAlert.builder()
                .status(null)
                .build();

        assertFalse(alert.isPending());
    }

    @Test
    @DisplayName("isPending should return false for ACKNOWLEDGED status")
    void testIsPending_AcknowledgedStatus() {
        FraudAlert alert = FraudAlert.builder()
                .status("ACKNOWLEDGED")
                .build();

        assertFalse(alert.isPending());
    }

    @Test
    @DisplayName("acknowledge should preserve other fields")
    void testAcknowledge_PreservesOtherFields() {
        FraudAlert alert = FraudAlert.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .alertType("FRAUD")
                .title("Test Alert")
                .status("PENDING")
                .build();

        alert.acknowledge("admin", "notes");

        assertEquals("Test Alert", alert.getTitle());
        assertEquals(TEST_TENANT_ID, alert.getTenantId());
        assertEquals("FRAUD", alert.getAlertType());
        assertEquals("ACKNOWLEDGED", alert.getStatus());
        assertEquals("admin", alert.getAcknowledgedBy());
        assertEquals("notes", alert.getAcknowledgmentNotes());
    }

    @Test
    @DisplayName("resolve should preserve other fields")
    void testResolve_PreservesOtherFields() {
        FraudAlert alert = FraudAlert.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .alertType("FRAUD")
                .title("Test Alert")
                .status("ACKNOWLEDGED")
                .build();

        alert.resolve("admin", "resolution");

        assertEquals("Test Alert", alert.getTitle());
        assertEquals(TEST_TENANT_ID, alert.getTenantId());
        assertEquals("FRAUD", alert.getAlertType());
        assertEquals("RESOLVED", alert.getStatus());
        assertEquals("admin", alert.getResolvedBy());
        assertEquals("resolution", alert.getResolutionNotes());
    }

    @Test
    @DisplayName("escalate should preserve other fields")
    void testEscalate_PreservesOtherFields() {
        FraudAlert alert = FraudAlert.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .alertType("FRAUD")
                .title("Test Alert")
                .escalationLevel(1)
                .build();

        alert.escalate();

        assertEquals("Test Alert", alert.getTitle());
        assertEquals(TEST_TENANT_ID, alert.getTenantId());
        assertEquals("FRAUD", alert.getAlertType());
        assertEquals(2, alert.getEscalationLevel());
    }

    @Test
    @DisplayName("linkToCase should preserve other fields")
    void testLinkToCase_PreservesOtherFields() {
        UUID caseId = UUID.randomUUID();
        FraudAlert alert = FraudAlert.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .alertType("FRAUD")
                .title("Test Alert")
                .fraudCaseId(null)
                .caseCreated(false)
                .build();

        alert.linkToCase(caseId);

        assertEquals("Test Alert", alert.getTitle());
        assertEquals(TEST_TENANT_ID, alert.getTenantId());
        assertEquals("FRAUD", alert.getAlertType());
        assertEquals(caseId, alert.getFraudCaseId());
        assertTrue(alert.getCaseCreated());
    }

    @Test
    @DisplayName("acknowledge should set updatedAt timestamp")
    void testAcknowledge_SetsUpdatedAt() {
        FraudAlert alert = FraudAlert.builder()
                .updatedAt(null)
                .build();

        alert.acknowledge("admin", "notes");

        assertNotNull(alert.getUpdatedAt());
    }

    @Test
    @DisplayName("resolve should set updatedAt timestamp")
    void testResolve_SetsUpdatedAt() {
        FraudAlert alert = FraudAlert.builder()
                .updatedAt(null)
                .build();

        alert.resolve("admin", "resolution");

        assertNotNull(alert.getUpdatedAt());
    }

    @Test
    @DisplayName("escalate should set updatedAt timestamp")
    void testEscalate_SetsUpdatedAt() {
        FraudAlert alert = FraudAlert.builder()
                .updatedAt(null)
                .build();

        alert.escalate();

        assertNotNull(alert.getUpdatedAt());
    }

    @Test
    @DisplayName("linkToCase should set updatedAt timestamp")
    void testLinkToCase_SetsUpdatedAt() {
        FraudAlert alert = FraudAlert.builder()
                .updatedAt(null)
                .build();

        alert.linkToCase(UUID.randomUUID());

        assertNotNull(alert.getUpdatedAt());
    }

    @Test
    @DisplayName("toBuilder should create copy with modified field")
    void testToBuilder_CopyWithModification() {
        FraudAlert original = FraudAlert.builder()
                .id(UUID.randomUUID())
                .tenantId(TEST_TENANT_ID)
                .alertType("FRAUD")
                .status("PENDING")
                .escalationLevel(1)
                .build();

        FraudAlert modified = original.toBuilder()
                .status("RESOLVED")
                .escalationLevel(2)
                .build();

        assertEquals(original.getId(), modified.getId());
        assertEquals(TEST_TENANT_ID, modified.getTenantId());
        assertEquals("FRAUD", modified.getAlertType());
        assertEquals("RESOLVED", modified.getStatus());
        assertEquals(2, modified.getEscalationLevel());
    }

    @Test
    @DisplayName("escalate from level 1 to level 2 should preserve escalation counter")
    void testEscalate_Sequential() {
        FraudAlert alert = FraudAlert.builder()
                .escalationLevel(1)
                .build();

        alert.escalate();
        assertEquals(2, alert.getEscalationLevel());

        alert.escalate();
        assertEquals(3, alert.getEscalationLevel());

        alert.escalate();
        assertEquals(4, alert.getEscalationLevel());
    }

    @Test
    @DisplayName("getAgeInHours should return approximately correct value")
    void testGetAgeInHours_Approximate() {
        LocalDateTime createdAt = LocalDateTime.now().minusHours(5);

        FraudAlert alert = FraudAlert.builder()
                .createdAt(createdAt)
                .build();

        long age = alert.getAgeInHours();

        assertTrue(age >= 4 && age <= 6, "Age should be approximately 5 hours");
    }

    // ========== Mutation-Killing Tests: Escalation Logic ==========

    @Test
    @DisplayName("escalate should work from various starting levels")
    void testEscalate_FromVariousLevels() {
        // From level 0 to 1
        FraudAlert alert1 = FraudAlert.builder()
                .escalationLevel(0)
                .build();
        alert1.escalate();
        assertEquals(1, alert1.getEscalationLevel());

        // From level 2 to 3
        FraudAlert alert2 = FraudAlert.builder()
                .escalationLevel(2)
                .build();
        alert2.escalate();
        assertEquals(3, alert2.getEscalationLevel());

        // From level 5 to 6
        FraudAlert alert3 = FraudAlert.builder()
                .escalationLevel(5)
                .build();
        alert3.escalate();
        assertEquals(6, alert3.getEscalationLevel());

        // From level 9 to 10
        FraudAlert alert4 = FraudAlert.builder()
                .escalationLevel(9)
                .build();
        alert4.escalate();
        assertEquals(10, alert4.getEscalationLevel());
    }

    @Test
    @DisplayName("escalate should handle negative starting level")
    void testEscalate_FromNegativeLevel() {
        FraudAlert alert = FraudAlert.builder()
                .escalationLevel(-1)
                .build();

        alert.escalate();
        assertEquals(0, alert.getEscalationLevel());
    }

    @Test
    @DisplayName("escalate should handle large level increments")
    void testEscalate_LargeIncrements() {
        FraudAlert alert = FraudAlert.builder()
                .escalationLevel(95)
                .build();

        alert.escalate();
        assertEquals(96, alert.getEscalationLevel());

        alert.escalate();
        assertEquals(97, alert.getEscalationLevel());

        alert.escalate();
        assertEquals(98, alert.getEscalationLevel());
    }

    // ========== Mutation-Killing Tests: Status Logic ==========

    @Test
    @DisplayName("isPending should return correct value for all statuses")
    void testIsPending_AllStatuses() {
        // PENDING => true
        FraudAlert alert1 = FraudAlert.builder()
                .status("PENDING")
                .build();
        assertTrue(alert1.isPending());

        // OPEN => true
        FraudAlert alert2 = FraudAlert.builder()
                .status("OPEN")
                .build();
        assertTrue(alert2.isPending());

        // ACKNOWLEDGED => false
        FraudAlert alert3 = FraudAlert.builder()
                .status("ACKNOWLEDGED")
                .build();
        assertFalse(alert3.isPending());

        // RESOLVED => false
        FraudAlert alert4 = FraudAlert.builder()
                .status("RESOLVED")
                .build();
        assertFalse(alert4.isPending());

        // CLOSED => false
        FraudAlert alert5 = FraudAlert.builder()
                .status("CLOSED")
                .build();
        assertFalse(alert5.isPending());

        // null => false
        FraudAlert alert6 = FraudAlert.builder()
                .status(null)
                .build();
        assertFalse(alert6.isPending());
    }

    @Test
    @DisplayName("isPending should be case-insensitive")
    void testIsPending_CaseInsensitive() {
        FraudAlert alert1 = FraudAlert.builder()
                .status("pending")
                .build();
        assertTrue(alert1.isPending());

        FraudAlert alert2 = FraudAlert.builder()
                .status("Pending")
                .build();
        assertTrue(alert2.isPending());

        FraudAlert alert3 = FraudAlert.builder()
                .status("open")
                .build();
        assertTrue(alert3.isPending());

        FraudAlert alert4 = FraudAlert.builder()
                .status("Open")
                .build();
        assertTrue(alert4.isPending());
    }

    // ========== Mutation-Killing Tests: Critical Logic ==========

    @Test
    @DisplayName("isCritical should return true for all severity combinations that indicate critical")
    void testIsCritical_AllCriticalCombinations() {
        // CRITICAL severity, various escalation levels
        FraudAlert alert1 = FraudAlert.builder()
                .severity("CRITICAL")
                .escalationLevel(0)
                .build();
        assertTrue(alert1.isCritical());

        FraudAlert alert2 = FraudAlert.builder()
                .severity("CRITICAL")
                .escalationLevel(null)
                .build();
        assertTrue(alert2.isCritical());

        // HIGH severity with escalation level >= 3
        FraudAlert alert3 = FraudAlert.builder()
                .severity("HIGH")
                .escalationLevel(3)
                .build();
        assertTrue(alert3.isCritical());

        FraudAlert alert4 = FraudAlert.builder()
                .severity("HIGH")
                .escalationLevel(4)
                .build();
        assertTrue(alert4.isCritical());

        FraudAlert alert5 = FraudAlert.builder()
                .severity("HIGH")
                .escalationLevel(10)
                .build();
        assertTrue(alert5.isCritical());

        // MEDIUM severity with high escalation
        FraudAlert alert6 = FraudAlert.builder()
                .severity("MEDIUM")
                .escalationLevel(5)
                .build();
        assertTrue(alert6.isCritical());

        // LOW severity with high escalation
        FraudAlert alert7 = FraudAlert.builder()
                .severity("LOW")
                .escalationLevel(3)
                .build();
        assertTrue(alert7.isCritical());

        // null severity with high escalation
        FraudAlert alert8 = FraudAlert.builder()
                .severity(null)
                .escalationLevel(3)
                .build();
        assertTrue(alert8.isCritical());
    }

    @Test
    @DisplayName("isCritical should return false for non-critical combinations")
    void testIsCritical_AllNonCriticalCombinations() {
        // HIGH severity, escalation < 3
        FraudAlert alert1 = FraudAlert.builder()
                .severity("HIGH")
                .escalationLevel(0)
                .build();
        assertFalse(alert1.isCritical());

        FraudAlert alert2 = FraudAlert.builder()
                .severity("HIGH")
                .escalationLevel(1)
                .build();
        assertFalse(alert2.isCritical());

        FraudAlert alert3 = FraudAlert.builder()
                .severity("HIGH")
                .escalationLevel(2)
                .build();
        assertFalse(alert3.isCritical());

        FraudAlert alert4 = FraudAlert.builder()
                .severity("HIGH")
                .escalationLevel(null)
                .build();
        assertFalse(alert4.isCritical());

        // MEDIUM severity, escalation < 3
        FraudAlert alert5 = FraudAlert.builder()
                .severity("MEDIUM")
                .escalationLevel(2)
                .build();
        assertFalse(alert5.isCritical());

        // LOW severity, escalation < 3
        FraudAlert alert6 = FraudAlert.builder()
                .severity("LOW")
                .escalationLevel(0)
                .build();
        assertFalse(alert6.isCritical());

        FraudAlert alert7 = FraudAlert.builder()
                .severity("LOW")
                .escalationLevel(1)
                .build();
        assertFalse(alert7.isCritical());

        FraudAlert alert8 = FraudAlert.builder()
                .severity("LOW")
                .escalationLevel(2)
                .build();
        assertFalse(alert8.isCritical());

        // Both null
        FraudAlert alert9 = FraudAlert.builder()
                .severity(null)
                .escalationLevel(null)
                .build();
        assertFalse(alert9.isCritical());
    }

    @Test
    @DisplayName("isCritical should be case-insensitive for CRITICAL severity")
    void testIsCritical_CaseInsensitive() {
        FraudAlert alert1 = FraudAlert.builder()
                .severity("critical")
                .escalationLevel(0)
                .build();
        assertTrue(alert1.isCritical());

        FraudAlert alert2 = FraudAlert.builder()
                .severity("Critical")
                .escalationLevel(0)
                .build();
        assertTrue(alert2.isCritical());

        FraudAlert alert3 = FraudAlert.builder()
                .severity("CRITICAL")
                .escalationLevel(0)
                .build();
        assertTrue(alert3.isCritical());
    }

    // ========== Mutation-Killing Tests: Age Calculations ==========

    @Test
    @DisplayName("getAgeInHours should calculate correctly for various ages")
    void testGetAgeInHours_VariousAges() {
        // 1 hour ago
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        FraudAlert alert1 = FraudAlert.builder()
                .createdAt(oneHourAgo)
                .build();
        assertEquals(1, alert1.getAgeInHours());

        // 24 hours ago
        LocalDateTime oneDayAgo = LocalDateTime.now().minusHours(24);
        FraudAlert alert2 = FraudAlert.builder()
                .createdAt(oneDayAgo)
                .build();
        assertEquals(24, alert2.getAgeInHours());

        // 48 hours ago
        LocalDateTime twoDaysAgo = LocalDateTime.now().minusHours(48);
        FraudAlert alert3 = FraudAlert.builder()
                .createdAt(twoDaysAgo)
                .build();
        assertEquals(48, alert3.getAgeInHours());
    }

    // ========== Mutation-Killing Tests: hashCode() and equals() ==========

    @Test
    @DisplayName("hashCode - Objects with same ID should have same hashCode")
    void testHashCode_SameId_SameHashCode() {
        UUID id = UUID.randomUUID();
        FraudAlert alert1 = FraudAlert.builder().id(id).build();
        FraudAlert alert2 = FraudAlert.builder().id(id).build();

        // This kills Math mutants in hashCode()
        assertEquals(alert1.hashCode(), alert2.hashCode());
    }

    @Test
    @DisplayName("hashCode - Objects with different IDs should have different hashCodes")
    void testHashCode_DifferentId_DifferentHashCode() {
        FraudAlert alert1 = FraudAlert.builder().id(UUID.randomUUID()).build();
        FraudAlert alert2 = FraudAlert.builder().id(UUID.randomUUID()).build();

        // This kills Math mutants in hashCode()
        assertNotEquals(alert1.hashCode(), alert2.hashCode());
    }

    @Test
    @DisplayName("HashSet should work correctly with FraudAlert")
    void testHashSet_UsesHashCode() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudAlert alert1 = FraudAlert.builder().id(id1).title("ALERT-001").build();
        FraudAlert alert2 = FraudAlert.builder().id(id2).title("ALERT-002").build();
        FraudAlert alert1Duplicate = FraudAlert.builder().id(id1).title("ALERT-001-DUP").build();

        java.util.HashSet<FraudAlert> set = new java.util.HashSet<>();
        assertTrue(set.add(alert1));
        assertTrue(set.add(alert2));
        assertFalse(set.add(alert1Duplicate)); // Should not add - same ID as alert1

        assertEquals(2, set.size());
        assertTrue(set.contains(alert1));
        assertTrue(set.contains(alert2));
    }

    @Test
    @DisplayName("HashMap should work correctly with FraudAlert as key")
    void testHashMap_UsesHashCodeAndEquals() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudAlert key1 = FraudAlert.builder().id(id1).build();
        FraudAlert key2 = FraudAlert.builder().id(id2).build();
        FraudAlert key1Duplicate = FraudAlert.builder().id(id1).build();

        java.util.Map<FraudAlert, String> map = new java.util.HashMap<>();
        map.put(key1, "First");
        map.put(key2, "Second");
        map.put(key1Duplicate, "First Updated");

        // This kills Math mutants in hashCode() and NegateConditionalsMutator in equals()
        assertEquals(2, map.size());
        assertEquals("First Updated", map.get(key1));
        assertEquals("Second", map.get(key2));
        assertEquals("First Updated", map.get(key1Duplicate));
    }

    @Test
    @DisplayName("equals - Same object reference")
    void testEquals_SameObjectReference() {
        FraudAlert alert = FraudAlert.builder().id(UUID.randomUUID()).build();
        // This kills NegateConditionalsMutator in equals()
        assertEquals(alert, alert);
    }

    @Test
    @DisplayName("equals - Null comparison")
    void testEquals_NullComparison() {
        FraudAlert alert = FraudAlert.builder().id(UUID.randomUUID()).build();
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(null, alert);
    }

    @Test
    @DisplayName("equals - Different type comparison")
    void testEquals_DifferentType() {
        FraudAlert alert = FraudAlert.builder().id(UUID.randomUUID()).build();
        String other = "Not a FraudAlert";
        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(alert, other);
    }

    @Test
    @DisplayName("equals - All fields same except ID")
    void testEquals_AllFieldsSameExceptId() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudAlert alert1 = FraudAlert.builder()
                .id(id1)
                .alertType("ALERT")
                .severity("HIGH")
                .build();

        FraudAlert alert2 = FraudAlert.builder()
                .id(id2)
                .alertType("ALERT")
                .severity("HIGH")
                .build();

        // This kills NegateConditionalsMutator in equals()
        assertNotEquals(alert1, alert2);
    }

    @Test
    @DisplayName("equals - Both IDs null")
    void testEquals_BothIdsNull() {
        FraudAlert alert1 = FraudAlert.builder().build();
        FraudAlert alert2 = FraudAlert.builder().build();

        assertEquals(alert1, alert2);
        assertEquals(alert1.hashCode(), alert2.hashCode());
    }

    @Test
    @DisplayName("equals - One ID null")
    void testEquals_OneIdNull() {
        UUID id = UUID.randomUUID();
        FraudAlert alert1 = FraudAlert.builder().id(id).build();
        FraudAlert alert2 = FraudAlert.builder().id(null).build();

        assertNotEquals(alert1, alert2);
    }

    @Test
    @DisplayName("canEqual - Same class should return true")
    void testCanEqual_SameClass() {
        FraudAlert alert = FraudAlert.builder().build();
        FraudAlert other = FraudAlert.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertTrue(alert.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Different class should return false")
    void testCanEqual_DifferentClass() {
        FraudAlert alert = FraudAlert.builder().build();
        String other = "Not a FraudAlert";

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(alert.canEqual(other));
    }

    @Test
    @DisplayName("canEqual - Null should return false")
    void testCanEqual_Null() {
        FraudAlert alert = FraudAlert.builder().build();

        // This kills BooleanReturnValsMutator in canEqual()
        assertFalse(alert.canEqual(null));
    }

    @Test
    @DisplayName("equals - CanEqual check between different types")
    void testEquals_WithCanEqualCheck() {
        UUID id = UUID.randomUUID();
        FraudAlert alert = FraudAlert.builder().id(id).build();
        FraudRule otherRule = FraudRule.builder().id(id).ruleName("RULE").build();

        // This tests canEqual within equals
        assertNotEquals(alert, otherRule);
    }

    @Test
    @DisplayName("HashSet contains and remove operations")
    void testHashSet_ContainsAndRemove() {
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        FraudAlert alert1 = FraudAlert.builder().id(id1).title("ALERT-001").build();
        FraudAlert alert2 = FraudAlert.builder().id(id2).title("ALERT-002").build();

        java.util.HashSet<FraudAlert> set = new java.util.HashSet<>();
        set.add(alert1);
        set.add(alert2);

        // Test contains - uses hashCode() and equals()
        assertTrue(set.contains(alert1));
        assertTrue(set.contains(alert2));

        // Create duplicate with same ID
        FraudAlert alert1Dup = FraudAlert.builder().id(id1).title("ALERT-001-DUP").build();
        assertTrue(set.contains(alert1Dup));

        // Test remove - uses hashCode() and equals()
        assertTrue(set.remove(alert1Dup));
        assertEquals(1, set.size());
        assertFalse(set.contains(alert1));
    }

    @Test
    @DisplayName("hashCode consistency across multiple calls")
    void testHashCode_Consistency() {
        FraudAlert alert = FraudAlert.builder()
                .id(UUID.randomUUID())
                .title("ALERT-001")
                .build();

        int hash1 = alert.hashCode();
        int hash2 = alert.hashCode();
        int hash3 = alert.hashCode();

        assertEquals(hash1, hash2);
        assertEquals(hash2, hash3);
    }

    @Test
    @DisplayName("hashCode with null fields")
    void testHashCode_NullFields() {
        UUID id = UUID.randomUUID();
        FraudAlert alert1 = FraudAlert.builder()
                .id(id)
                .title(null)
                .build();

        FraudAlert alert2 = FraudAlert.builder()
                .id(id)
                .title(null)
                .build();

        assertEquals(alert1.hashCode(), alert2.hashCode());
    }
}
