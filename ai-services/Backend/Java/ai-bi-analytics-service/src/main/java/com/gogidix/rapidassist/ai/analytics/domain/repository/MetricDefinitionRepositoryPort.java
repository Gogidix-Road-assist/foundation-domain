package com.gogidix.rapidassist.ai.analytics.domain.repository;

import com.gogidix.rapidassist.ai.analytics.domain.model.MetricDefinition;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for MetricDefinition
 */
public interface MetricDefinitionRepositoryPort {

    /**
     * Save a metric definition
     */
    MetricDefinition save(MetricDefinition metric);

    /**
     * Find metric by ID
     */
    Optional<MetricDefinition> findById(UUID id);

    /**
     * Find metric by ID and tenant ID
     */
    Optional<MetricDefinition> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Find metric by code
     */
    Optional<MetricDefinition> findByCodeAndTenantId(String code, String tenantId);

    /**
     * Find all metrics for a tenant
     */
    List<MetricDefinition> findByTenantId(String tenantId);

    /**
     * Find active metrics for a tenant
     */
    List<MetricDefinition> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    /**
     * Find metrics by category
     */
    List<MetricDefinition> findByTenantIdAndCategory(String tenantId, String category);

    /**
     * Delete a metric
     */
    void deleteById(UUID id);

    /**
     * Check if metric exists
     */
    boolean existsById(UUID id);

    /**
     * Check if metric code exists
     */
    boolean existsByCodeAndTenantId(String code, String tenantId);
}
