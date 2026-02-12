package com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.repository;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityIssue;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.entity.DataQualityIssueEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for DataQualityIssueEntity
 */
@Repository
public interface SpringDataQualityIssueRepository extends MongoRepository<DataQualityIssueEntity, UUID> {

    List<DataQualityIssueEntity> findByTenantId(String tenantId);

    List<DataQualityIssueEntity> findByTenantIdAndStatus(String tenantId, DataQualityIssue.IssueStatus status);

    List<DataQualityIssueEntity> findByTenantIdAndSeverity(String tenantId, DataQualityIssue.IssueSeverity severity);

    List<DataQualityIssueEntity> findByTenantIdAndEntityType(String tenantId, String entityType);

    List<DataQualityIssueEntity> findByCheckId(String tenantId, UUID checkId);

    List<DataQualityIssueEntity> findByRuleId(String tenantId, UUID ruleId);

    @Query("{ 'tenantId': ?0, 'status': 'OPEN' }")
    List<DataQualityIssueEntity> findOpenIssues(String tenantId);

    @Query("{ 'tenantId': ?0, 'severity': 'CRITICAL' }")
    List<DataQualityIssueEntity> findCriticalIssues(String tenantId);

    @Query("{ 'tenantId': ?0, 'status': { $ne: 'RESOLVED' }, 'detectedAt': { $lt: ?1 } }")
    List<DataQualityIssueEntity> findStaleIssues(String tenantId, LocalDateTime staleThreshold);

    @Query("{ 'tenantId': ?0, 'detectedAt': { $gte: ?1, $lte: ?2 } }")
    List<DataQualityIssueEntity> findIssuesDetectedBetween(String tenantId, LocalDateTime startDate, LocalDateTime endDate);

    @Query("{ 'tenantId': ?0, 'id': ?1 }")
    Optional<DataQualityIssueEntity> findByTenantIdAndId(String tenantId, UUID id);

    @Query(value = "{ 'tenantId': ?0, 'status': ?1 }", count = true)
    long countByTenantIdAndStatus(String tenantId, DataQualityIssue.IssueStatus status);

    @Query(value = "{ 'tenantId': ?0, 'severity': ?1 }", count = true)
    long countByTenantIdAndSeverity(String tenantId, DataQualityIssue.IssueSeverity severity);
}
