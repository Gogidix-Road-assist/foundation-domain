package com.gogidix.rapidassist.geo.location.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.geo.location.service.domain.model.Geofence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeofenceRepository extends MongoRepository<GeofenceDocument, String> {

    Optional<GeofenceDocument> findByTenantIdAndGeofenceId(String tenantId, String geofenceId);

    List<GeofenceDocument> findByTenantId(String tenantId);

    List<GeofenceDocument> findByTenantIdAndIsActive(String tenantId, boolean isActive);

    @Query(value = "{ 'tenantId': ?0, 'geometry.geometryType': 'circle', 'geometry.center.0': { $gte: ?2, $lte: ?3 }, 'geometry.center.1': { $gte: ?4, $lte: ?5 } }", count = true)
    List<GeofenceDocument> findCirclesContainingPoint(String tenantId, double minLat, double maxLat, double minLon, double maxLon);

    @Query(value = "{ 'tenantId': ?0, 'geometry.geometryType': 'rectangle', 'geometry.southWest.0': { $lte: ?1 }, 'geometry.northEast.0': { $gte: ?1 }, 'geometry.southWest.1': { $lte: ?2 }, 'geometry.northEast.1': { $gte: ?2 } }", count = true)
    List<GeofenceDocument> findRectanglesContainingPoint(String tenantId, double latitude, double longitude);

    void deleteByTenantIdAndGeofenceId(String tenantId, String geofenceId);
}