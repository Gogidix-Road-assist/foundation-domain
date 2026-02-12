package com.gogidix.rapidassist.ai.anomaly.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.anomaly.domain.model.AlertSeverity;
import com.gogidix.rapidassist.ai.anomaly.domain.model.AlertStatus;
import com.gogidix.rapidassist.ai.anomaly.infrastructure.persistence.entity.AnomalyAlertEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for AnomalyAlertEntity.
 */
@Repository
public interface AnomalyAlertMongoRepository extends MongoRepository<AnomalyAlertEntity, String> {

    List<AnomalyAlertEntity> findByTenantId(String tenantId);

    List<AnomalyAlertEntity> findByTenantIdAndDetectionId(String tenantId, UUID detectionId);

    List<AnomalyAlertEntity> findByTenantIdAndSeverity(String tenantId, AlertSeverity severity);

    List<AnomalyAlertEntity> findByTenantIdAndStatus(String tenantId, AlertStatus status);

    List<AnomalyAlertEntity> findByTenantIdAndAssignedTo(String tenantId, String assignedTo);

    List<AnomalyAlertEntity> findByTenantIdAndTriggeredAtBetween(
        String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'tenantId': ?0, 'status': 'OPEN' }")
    List<AnomalyAlertEntity> findOpenAlerts(String tenantId);

    @Query("{ 'tenantId': ?0, 'severity': 'CRITICAL' }")
    List<AnomalyAlertEntity> findCriticalAlerts(String tenantId);

    AnomalyAlertEntity findByTenantIdAndAlertId(String tenantId, String alertId);

    boolean existsByTenantIdAndUuid(String tenantId, UUID uuid);

    long countByTenantId(String tenantId);

    long countByTenantIdAndStatus(String tenantId, AlertStatus status);
}
