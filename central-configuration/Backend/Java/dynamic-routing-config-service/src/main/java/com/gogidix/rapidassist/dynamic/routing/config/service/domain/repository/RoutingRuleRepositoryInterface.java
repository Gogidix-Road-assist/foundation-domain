package com.gogidix.rapidassist.dynamic.routing.config.service.domain.repository;

import com.gogidix.rapidassist.dynamic.routing.config.service.domain.model.RoutingRule;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * Domain repository interface for Routing Rule entities.
 *
 * <p>This is a PORT in the hexagonal architecture pattern. It defines
 * the contract that the infrastructure layer must implement.
 *
 * <p>All operations are tenant-scoped for multi-tenancy isolation.
 * The tenantId parameter ensures queries are always filtered by tenant.
 *
 * <p>This interface is part of the DOMAIN layer and contains NO
 * infrastructure-specific details (no MongoDB, Redis annotations, etc.).
 */
public interface RoutingRuleRepositoryInterface {

    // ========================================================================
    // CRUD Operations
    // ========================================================================

    /**
     * Saves a routing rule entity.
     *
     * @param routingRule the routing rule to save
     * @return CompletableFuture containing the saved routing rule
     */
    CompletableFuture<RoutingRule> save(RoutingRule routingRule);

    /**
     * Finds a routing rule by its unique ID.
     * The result is filtered by tenant context to ensure isolation.
     *
     * @param id the routing rule ID
     * @return CompletableFuture containing the routing rule if found
     */
    CompletableFuture<Optional<RoutingRule>> findById(String id);

    /**
     * Finds a routing rule by its natural key (tenantId + ruleName + environment).
     *
     * @param tenantId    the tenant ID
     * @param ruleName    the rule name
     * @param environment the environment
     * @return CompletableFuture containing the routing rule if found
     */
    CompletableFuture<Optional<RoutingRule>> findByNaturalKey(
        String tenantId,
        String ruleName,
        String environment
    );

    /**
     * Finds all routing rules for a specific tenant.
     * IMPORTANT: This method MUST filter by tenantId for multi-tenancy.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing list of routing rules
     */
    CompletableFuture<List<RoutingRule>> findByTenantId(String tenantId);

    // ========================================================================
    // Query Operations (Tenant-Scoped)
    // ========================================================================

    /**
     * Finds all active routing rules for a specific tenant and environment.
     *
     * @param tenantId    the tenant ID
     * @param environment the environment
     * @return CompletableFuture containing list of active routing rules
     */
    CompletableFuture<List<RoutingRule>> findActiveByTenantAndEnvironment(
        String tenantId,
        String environment
    );

    /**
     * Finds all routing rules with a specific priority.
     *
     * @param tenantId the tenant ID
     * @param priority the priority
     * @return CompletableFuture containing list of routing rules
     */
    CompletableFuture<List<RoutingRule>> findByPriority(
        String tenantId,
        int priority
    );

    /**
     * Finds routing rules updated after a specific timestamp.
     *
     * @param tenantId the tenant ID
     * @param since    the timestamp to search from
     * @return CompletableFuture containing list of routing rules
     */
    CompletableFuture<List<RoutingRule>> findUpdatedSince(String tenantId, Instant since);

    /**
     * Searches routing rules by keyword in rule name.
     *
     * @param tenantId the tenant ID
     * @param keyword  the search keyword
     * @return CompletableFuture containing list of routing rules
     */
    CompletableFuture<List<RoutingRule>> searchByKeyword(String tenantId, String keyword);

    /**
     * Finds all versions of a routing rule.
     *
     * @param tenantId    the tenant ID
     * @param ruleName    the rule name
     * @param environment the environment
     * @return CompletableFuture containing list of all versions
     */
    CompletableFuture<List<RoutingRule>> findAllVersions(
        String tenantId,
        String ruleName,
        String environment
    );

    /**
     * Finds the latest version of a routing rule.
     *
     * @param tenantId    the tenant ID
     * @param ruleName    the rule name
     * @param environment the environment
     * @return CompletableFuture containing the latest version if found
     */
    CompletableFuture<Optional<RoutingRule>> findLatestVersion(
        String tenantId,
        String ruleName,
        String environment
    );

    /**
     * Finds all routing rules matching a path pattern.
     *
     * @param tenantId the tenant ID
     * @param pattern  the path pattern to match
     * @return CompletableFuture containing list of matching routing rules
     */
    CompletableFuture<List<RoutingRule>> findByPattern(
        String tenantId,
        String pattern
    );

    // ========================================================================
    // Delete Operations
    // ========================================================================

    /**
     * Deletes a routing rule by its ID.
     * The tenant context is verified before deletion.
     *
     * @param id the routing rule ID
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteById(String id);

    /**
     * Deletes a routing rule by its natural key.
     * IMPORTANT: Must verify tenantId matches before deletion.
     *
     * @param tenantId    the tenant ID
     * @param ruleName    the rule name
     * @param environment the environment
     * @return CompletableFuture containing true if deleted
     */
    CompletableFuture<Boolean> deleteByNaturalKey(
        String tenantId,
        String ruleName,
        String environment
    );

    // ========================================================================
    // Batch Operations
    // ========================================================================

    /**
     * Saves multiple routing rules in a batch.
     *
     * @param routingRules list of routing rules to save
     * @return CompletableFuture containing list of saved routing rules
     */
    CompletableFuture<List<RoutingRule>> saveAll(List<RoutingRule> routingRules);

    /**
     * Finds all routing rules matching the given IDs.
     * Results are filtered by tenant context.
     *
     * @param ids list of routing rule IDs
     * @return CompletableFuture containing list of routing rules
     */
    CompletableFuture<List<RoutingRule>> findAllById(List<String> ids);

    // ========================================================================
    // Count Operations
    // ========================================================================

    /**
     * Counts all routing rules for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countByTenantId(String tenantId);

    /**
     * Counts active routing rules for a tenant.
     *
     * @param tenantId the tenant ID
     * @return CompletableFuture containing the count
     */
    CompletableFuture<Long> countActiveByTenantId(String tenantId);

    /**
     * Checks if a routing rule exists by natural key.
     *
     * @param tenantId    the tenant ID
     * @param ruleName    the rule name
     * @param environment the environment
     * @return CompletableFuture containing true if exists
     */
    CompletableFuture<Boolean> existsByNaturalKey(
        String tenantId,
        String ruleName,
        String environment
    );
}
