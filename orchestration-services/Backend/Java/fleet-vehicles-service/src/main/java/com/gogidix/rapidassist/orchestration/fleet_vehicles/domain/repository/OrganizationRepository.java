package com.gogidix.rapidassist.orchestration.fleetorganization.domain.repository;

import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.Organization;
import com.gogidix.rapidassist.orchestration.fleetorganization.domain.model.Organization.OrganizationType;

import java.util.List;
import java.util.Optional;

/**
 * Domain repository port for Organization entity.
 * This is the interface that defines the contract for Organization persistence operations.
 * Implemented by infrastructure layer (MongoDB).
 */
public interface OrganizationRepository {

    /**
     * Save an organization (create or update)
     */
    Organization save(Organization organization);

    /**
     * Find organization by ID and tenant ID
     */
    Optional<Organization> findByIdAndTenantId(String organizationId, String tenantId);

    /**
     * Find all organizations for a tenant
     */
    List<Organization> findByTenantId(String tenantId);

    /**
     * Find organizations by parent ID
     */
    List<Organization> findByParentIdAndTenantId(String parentId, String tenantId);

    /**
     * Find organizations by type
     */
    List<Organization> findByTypeAndTenantId(OrganizationType type, String tenantId);

    /**
     * Find root organization for tenant
     */
    Optional<Organization> findRootByTenantId(String tenantId);

    /**
     * Find organizations by path prefix (for hierarchy traversal)
     */
    List<Organization> findByPathStartingWithAndTenantId(String pathPrefix, String tenantId);

    /**
     * Find active organizations by tenant
     */
    List<Organization> findByTenantIdAndIsActive(String tenantId, boolean isActive);

    /**
     * Delete organization by ID and tenant
     */
    void deleteByIdAndTenantId(String organizationId, String tenantId);

    /**
     * Check if organization exists
     */
    boolean existsByIdAndTenantId(String organizationId, String tenantId);

    /**
     * Count organizations by tenant
     */
    long countByTenantId(String tenantId);
}
