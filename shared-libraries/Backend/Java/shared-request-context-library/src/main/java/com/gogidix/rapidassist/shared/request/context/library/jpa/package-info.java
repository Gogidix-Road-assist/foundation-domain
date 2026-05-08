/**
 * JPA package for the shared request context library.
 *
 * <p>This package contains JPA-specific utilities that provide Long-based
 * tenant ID access, bridging the gap between the string-based TenantContext
 * and the Long-based tenant IDs commonly used in JPA entities.</p>
 *
 * <p>Key classes in this package:</p>
 * <ul>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.jpa.JpaTenantContext} -
 *       JPA-specific tenant context utility with Long-based tenant IDs</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * // Get current tenant ID as Long for JPA operations
 * Long tenantId = JpaTenantContext.getTenantIdOrThrow();
 *
 * // Use in repository queries
 * List<Entity> entities = repository.findByTenantId(tenantId);
 * }</pre>
 *
 * @author Rapid Assist
 * @version 1.0.0
 * @since 1.0.0
 */
package com.gogidix.rapidassist.shared.request.context.library.jpa;
