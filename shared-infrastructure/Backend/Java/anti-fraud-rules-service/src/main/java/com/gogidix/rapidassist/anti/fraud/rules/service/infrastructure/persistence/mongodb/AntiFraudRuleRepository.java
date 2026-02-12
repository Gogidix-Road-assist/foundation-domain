package com.gogidix.rapidassist.anti.fraud.rules.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.anti.fraud.rules.service.domain.model.AntiFraudRule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for AntiFraudRule with MANDATORY tenant filtering.
 * CRITICAL: All queries MUST filter by tenantId to prevent cross-tenant data leakage.
 */
@Repository
public interface AntiFraudRuleRepository extends MongoRepository<AntiFraudRuleDocument, String> {

    /**
     * Find all rules for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    List<AntiFraudRuleDocument> findByTenantId(String tenantId);

    /**
     * Find active rules for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    List<AntiFraudRuleDocument> findByTenantIdAndActiveTrue(String tenantId);

    /**
     * Find active rules by type for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    List<AntiFraudRuleDocument> findByTenantIdAndRuleTypeAndActiveTrue(
            String tenantId,
            AntiFraudRule.RuleType ruleType
    );

    /**
     * Find rules by priority range for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    List<AntiFraudRuleDocument> findByTenantIdAndPriorityBetweenOrderByPriorityDesc(
            String tenantId,
            int minPriority,
            int maxPriority
    );

    /**
     * Find a specific rule by ID and tenant.
     * CRITICAL: Always filter by tenantId to prevent cross-tenant access.
     */
    Optional<AntiFraudRuleDocument> findByIdAndTenantId(String id, String tenantId);

    /**
     * Check if a rule exists for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    boolean existsByIdAndTenantId(String id, String tenantId);

    /**
     * Delete all rules for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    void deleteByTenantId(String tenantId);

    /**
     * Find active rules by priority for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    @Query("{ 'tenantId': ?0, 'active': true }")
    List<AntiFraudRuleDocument> findActiveByTenantIdOrderByPriorityDesc(String tenantId);

    /**
     * Count active rules for a specific tenant.
     * CRITICAL: Always filter by tenantId.
     */
    long countByTenantIdAndActiveTrue(String tenantId);
}
