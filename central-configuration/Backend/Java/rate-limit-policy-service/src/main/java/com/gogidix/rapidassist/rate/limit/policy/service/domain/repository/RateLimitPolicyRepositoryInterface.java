package com.gogidix.rapidassist.rate.limit.policy.service.domain.repository;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Repository interface for Rate Limit Policy aggregates.
 *
 * <p>This is a port interface (outbound) in the hexagonal architecture.
 * It defines the contract for persisting and retrieving rate limit policies.
 *
 * <p>Implementations are provided by the infrastructure layer.
 */
public interface RateLimitPolicyRepositoryInterface {

    /**
     * Saves a rate limit policy (create or update).
     *
     * @param policy the policy to save
     * @return the saved policy
     */
    RateLimitPolicy save(RateLimitPolicy policy);

    /**
     * Finds a policy by its ID.
     *
     * @param id the policy ID
     * @return the policy if found
     */
    Optional<RateLimitPolicy> findById(String id);

    /**
     * Finds a policy by tenant ID and policy key.
     *
     * @param tenantId  the tenant ID
     * @param policyKey the policy key
     * @return the policy if found
     */
    Optional<RateLimitPolicy> findByTenantIdAndPolicyKey(String tenantId, String policyKey);

    /**
     * Finds all policies for a tenant.
     *
     * @param tenantId the tenant ID
     * @param pageable the pagination information
     * @return page of policies
     */
    Page<RateLimitPolicy> findByTenantId(String tenantId, Pageable pageable);

    /**
     * Finds all policies for a tenant in a specific environment.
     *
     * @param tenantId    the tenant ID
     * @param environment the environment
     * @param pageable    the pagination information
     * @return page of policies
     */
    Page<RateLimitPolicy> findByTenantIdAndEnvironment(String tenantId, String environment, Pageable pageable);

    /**
     * Finds all enabled policies for a tenant.
     *
     * @param tenantId the tenant ID
     * @param pageable the pagination information
     * @return page of enabled policies
     */
    Page<RateLimitPolicy> findByTenantIdAndEnabled(String tenantId, boolean enabled, Pageable pageable);

    /**
     * Finds policies by limit type.
     *
     * @param tenantId  the tenant ID
     * @param limitType the limit type
     * @param pageable  the pagination information
     * @return page of policies
     */
    Page<RateLimitPolicy> findByTenantIdAndLimitType(String tenantId, RateLimitPolicy.LimitType limitType, Pageable pageable);

    /**
     * Deletes a policy by its ID.
     *
     * @param id the policy ID
     */
    void deleteById(String id);

    /**
     * Checks if a policy exists by tenant ID and policy key.
     *
     * @param tenantId  the tenant ID
     * @param policyKey the policy key
     * @return true if the policy exists
     */
    boolean existsByTenantIdAndPolicyKey(String tenantId, String policyKey);

    /**
     * Counts all policies for a tenant.
     *
     * @param tenantId the tenant ID
     * @return the count of policies
     */
    long countByTenantId(String tenantId);
}
