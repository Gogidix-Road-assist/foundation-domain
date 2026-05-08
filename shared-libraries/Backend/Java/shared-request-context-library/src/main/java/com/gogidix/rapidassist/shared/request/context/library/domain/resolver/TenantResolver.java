package com.gogidix.rapidassist.shared.request.context.library.domain.resolver;

import java.util.Optional;

/**
 * Strategy interface for tenant resolution.
 *
 * <p>Implementations of this interface provide different strategies for resolving
 * the tenant ID from various sources such as HTTP headers, JWT claims, or other
 * request attributes.</p>
 *
 * <p>Resolvers are typically ordered by priority, with higher priority resolvers
 * being consulted first. If a resolver cannot determine the tenant ID, the next
 * resolver in the chain is consulted.</p>
 *
 * <p>Implementations must be thread-safe as they may be called concurrently.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * public class CustomTenantResolver implements TenantResolver {
 *     @Override
 *     public Optional<String> resolve(HttpServletRequest request) {
 *         return Optional.ofNullable(request.getHeader("X-Custom-Tenant"));
 *     }
 *
 *     @Override
 *     public boolean supports(HttpServletRequest request) {
 *         return request.getHeader("X-Custom-Tenant") != null;
 *     }
 *
 *     @Override
 *     public int getPriority() {
 *         return 100;
 *     }
 * }
 * }</pre>
 */
public interface TenantResolver {

    /**
     * Resolves the tenant ID from the given request.
     *
     * @param request the request to resolve the tenant from
     * @return Optional containing the tenant ID if resolved, empty otherwise
     */
    Optional<String> resolve(Object request);

    /**
     * Determines if this resolver supports the given request.
     *
     * <p>This method can be used to quickly determine if the resolver should
     * be attempted based on the presence of specific headers, attributes, or
     * other request characteristics.</p>
     *
     * @param request the request to check
     * @return true if this resolver supports the request, false otherwise
     */
    boolean supports(Object request);

    /**
     * Returns the priority of this resolver.
     *
     * <p>Higher priority resolvers are consulted first. Resolvers with the same
     * priority may be consulted in any order.</p>
     *
     * <p>Recommended priority ranges:</p>
     * <ul>
     *   <li>0-99: System/fallback resolvers</li>
     *   <li>100-199: Header-based resolvers</li>
     *   <li>200-299: JWT-based resolvers</li>
     *   <li>300-399: Custom business logic resolvers</li>
     *   <li>400+: High-priority resolvers</li>
     * </ul>
     *
     * @return the priority value (higher values = higher priority)
     */
    int getPriority();
}
