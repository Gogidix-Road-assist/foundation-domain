package com.gogidix.rapidassist.orchestration.location.domain.port.out;

import com.gogidix.rapidassist.orchestration.location.domain.model.Location;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Output port for Location persistence operations
 */
public interface LocationRepositoryPort {

    Location save(Location location);

    Optional<Location> findById(String id);

    Optional<Location> findByTenantIdAndEntityTypeAndEntityId(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId
    );

    List<Location> findByTenantIdAndEntityType(String tenantId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType);

    List<Location> findByTenantIdAndEntityTypeAndEntityIds(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            List<String> entityIds
    );

    /**
     * Find locations within radius of a point
     */
    List<Location> findLocationsWithinRadius(
            String tenantId,
            Double latitude,
            Double longitude,
            Double radiusInMeters
    );

    /**
     * Find locations near a point with optional entity type filter
     */
    List<Location> findLocationsNearPoint(
            String tenantId,
            Double latitude,
            Double longitude,
            Double maxDistance,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType
    );

    /**
     * Find active/inactive locations
     */
    List<Location> findByTenantIdAndStatus(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.LocationStatus status
    );

    /**
     * Find locations updated after a specific time
     */
    List<Location> findByTenantIdAndLastUpdatedAfter(
            String tenantId,
            LocalDateTime timestamp
    );

    List<Location> findByTenantId(String tenantId);

    void deleteById(String id);

    void deleteByTenantIdAndEntityTypeAndEntityId(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId
    );

    boolean existsByTenantIdAndEntityTypeAndEntityId(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId
    );

    /**
     * Bulk update locations
     */
    List<Location> saveAll(List<Location> locations);
}
