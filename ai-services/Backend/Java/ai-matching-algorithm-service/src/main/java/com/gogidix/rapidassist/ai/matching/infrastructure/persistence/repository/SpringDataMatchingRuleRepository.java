package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.repository;

import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity.MatchingRuleEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data MongoDB Repository for MatchingRuleEntity.
 */
@Repository
public interface SpringDataMatchingRuleRepository extends MongoRepository<MatchingRuleEntity, String> {

    /**
     * Find rule by UUID and tenant.
     */
    Optional<MatchingRuleEntity> findByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Find rule by rule code and tenant.
     */
    Optional<MatchingRuleEntity> findByRuleCodeAndTenantId(String ruleCode, String tenantId);

    /**
     * Find all rules by tenant.
     */
    List<MatchingRuleEntity> findByTenantId(String tenantId);

    /**
     * Find rules by entity types.
     */
    List<MatchingRuleEntity> findBySourceEntityTypeAndTargetEntityTypeAndTenantId(
            String sourceEntityType, String targetEntityType, String tenantId);

    /**
     * Find active rules by tenant.
     */
    List<MatchingRuleEntity> findByActiveTrueAndTenantId(String tenantId);

    /**
     * Find active rules by entity types and tenant, ordered by priority.
     */
    List<MatchingRuleEntity> findByActiveTrueAndSourceEntityTypeAndTargetEntityTypeAndTenantIdOrderByPriorityDesc(
            String sourceEntityType, String targetEntityType, String tenantId);

    /**
     * Check if rule exists.
     */
    boolean existsByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Delete rule by UUID and tenant.
     */
    void deleteByUuidAndTenantId(UUID uuid, String tenantId);

    /**
     * Count rules by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Check if rule code exists.
     */
    boolean existsByRuleCodeAndTenantId(String ruleCode, String tenantId);
}
