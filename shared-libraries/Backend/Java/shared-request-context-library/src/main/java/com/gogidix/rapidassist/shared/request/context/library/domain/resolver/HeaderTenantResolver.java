package com.gogidix.rapidassist.shared.request.context.library.domain.resolver;

import com.gogidix.rapidassist.shared.request.context.library.constant.TenantConstants;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

/**
 * Tenant resolver that extracts the tenant ID from HTTP request headers.
 *
 * <p>This resolver checks for the tenant ID in the following header (by default):</p>
 * <ul>
 *   <li>X-Tenant-ID</li>
 * </ul>
 *
 * <p>The header name can be customized by setting the {@code tenantHeaderName}
 * constructor parameter.</p>
 *
 * <p>This resolver has a priority of 150, making it higher than fallback resolvers
 * but lower than JWT-based resolvers.</p>
 */
public class HeaderTenantResolver implements TenantResolver {

    private final String tenantHeaderName;
    private final int priority;

    /**
     * Creates a new HeaderTenantResolver with the default header name.
     */
    public HeaderTenantResolver() {
        this(TenantConstants.DEFAULT_TENANT_HEADER);
    }

    /**
     * Creates a new HeaderTenantResolver with a custom header name.
     *
     * @param tenantHeaderName the name of the header containing the tenant ID
     * @throws IllegalArgumentException if tenantHeaderName is null or blank
     */
    public HeaderTenantResolver(String tenantHeaderName) {
        this(tenantHeaderName, 150);
    }

    /**
     * Creates a new HeaderTenantResolver with a custom header name and priority.
     *
     * @param tenantHeaderName the name of the header containing the tenant ID
     * @param priority         the priority of this resolver
     * @throws IllegalArgumentException if tenantHeaderName is null or blank
     */
    public HeaderTenantResolver(String tenantHeaderName, int priority) {
        if (tenantHeaderName == null || tenantHeaderName.isBlank()) {
            throw new IllegalArgumentException("Tenant header name cannot be null or blank");
        }
        this.tenantHeaderName = tenantHeaderName;
        this.priority = priority;
    }

    @Override
    public Optional<String> resolve(Object request) {
        if (!(request instanceof HttpServletRequest httpRequest)) {
            return Optional.empty();
        }

        String tenantId = httpRequest.getHeader(tenantHeaderName);

        if (tenantId != null && !tenantId.isBlank()) {
            return Optional.of(tenantId.trim());
        }

        return Optional.empty();
    }

    @Override
    public boolean supports(Object request) {
        if (!(request instanceof HttpServletRequest httpRequest)) {
            return false;
        }

        String tenantId = httpRequest.getHeader(tenantHeaderName);
        return tenantId != null && !tenantId.isBlank();
    }

    @Override
    public int getPriority() {
        return priority;
    }

    /**
     * Returns the header name used to resolve the tenant ID.
     *
     * @return the tenant header name
     */
    public String getTenantHeaderName() {
        return tenantHeaderName;
    }
}
