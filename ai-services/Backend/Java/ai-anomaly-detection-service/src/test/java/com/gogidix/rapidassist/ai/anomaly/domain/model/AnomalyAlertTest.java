package com.gogidix.rapidassist.ai.anomaly.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AnomalyAlert domain model.
 */
class AnomalyAlertTest {

    @Test
    void testAnomalyAlertCreation() {
        AnomalyAlert alert = AnomalyAlert.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-1")
                .alertId("alert-123")
                .detectionId(UUID.randomUUID())
                .ruleId(UUID.randomUUID())
                .title("Critical Anomaly Detected")
                .message("High CPU usage detected")
                .severity(AlertSeverity.CRITICAL)
                .status(AlertStatus.OPEN)
                .triggeredAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .escalationLevel(0)
                .build();

        assertNotNull(alert);
        assertEquals("tenant-1", alert.getTenantId());
        assertEquals("Critical Anomaly Detected", alert.getTitle());
        assertEquals(AlertSeverity.CRITICAL, alert.getSeverity());
        assertEquals(AlertStatus.OPEN, alert.getStatus());
    }

    @Test
    void testAcknowledgeAlert() {
        AnomalyAlert alert = AnomalyAlert.builder()
                .status(AlertStatus.OPEN)
                .build();

        alert.acknowledge("user-1");

        assertEquals(AlertStatus.ACKNOWLEDGED, alert.getStatus());
        assertEquals("user-1", alert.getAcknowledgedBy());
        assertNotNull(alert.getAcknowledgedAt());
    }

    @Test
    void testResolveAlert() {
        AnomalyAlert alert = AnomalyAlert.builder()
                .status(AlertStatus.ACKNOWLEDGED)
                .build();

        alert.resolve("user-1", "Issue resolved");

        assertEquals(AlertStatus.RESOLVED, alert.getStatus());
        assertEquals("user-1", alert.getResolvedBy());
        assertEquals("Issue resolved", alert.getResolutionNotes());
        assertNotNull(alert.getResolvedAt());
    }

    @Test
    void testEscalateAlert() {
        AnomalyAlert alert = AnomalyAlert.builder()
                .escalationLevel(0)
                .build();

        alert.escalate();

        assertEquals(1, alert.getEscalationLevel());
        assertNotNull(alert.getLastEscalatedAt());
    }

    @Test
    void testIsOpen() {
        AnomalyAlert alert = AnomalyAlert.builder()
                .status(AlertStatus.OPEN)
                .build();

        assertTrue(alert.isOpen());
    }

    @Test
    void testIsCritical() {
        AnomalyAlert alert = AnomalyAlert.builder()
                .severity(AlertSeverity.CRITICAL)
                .build();

        assertTrue(alert.isCritical());
    }

    @Test
    void testGetMinutesSinceTriggered() {
        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusMinutes(10);

        AnomalyAlert alert = AnomalyAlert.builder()
                .triggeredAt(tenMinutesAgo)
                .build();

        long minutes = alert.getMinutesSinceTriggered();
        assertTrue(minutes >= 10);
    }

    @Test
    void testRequiresEscalation() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        AnomalyAlert alert = AnomalyAlert.builder()
                .status(AlertStatus.OPEN)
                .triggeredAt(oneHourAgo)
                .build();

        assertTrue(alert.requiresEscalation(30)); // 30 minutes threshold
    }
}
