package com.gogidix.rapidassist.orchestration.location.domain.port.out;

import com.gogidix.rapidassist.orchestration.location.domain.model.Location;
import com.gogidix.rapidassist.orchestration.location.domain.model.LocationAlert;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Output port for LocationAlert persistence operations
 */
public interface LocationAlertRepositoryPort {

    LocationAlert save(LocationAlert alert);

    Optional<LocationAlert> findById(String id);

    /**
     * Find alerts by geofence
     */
    List<LocationAlert> findByGeofenceId(String geofenceId);

    /**
     * Find alerts by entity
     */
    List<LocationAlert> findByTenantIdAndEntityTypeAndEntityId(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId
    );

    /**
     * Find alerts by status
     */
    List<LocationAlert> findByTenantIdAndStatus(
            String tenantId,
            LocationAlert.AlertStatus status
    );

    /**
     * Find pending alerts for notification
     */
    List<LocationAlert> findPendingAlerts(String tenantId);

    /**
     * Find alerts within time range
     */
    List<LocationAlert> findByTenantIdAndTimestampBetween(
            String tenantId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * Find recent alerts
     */
    List<LocationAlert> findRecentAlerts(
            String tenantId,
            Integer limit
    );

    /**
     * Delete old alerts based on retention policy
     */
    void deleteAlertsOlderThan(String tenantId, LocalDateTime retentionDate);

    /**
     * Bulk save alerts
     */
    List<LocationAlert> saveAll(List<LocationAlert> alerts);
}
