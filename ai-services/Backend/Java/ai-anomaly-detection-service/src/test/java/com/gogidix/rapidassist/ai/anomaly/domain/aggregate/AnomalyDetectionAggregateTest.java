package com.gogidix.rapidassist.ai.anomaly.domain.aggregate;

import com.gogidix.rapidassist.ai.anomaly.domain.model.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AnomalyDetectionAggregate.
 */
class AnomalyDetectionAggregateTest {

    @Test
    void testInitializeAggregate() {
        AnomalyDetectionAggregate aggregate = AnomalyDetectionAggregate.initialize(
                "tenant-1", "system-metrics", "STATISTICAL", "system"
        );

        assertNotNull(aggregate);
        assertEquals("tenant-1", aggregate.getTenantId());
        assertEquals("system-metrics", aggregate.getDataSource());
        assertEquals("STATISTICAL", aggregate.getDetectionMethod());
        assertNotNull(aggregate.getId());
        assertNotNull(aggregate.getDetections());
        assertTrue(aggregate.getDetections().isEmpty());
    }

    @Test
    void testAddDetection() {
        AnomalyDetectionAggregate aggregate = AnomalyDetectionAggregate.initialize(
                "tenant-1", "system-metrics", "STATISTICAL", "system"
        );

        AnomalyDetection detection = AnomalyDetection.builder()
                .severity(AnomalySeverity.HIGH)
                .anomalyScore(0.85)
                .build();

        aggregate.addDetection(detection);

        assertEquals(1, aggregate.getDetectionCount());
        assertNotNull(detection.getId());
        assertEquals("tenant-1", detection.getTenantId());
    }

    @Test
    void testAddAlert() {
        AnomalyDetectionAggregate aggregate = AnomalyDetectionAggregate.initialize(
                "tenant-1", "system-metrics", "STATISTICAL", "system"
        );

        AnomalyAlert alert = AnomalyAlert.builder()
                .title("Test Alert")
                .severity(AlertSeverity.CRITICAL)
                .build();

        aggregate.addAlert(alert);

        assertEquals(1, aggregate.getAlertCount());
        assertNotNull(alert.getId());
        assertEquals("tenant-1", alert.getTenantId());
    }

    @Test
    void testGetCriticalDetections() {
        AnomalyDetectionAggregate aggregate = AnomalyDetectionAggregate.initialize(
                "tenant-1", "system-metrics", "STATISTICAL", "system"
        );

        AnomalyDetection critical1 = AnomalyDetection.builder()
                .severity(AnomalySeverity.CRITICAL)
                .build();

        AnomalyDetection medium = AnomalyDetection.builder()
                .severity(AnomalySeverity.MEDIUM)
                .build();

        AnomalyDetection critical2 = AnomalyDetection.builder()
                .severity(AnomalySeverity.CRITICAL)
                .build();

        aggregate.addDetection(critical1);
        aggregate.addDetection(medium);
        aggregate.addDetection(critical2);

        assertEquals(2, aggregate.getCriticalDetections().size());
    }

    @Test
    void testGetOpenAlerts() {
        AnomalyDetectionAggregate aggregate = AnomalyDetectionAggregate.initialize(
                "tenant-1", "system-metrics", "STATISTICAL", "system"
        );

        AnomalyAlert openAlert1 = AnomalyAlert.builder()
                .status(AlertStatus.OPEN)
                .build();

        AnomalyAlert acknowledgedAlert = AnomalyAlert.builder()
                .status(AlertStatus.ACKNOWLEDGED)
                .build();

        AnomalyAlert openAlert2 = AnomalyAlert.builder()
                .status(AlertStatus.OPEN)
                .build();

        aggregate.addAlert(openAlert1);
        aggregate.addAlert(acknowledgedAlert);
        aggregate.addAlert(openAlert2);

        assertEquals(2, aggregate.getOpenAlerts().size());
    }

    @Test
    void testHasCriticalIssues() {
        AnomalyDetectionAggregate aggregate = AnomalyDetectionAggregate.initialize(
                "tenant-1", "system-metrics", "STATISTICAL", "system"
        );

        AnomalyDetection critical = AnomalyDetection.builder()
                .severity(AnomalySeverity.CRITICAL)
                .build();

        aggregate.addDetection(critical);

        assertTrue(aggregate.hasCriticalIssues());
    }

    @Test
    void testGetTotalAnomalyScore() {
        AnomalyDetectionAggregate aggregate = AnomalyDetectionAggregate.initialize(
                "tenant-1", "system-metrics", "STATISTICAL", "system"
        );

        AnomalyDetection detection1 = AnomalyDetection.builder()
                .anomalyScore(0.75)
                .build();

        AnomalyDetection detection2 = AnomalyDetection.builder()
                .anomalyScore(0.85)
                .build();

        aggregate.addDetection(detection1);
        aggregate.addDetection(detection2);

        assertEquals(1.6, aggregate.getTotalAnomalyScore(), 0.01);
    }

    @Test
    void testGetAverageAnomalyScore() {
        AnomalyDetectionAggregate aggregate = AnomalyDetectionAggregate.initialize(
                "tenant-1", "system-metrics", "STATISTICAL", "system"
        );

        AnomalyDetection detection1 = AnomalyDetection.builder()
                .anomalyScore(0.75)
                .build();

        AnomalyDetection detection2 = AnomalyDetection.builder()
                .anomalyScore(0.85)
                .build();

        aggregate.addDetection(detection1);
        aggregate.addDetection(detection2);

        assertEquals(0.8, aggregate.getAverageAnomalyScore(), 0.01);
    }
}
