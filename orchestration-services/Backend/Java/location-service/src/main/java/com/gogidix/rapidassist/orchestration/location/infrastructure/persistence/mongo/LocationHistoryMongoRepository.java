package com.gogidix.rapidassist.orchestration.location.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.orchestration.location.domain.model.LocationHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MongoDB repository for LocationHistory with geospatial indexing
 */
public interface LocationHistoryMongoRepository extends MongoRepository<LocationHistory, String> {

    List<LocationHistory> findByTenantIdAndEntityTypeAndEntityIdAndTimestampBetween(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    List<LocationHistory> findByTenantIdAndEntityTypeAndEntityIdOrderByTimestampDesc(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId,
            Pageable pageable
    );

    List<LocationHistory> findBySessionId(String sessionId);

    /**
     * Find history within geographic area and time range
     * Uses 2dsphere index for geospatial queries
     */
    @Query("{ 'tenantId': ?0, 'coordinates': { $geoWithin: { $centerSphere: [ [ ?1, ?2 ], ?3 ] } }, 'timestamp': { $gte: ?4, $lte: ?5 } }")
    List<LocationHistory> findHistoryWithinAreaAndTimeRange(
            String tenantId,
            Double longitude,
            Double latitude,
            Double radiusInRadians,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    /**
     * Find history older than specific date (for cleanup)
     */
    List<LocationHistory> findByTenantIdAndTimestampBefore(
            String tenantId,
            LocalDateTime timestamp
    );

    void deleteByTenantIdAndTimestampBefore(
            String tenantId,
            LocalDateTime timestamp
    );
}
