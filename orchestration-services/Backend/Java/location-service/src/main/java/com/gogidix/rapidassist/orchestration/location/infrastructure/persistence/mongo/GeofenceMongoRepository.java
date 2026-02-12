package com.gogidix.rapidassist.orchestration.location.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.orchestration.location.domain.model.Geofence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MongoDB repository for Geofence with geospatial indexing
 */
public interface GeofenceMongoRepository extends MongoRepository<Geofence, String> {

    List<Geofence> findByTenantId(String tenantId);

    List<Geofence> findByTenantIdAndStatus(
            String tenantId,
            Geofence.GeofenceStatus status
    );

    /**
     * Find active geofences that monitor specific entity type
     */
    @Query("{ 'tenantId': ?0, 'status': 'ACTIVE', 'monitoredEntityTypes': { $in: [?1, null] }, $or: [ { 'expiresAt': null }, { 'expiresAt': { $gt: ?2 } } ] }")
    List<Geofence> findActiveGeofencesForEntityType(
            String tenantId,
            String entityType,
            LocalDateTime now
    );

    /**
     * Find circle geofences containing a point
     */
    @Query("{ 'tenantId': ?0, 'type': 'CIRCLE', 'status': 'ACTIVE', 'center': { $geoWithin: { $centerSphere: [ [ ?1, ?2 ], ?3 ] } }, $or: [ { 'expiresAt': null }, { 'expiresAt': { $gt: ?4 } } ] }")
    List<Geofence> findCircleGeofencesContainingPoint(
            String tenantId,
            Double longitude,
            Double latitude,
            Double radiusInRadians,
            LocalDateTime now
    );

    /**
     * Find polygon geofences containing a point
     * Note: Polygon containment requires $geoIntersects with Point
     */
    @Query("{ 'tenantId': ?0, 'type': 'POLYGON', 'status': 'ACTIVE', 'geometry': { $geoIntersects: { $geometry: { type: 'Point', coordinates: [ ?1, ?2 ] } } }, $or: [ { 'expiresAt': null }, { 'expiresAt': { $gt: ?3 } } ] }")
    List<Geofence> findPolygonGeofencesContainingPoint(
            String tenantId,
            Double longitude,
            Double latitude,
            LocalDateTime now
    );

    List<Geofence> findByTenantIdAndCategory(
            String tenantId,
            String category
    );
}
