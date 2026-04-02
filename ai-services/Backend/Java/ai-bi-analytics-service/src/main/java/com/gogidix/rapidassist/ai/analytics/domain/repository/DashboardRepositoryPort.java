package com.gogidix.rapidassist.ai.analytics.domain.repository;

import com.gogidix.rapidassist.ai.analytics.domain.model.Dashboard;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for Dashboard
 */
public interface DashboardRepositoryPort {

    /**
     * Save a dashboard
     */
    Dashboard save(Dashboard dashboard);

    /**
     * Find dashboard by ID
     */
    Optional<Dashboard> findById(UUID id);

    /**
     * Find dashboard by ID and tenant ID
     */
    Optional<Dashboard> findByIdAndTenantId(UUID id, String tenantId);

    /**
     * Find all dashboards for a tenant
     */
    List<Dashboard> findByTenantId(String tenantId);

    /**
     * Find public dashboards for a tenant
     */
    List<Dashboard> findByTenantIdAndIsPublic(String tenantId, Boolean isPublic);

    /**
     * Find active dashboards for a tenant
     */
    List<Dashboard> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    /**
     * Find dashboards by category
     */
    List<Dashboard> findByTenantIdAndCategory(String tenantId, String category);

    /**
     * Delete a dashboard
     */
    void deleteById(UUID id);

    /**
     * Check if dashboard exists
     */
    boolean existsById(UUID id);
}
