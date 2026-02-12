package com.gogidix.rapidassist.orchestration.location.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.orchestration.location.domain.model.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB repository for Location with geospatial indexing
 */
public interface LocationMongoRepository extends MongoRepository<Location, String> {

    Optional<Location> findByTenantIdAndEntityTypeAndEntityId(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId
    );

    List<Location> findByTenantIdAndEntityType(String tenantId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType);

    /**
     * Find locations within radius using geospatial query
     * Uses 2dsphere index for efficient proximity searches
     */
    @Query("{ 'tenantId': ?0, 'coordinates': { $geoWithin: { $centerSphere: [ [ ?1, ?2 ], ?3 ] } } }")
    List<Location> findLocationsWithinRadius(
            String tenantId,
            Double longitude,
            Double latitude,
            Double radiusInRadians
    );

    /**
     * Find locations near a point
     */
    @Query("{ 'tenantId': ?0, 'coordinates': { $near: { $geometry: { type: 'Point', coordinates: [ ?1, ?2 ] }, $maxDistance: ?3 } } }")
    List<Location> findLocationsNearPoint(
            String tenantId,
            Double longitude,
            Double latitude,
            Double maxDistance
    );

    /**
     * Find locations near point with entity type filter
     */
    @Query("{ 'tenantId': ?0, 'entityType': ?1, 'coordinates': { $near: { $geometry: { type: 'Point', coordinates: [ ?2, ?3 ] }, $maxDistance: ?4 } } }")
    List<Location> findLocationsNearPointByEntityType(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            Double longitude,
            Double latitude,
            Double maxDistance
    );

    List<Location> findByTenantIdAndStatus(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.LocationStatus status
    );

    List<Location> findByTenantIdAndLastUpdatedAfter(
            String tenantId,
            LocalDateTime timestamp
    );

    List<Location> findByTenantId(String tenantId);

    boolean existsByTenantIdAndEntityTypeAndEntityId(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId
    );

    void deleteByTenantIdAndEntityTypeAndEntityId(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId
    );
}
