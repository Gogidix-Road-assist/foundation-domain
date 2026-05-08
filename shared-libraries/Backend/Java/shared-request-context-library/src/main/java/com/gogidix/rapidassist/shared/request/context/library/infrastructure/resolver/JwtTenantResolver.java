package com.gogidix.rapidassist.shared.request.context.library.infrastructure.resolver;

import com.gogidix.rapidassist.shared.request.context.library.constant.TenantConstants;
import com.gogidix.rapidassist.shared.request.context.library.domain.resolver.TenantResolver;

import jakarta.servlet.http.HttpServletRequest;

import java.util.Optional;

/**
 * Tenant resolver that extracts the tenant ID from JWT claims.
 *
 * <p>This resolver attempts to extract the tenant ID from the JWT token's claims.
 * By default, it looks for a claim named "tenantId", but this can be customized.</p>
 *
 * <p>The resolver expects the JWT to be already authenticated and parsed by Spring
 * Security, with the claims available in the request attributes.</p>
 *
 * <p>This resolver has a priority of 250, making it higher than header-based
 * resolvers as JWT claims are considered more authoritative.</p>
 *
 * <p>Note: This resolver requires the spring-security-oauth2-resource-server
 * dependency to be present. If the dependency is not available, this resolver
 * will gracefully return empty results.</p>
 */
public class JwtTenantResolver implements TenantResolver {

    private final String tenantClaimName;
    private final int priority;

    /**
     * Creates a new JwtTenantResolver with the default claim name.
     */
    public JwtTenantResolver() {
        this(TenantConstants.DEFAULT_TENANT_CLAIM);
    }

    /**
     * Creates a new JwtTenantResolver with a custom claim name.
     *
     * @param tenantClaimName the name of the JWT claim containing the tenant ID
     * @throws IllegalArgumentException if tenantClaimName is null or blank
     */
    public JwtTenantResolver(final String tenantClaimName) {
        this(tenantClaimName, TenantConstants.DEFAULT_JWT_RESOLVER_PRIORITY);
    }

    /**
     * Creates a new JwtTenantResolver with a custom claim name and priority.
     *
     * @param tenantClaimName the name of the JWT claim containing the tenant ID
     * @param priority        the priority of this resolver
     * @throws IllegalArgumentException if tenantClaimName is null or blank
     */
    public JwtTenantResolver(final String tenantClaimName, final int priority) {
        if (tenantClaimName == null || tenantClaimName.isBlank()) {
            throw new IllegalArgumentException("Tenant claim name cannot be null or blank");
        }
        this.tenantClaimName = tenantClaimName;
        this.priority = priority;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Optional<String> resolve(final Object request) {
        if (!(request instanceof HttpServletRequest httpRequest)) {
            return Optional.empty();
        }

        try {
            // Try to get the JWT from Spring Security's authentication
            Object auth = httpRequest.getAttribute("java.security.Principal");

            if (auth instanceof org.springframework.security.core.Authentication
                    authentication) {
                Object principal = authentication.getPrincipal();

                // Check if it's a JwtAuthenticationToken (Spring Security OAuth2)
                if (principal.getClass().getName().equals(
                        "org.springframework.security.oauth2.jwt.Jwt")) {
                    // Use reflection to avoid hard dependency on OAuth2
                    try {
                        Object claims = principal.getClass()
                                .getMethod("getClaims")
                                .invoke(principal);

                        if (claims instanceof java.util.Map<?, ?>
                                claimsMap) {
                            Object tenantId = claimsMap.get(tenantClaimName);

                            if (tenantId != null) {
                                String tenantIdStr = tenantId.toString();
                                if (!tenantIdStr.isBlank()) {
                                    return Optional.of(tenantIdStr.trim());
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Reflection failed, return empty
                        return Optional.empty();
                    }
                }
            }

            // Fallback: Try to get from request attributes directly
            Object tenantId = httpRequest.getAttribute(TenantConstants.TENANT_CONTEXT_KEY);
            if (tenantId != null) {
                String tenantIdStr = tenantId.toString();
                if (!tenantIdStr.isBlank()) {
                    return Optional.of(tenantIdStr.trim());
                }
            }
        } catch (Exception e) {
            // Any exception during resolution, return empty
            return Optional.empty();
        }

        return Optional.empty();
    }

    @Override
    public boolean supports(final Object request) {
        if (!(request instanceof HttpServletRequest httpRequest)) {
            return false;
        }

        try {
            // Check if we have a JWT authentication
            Object auth = httpRequest.getAttribute("java.security.Principal");

            if (auth instanceof org.springframework.security.core.Authentication
                    authentication) {
                Object principal = authentication.getPrincipal();
                return principal.getClass().getName().equals(
                        "org.springframework.security.oauth2.jwt.Jwt");
            }

            // Fallback: Check request attributes
            Object tenantId = httpRequest.getAttribute(TenantConstants.TENANT_CONTEXT_KEY);
            return tenantId != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int getPriority() {
        return priority;
    }

    /**
     * Returns the claim name used to resolve the tenant ID.
     *
     * @return the tenant claim name
     */
    public String getTenantClaimName() {
        return tenantClaimName;
    }
}
