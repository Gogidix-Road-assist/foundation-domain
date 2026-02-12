package com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.repository;

import com.gogidix.rapidassist.ai.dataquality.domain.model.DataQualityRule;
import com.gogidix.rapidassist.ai.dataquality.infrastructure.persistence.mongodb.entity.DataQualityRuleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for DataQualityRuleEntity
 */
@Repository
public interface SpringDataQualityRuleRepository extends MongoRepository<DataQualityRuleEntity, UUID> {

    List<DataQualityRuleEntity> findByTenantId(String tenantId);

    Optional<DataQualityRuleEntity> findByTenantIdAndName(String tenantId, String name);

    List<DataQualityRuleEntity> findByTenantIdAndActive(String tenantId, boolean active);

    List<DataQualityRuleEntity> findByTenantIdAndEntityType(String tenantId, String entityType);

    List<DataQualityRuleEntity> findByTenantIdAndRuleType(String tenantId, DataQualityRule.RuleType ruleType);

    List<DataQualityRuleEntity> findByTenantIdAndSeverity(String tenantId, DataQualityRule.RuleSeverity severity);

    boolean existsByTenantIdAndName(String tenantId, String name);

    @Query("{ 'tenantId': ?0, 'id': ?1 }")
    Optional<DataQualityRuleEntity> findByTenantIdAndId(String tenantId, UUID id);

    @Query("{ 'tenantId': ?0 }")
    List<DataQualityRuleEntity> deleteByTenantIdAndId(String tenantId, UUID id);
}
