package com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.entity.TaggingRuleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB repository for TaggingRuleEntity
 */
@Repository
public interface SpringDataTaggingRuleRepository extends MongoRepository<TaggingRuleEntity, String> {

    List<TaggingRuleEntity> findByTenantId(String tenantId);

    List<TaggingRuleEntity> findByTenantIdAndStatus(String tenantId, com.gogidix.rapidassist.ai.tagging.domain.model.TaggingRule.RuleStatus status);

    List<TaggingRuleEntity> findByTenantIdAndStatusAndRuleType(String tenantId,
                                                                 com.gogidix.rapidassist.ai.tagging.domain.model.TaggingRule.RuleStatus status,
                                                                 com.gogidix.rapidassist.ai.tagging.domain.model.TaggingRule.RuleType ruleType);

    List<TaggingRuleEntity> findByTenantIdAndPriority(String tenantId, com.gogidix.rapidassist.ai.tagging.domain.model.TaggingRule.RulePriority priority);

    List<TaggingRuleEntity> findByTenantIdAndNameContainingIgnoreCase(String tenantId, String name);

    boolean existsByTenantIdAndName(String tenantId, String name);

    @Query("{ 'uuid': ?0 }")
    Optional<TaggingRuleEntity> findByUuid(UUID uuid);

    TaggingRuleEntity findByTenantIdAndUuid(String tenantId, UUID uuid);

    void deleteByTenantIdAndUuid(String tenantId, UUID uuid);

    long countByTenantId(String tenantId);

    long countByTenantIdAndStatus(String tenantId, com.gogidix.rapidassist.ai.tagging.domain.model.TaggingRule.RuleStatus status);
}
