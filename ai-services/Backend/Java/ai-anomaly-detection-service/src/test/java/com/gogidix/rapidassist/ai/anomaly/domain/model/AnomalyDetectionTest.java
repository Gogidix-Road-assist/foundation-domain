package com.gogidix.rapidassist.ai.anomaly.domain.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AnomalyDetection domain model.
 */
class AnomalyDetectionTest {

    @Test
    void testAnomalyDetectionCreation() {
        Map<String, Object> data = new HashMap<>();
        data.put("value", 100);

        AnomalyDetection detection = AnomalyDetection.builder()
                .id(UUID.randomUUID())
                .tenantId("tenant-1")
                .dataSource("system-metrics")
                .dataPoint("cpu-usage")
                .severity(AnomalySeverity.HIGH)
                .status(AnomalyStatus.PENDING)
                .anomalyScore(0.85)
                .confidence(0.90)
                .detectionMethod("STATISTICAL")
                .data(data)
                .detectedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        assertNotNull(detection);
        assertEquals("tenant-1", detection.getTenantId());
        assertEquals(AnomalySeverity.HIGH, detection.getSeverity());
        assertEquals(AnomalyStatus.PENDING, detection.getStatus());
        assertEquals(0.85, detection.getAnomalyScore());
    }

    @Test
    void testIsCritical() {
        AnomalyDetection detection = AnomalyDetection.builder()
                .severity(AnomalySeverity.CRITICAL)
                .build();

        assertTrue(detection.isCritical());
    }

    @Test
    void testIsNotCritical() {
        AnomalyDetection detection = AnomalyDetection.builder()
                .severity(AnomalySeverity.MEDIUM)
                .build();

        assertFalse(detection.isCritical());
    }

    @Test
    void testAcknowledgePending() {
        AnomalyDetection detection = AnomalyDetection.builder()
                .status(AnomalyStatus.PENDING)
                .build();

        detection.acknowledge("user-1");

        assertEquals(AnomalyStatus.ACKNOWLEDGED, detection.getStatus());
        assertEquals("user-1", detection.getAcknowledgedBy());
        assertNotNull(detection.getAcknowledgedAt());
    }

    @Test
    void testAcknowledgeFailsWhenNotPending() {
        AnomalyDetection detection = AnomalyDetection.builder()
                .status(AnomalyStatus.RESOLVED)
                .build();

        assertThrows(IllegalStateException.class, () -> detection.acknowledge("user-1"));
    }

    @Test
    void testResolve() {
        AnomalyDetection detection = AnomalyDetection.builder()
                .status(AnomalyStatus.ACKNOWLEDGED)
                .build();

        detection.resolve("user-1");

        assertEquals(AnomalyStatus.RESOLVED, detection.getStatus());
        assertEquals("user-1", detection.getResolvedBy());
        assertNotNull(detection.getResolvedAt());
    }

    @Test
    void testGetHoursSinceDetection() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);

        AnomalyDetection detection = AnomalyDetection.builder()
                .detectedAt(oneHourAgo)
                .build();

        long hours = detection.getHoursSinceDetection();
        assertTrue(hours >= 1);
    }
}
