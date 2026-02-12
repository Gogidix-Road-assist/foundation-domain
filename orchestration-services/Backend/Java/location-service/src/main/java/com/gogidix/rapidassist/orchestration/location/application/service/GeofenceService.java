package com.gogidix.rapidassist.orchestration.location.application.service;

import com.gogidix.rapidassist.orchestration.location.domain.model.Geofence;
import com.gogidix.rapidassist.orchestration.location.domain.model.Location;
import com.gogidix.rapidassist.orchestration.location.domain.model.LocationAlert;
import com.gogidix.rapidassist.orchestration.location.domain.port.in.GeofenceServicePort;
import com.gogidix.rapidassist.orchestration.location.domain.port.out.GeofenceRepositoryPort;
import com.gogidix.rapidassist.orchestration.location.domain.port.out.LocationAlertRepositoryPort;
import com.gogidix.rapidassist.orchestration.location.domain.port.out.LocationRepositoryPort;
import com.gogidix.rapidassist.orchestration.location.infrastructure.messaging.kafka.GeofenceEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Application service for Geofence operations
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeofenceService implements GeofenceServicePort {

    private final GeofenceRepositoryPort geofenceRepository;
    private final LocationRepositoryPort locationRepository;
    private final LocationAlertRepositoryPort alertRepository;
    private final GeofenceEventPublisher eventPublisher;

    // Track entity states for dwell time detection
    private final Map<String, Map<String, LocalDateTime>> entityGeofenceEntryTimes = new ConcurrentHashMap<>();

    @Override
    @Transactional
    public Geofence createGeofence(String tenantId, Geofence geofence) {
        log.info("Creating geofence: {} for tenant: {}", geofence.getName(), tenantId);

        geofence.setTenantId(tenantId);
        geofence.setId(UUID.randomUUID().toString());
        geofence.setCreatedAt(LocalDateTime.now());
        geofence.setUpdatedAt(LocalDateTime.now());

        if (geofence.getStatus() == null) {
            geofence.setStatus(Geofence.GeofenceStatus.ACTIVE);
        }

        Geofence saved = geofenceRepository.save(geofence);
        eventPublisher.publishGeofenceCreated(saved);

        return saved;
    }

    @Override
    @Transactional
    public Geofence updateGeofence(String tenantId, String geofenceId, Geofence geofence) {
        log.info("Updating geofence: {} for tenant: {}", geofenceId, tenantId);

        Geofence existing = geofenceRepository.findById(geofenceId)
                .orElseThrow(() -> new IllegalArgumentException("Geofence not found: " + geofenceId));

        if (!existing.getTenantId().equals(tenantId)) {
            throw new IllegalArgumentException("Geofence does not belong to tenant");
        }

        geofence.setId(geofenceId);
        geofence.setTenantId(tenantId);
        geofence.setUpdatedAt(LocalDateTime.now());

        Geofence updated = geofenceRepository.save(geofence);
        eventPublisher.publishGeofenceUpdated(updated);

        return updated;
    }

    @Override
    public Geofence getGeofence(String tenantId, String geofenceId) {
        return geofenceRepository.findById(geofenceId)
                .filter(g -> g.getTenantId().equals(tenantId))
                .orElseThrow(() -> new IllegalArgumentException("Geofence not found: " + geofenceId));
    }

    @Override
    public List<Geofence> getGeofences(String tenantId) {
        return geofenceRepository.findByTenantId(tenantId);
    }

    @Override
    public List<Geofence> getActiveGeofencesForEntityType(String tenantId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType) {
        return geofenceRepository.findActiveGeofencesForEntityType(tenantId, entityType);
    }

    @Override
    public List<Geofence> findGeofencesContainingPoint(String tenantId, Double latitude, Double longitude) {
        return geofenceRepository.findGeofencesContainingPoint(tenantId, latitude, longitude);
    }

    @Override
    public boolean isLocationInGeofence(Location location, Geofence geofence) {
        if (!geofence.isActive()) {
            return false;
        }

        if (geofence.getType() == Geofence.GeofenceType.CIRCLE) {
            Object[] coords = ((java.util.List<Object>) geofence.getCenter().get("coordinates")).toArray();
            return location.isWithinRadius(
                    (Double) coords[1],
                    (Double) coords[0],
                    geofence.getRadius()
            );
        } else {
            // Polygon containment check would require GeoJSON library
            // For now, use MongoDB's geoIntersects query
            return !geofenceRepository.findGeofencesContainingPoint(
                    geofence.getTenantId(),
                    location.getLatitude(),
                    location.getLongitude()
            ).isEmpty();
        }
    }

    @Override
    @Transactional
    public void deleteGeofence(String tenantId, String geofenceId) {
        log.info("Deleting geofence: {} for tenant: {}", geofenceId, tenantId);

        Geofence geofence = getGeofence(tenantId, geofenceId);
        geofenceRepository.deleteById(geofenceId);

        eventPublisher.publishGeofenceDeleted(geofence);
    }

    @Override
    public List<LocationAlert> getGeofenceAlerts(String tenantId, String geofenceId) {
        return alertRepository.findByGeofenceId(geofenceId)
                .stream()
                .filter(a -> a.getTenantId().equals(tenantId))
                .toList();
    }

    @Override
    @Transactional
    public void processGeofenceBreaches(String tenantId, String entityId, com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType, Location location) {
        log.debug("Processing geofence breaches for entity: {} {}", entityType, entityId);

        List<Geofence> geofences = getActiveGeofencesForEntityType(tenantId, entityType);

        for (Geofence geofence : geofences) {
            if (!geofence.monitorsEntity(entityId)) {
                continue;
            }

            boolean wasInside = isEntityInGeofence(tenantId, entityId, geofence.getId());
            boolean isInside = isLocationInGeofence(location, geofence);

            processGeofenceTransition(tenantId, entityId, entityType, location, geofence, wasInside, isInside);
        }
    }

    private void processGeofenceTransition(
            String tenantId,
            String entityId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            Location location,
            Geofence geofence,
            boolean wasInside,
            boolean isInside
    ) {
        String key = tenantId + ":" + entityId;

        if (!wasInside && isInside && geofence.getMonitorEntry()) {
            // ENTER event
            createAlert(tenantId, geofence, entityId, entityType, location, LocationAlert.AlertEventType.ENTER);
            entityGeofenceEntryTimes.computeIfAbsent(key, k -> new ConcurrentHashMap<>())
                    .put(geofence.getId(), LocalDateTime.now());

        } else if (wasInside && !isInside && geofence.getMonitorExit()) {
            // EXIT event
            createAlert(tenantId, geofence, entityId, entityType, location, LocationAlert.AlertEventType.EXIT);
            entityGeofenceEntryTimes.getOrDefault(key, new ConcurrentHashMap<>()).remove(geofence.getId());

        } else if (isInside && geofence.getMonitorDwell()) {
            // Check dwell time
            LocalDateTime entryTime = entityGeofenceEntryTimes.getOrDefault(key, new ConcurrentHashMap<>())
                    .get(geofence.getId());

            if (entryTime != null) {
                long dwellDuration = java.time.Duration.between(entryTime, LocalDateTime.now()).toMillis();
                if (dwellDuration >= geofence.getDwellTimeThreshold()) {
                    createAlert(tenantId, geofence, entityId, entityType, location, LocationAlert.AlertEventType.DWELL, (int) dwellDuration);
                    // Remove to avoid duplicate alerts
                    entityGeofenceEntryTimes.get(key).remove(geofence.getId());
                }
            }
        }
    }

    private void createAlert(
            String tenantId,
            Geofence geofence,
            String entityId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            Location location,
            LocationAlert.AlertEventType eventType
    ) {
        createAlert(tenantId, geofence, entityId, entityType, location, eventType, null);
    }

    private void createAlert(
            String tenantId,
            Geofence geofence,
            String entityId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            Location location,
            LocationAlert.AlertEventType eventType,
            Integer dwellDuration
    ) {
        LocationAlert alert = LocationAlert.builder()
                .id(UUID.randomUUID().toString())
                .tenantId(tenantId)
                .geofenceId(geofence.getId())
                .geofenceName(geofence.getName())
                .entityType(entityType)
                .entityId(entityId)
                .latitude(location.getLatitude())
                .longitude(location.getLongitude())
                .coordinates(location.getCoordinates())
                .eventType(eventType)
                .status(LocationAlert.AlertStatus.PENDING)
                .timestamp(LocalDateTime.now())
                .dwellDuration(dwellDuration)
                .dwellThreshold(geofence.getDwellTimeThreshold())
                .message(buildAlertMessage(eventType, geofence.getName()))
                .severity(determineSeverity(eventType, geofence))
                .build();

        LocationAlert saved = alertRepository.save(alert);
        eventPublisher.publishAlertCreated(saved);
    }

    private String buildAlertMessage(LocationAlert.AlertEventType eventType, String geofenceName) {
        return String.format("Entity %s geofence: %s", eventType.name(), geofenceName);
    }

    private String determineSeverity(LocationAlert.AlertEventType eventType, Geofence geofence) {
        // Could be enhanced with geofence-specific severity rules
        return switch (eventType) {
            case ENTER -> "MEDIUM";
            case EXIT -> "LOW";
            case DWELL -> "HIGH";
        };
    }

    private boolean isEntityInGeofence(String tenantId, String entityId, String geofenceId) {
        String key = tenantId + ":" + entityId;
        Map<String, LocalDateTime> geofenceStates = entityGeofenceEntryTimes.get(key);
        return geofenceStates != null && geofenceStates.containsKey(geofenceId);
    }
}
