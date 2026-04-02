package com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.repository;

import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.entity.DataQualityMetricEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for DataQualityMetricEntity
 */
@Repository
public interface SpringDataQualityMetricRepository extends MongoRepository<DataQualityMetricEntity, UUID> {

    List<DataQualityMetricEntity> findByTenantId(String tenantId);

    List<DataQualityMetricEntity> findByTenantIdAndMetricName(String tenantId, String metricName);

    List<DataQualityMetricEntity> findByTenantIdAndEntityType(String tenantId, String entityType);

    @Query("{ 'tenantId': ?0, 'metricTimestamp': { $gte: ?1, $lte: ?2 } }")
    List<DataQualityMetricEntity> findByTenantIdAndMetricTimestampBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'tenantId': ?0, 'metricName': ?1 }")
    List<DataQualityMetricEntity> findLatestMetricsByTenantIdAndMetricName(String tenantId, String metricName);

    @Query("{ 'tenantId': ?0, 'metricName': ?1, 'entityType': ?2 }")
    Optional<DataQualityMetricEntity> findLatestByTenantIdAndMetricNameAndEntityType(String tenantId, String metricName, String entityType);

    @Query("{ 'tenantId': ?0 }")
    List<DataQualityMetricEntity> findRecentMetrics(String tenantId);

    @Query("{ 'tenantId': ?0, 'id': ?1 }")
    Optional<DataQualityMetricEntity> findByTenantIdAndId(String tenantId, UUID id);

    @Query(value = "{ 'tenantId': ?0 }", count = true)
    long countByTenantId(String tenantId);
}
