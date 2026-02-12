package com.gogidix.rapidassist.orchestration.location.infrastructure.adapter.storage;

import com.gogidix.rapidassist.orchestration.location.domain.model.LocationAlert;
import com.gogidix.rapidassist.orchestration.location.domain.port.out.LocationAlertRepositoryPort;
import com.gogidix.rapidassist.orchestration.location.infrastructure.persistence.mongo.LocationAlertMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * MongoDB adapter for LocationAlert repository
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocationAlertRepositoryAdapter implements LocationAlertRepositoryPort {

    private final LocationAlertMongoRepository repository;

    @Override
    public LocationAlert save(LocationAlert alert) {
        log.debug("Saving location alert: {}", alert.getId());
        return repository.save(alert);
    }

    @Override
    public Optional<LocationAlert> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public List<LocationAlert> findByGeofenceId(String geofenceId) {
        return repository.findByGeofenceId(geofenceId);
    }

    @Override
    public List<LocationAlert> findByTenantIdAndEntityTypeAndEntityId(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId
    ) {
        return repository.findByTenantIdAndEntityTypeAndEntityId(tenantId, entityType, entityId);
    }

    @Override
    public List<LocationAlert> findByTenantIdAndStatus(String tenantId, LocationAlert.AlertStatus status) {
        return repository.findByTenantIdAndStatus(tenantId, status);
    }

    @Override
    public List<LocationAlert> findPendingAlerts(String tenantId) {
        return repository.findByTenantIdAndStatusOrderByTimestampAsc(tenantId, LocationAlert.AlertStatus.PENDING);
    }

    @Override
    public List<LocationAlert> findByTenantIdAndTimestampBetween(
            String tenantId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return repository.findByTenantIdAndTimestampBetween(tenantId, startTime, endTime);
    }

    @Override
    public List<LocationAlert> findRecentAlerts(String tenantId, Integer limit) {
        return repository.findByTenantIdOrderByTimestampDesc(tenantId, PageRequest.of(0, limit));
    }

    @Override
    public void deleteAlertsOlderThan(String tenantId, LocalDateTime retentionDate) {
        repository.deleteByTenantIdAndTimestampBefore(tenantId, retentionDate);
    }

    @Override
    public List<LocationAlert> saveAll(List<LocationAlert> alerts) {
        return repository.saveAll(alerts);
    }
}
