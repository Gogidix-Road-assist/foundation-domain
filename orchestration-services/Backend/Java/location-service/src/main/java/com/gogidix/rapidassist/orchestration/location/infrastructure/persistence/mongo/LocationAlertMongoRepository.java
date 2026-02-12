package com.gogidix.rapidassist.orchestration.location.infrastructure.persistence.mongo;

import com.gogidix.rapidassist.orchestration.location.domain.model.LocationAlert;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * MongoDB repository for LocationAlert
 */
public interface LocationAlertMongoRepository extends MongoRepository<LocationAlert, String> {

    List<LocationAlert> findByGeofenceId(String geofenceId);

    List<LocationAlert> findByTenantIdAndEntityTypeAndEntityId(
            String tenantId,
            com.gogidix.rapidassist.orchestration.location.domain.model.Location.EntityType entityType,
            String entityId
    );

    List<LocationAlert> findByTenantIdAndStatus(
            String tenantId,
            LocationAlert.AlertStatus status
    );

    List<LocationAlert> findByTenantIdAndStatusOrderByTimestampAsc(
            String tenantId,
            LocationAlert.AlertStatus status
    );

    List<LocationAlert> findByTenantIdAndTimestampBetween(
            String tenantId,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    List<LocationAlert> findByTenantIdOrderByTimestampDesc(
            String tenantId,
            org.springframework.data.domain.Pageable pageable
    );

    List<LocationAlert> findByTenantIdAndTimestampBefore(
            String tenantId,
            LocalDateTime timestamp
    );

    void deleteByTenantIdAndTimestampBefore(
            String tenantId,
            LocalDateTime timestamp
    );
}
