package com.gogidix.rapidassist.orchestration.location.infrastructure.adapter.storage;

import java.time.LocalDateTime;

import com.gogidix.rapidassist.orchestration.location.domain.model.Geofence;
import com.gogidix.rapidassist.orchestration.location.domain.port.out.GeofenceRepositoryPort;
import com.gogidix.rapidassist.orchestration.location.infrastructure.persistence.mongo.GeofenceMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * MongoDB adapter for Geofence repository
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GeofenceRepositoryAdapter implements GeofenceRepositoryPort {

    private final GeofenceMongoRepository repository;

    @Override
    public Geofence save(Geofence geofence) {
        log.debug("Saving geofence: {}", geofence.getName());
        return repository.save(geofence);
    }

    @Override
    public Optional<Geofence> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<Geofence> findByTenantId(String tenantId) {
        return repository.findByTenantId(tenantId);
    }

    @Override
    public List<Geofence> findByTenantIdAndStatus(String tenantId, Geofence.GeofenceStatus status) {
        return repository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public List<Geofence> findActiveGeofencesForEntityType(String tenantId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType) {
        return repository.findActiveGeofencesForEntityType(tenantId, entityType.name(), java.time.LocalDateTime.now());
    }

    @Override
    public List<Geofence> findGeofencesContainingPoint(String tenantId, Double latitude, Double longitude) {
        LocalDateTime now = java.time.LocalDateTime.now();

        // Find both circle and polygon geofences
        List<Geofence> circleGeofences = repository.findCircleGeofencesContainingPoint(
                tenantId, longitude, latitude, Double.MAX_VALUE, now
        );
        List<Geofence> polygonGeofences = repository.findPolygonGeofencesContainingPoint(
                tenantId, longitude, latitude, now
        );

        // Combine results
        circleGeofences.addAll(polygonGeofences);
        return circleGeofences;
    }

    @Override
    public List<Geofence> findByTenantIdAndCategory(String tenantId, String category) {
        return repository.findByTenantIdAndCategory(tenantId, category);
    }

    @Override
    public void deleteById(String id) {
        repository.deleteById(id);
    }

    @Override
    public List<Geofence> saveAll(List<Geofence> geofences) {
        return repository.saveAll(geofences);
    }
}
