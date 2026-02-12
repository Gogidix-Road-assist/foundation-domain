package com.gogidix.rapidassist.ai.anomaly.domain.aggregate;

import com.gogidix.rapidassist.ai.anomaly.domain.model.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Aggregate Root for Anomaly Detection.
 * Manages the lifecycle and business logic of anomaly detection operations.
 * Contains: AnomalyDetection, AnomalyAlert, and related entities as children.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnomalyDetectionAggregate {

    private UUID id;
    private String tenantId;
    private String dataSource;
    private String detectionMethod;
    private LocalDateTime detectionTimestamp;
    private String createdBy;

    // Child entities (part of aggregate)
    @Builder.Default
    private List<AnomalyDetection> detections = new ArrayList<>();

    @Builder.Default
    private List<AnomalyAlert> alerts = new ArrayList<>();

    @Builder.Default
    private List<DetectionRule> matchedRules = new ArrayList<>();

    /**
     * Business logic: Initialize a new detection aggregate
     */
    public static AnomalyDetectionAggregate initialize(String tenantId, String dataSource, String detectionMethod, String createdBy) {
        return AnomalyDetectionAggregate.builder()
                .id(UUID.randomUUID())
                .tenantId(tenantId)
                .dataSource(dataSource)
                .detectionMethod(detectionMethod)
                .detectionTimestamp(LocalDateTime.now())
                .createdBy(createdBy)
                .detections(new ArrayList<>())
                .alerts(new ArrayList<>())
                .matchedRules(new ArrayList<>())
                .build();
    }

    /**
     * Business logic: Add a detection result
     */
    public void addDetection(AnomalyDetection detection) {
        detection.setId(UUID.randomUUID());
        detection.setTenantId(this.tenantId);
        detection.setCreatedAt(LocalDateTime.now());
        detection.setUpdatedAt(LocalDateTime.now());
        detection.setDetectedAt(LocalDateTime.now());
        this.detections.add(detection);
    }

    /**
     * Business logic: Add an alert
     */
    public void addAlert(AnomalyAlert alert) {
        alert.setId(UUID.randomUUID());
        alert.setTenantId(this.tenantId);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setUpdatedAt(LocalDateTime.now());
        alert.setTriggeredAt(LocalDateTime.now());
        alert.setAlertId(UUID.randomUUID().toString());
        this.alerts.add(alert);
    }

    /**
     * Business logic: Add a matched rule
     */
    public void addMatchedRule(DetectionRule rule) {
        this.matchedRules.add(rule);
    }

    /**
     * Business logic: Get critical detections
     */
    public List<AnomalyDetection> getCriticalDetections() {
        return this.detections.stream()
                .filter(AnomalyDetection::isCritical)
                .toList();
    }

    /**
     * Business logic: Get pending detections
     */
    public List<AnomalyDetection> getPendingDetections() {
        return this.detections.stream()
                .filter(AnomalyDetection::isPending)
                .toList();
    }

    /**
     * Business logic: Get open alerts
     */
    public List<AnomalyAlert> getOpenAlerts() {
        return this.alerts.stream()
                .filter(AnomalyAlert::isOpen)
                .toList();
    }

    /**
     * Business logic: Get critical alerts
     */
    public List<AnomalyAlert> getCriticalAlerts() {
        return this.alerts.stream()
                .filter(AnomalyAlert::isCritical)
                .toList();
    }

    /**
     * Business logic: Acknowledge all pending detections
     */
    public void acknowledgeAllPending(String acknowledgedBy) {
        this.detections.stream()
                .filter(AnomalyDetection::isPending)
                .forEach(detection -> detection.acknowledge(acknowledgedBy));
    }

    /**
     * Business logic: Check if has critical issues
     */
    public boolean hasCriticalIssues() {
        return !getCriticalDetections().isEmpty() || !getCriticalAlerts().isEmpty();
    }

    /**
     * Business logic: Get detection count
     */
    public int getDetectionCount() {
        return this.detections.size();
    }

    /**
     * Business logic: Get alert count
     */
    public int getAlertCount() {
        return this.alerts.size();
    }

    /**
     * Business logic: Get total anomaly score
     */
    public double getTotalAnomalyScore() {
        return this.detections.stream()
                .mapToDouble(d -> d.getAnomalyScore() != null ? d.getAnomalyScore() : 0.0)
                .sum();
    }

    /**
     * Business logic: Get average anomaly score
     */
    public double getAverageAnomalyScore() {
        if (this.detections.isEmpty()) {
            return 0.0;
        }
        return getTotalAnomalyScore() / this.detections.size();
    }
}
