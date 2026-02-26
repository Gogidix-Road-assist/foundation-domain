package com.gogidix.rapidassist.orchestration.location.domain.port.out;

import com.gogidix.rapidassist.orchestration.location.domain.model.Geofence;
import com.gogidix.rapidassist.orchestration.location.domain.model.Location;

import java.util.List;
import java.util.Optional;

/**
 * Output port for Geofence persistence operations
 */
public interface GeofenceRepositoryPort {

    Geofence save(Geofence geofence);

    Optional<Geofence> findById(String id);

    List<Geofence> findByTenantId(String tenantId);

    List<Geofence> findByTenantIdAndStatus(
            String tenantId,
            Geofence.GeofenceStatus status
    );

    /**
     * Find active geofences for a specific entity type
     */
    List<Geofence> findActiveGeofencesForEntityType(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType
    );

    /**
     * Find geofences that contain a specific point
     */
    List<Geofence> findGeofencesContainingPoint(
            String tenantId,
            Double latitude,
            Double longitude
    );

    /**
     * Find geofences by category
     */
    List<Geofence> findByTenantIdAndCategory(
            String tenantId,
            String category
    );

    void deleteById(String id);

    List<Geofence> saveAll(List<Geofence> geofences);
}
