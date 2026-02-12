package com.gogidix.rapidassist.orchestration.location.infrastructure.adapter.storage;

import com.gogidix.rapidassist.orchestration.location.domain.model.LocationHistory;
import com.gogidix.rapidassist.orchestration.location.domain.port.out.LocationHistoryRepositoryPort;
import com.gogidix.rapidassist.orchestration.location.infrastructure.persistence.mongo.LocationHistoryMongoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MongoDB adapter for LocationHistory repository
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LocationHistoryRepositoryAdapter implements LocationHistoryRepositoryPort {

    private final LocationHistoryMongoRepository repository;

    @Override
    public LocationHistory save(LocationHistory history) {
        log.debug("Saving location history for entity: {} {}", history.getEntityType(), history.getEntityId());
        return repository.save(history);
    }

    @Override
    public List<LocationHistory> findByTenantIdAndEntityTypeAndEntityIdAndTimestampBetween(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return repository.findByTenantIdAndEntityTypeAndEntityIdAndTimestampBetween(
                tenantId, entityType, entityId, startTime, endTime
        );
    }

    @Override
    public List<LocationHistory> findRecentHistory(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId,
            Integer limit
    ) {
        return repository.findByTenantIdAndEntityTypeAndEntityIdOrderByTimestampDesc(
                tenantId, entityType, entityId, PageRequest.of(0, limit)
        );
    }

    @Override
    public List<LocationHistory> findBySessionId(String sessionId) {
        return repository.findBySessionId(sessionId);
    }

    @Override
    public List<LocationHistory> findHistoryWithinAreaAndTimeRange(
            String tenantId,
            Double latitude,
            Double longitude,
            Double radiusInMeters,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        double radiusInRadians = radiusInMeters / 6371000.0;
        return repository.findHistoryWithinAreaAndTimeRange(
                tenantId, longitude, latitude, radiusInRadians, startTime, endTime
        );
    }

    @Override
    public void deleteHistoryOlderThan(String tenantId, LocalDateTime retentionDate) {
        repository.deleteByTenantIdAndTimestampBefore(tenantId, retentionDate);
    }

    @Override
    public List<LocationHistory> saveAll(List<LocationHistory> historyList) {
        return repository.saveAll(historyList);
    }
}
