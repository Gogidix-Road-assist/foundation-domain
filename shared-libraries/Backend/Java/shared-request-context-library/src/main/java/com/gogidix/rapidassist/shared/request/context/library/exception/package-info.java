/**
 * Exception package for the shared request context library.
 *
 * <p>This package contains all tenant-related exceptions used throughout the library.
 * All exceptions extend from {@link com.gogidix.rapidassist.shared.request.context.library.exception.TenantException}
 * as the base class.</p>
 *
 * <p>Exception types in this package:</p>
 * <ul>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.exception.TenantException} -
 *       Base exception for all tenant-related errors</li>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.exception.TenantNotFoundException} -
 *       Thrown when a tenant ID is required but not present</li>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.exception.TenantMismatchException} -
 *       Thrown when tenant IDs don't match across operations</li>
 *   <li>{@link com.gogidix.rapidassist.shared.request.context.library.exception.TenantValidationException} -
 *       Thrown when tenant validation fails</li>
 * </ul>
 *
 * <p>Usage example:</p>
 * <pre>{@code
 * try {
 *     String tenantId = TenantContext.getTenantIdOrThrow();
 *     // Process tenant-specific logic
 * } catch (TenantException e) {
 *     // Handle any tenant-related error
 *     logger.error("Tenant error: {}", e.getMessage());
 * }
 * }</pre>
 *
 * @author Rapid Assist
 * @version 1.0.0
 * @since 1.0.0
 */
package com.gogidix.rapidassist.shared.request.context.library.exception;
