package com.gogidix.rapidassist.ai.anomaly.domain.repository;

import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalyAlert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving AnomalyAlert entities.
 * Implementations are provided by the Infrastructure layer.
 */
public interface AnomalyAlertRepositoryPort {

    /**
     * Save an anomaly alert (create or update).
     */
    AnomalyAlert save(String tenantId, AnomalyAlert alert);

    /**
     * Find an alert by ID and tenant.
     */
    Optional<AnomalyAlert> findById(String tenantId, UUID alertId);

    /**
     * Find an alert by alert ID string and tenant.
     */
    Optional<AnomalyAlert> findByAlertId(String tenantId, String alertId);

    /**
     * Find all alerts for a tenant.
     */
    List<AnomalyAlert> findByTenantId(String tenantId);

    /**
     * Find alerts by detection ID and tenant.
     */
    List<AnomalyAlert> findByDetectionId(String tenantId, UUID detectionId);

    /**
     * Find alerts by severity and tenant.
     */
    List<AnomalyAlert> findBySeverity(String tenantId, String severity);

    /**
     * Find alerts by status and tenant.
     */
    List<AnomalyAlert> findByStatus(String tenantId, String status);

    /**
     * Find alerts by assigned user and tenant.
     */
    List<AnomalyAlert> findByAssignedTo(String tenantId, String assignedTo);

    /**
     * Find open alerts for a tenant.
     */
    List<AnomalyAlert> findOpenAlerts(String tenantId);

    /**
     * Find critical alerts for a tenant.
     */
    List<AnomalyAlert> findCriticalAlerts(String tenantId);

    /**
     * Find alerts by date range and tenant.
     */
    List<AnomalyAlert> findByTriggeredAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Delete an alert by ID and tenant.
     */
    void delete(String tenantId, UUID alertId);

    /**
     * Check if an alert exists.
     */
    boolean exists(String tenantId, UUID alertId);

    /**
     * Count alerts by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count alerts by status and tenant.
     */
    long countByStatus(String tenantId, String status);
}
