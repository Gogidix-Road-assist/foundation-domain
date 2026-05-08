/**
 * Tenant resolver package for the shared request context library.
 *
 * <p>This package contains the strategy interface and implementations for resolving
 * tenant IDs from various sources such as HTTP headers, JWT claims, and other request attributes.</p>
 *
 * <p>Key classes in this package:</p>
 * <ul>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.domain.resolver.TenantResolver} -
 *       Strategy interface for tenant resolution</li>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.domain.resolver.HeaderTenantResolver} -
 *       Resolves tenant ID from HTTP headers</li>
 * </ul>
 *
 * <p>Custom implementations can be created to resolve tenant IDs from other sources:</p>
 * <pre>{@code
 * public class CustomTenantResolver implements TenantResolver {
 *     @Override
 *     public Optional<String> resolve(Object request) {
 *         // Custom resolution logic
 *         return Optional.of("custom-tenant");
 *     }
 *
 *     @Override
 *     public boolean supports(Object request) {
 *         return true;
 *     }
 *
 *     @Override
 *     public int getPriority() {
 *         return 100;
 *     }
 * }
 * }</pre>
 *
 * @author Rapid Assist
 * @version 1.0.0
 * @since 1.0.0
 */
package com.gogidix.rapidassist.shared.request.context.library.domain.resolver;
