package com.gogidix.rapidassist.orchestration.fleet_policy.domain.repository;

import com.gogidix.rapidassist.orchestration.fleet_policy.domain.model.Policy;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Policy entities
 * Port for policy data access operations
 */
public interface PolicyRepository {

    /**
     * Save a policy
     */
    Policy save(Policy policy);

    /**
     * Find policy by ID
     */
    Optional<Policy> findById(String id);

    /**
     * Find policy by tenant ID and policy code
     */
    Optional<Policy> findByTenantIdAndPolicyCode(String tenantId, String policyCode);

    /**
     * Find all policies for a tenant
     */
    List<Policy> findByTenantId(String tenantId);

    /**
     * Find active policies for a tenant
     */
    List<Policy> findByTenantIdAndIsActive(String tenantId, Boolean isActive);

    /**
     * Find policies by type
     */
    List<Policy> findByTenantIdAndPolicyType(String tenantId, Policy.PolicyType policyType);

    /**
     * Find effective policies for a tenant
     */
    List<Policy> findEffectivePolicies(String tenantId, LocalDateTime dateTime);

    /**
     * Find policies by status
     */
    List<Policy> findByTenantIdAndStatus(String tenantId, Policy.PolicyStatus status);

    /**
     * Find policies requiring approval
     */
    List<Policy> findByTenantIdAndRequiresApproval(String tenantId, Boolean requiresApproval);

    /**
     * Search policies by name or description
     */
    List<Policy> searchPolicies(String tenantId, String searchTerm);

    /**
     * Delete policy by ID
     */
    void deleteById(String id);

    /**
     * Check if policy code exists
     */
    boolean existsByTenantIdAndPolicyCode(String tenantId, String policyCode);

    /**
     * Count policies by tenant and type
     */
    long countByTenantIdAndPolicyType(String tenantId, Policy.PolicyType policyType);

    /**
     * Delete all policies for a tenant
     */
    void deleteAll();
}
