package com.gogidix.rapidassist.geo.location.service.domain.port.out;

import com.gogidix.rapidassist.geo.location.service.domain.model.Location;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface LocationStore {

    Optional<Location> find(String tenantId, String locationId);

    List<Location> findByTenantId(String tenantId);

    List<Location> findByDeviceId(String tenantId, String deviceId);

    List<Location> findByUserId(String tenantId, String userId);

    List<Location> findByDeviceIdAndTimeRange(String tenantId, String deviceId, Instant from, Instant to);

    List<Location> findByTimeRange(String tenantId, Instant from, Instant to);

    List<Location> findNearby(String tenantId, double latitude, double longitude, double radiusKm);

    List<Location> findInBoundingBox(String tenantId, double minLat, double minLon,
                                    double maxLat, double maxLon);

    Location save(Location location);

    void delete(String tenantId, String locationId);

    List<Location> findLatestByDevices(String tenantId, List<String> deviceIds);

    List<Location> findByIpAddress(String tenantId, String ipAddress);
}