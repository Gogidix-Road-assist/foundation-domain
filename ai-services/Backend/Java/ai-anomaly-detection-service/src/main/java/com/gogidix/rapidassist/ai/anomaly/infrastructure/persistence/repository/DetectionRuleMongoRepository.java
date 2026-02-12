package com.gogidix.rapidassist.ai.anomaly.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.anomaly.infrastructure.persistence.entity.DetectionRuleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for DetectionRuleEntity.
 */
@Repository
public interface DetectionRuleMongoRepository extends MongoRepository<DetectionRuleEntity, String> {

    List<DetectionRuleEntity> findByTenantId(String tenantId);

    List<DetectionRuleEntity> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    List<DetectionRuleEntity> findByTenantIdAndCategory(String tenantId, String category);

    List<DetectionRuleEntity> findByTenantIdAndDataSource(String tenantId, String dataSource);

    List<DetectionRuleEntity> findByTenantIdAndPriorityBetween(
        String tenantId, Integer minPriority, Integer maxPriority);

    @Query("{ 'tenantId': ?0, 'priority': { $gte: 8 }, 'isActive': true }")
    List<DetectionRuleEntity> findHighPriorityRules(String tenantId);

    boolean existsByTenantIdAndUuid(String tenantId, java.util.UUID uuid);

    long countByTenantId(String tenantId);

    long countByTenantIdAndIsActive(String tenantId, Boolean isActive);
}
