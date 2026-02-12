package com.gogidix.rapidassist.orchestration.location.application.service;

import com.gogidix.rapidassist.orchestration.location.domain.model.Location;
import com.gogidix.rapidassist.orchestration.location.domain.model.LocationHistory;
import com.gogidix.rapidassist.orchestration.location.domain.port.in.LocationServicePort;
import com.gogidix.rapidassist.orchestration.location.domain.port.out.LocationHistoryRepositoryPort;
import com.gogidix.rapidassist.orchestration.location.domain.port.out.LocationRepositoryPort;
import com.gogidix.rapidassist.orchestration.location.infrastructure.messaging.kafka.LocationEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Application service for Location operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService implements LocationServicePort {

    private final LocationRepositoryPort locationRepository;
    private final LocationHistoryRepositoryPort historyRepository;
    private final LocationEventPublisher eventPublisher;

    @Override
    @Transactional
    public Location updateLocation(String tenantId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType, String entityId, Location location) {
        log.info("Updating location for tenant: {}, entity: {} {}", tenantId, entityType, entityId);

        location.setTenantId(tenantId);
        location.setEntityType(entityType);
        location.setEntityId(entityId);
        location.setTimestamp(LocalDateTime.now());
        location.setLastUpdated(LocalDateTime.now());

        // Check if location exists
        var existingLocation = locationRepository.findByTenantIdAndEntityTypeAndEntityId(tenantId, entityType, entityId);

        Location savedLocation;
        if (existingLocation.isPresent()) {
            Location existing = existingLocation.get();
            location.setId(existing.getId());

            // Save history before updating
            LocationHistory history = LocationHistory.fromLocation(existing);
            historyRepository.save(history);

            savedLocation = locationRepository.save(location);
        } else {
            location.setId(UUID.randomUUID().toString());
            savedLocation = locationRepository.save(location);
        }

        // Publish event
        eventPublisher.publishLocationUpdated(savedLocation);

        return savedLocation;
    }

    @Override
    public Location getLocation(String tenantId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType, String entityId) {
        return locationRepository.findByTenantIdAndEntityTypeAndEntityId(tenantId, entityType, entityId)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("Location not found for entity: %s %s", entityType, entityId)
                ));
    }

    @Override
    public List<Location> getLocations(String tenantId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType, List<String> entityIds) {
        return locationRepository.findByTenantIdAndEntityTypeAndEntityIds(tenantId, entityType, entityIds);
    }

    @Override
    public List<Location> findLocationsWithinRadius(
            String tenantId,
            Double latitude,
            Double longitude,
            Double radiusInMeters
    ) {
        log.debug("Finding locations within {} meters of ({}, {})", radiusInMeters, latitude, longitude);
        return locationRepository.findLocationsWithinRadius(tenantId, latitude, longitude, radiusInMeters);
    }

    @Override
    public List<Location> findLocationsNearPoint(
            String tenantId,
            Double latitude,
            Double longitude,
            Double maxDistance,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType
    ) {
        log.debug("Finding locations near ({}, {}) within {} meters", latitude, longitude, maxDistance);
        return locationRepository.findLocationsNearPoint(tenantId, latitude, longitude, maxDistance, entityType);
    }

    @Override
    public List<LocationHistory> getLocationHistory(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return historyRepository.findByTenantIdAndEntityTypeAndEntityIdAndTimestampBetween(
                tenantId, entityType, entityId, startTime, endTime
        );
    }

    @Override
    public List<LocationHistory> getRecentHistory(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId,
            Integer limit
    ) {
        return historyRepository.findRecentHistory(tenantId, entityType, entityId, limit);
    }

    @Override
    @Transactional
    public void deleteLocation(String tenantId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType, String entityId) {
        log.info("Deleting location for tenant: {}, entity: {} {}", tenantId, entityType, entityId);
        locationRepository.deleteByTenantIdAndEntityTypeAndEntityId(tenantId, entityType, entityId);
    }

    @Override
    @Transactional
    public List<Location> bulkUpdateLocations(String tenantId, List<Location> locations) {
        log.info("Bulk updating {} locations for tenant: {}", locations.size(), tenantId);

        locations.forEach(loc -> {
            loc.setTenantId(tenantId);
            if (loc.getId() == null) {
                loc.setId(UUID.randomUUID().toString());
            }
            loc.setLastUpdated(LocalDateTime.now());
        });

        List<Location> savedLocations = locationRepository.saveAll(locations);

        // Publish bulk update event
        eventPublisher.publishBulkLocationsUpdated(tenantId, savedLocations);

        return savedLocations;
    }

    @Override
    public void checkGeofencesAndGenerateAlerts(String tenantId, String entityId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType) {
        // This will be handled by GeofenceService
        log.debug("Delegating geofence check for entity: {} {}", entityType, entityId);
    }
}
