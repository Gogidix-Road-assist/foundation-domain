package com.gogidix.rapidassist.orchestration.fleetorganization.domain.repository;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetPolicy;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetPolicy.PolicyType;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.FleetPolicy.PolicyScope;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for FleetPolicy entity.
 * This is the interface that defines the contract for FleetPolicy persistence operations.
 * Implemented by infrastructure layer (MongoDB).
 */
public interface FleetPolicyRepository {

    /**
     * Save a policy (create or update)
     */
    FleetPolicy save(FleetPolicy policy);

    /**
     * Find policy by ID and tenant ID
     */
    Optional<FleetPolicy> findByIdAndTenantId(String policyId, String tenantId);

    /**
     * Find all policies for a tenant
     */
    List<FleetPolicy> findByTenantId(String tenantId);

    /**
     * Find policies by organization ID
     */
    List<FleetPolicy> findByOrganizationIdAndTenantId(String organizationId, String tenantId);

    /**
     * Find policies by type
     */
    List<FleetPolicy> findByTypeAndTenantId(PolicyType type, String tenantId);

    /**
     * Find active policies for tenant
     */
    List<FleetPolicy> findByTenantIdAndIsActive(String tenantId, boolean isActive);

    /**
     * Find currently effective policies (within date range and active)
     */
    List<FleetPolicy> findEffectiveByTenantId(String tenantId, LocalDateTime currentDate);

    /**
     * Find policies by scope
     */
    List<FleetPolicy> findByScopeAndTenantId(PolicyScope scope, String tenantId);

    /**
     * Find policies by priority (ordered by priority descending)
     */
    List<FleetPolicy> findByTenantIdOrderByPriorityDesc(String tenantId);

    /**
     * Find policies by organization and type
     */
    List<FleetPolicy> findByOrganizationIdAndTypeAndTenantId(
        String organizationId, PolicyType type, String tenantId
    );

    /**
     * Delete policy by ID and tenant
     */
    void deleteByIdAndTenantId(String policyId, String tenantId);

    /**
     * Check if policy exists
     */
    boolean existsByIdAndTenantId(String policyId, String tenantId);

    /**
     * Count policies by organization
     */
    long countByOrganizationIdAndTenantId(String organizationId, String tenantId);

    /**
     * Count policies by tenant
     */
    long countByTenantId(String tenantId);
}
