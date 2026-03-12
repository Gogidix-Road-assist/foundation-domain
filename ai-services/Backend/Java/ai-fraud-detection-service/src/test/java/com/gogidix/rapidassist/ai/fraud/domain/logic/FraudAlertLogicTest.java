package com.gogidix.rapidassist.ai.fraud.domain.logic;

import com.gogidix.rapidassist.ai.fraud.domain.model.FraudAlert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FraudAlert Domain Logic Tests")
class FraudAlertLogicTest {

    private static final String TENANT_ID = "tenant-123";

    @Test
    @DisplayName("acknowledge should update status and acknowledgement fields")
    void testAcknowledge_UpdatesFields() {
        String acknowledgedBy = "admin-user";
        String notes = "Alert acknowledged for review";

        FraudAlert alert = FraudAlert.builder()
                .status("PENDING")
                .acknowledgedBy(null)
                .acknowledgedAt(null)
                .build();

        alert.acknowledge(acknowledgedBy, notes);

        assertEquals("ACKNOWLEDGED", alert.getStatus());
        assertEquals(acknowledgedBy, alert.getAcknowledgedBy());
        assertEquals(notes, alert.getAcknowledgmentNotes());
        assertNotNull(alert.getAcknowledgedAt());
        assertNotNull(alert.getUpdatedAt());
    }

    @Test
    @DisplayName("acknowledge should handle null acknowledgedBy")
    void testAcknowledge_NullAcknowledgedBy() {
        FraudAlert alert = FraudAlert.builder()
                .status("PENDING")
                .build();

        alert.acknowledge(null, "Auto-acknowledged");

        assertEquals("ACKNOWLEDGED", alert.getStatus());
        assertNull(alert.getAcknowledgedBy());
        assertEquals("Auto-acknowledged", alert.getAcknowledgmentNotes());
    }

    @Test
    @DisplayName("resolve should update status and resolution fields")
    void testResolve_UpdatesFields() {
        String resolvedBy = "admin-user";
        String notes = "False positive - verified as legitimate";

        FraudAlert alert = FraudAlert.builder()
                .status("ACKNOWLEDGED")
                .resolvedBy(null)
                .resolvedAt(null)
                .build();

        alert.resolve(resolvedBy, notes);

        assertEquals("RESOLVED", alert.getStatus());
        assertEquals(resolvedBy, alert.getResolvedBy());
        assertEquals(notes, alert.getResolutionNotes());
        assertNotNull(alert.getResolvedAt());
        assertNotNull(alert.getUpdatedAt());
    }

    @Test
    @DisplayName("escalate should initialize escalation level to 1 when null")
    void testEscalate_InitializesLevel() {
        FraudAlert alert = FraudAlert.builder()
                .escalationLevel(null)
                .build();

        alert.escalate();

        assertEquals(1, alert.getEscalationLevel());
        assertNotNull(alert.getUpdatedAt());
    }

    @Test
    @DisplayName("escalate should increment from existing level")
    void testEscalate_IncrementsFromExisting() {
        FraudAlert alert = FraudAlert.builder()
                .escalationLevel(2)
                .build();

        alert.escalate();

        assertEquals(3, alert.getEscalationLevel());
    }

    @Test
    @DisplayName("linkToCase should update case fields")
    void testLinkToCase_UpdatesFields() {
        UUID caseId = UUID.randomUUID();

        FraudAlert alert = FraudAlert.builder()
                .caseCreated(false)
                .fraudCaseId(null)
                .build();

        alert.linkToCase(caseId);

        assertTrue(alert.getCaseCreated());
        assertEquals(caseId, alert.getFraudCaseId());
        assertNotNull(alert.getUpdatedAt());
    }

    @ParameterizedTest
    @ValueSource(strings = {"CRITICAL", "critical", "Critical"})
    @DisplayName("isCritical should return true for CRITICAL severity")
    void testIsCritical_TrueForCriticalSeverity(String severity) {
        FraudAlert alert = FraudAlert.builder()
                .severity(severity)
                .escalationLevel(0)
                .build();

        assertTrue(alert.isCritical());
    }

    @Test
    @DisplayName("isCritical should return true for escalation level >= 3")
    void testIsCritical_TrueForHighEscalation() {
        FraudAlert alert = FraudAlert.builder()
                .severity("LOW")
                .escalationLevel(3)
                .build();

        assertTrue(alert.isCritical());
    }

    @ParameterizedTest
    @ValueSource(strings = {"HIGH", "MEDIUM", "LOW", "INFO"})
    @DisplayName("isCritical should return false for non-CRITICAL severity with low escalation")
    void testIsCritical_FalseForNonCritical(String severity) {
        FraudAlert alert = FraudAlert.builder()
                .severity(severity)
                .escalationLevel(0)
                .build();

        assertFalse(alert.isCritical());
    }

    @ParameterizedTest
    @ValueSource(strings = {"PENDING", "pending", "OPEN", "open"})
    @DisplayName("isPending should return true for PENDING and OPEN status")
    void testIsPending_TrueForPendingAndOpen(String status) {
        FraudAlert alert = FraudAlert.builder()
                .status(status)
                .build();

        assertTrue(alert.isPending());
    }

    @ParameterizedTest
    @ValueSource(strings = {"ACKNOWLEDGED", "RESOLVED", "CLOSED"})
    @DisplayName("isPending should return false for non-pending status")
    void testIsPending_FalseForNonPending(String status) {
        FraudAlert alert = FraudAlert.builder()
                .status(status)
                .build();

        assertFalse(alert.isPending());
    }

    @Test
    @DisplayName("getAgeInHours should calculate age correctly")
    void testGetAgeInHours_CalculatesCorrectly() {
        LocalDateTime createdAt = LocalDateTime.now().minusHours(5);

        FraudAlert alert = FraudAlert.builder()
                .createdAt(createdAt)
                .build();

        long age = alert.getAgeInHours();

        assertTrue(age >= 4 && age <= 6);
    }

    @Test
    @DisplayName("getAgeInHours should return 0 for null createdAt")
    void testGetAgeInHours_NullCreatedAt() {
        FraudAlert alert = FraudAlert.builder()
                .createdAt(null)
                .build();

        assertEquals(0, alert.getAgeInHours());
    }

    @Test
    @DisplayName("Builder should create instance with all fields")
    void testBuilder_AllFields() {
        UUID id = UUID.randomUUID();
        UUID detectionId = UUID.randomUUID();
        UUID caseId = UUID.randomUUID();
        Map<String, Object> details = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        FraudAlert alert = FraudAlert.builder()
                .id(id)
                .tenantId(TENANT_ID)
                .fraudDetectionId(detectionId)
                .alertType("SUSPICIOUS_PATTERN")
                .severity("HIGH")
                .title("Unusual activity")
                .description("Multiple transactions")
                .alertDetails(details)
                .status("PENDING")
                .escalationLevel(0)
                .fraudCaseId(caseId)
                .acknowledgedBy("reviewer")
                .acknowledgedAt(now)
                .acknowledgmentNotes("Review")
                .build();

        assertEquals(id, alert.getId());
        assertEquals(TENANT_ID, alert.getTenantId());
        assertEquals("SUSPICIOUS_PATTERN", alert.getAlertType());
        assertEquals("HIGH", alert.getSeverity());
        assertEquals("reviewer", alert.getAcknowledgedBy());
        assertEquals("Review", alert.getAcknowledgmentNotes());
    }

    @Test
    @DisplayName("NoArgsConstructor should create instance with null fields")
    void testNoArgsConstructor_AllFieldsNull() {
        FraudAlert alert = new FraudAlert();

        assertNull(alert.getId());
        assertNull(alert.getTenantId());
        assertNull(alert.getAlertType());
    }
}
