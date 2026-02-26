package com.gogidix.rapidassist.orchestration.location.domain.port.out;

import com.gogidix.rapidassist.orchestration.location.domain.model.Location;
import com.gogidix.rapidassist.orchestration.location.domain.model.LocationHistory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Output port for LocationHistory persistence operations
 */
public interface LocationHistoryRepositoryPort {

    LocationHistory save(LocationHistory history);

    /**
     * Find history for an entity within time range
     */
    List<LocationHistory> findByTenantIdAndEntityTypeAndEntityIdAndTimestampBetween(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * Find recent history for an entity
     */
    List<LocationHistory> findRecentHistory(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId,
            Integer limit
    );

    /**
     * Find history by session
     */
    List<LocationHistory> findBySessionId(String sessionId);

    /**
     * Find history within a geographic area and time range
     */
    List<LocationHistory> findHistoryWithinAreaAndTimeRange(
            String tenantId,
            Double latitude,
            Double longitude,
            Double radiusInMeters,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * Delete old history based on retention policy
     */
    void deleteHistoryOlderThan(String tenantId, LocalDateTime retentionDate);

    /**
     * Bulk save history entries
     */
    List<LocationHistory> saveAll(List<LocationHistory> historyList);
}
