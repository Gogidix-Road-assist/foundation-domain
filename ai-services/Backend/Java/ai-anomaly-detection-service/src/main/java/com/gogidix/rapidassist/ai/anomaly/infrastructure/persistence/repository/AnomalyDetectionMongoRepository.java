package com.gogidix.rapidassist.ai.anomaly.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalySeverity;
import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalyStatus;
import com.gogidix.rapidassist.ai.anomaly.infrastructure.persistence.entity.AnomalyDetectionEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data MongoDB repository for AnomalyDetectionEntity.
 */
@Repository
public interface AnomalyDetectionMongoRepository extends MongoRepository<AnomalyDetectionEntity, String> {

    List<AnomalyDetectionEntity> findByTenantId(String tenantId);

    List<AnomalyDetectionEntity> findByTenantIdAndDataSource(String tenantId, String dataSource);

    List<AnomalyDetectionEntity> findByTenantIdAndSeverity(String tenantId, AnomalySeverity severity);

    List<AnomalyDetectionEntity> findByTenantIdAndStatus(String tenantId, AnomalyStatus status);

    List<AnomalyDetectionEntity> findByTenantIdAndDetectedAtBetween(
        String tenantId, LocalDateTime startDate, LocalDateTime endDate);


    List<AnomalyDetectionEntity> findByTenantIdAndAnomalyScoreGreaterThan(
        String tenantId, Double threshold);

    @Query("{ 'tenantId': ?0, 'status': 'PENDING' }")
    List<AnomalyDetectionEntity> findPendingDetections(String tenantId);

    @Query("{ 'tenantId': ?0, 'severity': 'CRITICAL' }")
    List<AnomalyDetectionEntity> findCriticalDetections(String tenantId);

    boolean existsByTenantIdAndUuid(String tenantId, java.util.UUID uuid);

    long countByTenantId(String tenantId);

    long countByTenantIdAndStatus(String tenantId, AnomalyStatus status);
}
