/**
 * Infrastructure resolver package for the shared request context library.
 *
 * <p>This package contains infrastructure-level tenant resolver implementations
 * that integrate with external systems and frameworks.</p>
 *
 * <p>Key classes in this package:</p>
 * <ul>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.infrastructure.resolver.JwtTenantResolver} -
 *       Resolves tenant ID from JWT claims using Spring Security</li>
 * </ul>
 *
 * <p>These resolvers use reflection to avoid hard dependencies on specific
 * frameworks, making them more flexible and easier to use in various environments.</p>
 *
 * @author Rapid Assist
 * @version 1.0.0
 * @since 1.0.0
 */
package com.gogidix.rapidassist.shared.request.context.library.infrastructure.resolver;
