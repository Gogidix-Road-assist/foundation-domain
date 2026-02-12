package com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.repository;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityCheck;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.entity.DataQualityCheckEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for DataQualityCheckEntity
 */
@Repository
public interface SpringDataQualityCheckRepository extends MongoRepository<DataQualityCheckEntity, UUID> {

    List<DataQualityCheckEntity> findByTenantId(String tenantId);

    List<DataQualityCheckEntity> findByTenantIdAndRuleId(String tenantId, UUID ruleId);

    List<DataQualityCheckEntity> findByTenantIdAndStatus(String tenantId, DataQualityCheck.CheckStatus status);

    List<DataQualityCheckEntity> findByTenantIdAndEntityType(String tenantId, String entityType);

    @Query("{ 'tenantId': ?0, 'executedAt': { $gte: ?1, $lte: ?2 } }")
    List<DataQualityCheckEntity> findByTenantIdAndExecutedAtBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'tenantId': ?0, 'status': { $in: ['FAILED', 'COMPLETED'] }, 'failedRecords': { $gt: 0 } }")
    List<DataQualityCheckEntity> findFailedChecks(String tenantId);

    @Query("{ 'tenantId': ?0, 'executedAt': { $gte: ?1 } }")
    List<DataQualityCheckEntity> findRecentChecks(String tenantId, LocalDateTime since);

    @Query("{ 'tenantId': ?0, 'id': ?1 }")
    Optional<DataQualityCheckEntity> findByTenantIdAndId(String tenantId, UUID id);

    @Query(value = "{ 'tenantId': ?0, 'status': ?1 }", count = true)
    long countByTenantIdAndStatus(String tenantId, DataQualityCheck.CheckStatus status);
}
