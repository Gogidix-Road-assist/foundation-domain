package com.gogidix.rapidassist.orchestration.location.domain.port.in;

import com.gogidix.rapidassist.orchestration.location.domain.model.LocationAlert;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Input port for Alert operations
 */
public interface AlertServicePort {

    /**
     * Create alert
     */
    LocationAlert createAlert(String tenantId, LocationAlert alert);

    /**
     * Get alert by ID
     */
    LocationAlert getAlert(String tenantId, String alertId);

    /**
     * Get pending alerts for notification
     */
    List<LocationAlert> getPendingAlerts(String tenantId);

    /**
     * Get alerts for entity
     */
    List<LocationAlert> getAlertsForEntity(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId
    );

    /**
     * Get alerts within time range
     */
    List<LocationAlert> getAlertsWithinTimeRange(
            String tenantId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * Get recent alerts
     */
    List<LocationAlert> getRecentAlerts(String tenantId, Integer limit);

    /**
     * Acknowledge alert
     */
    LocationAlert acknowledgeAlert(String tenantId, String alertId, String acknowledgedBy);

    /**
     * Resolve alert
     */
    LocationAlert resolveAlert(String tenantId, String alertId);

    /**
     * Send pending alerts
     */
    void sendPendingAlerts(String tenantId);
}
