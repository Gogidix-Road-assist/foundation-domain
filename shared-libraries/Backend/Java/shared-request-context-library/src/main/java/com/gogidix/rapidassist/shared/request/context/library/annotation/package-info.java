/**
 * Annotations package for the shared request context library.
 *
 * <p>This package contains custom annotations used to mark classes, methods,
 * and constructors that require tenant awareness or request context
 * information.</p>
 *
 * <p>The primary annotation in this package is {@link
 * com.gogidix.rapidassist.shared.request.context.library.annotation
 * .TenantAware} which can be used to indicate that a component requires
 * tenant context to function properly.</p>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * @TenantAware
 * @Service
 * public class TenantSpecificService {
 *     public void processData() {
 *         String tenantId = TenantContext.getTenantIdOrThrow();
 *         // Process tenant-specific data
 *     }
 * }
 * }</pre>
 *
 * @author Rapid Assist
 * @version 1.0.0
 * @since 1.0.0
 */
package com.gogidix.rapidassist.shared.request.context.library.annotation;
