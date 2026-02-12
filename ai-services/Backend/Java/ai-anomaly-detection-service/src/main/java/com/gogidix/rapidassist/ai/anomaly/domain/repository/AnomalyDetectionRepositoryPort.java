package com.gogidix.rapidassist.ai.anomaly.domain.repository;

import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalyDetection;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving AnomalyDetection entities.
 * Implementations are provided by the Infrastructure layer.
 */
public interface AnomalyDetectionRepositoryPort {

    /**
     * Save an anomaly detection (create or update).
     */
    AnomalyDetection save(String tenantId, AnomalyDetection detection);

    /**
     * Find a detection by ID and tenant.
     */
    Optional<AnomalyDetection> findById(String tenantId, UUID detectionId);

    /**
     * Find all detections for a tenant.
     */
    List<AnomalyDetection> findByTenantId(String tenantId);

    /**
     * Find detections by data source and tenant.
     */
    List<AnomalyDetection> findByDataSource(String tenantId, String dataSource);

    /**
     * Find detections by severity and tenant.
     */
    List<AnomalyDetection> findBySeverity(String tenantId, String severity);

    /**
     * Find detections by status and tenant.
     */
    List<AnomalyDetection> findByStatus(String tenantId, String status);

    /**
     * Find detections by date range and tenant.
     */
    List<AnomalyDetection> findByDetectedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find pending detections for a tenant.
     */
    List<AnomalyDetection> findPendingDetections(String tenantId);

    /**
     * Find critical detections for a tenant.
     */
    List<AnomalyDetection> findCriticalDetections(String tenantId);

    /**
     * Find detections by anomaly score threshold.
     */
    List<AnomalyDetection> findByAnomalyScoreGreaterThan(String tenantId, Double threshold);

    /**
     * Delete a detection by ID and tenant.
     */
    void delete(String tenantId, UUID detectionId);

    /**
     * Check if a detection exists.
     */
    boolean exists(String tenantId, UUID detectionId);

    /**
     * Count detections by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count detections by status and tenant.
     */
    long countByStatus(String tenantId, String status);
}
