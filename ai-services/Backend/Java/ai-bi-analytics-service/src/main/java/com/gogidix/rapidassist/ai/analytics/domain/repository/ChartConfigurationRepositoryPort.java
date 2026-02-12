package com.gogidix.rapidassist.ai.analytics.domain.repository;

import com.gogidix.rapidassist.ai.analytics.domain.model.ChartConfiguration;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for ChartConfiguration
 */
public interface ChartConfigurationRepositoryPort {

    /**
     * Save a chart configuration
     */
    ChartConfiguration save(ChartConfiguration chart);

    /**
     * Find chart by ID
     */
    Optional<ChartConfiguration> findById(UUID id);

    /**
     * Find chart by ID and tenant ID
     */
    Optional<ChartConfiguration> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Find all charts for a tenant
     */
    List<ChartConfiguration> findByTenantId(String tenantId);

    /**
     * Find charts by dashboard ID
     */
    List<ChartConfiguration> findByDashboardId(UUID dashboardId);

    /**
     * Find active charts for a tenant
     */
    List<ChartConfiguration> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    /**
     * Delete a chart
     */
    void deleteById(UUID id);

    /**
     * Check if chart exists
     */
    boolean existsById(UUID id);
}
