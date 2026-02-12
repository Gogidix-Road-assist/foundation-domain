package com.gogidix.rapidassist.orchestration.location.domain.port.in;

import com.gogidix.rapidassist.orchestration.location.domain.model.Geofence;
import com.gogidix.rapidassist.orchestration.location.domain.model.Location;
import com.gogidix.rapidassist.orchestration.location.domain.model.LocationAlert;

import java.util.List;

/**
 * Input port for Geofence operations
 */
public interface GeofenceServicePort {

    /**
     * Create a new geofence
     */
    Geofence createGeofence(String tenantId, Geofence geofence);

    /**
     * Update geofence
     */
    Geofence updateGeofence(String tenantId, String geofenceId, Geofence geofence);

    /**
     * Get geofence by ID
     */
    Geofence getGeofence(String tenantId, String geofenceId);

    /**
     * Get all geofences for tenant
     */
    List<Geofence> getGeofences(String tenantId);

    /**
     * Get active geofences for entity type
     */
    List<Geofence> getActiveGeofencesForEntityType(String tenantId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType);

    /**
     * Find geofences containing a point
     */
    List<Geofence> findGeofencesContainingPoint(
            String tenantId,
            Double latitude,
            Double longitude
    );

    /**
     * Check if location is within geofence
     */
    boolean isLocationInGeofence(Location location, Geofence geofence);

    /**
     * Delete geofence
     */
    void deleteGeofence(String tenantId, String geofenceId);

    /**
     * Get alerts for geofence
     */
    List<LocationAlert> getGeofenceAlerts(String tenantId, String geofenceId);

    /**
     * Process geofence breach detection
     */
    void processGeofenceBreaches(String tenantId, String entityId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType, Location location);
}
