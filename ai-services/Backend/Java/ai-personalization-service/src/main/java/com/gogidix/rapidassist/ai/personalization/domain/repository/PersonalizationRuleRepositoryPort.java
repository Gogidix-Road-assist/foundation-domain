package com.gogidix.rapidassist.ai.personalization.domain.repository;

import com.gogidix.rapidassist.ai.personalization.domain.model.PersonalizationRule;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port (Hexagonal Architecture - Outbound Port).
 * Defines the contract for persisting and retrieving PersonalizationRule entities.
 */
public interface PersonalizationRuleRepositoryPort {

    /**
     * Save a personalization rule (create or update).
     */
    PersonalizationRule save(String tenantId, PersonalizationRule rule);

    /**
     * Find a rule by ID and tenant.
     */
    Optional<PersonalizationRule> findById(String tenantId, UUID ruleId);

    /**
     * Find a rule by code and tenant.
     */
    Optional<PersonalizationRule> findByRuleCode(String tenantId, String ruleCode);

    /**
     * Find all rules for a tenant.
     */
    List<PersonalizationRule> findByTenantId(String tenantId);

    /**
     * Find rules by type and tenant.
     */
    List<PersonalizationRule> findByRuleType(String tenantId, String ruleType);

    /**
     * Find rules by status and tenant.
     */
    List<PersonalizationRule> findByStatus(String tenantId, String status);

    /**
     * Find rules by category and tenant.
     */
    List<PersonalizationRule> findByCategory(String tenantId, String category);

    /**
     * Find active rules at a given datetime.
     */
    List<PersonalizationRule> findActiveRules(String tenantId, LocalDateTime dateTime);

    /**
     * Find rules by minimum priority.
     */
    List<PersonalizationRule> findByPriorityGreaterThanEqual(String tenantId, Integer priority);

    /**
     * Delete a rule by ID and tenant.
     */
    void delete(String tenantId, UUID ruleId);

    /**
     * Check if a rule exists.
     */
    boolean exists(String tenantId, UUID ruleId);

    /**
     * Check if a rule exists by code.
     */
    boolean existsByRuleCode(String tenantId, String ruleCode);

    /**
     * Count rules by tenant.
     */
    long countByTenantId(String tenantId);
}
