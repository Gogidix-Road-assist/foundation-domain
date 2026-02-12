package com.gogidix.rapidassist.orchestration.location.domain.port.in;

import com.gogidix.rapidassist.orchestration.location.domain.model.Location;
import com.gogidix.rapidassist.orchestration.location.domain.model.LocationHistory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Input port for Location operations
 */
public interface LocationServicePort {

    /**
     * Update or create location for an entity
     */
    Location updateLocation(String tenantId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType, String entityId, Location location);

    /**
     * Get current location for an entity
     */
    Location getLocation(String tenantId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType, String entityId);

    /**
     * Get multiple locations for entities
     */
    List<Location> getLocations(String tenantId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType, List<String> entityIds);

    /**
     * Find locations within radius
     */
    List<Location> findLocationsWithinRadius(
            String tenantId,
            Double latitude,
            Double longitude,
            Double radiusInMeters
    );

    /**
     * Find locations near point
     */
    List<Location> findLocationsNearPoint(
            String tenantId,
            Double latitude,
            Double longitude,
            Double maxDistance,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType
    );

    /**
     * Get location history for an entity
     */
    List<LocationHistory> getLocationHistory(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * Get recent location history
     */
    List<LocationHistory> getRecentHistory(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId,
            Integer limit
    );

    /**
     * Delete location for an entity
     */
    void deleteLocation(String tenantId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType, String entityId);

    /**
     * Bulk update locations
     */
    List<Location> bulkUpdateLocations(String tenantId, List<Location> locations);

    /**
     * Check geofence and generate alerts
     */
    void checkGeofencesAndGenerateAlerts(String tenantId, String entityId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType);
}
