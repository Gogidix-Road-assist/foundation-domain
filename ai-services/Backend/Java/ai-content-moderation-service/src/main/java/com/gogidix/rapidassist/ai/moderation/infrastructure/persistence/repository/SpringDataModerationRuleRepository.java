package com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.moderation.domain.model.ModerationRule;
import com.gogidix.rapidassist.ai.moderation.infrastructure.persistence.document.ModerationRuleDocument;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data MongoDB repository for ModerationRuleDocument
 */
@Repository
public interface SpringDataModerationRuleRepository extends MongoRepository<ModerationRuleDocument, String> {

    List<ModerationRuleDocument> findByTenantId(String tenantId);

    List<ModerationRuleDocument> findByTenantIdAndActiveTrue(String tenantId);

    List<ModerationRuleDocument> findByTenantIdAndRuleType(String tenantId, ModerationRule.RuleType ruleType);

    boolean existsByTenantIdAndName(String tenantId, String name);

    @Query("{ 'tenantId': ?0, 'active': true, 'priority': { $gte: ?1 } }")
    List<ModerationRuleDocument> findByTenantIdAndActiveTrueAndPriorityGreaterThanEqual(String tenantId, Integer priority);
}
