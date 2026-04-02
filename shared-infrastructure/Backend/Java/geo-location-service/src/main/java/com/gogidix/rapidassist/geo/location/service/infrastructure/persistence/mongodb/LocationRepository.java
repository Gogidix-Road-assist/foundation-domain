package com.gogidix.rapidassist.geo.location.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.geo.location.service.domain.model.Location;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends MongoRepository<LocationDocument, String> {

    Optional<LocationDocument> findByTenantIdAndLocationId(String tenantId, String locationId);

    List<LocationDocument> findByTenantId(String tenantId);

    List<LocationDocument> findByTenantIdAndDeviceId(String tenantId, String deviceId);

    List<LocationDocument> findByTenantIdAndUserId(String tenantId, String userId);

    @Query("{ 'tenantId': ?0, 'deviceId': ?1, 'timestamp': { $gte: ?2, $lte: ?3 } }")
    List<LocationDocument> findByTenantIdAndDeviceIdAndTimeRange(String tenantId, String deviceId, Instant from, Instant to);

    @Query("{ 'tenantId': ?0, 'timestamp': { $gte: ?1, $lte: ?2 } }")
    List<LocationDocument> findByTenantIdAndTimeRange(String tenantId, Instant from, Instant to);

    @Query(value = "{ 'tenantId': ?0, 'coordinates': { $near: { $geometry: { type: 'Point', coordinates: [?2, ?1] }, $maxDistance: ?3 } } }")
    List<LocationDocument> findNearby(String tenantId, double longitude, double latitude, double maxDistanceMeters);

    @Query("{ 'tenantId': ?0, 'coordinates': { $geoWithin: { $box: [?2, ?1, ?4, ?3] } } }")
    List<LocationDocument> findInBoundingBox(String tenantId, double minLat, double minLon, double maxLat, double maxLon);

    @Query(value = "{ 'tenantId': ?0, 'deviceId': { $in: ?1 } }", sort = "{ 'timestamp': -1 }")
    List<LocationDocument> findLatestByDeviceIds(String tenantId, List<String> deviceIds);

    List<LocationDocument> findByTenantIdAndIpAddress(String tenantId, String ipAddress);

    @Tailable
    @Query(value = "{ 'tenantId': ?0 }", sort = "{ 'timestamp': -1 }")
    List<LocationDocument> findStreamByTenantId(String tenantId);
}