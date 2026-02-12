package com.gogidix.rapidassist.orchestration.alerting_service.domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Alert domain model
 */
class AlertTest {

    private Alert alert;

    @BeforeEach
    void setUp() {
        alert = Alert.builder()
            .alertId("ALT-TEST123")
            .requestId("REQ-456")
            .tenantId("tenant-001")
            .type(Alert.AlertType.EMERGENCY)
            .severity(Alert.AlertSeverity.CRITICAL)
            .title("Emergency Alert")
            .description("Test emergency alert")
            .source("API")
            .status(Alert.AlertStatus.PENDING)
            .escalationLevel(0)
            .escalationRequired(false)
            .createdAt(LocalDateTime.now())
            .build();
    }

    @Test
    @DisplayName("Should successfully acknowledge a pending alert")
    void testAcknowledgeAlert() {
        // When
        alert.acknowledge("user-123", "provider-456");

        // Then
        assertEquals(Alert.AlertStatus.ACKNOWLEDGED, alert.getStatus());
        assertEquals("user-123", alert.getAcknowledgedBy());
        assertEquals("provider-456", alert.getAssignedTo());
        assertNotNull(alert.getAcknowledgedAt());
        assertEquals(1, alert.getActions().size());
        assertEquals(Alert.AlertAction.ActionType.ACKNOWLEDGED, alert.getActions().get(0).getActionType());
    }

    @Test
    @DisplayName("Should throw exception when acknowledging already acknowledged alert")
    void testAcknowledgeAlreadyAcknowledgedAlert() {
        // Given
        alert.acknowledge("user-123", "provider-456");

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> alert.acknowledge("user-789", "provider-999")
        );

        assertTrue(exception.getMessage().contains("already acknowledged"));
    }

    @Test
    @DisplayName("Should throw exception when acknowledging resolved alert")
    void testAcknowledgeResolvedAlert() {
        // Given
        alert.resolve("user-123", "Resolved");

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> alert.acknowledge("user-789", "provider-999")
        );

        assertTrue(exception.getMessage().contains("Cannot acknowledge resolved"));
    }

    @Test
    @DisplayName("Should successfully start progress on acknowledged alert")
    void testStartProgress() {
        // Given
        alert.acknowledge("user-123", "provider-456");

        // When
        alert.startProgress();

        // Then
        assertEquals(Alert.AlertStatus.IN_PROGRESS, alert.getStatus());
        assertEquals(2, alert.getActions().size());
    }

    @Test
    @DisplayName("Should successfully resolve an alert")
    void testResolveAlert() {
        // Given
        alert.acknowledge("user-123", "provider-456");

        // When
        alert.resolve("provider-456", "Issue fixed successfully");

        // Then
        assertEquals(Alert.AlertStatus.RESOLVED, alert.getStatus());
        assertEquals("provider-456", alert.getResolvedBy());
        assertEquals("Issue fixed successfully", alert.getResolutionNotes());
        assertNotNull(alert.getResolvedAt());
        assertEquals(2, alert.getActions().size());
    }

    @Test
    @DisplayName("Should throw exception when resolving already resolved alert")
    void testResolveAlreadyResolvedAlert() {
        // Given
        alert.resolve("user-123", "First resolution");

        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> alert.resolve("user-789", "Second resolution")
        );

        assertTrue(exception.getMessage().contains("already resolved"));
    }

    @Test
    @DisplayName("Should successfully escalate an alert")
    void testEscalateAlert() {
        // When
        alert.escalate(2, "manager-123");

        // Then
        assertEquals(Alert.AlertStatus.ESCALATED, alert.getStatus());
        assertEquals(2, alert.getEscalationLevel());
        assertEquals("manager-123", alert.getAssignedTo());
        assertFalse(alert.getEscalationRequired());
        assertEquals(1, alert.getActions().size());
    }

    @Test
    @DisplayName("Should throw exception when escalating to lower level")
    void testEscalateToLowerLevel() {
        // Given
        alert.escalate(2, "manager-123");

        // When & Then
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> alert.escalate(1, "supervisor-456")
        );

        assertTrue(exception.getMessage().contains("must be greater than current level"));
    }

    @Test
    @DisplayName("Should mark escalation as required")
    void testMarkEscalationRequired() {
        // When
        alert.markEscalationRequired();

        // Then
        assertTrue(alert.getEscalationRequired());
    }

    @Test
    @DisplayName("Should detect critical unattended alert")
    void testIsCriticalAndUnattended() {
        // Given - Critical and pending
        alert.setSeverity(Alert.AlertSeverity.CRITICAL);
        alert.setStatus(Alert.AlertStatus.PENDING);

        // Then
        assertTrue(alert.isCriticalAndUnattended());

        // When - Acknowledge it
        alert.acknowledge("user-123", "provider-456");

        // Then - Still critical and unattended (acknowledged but not in progress)
        assertTrue(alert.isCriticalAndUnattended());

        // When - Start progress
        alert.startProgress();

        // Then - No longer unattended
        assertFalse(alert.isCriticalAndUnattended());
    }

    @Test
    @DisplayName("Should require escalation for critical pending alerts over 30 minutes")
    void testRequiresEscalationCriticalOld() {
        // Given - Critical alert created 31 minutes ago
        alert.setSeverity(Alert.AlertSeverity.CRITICAL);
        alert.setStatus(Alert.AlertStatus.PENDING);
        alert.setCreatedAt(LocalDateTime.now().minusMinutes(31));

        // Then
        assertTrue(alert.requiresEscalation());
    }

    @Test
    @DisplayName("Should not require escalation for recent critical alerts")
    void testRequiresEscalationCriticalRecent() {
        // Given - Critical alert created 10 minutes ago
        alert.setSeverity(Alert.AlertSeverity.CRITICAL);
        alert.setStatus(Alert.AlertStatus.PENDING);
        alert.setCreatedAt(LocalDateTime.now().minusMinutes(10));

        // Then
        assertFalse(alert.requiresEscalation());
    }

    @Test
    @DisplayName("Should require escalation for high alerts not in progress after 1 hour")
    void testRequiresEscalationHighOld() {
        // Given - High alert created 61 minutes ago, not in progress
        alert.setSeverity(Alert.AlertSeverity.HIGH);
        alert.setStatus(Alert.AlertStatus.ACKNOWLEDGED);
        alert.setCreatedAt(LocalDateTime.now().minusMinutes(61));

        // Then
        assertTrue(alert.requiresEscalation());
    }

    @Test
    @DisplayName("Should close a resolved alert")
    void testCloseAlert() {
        // Given
        alert.resolve("provider-456", "Issue fixed");

        // When
        alert.close();

        // Then
        assertEquals(Alert.AlertStatus.CLOSED, alert.getStatus());
        assertEquals(3, alert.getActions().size());
    }

    @Test
    @DisplayName("Should throw exception when closing unresolved alert")
    void testCloseUnresolvedAlert() {
        // When & Then
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> alert.close()
        );

        assertTrue(exception.getMessage().contains("must be resolved before closing"));
    }

    @Test
    @DisplayName("Should create alert with all required fields")
    void testAlertBuilder() {
        // Then
        assertNotNull(alert.getAlertId());
        assertEquals("ALT-TEST123", alert.getAlertId());
        assertEquals("REQ-456", alert.getRequestId());
        assertEquals("tenant-001", alert.getTenantId());
        assertEquals(Alert.AlertType.EMERGENCY, alert.getType());
        assertEquals(Alert.AlertSeverity.CRITICAL, alert.getSeverity());
        assertEquals(Alert.AlertStatus.PENDING, alert.getStatus());
        assertEquals(0, alert.getEscalationLevel());
        assertFalse(alert.getEscalationRequired());
    }
}
