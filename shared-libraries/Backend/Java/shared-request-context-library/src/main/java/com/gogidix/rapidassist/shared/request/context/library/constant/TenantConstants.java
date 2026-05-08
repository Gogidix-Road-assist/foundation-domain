package com.gogidix.rapidassist.shared.request.context.library.constant;

/**
 * Constants for tenant handling in the shared request context library.
 *
 * <p>This class contains all tenant-related constants used throughout the
 * library, including default header names, JWT claim names, and context keys.</p>
 *
 * <p>These constants can be overridden in configuration when needed.</p>
 */
public final class TenantConstants {

    private TenantConstants() {
        // Utility class - prevent instantiation
    }

    /**
     * Default HTTP header name for tenant ID.
     * <p>
     * This header is used to pass the tenant ID in HTTP requests.
     * Clients should include this header in their requests to specify the tenant context.
     * </p>
     */
    public static final String DEFAULT_TENANT_HEADER = "X-Tenant-ID";

    /**
     * Default JWT claim name for tenant ID.
     * <p>
     * This claim name is used when extracting the tenant ID from JWT tokens.
     * The JWT token should contain this claim to specify the tenant context.
     * </p>
     */
    public static final String DEFAULT_TENANT_CLAIM = "tenantId";

    /**
     * Context key for storing tenant ID in request attributes.
     * <p>
     * This key is used to store and retrieve the tenant ID from HTTP request
     * attributes, providing an alternative mechanism for tenant context
     * propagation.
     * </p>
     */
    public static final String TENANT_CONTEXT_KEY = "tenant.id";

    /**
     * Default tenant ID for system-level operations.
     * <p>
     * This constant represents a default or system tenant ID used when no
     * specific tenant context is available. It should be used carefully and
     * only for operations that are truly tenant-agnostic.
     * </p>
     */
    public static final String DEFAULT_TENANT_ID = "system";

    /**
     * Prefix for tenant-specific cache keys.
     * <p>
     * This prefix is used when creating cache keys that are scoped to a specific
     * tenant, ensuring cache isolation between tenants.
     * </p>
     */
    public static final String TENANT_CACHE_KEY_PREFIX = "tenant:";

    /**
     * Separator for tenant cache key components.
     * <p>
     * This separator is used to combine the tenant ID with the cache key
     * identifier. Example: "tenant:TENANT123:user:456"
     * </p>
     */
    public static final String TENANT_CACHE_KEY_SEPARATOR = ":";

    /**
     * Regular expression pattern for validating tenant ID format.
     * <p>
     * Tenant IDs must match this pattern to be considered valid.
     * The pattern allows alphanumeric characters, hyphens, and underscores.
     * </p>
     */
    public static final String TENANT_ID_PATTERN = "^[a-zA-Z0-9_-]+$";

    /**
     * Maximum length for tenant IDs.
     * <p>
     * Tenant IDs exceeding this length should be rejected.
     * </p>
     */
    public static final int TENANT_ID_MAX_LENGTH = 128;

    /**
     * Minimum length for tenant IDs.
     * <p>
     * Tenant IDs shorter than this length should be rejected.
     * </p>
     */
    public static final int TENANT_ID_MIN_LENGTH = 1;

    /**
     * HTTP response header name for tenant validation errors.
     * <p>
     * This header is included in error responses when tenant validation fails,
     * providing additional context about the validation failure.
     * </p>
     */
    public static final String TENANT_ERROR_HEADER = "X-Tenant-Error";

    /**
     * Bean name for the default tenant resolver chain.
     * <p>
     * This constant is used for Spring bean registration of the tenant
     * resolver chain.
     * </p>
     */
    public static final String TENANT_RESOLVER_CHAIN_BEAN = "tenantResolverChain";

    /**
     * Bean name for the tenant interceptor.
     * <p>
     * This constant is used for Spring bean registration of the tenant
     * interceptor.
     * </p>
     */
    public static final String TENANT_INTERCEPTOR_BEAN = "tenantInterceptor";

    /**
     * Default core pool size for async task executor.
     * <p>
     * This is the number of threads to keep in the pool, even if they are idle.
     * </p>
     */
    public static final int DEFAULT_CORE_POOL_SIZE = 5;

    /**
     * Default maximum pool size for async task executor.
     * <p>
     * This is the maximum number of threads that can be created in the pool.
     * </p>
     */
    public static final int DEFAULT_MAX_POOL_SIZE = 10;

    /**
     * Default queue capacity for async task executor.
     * <p>
     * This is the capacity of the queue for holding tasks before new threads are created.
     * </p>
     */
    public static final int DEFAULT_QUEUE_CAPACITY = 100;

    /**
     * Default await termination seconds for async task executor.
     * <p>
     * This is the maximum time to wait for tasks to complete on shutdown.
     * </p>
     */
    public static final int DEFAULT_AWAIT_TERMINATION_SECONDS = 60;

    /**
     * Default priority for header-based tenant resolver.
     * <p>
     * Higher priority values are checked first. This value provides a balance
     * between primary resolvers (like JWT) and fallback resolvers.
     * </p>
     */
    public static final int DEFAULT_HEADER_RESOLVER_PRIORITY = 150;

    /**
     * Default filter order for RequestContext filter.
     * <p>
     * This ensures the filter runs early in the chain to establish context
     * before other filters. Negative values ensure it runs before Spring Security.
     * </p>
     */
    public static final int FILTER_ORDER_DEFAULT = -100;

    /**
     * Default priority for JWT-based tenant resolver.
     * <p>
     * This resolver has the highest priority as JWT claims are considered most
     * authoritative for tenant identification. Higher priority values are checked first.
     * </p>
     */
    public static final int DEFAULT_JWT_RESOLVER_PRIORITY = 250;
}
