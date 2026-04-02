package com.gogidix.rapidassist.rate.limit.policy.service.domain.port.out;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * Output port for persisting and retrieving Rate Limit Policy entities.
 *
 * <p>This is a secondary port (outbound) in the hexagonal architecture.
 * The domain layer depends on this interface, and the infrastructure layer
 * provides the implementation.
 */
public interface RateLimitPolicyStore {

    /**
     * Saves a rate limit policy.
     *
     * @param policy the policy to save
     * @return the saved policy
     */
    RateLimitPolicy save(RateLimitPolicy policy);

    /**
     * Finds a policy by ID.
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
     * Finds policies by tenant and environment.
     *
     * @param tenantId    the tenant ID
     * @param environment the environment
     * @param pageable    the pagination information
     * @return page of policies
     */
    Page<RateLimitPolicy> findByTenantIdAndEnvironment(String tenantId, String environment, Pageable pageable);

    /**
     * Deletes a policy by ID.
     *
     * @param id the policy ID
     */
    void deleteById(String id);

    /**
     * Checks if a policy exists.
     *
     * @param tenantId  the tenant ID
     * @param policyKey the policy key
     * @return true if exists
     */
    boolean existsByTenantIdAndPolicyKey(String tenantId, String policyKey);
}
