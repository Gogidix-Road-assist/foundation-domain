/**
 * Domain package for the shared request context library.
 *
 * <p>This package contains core domain classes that manage request-scoped context
 * information across the application, including tenant context and request context.</p>
 *
 * <p>Key classes in this package:</p>
 * <ul>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext} -
 *       Immutable record containing request context information</li>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder} -
 *       Thread-local holder for the current request context</li>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.domain.TenantContext} -
 *       Convenience facade for tenant-specific operations</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * // Get current request context
 * Optional<RequestContext> context = RequestContextHolder.get();
 *
 * // Get tenant ID
 * String tenantId = TenantContext.getTenantIdOrThrow();
 *
 * // Set context
 * RequestContextHolder.set(new RequestContext(...));
 * }</pre>
 *
 * @author Rapid Assist
 * @version 1.0.0
 * @since 1.0.0
 */
package com.gogidix.rapidassist.shared.request.context.library.domain;
