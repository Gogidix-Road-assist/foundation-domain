package com.gogidix.rapidassist.shared.request.context.library.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods, classes, or interfaces as tenant-aware.
 *
 * <p>When applied to a method or class, this annotation indicates that
 * the component requires tenant context to function properly. This can be
 * used by interceptors, aspects, or other infrastructure components to
 * enforce tenant presence or provide tenant-specific behavior.</p>
 *
 * <p>Usage examples:</p>
 * <pre>{@code
 * // Mark a service method as requiring tenant context
 * @TenantAware
 * public void processTenantData() {
 *     String tenantId = TenantContext.getTenantIdOrThrow();
 *     // Process tenant-specific data
 * }
 *
 * // Mark a class as tenant-aware
 * @TenantAware(requireTenant = false)
 * public class OptionalTenantService {
 *     // This service can work with or without tenant context
 * }
 *
 * // Mark a specific method as not requiring tenant
 * @TenantAware(requireTenant = false)
 * public void healthCheck() {
 *     // Health check doesn't require tenant context
 * }
 * }</pre>
 *
 * @see com.gogidix.rapidassist.shared.request.context.library.domain
 *     .TenantContext
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface TenantAware {

    /**
     * Specifies whether tenant context is required for the annotated element.
     *
     * <p>If set to {@code true} (default), the infrastructure should
     * enforce that a tenant ID is present in the current context before
     * allowing the method or class to be used.</p>
     *
     * <p>If set to {@code false}, the element can operate with or
     * without tenant context. This is useful for health checks, system
     * endpoints, or components that support both tenant-specific and
     * default behaviors.</p>
     *
     * @return true if tenant is required, false otherwise
     */
    boolean requireTenant() default true;

    /**
     * Optional description of why this element is tenant-aware or how it
     * uses tenant information. This is primarily for documentation purposes.
     *
     * @return description of tenant awareness
     */
    String description() default "";

    /**
     * Tenant IDs that are explicitly allowed for this element.
     *
     * <p>If specified, only the listed tenant IDs will be permitted to
     * access this element. If empty (default), all tenants are allowed.</p>
     *
     * <p>This can be used to restrict certain operations to specific
     * tenants.</p>
     *
     * @return array of allowed tenant IDs
     */
    String[] allowedTenants() default {};

    /**
     * Tenant IDs that are explicitly denied for this element.
     *
     * <p>If specified, the listed tenant IDs will be denied access to
     * this element. This takes precedence over {@link #allowedTenants()}
     * .</p>
     *
     * <p>This can be used to prevent certain tenants from accessing
     * specific operations.</p>
     *
     * @return array of denied tenant IDs
     */
    String[] deniedTenants() default {};
}
