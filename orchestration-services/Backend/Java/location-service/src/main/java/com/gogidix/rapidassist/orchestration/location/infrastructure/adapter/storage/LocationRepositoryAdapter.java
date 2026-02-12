package com.gogidix.rapidassist.orchestration.location.infrastructure.adapter.storage;

import com.gogidix.rapidassist.orchestration.location.domain.model.Location;
import com.gogidix.rapidassist.orchestration.location.domain.port.out.LocationRepositoryPort;
import com.gogidix.rapidassist.orchestration.location.infrastructure.persistence.mongo.LocationMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB adapter for Location repository
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocationRepositoryAdapter implements LocationRepositoryPort {

    private final LocationMongoRepository repository;

    @Override
    public Location save(Location location) {
        log.debug("Saving location for entity: {} {}", location.getEntityType(), location.getEntityId());
        return repository.save(location);
    }

    @Override
    public Optional<Location> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Location> findByTenantIdAndEntityTypeAndEntityId(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId
    ) {
        return repository.findByTenantIdAndEntityTypeAndEntityId(tenantId, entityType, entityId);
    }

    @Override
    public List<Location> findByTenantIdAndEntityType(String tenantId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType) {
        return repository.findByTenantIdAndEntityType(tenantId, entityType);
    }

    @Override
    public List<Location> findByTenantIdAndEntityTypeAndEntityIds(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            List<String> entityIds
    ) {
        return repository.findAllById(entityIds).stream()
                .filter(loc -> loc.getTenantId().equals(tenantId) && loc.getEntityType().equals(entityType))
                .toList();
    }

    @Override
    public List<Location> findLocationsWithinRadius(
            String tenantId,
            Double latitude,
            Double longitude,
            Double radiusInMeters
    ) {
        // Convert meters to radians (Earth radius = 6371000 meters)
        double radiusInRadians = radiusInMeters / 6371000.0;
        return repository.findLocationsWithinRadius(tenantId, longitude, latitude, radiusInRadians);
    }

    @Override
    public List<Location> findLocationsNearPoint(
            String tenantId,
            Double latitude,
            Double longitude,
            Double maxDistance,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType
    ) {
        if (entityType != null) {
            return repository.findLocationsNearPointByEntityType(
                    tenantId, entityType, longitude, latitude, maxDistance
            );
        }
        return repository.findLocationsNearPoint(tenantId, longitude, latitude, maxDistance);
    }

    @Override
    public List<Location> findByTenantIdAndStatus(String tenantId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.LocationStatus status) {
        return repository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public List<Location> findByTenantIdAndLastUpdatedAfter(String tenantId, LocalDateTime timestamp) {
        return repository.findByTenantIdAndLastUpdatedAfter(tenantId, timestamp);
    }

    @Override
    public List<Location> findByTenantId(String tenantId) {
        return repository.findByTenantId(tenantId);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public void deleteByTenantIdAndEntityTypeAndEntityId(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId
    ) {
        repository.deleteByTenantIdAndEntityTypeAndEntityId(tenantId, entityType, entityId);
    }

    @Override
    public boolean existsByTenantIdAndEntityTypeAndEntityId(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId
    ) {
        return repository.existsByTenantIdAndEntityTypeAndEntityId(tenantId, entityType, entityId);
    }

    @Override
    public List<Location> saveAll(List<Location> locations) {
        return repository.saveAll(locations);
    }
}
