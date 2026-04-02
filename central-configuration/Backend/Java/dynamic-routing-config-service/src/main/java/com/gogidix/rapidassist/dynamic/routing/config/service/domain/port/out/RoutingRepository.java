package com.gogidix.rapidassist.dynamic.routing.config.service.domain.port.out;

import com.gogidix.rapidassist.dynamic.routing.config.service.domain.model.RoutingRule;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Output port for persisting and retrieving Routing Rule entities.
 *
 * <p>This is a secondary port (outbound) in the hexagonal architecture.
 * The domain layer depends on this interface, and the infrastructure layer
 * provides the implementation.
 *
 * <p>All operations are tenant-scoped for multi-tenancy isolation.
 */
public interface RoutingRepository {

    // ========================================================================
    // CRUD Operations
    // ========================================================================

    /**
     * Saves a routing rule.
     *
     * @param rule the routing rule to save
     * @return CompletableFuture containing the saved routing rule
     */
    CompletableFuture<RoutingRule> save(RoutingRule rule);

    /**
     * Finds a routing rule by ID.
     *
     * @param ruleId the routing rule ID
     * @return CompletableFuture containing the routing rule if found
     */
    CompletableFuture<Optional<RoutingRule>> findById(String ruleId);

    /**
     * Finds a routing rule by its natural key (tenantId + ruleName + environment).
     *
     * @param tenantId the tenant ID
     * @param ruleName the rule name
     * @param environment the environment
     * @return CompletableFuture containing the routing rule if found
     */
    CompletableFuture<Optional<RoutingRule>> findByNaturalKey(String tenantId, String ruleName, String environment);

    /**
     * Finds all routing rules for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of routing rules
     */
    CompletableFuture<List<RoutingRule>> findByTenant(String tenantId);

    /**
     * Finds routing rules by tenant and environment.
     *
     * @param tenantId the tenant ID
     * @param environment the environment
     * @return CompletableFuture containing list of routing rules
     */
    CompletableFuture<List<RoutingRule>> findByTenantAndEnvironment(String tenantId, String environment);

    /**
     * Finds active routing rules for a tenant and environment.
     *
     * @param tenantId the tenant ID
     * @param environment the environment
     * @return CompletableFuture containing list of active routing rules
     */
    CompletableFuture<List<RoutingRule>> findActiveRules(String tenantId, String environment);

    /**
     * Finds routing rules by priority range.
     *
     * @param tenantId the tenant ID
     * @param min minimum priority
     * @param max maximum priority
     * @return CompletableFuture containing list of routing rules
     */
    CompletableFuture<List<RoutingRule>> findByPriorityRange(String tenantId, int min, int max);

    /**
     * Deletes a routing rule by ID.
     *
     * @param ruleId the routing rule ID
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteById(String ruleId);

    // ========================================================================
    // Cache Operations
    // ========================================================================

    /**
     * Caches a routing rule.
     *
     * @param rule the routing rule to cache
     * @return CompletableFuture completion signal
     */
    CompletableFuture<Void> cacheRule(RoutingRule rule);

    /**
     * Gets a cached routing rule.
     *
     * @param ruleId the routing rule ID
     * @return CompletableFuture containing the cached rule if found
     */
    CompletableFuture<Optional<RoutingRule>> getCachedRule(String ruleId);

    /**
     * Gets cached routing rules for a tenant and environment.
     *
     * @param tenantId the tenant ID
     * @param environment the environment
     * @return CompletableFuture containing list of cached rules
     */
    CompletableFuture<List<RoutingRule>> getCachedRules(String tenantId, String environment);

    /**
     * Evicts a routing rule from cache.
     *
     * @param ruleId the routing rule ID
     * @return CompletableFuture completion signal
     */
    CompletableFuture<Void> evictRule(String ruleId);

    /**
     * Evicts all cached routing rules.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture completion signal
     */
    CompletableFuture<Void> evictAll(String tenantId);
}
