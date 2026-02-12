package com.gogidix.rapidassist.ai.anomaly.domain.repository;

import com.gogidix.rapidassist.ai.anomaly.domain.model.DetectionRule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving DetectionRule entities.
 * Implementations are provided by the Infrastructure layer.
 */
public interface DetectionRuleRepositoryPort {

    /**
     * Save a detection rule (create or update).
     */
    DetectionRule save(String tenantId, DetectionRule rule);

    /**
     * Find a rule by ID and tenant.
     */
    Optional<DetectionRule> findById(String tenantId, UUID ruleId);

    /**
     * Find all rules for a tenant.
     */
    List<DetectionRule> findByTenantId(String tenantId);

    /**
     * Find active rules for a tenant.
     */
    List<DetectionRule> findActiveRules(String tenantId);

    /**
     * Find rules by category and tenant.
     */
    List<DetectionRule> findByCategory(String tenantId, String category);

    /**
     * Find rules by data source and tenant.
     */
    List<DetectionRule> findByDataSource(String tenantId, String dataSource);

    /**
     * Find rules by priority range and tenant.
     */
    List<DetectionRule> findByPriorityBetween(String tenantId, Integer minPriority, Integer maxPriority);

    /**
     * Find high priority rules for a tenant.
     */
    List<DetectionRule> findHighPriorityRules(String tenantId);

    /**
     * Delete a rule by ID and tenant.
     */
    void delete(String tenantId, UUID ruleId);

    /**
     * Check if a rule exists.
     */
    boolean exists(String tenantId, UUID ruleId);

    /**
     * Count rules by tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count active rules by tenant.
     */
    long countActiveRules(String tenantId);
}
