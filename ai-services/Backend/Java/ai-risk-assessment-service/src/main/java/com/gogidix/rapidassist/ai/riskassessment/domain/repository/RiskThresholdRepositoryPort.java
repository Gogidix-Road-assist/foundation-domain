package com.gogidix.rapidassist.ai.riskassessment.domain.repository;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskCategory;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskThreshold;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Port for Risk Threshold
 * Defines the contract for risk threshold persistence operations
 */
public interface RiskThresholdRepositoryPort {

    /**
     * Save risk threshold
     */
    RiskThreshold save(RiskThreshold threshold);

    /**
     * Find risk threshold by ID and tenant
     */
    Optional<RiskThreshold> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Find risk threshold by name and tenant
     */
    Optional<RiskThreshold> findByNameAndTenantId(String name, String tenantId);

    /**
     * Find all risk thresholds by tenant
     */
    List<RiskThreshold> findByTenantId(String tenantId);

    /**
     * Find active risk thresholds by tenant
     */
    List<RiskThreshold> findActiveByTenantId(String tenantId);

    /**
     * Find risk threshold by category and tenant
     */
    Optional<RiskThreshold> findByCategoryAndTenantId(RiskCategory category, String tenantId);

    /**
     * Find default risk threshold for tenant
     */
    Optional<RiskThreshold> findDefaultByTenantId(String tenantId);

    /**
     * Check if risk threshold exists
     */
    boolean existsByIdAndTenantId(UUID id, String tenantId);

    /**
     * Delete risk threshold by ID and tenant
     */
    void deleteByIdAndTenantId(UUID id, String tenantId);

    /**
     * Count risk thresholds by tenant
     */
    long countByTenantId(String tenantId);
}
