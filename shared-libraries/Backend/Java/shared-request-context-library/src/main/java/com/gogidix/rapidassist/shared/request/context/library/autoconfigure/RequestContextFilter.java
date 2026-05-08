package com.gogidix.rapidassist.shared.request.context.library.autoconfigure;

import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * Servlet filter that captures request context from HTTP headers and JWT claims.
 *
 * <p>This filter extracts tenant, country, user, and correlation information from
 * incoming requests and stores them in the RequestContextHolder for access throughout
 * the request processing chain.</p>
 *
 * @author Rapid Assist
 * @version 1.0.0
 * @since 1.0.0
 */
public class RequestContextFilter extends OncePerRequestFilter {

    private final RequestContextProperties properties;

    /**
     * Constructs a new RequestContextFilter with the specified properties.
     *
     * @param properties the configuration properties for request context
     */
    public RequestContextFilter(final RequestContextProperties properties) {
        this.properties = properties;
    }

    /**
     * Determines whether the filter should be applied to the given request.
     *
     * <p>By default, the filter skips actuator endpoints and error pages.</p>
     *
     * @param request the HTTP request
     * @return true if the filter should not be applied, false otherwise
     */
    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        String path = request.getRequestURI();
        // Bypass tenant context validation for actuator endpoints and error pages
        return path.startsWith("/actuator") || path.startsWith("/error");
    }

    /**
     * Processes the HTTP request and response.
     *
     * <p>Extracts context information from headers and JWT claims, validates required
     * fields, and stores the context in RequestContextHolder.</p>
     *
     * @param request the HTTP request
     * @param response the HTTP response
     * @param filterChain the filter chain
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain)
            throws ServletException, IOException {

        String correlationIdHeader = properties.getCorrelationIdHeader();
        String correlationId = headerValue(request, correlationIdHeader);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        String countryHeaderValue = headerValue(request, properties.getCountryHeader());
        String tenantIdHeaderValue = headerValue(request, properties.getTenantIdHeader());
        String userIdHeaderValue = headerValue(request, properties.getUserIdHeader());
        String requestIdHeaderValue = headerValue(request, properties.getRequestIdHeader());

        String country = countryHeaderValue;
        String tenantId = tenantIdHeaderValue;
        String userId = userIdHeaderValue;
        String requestId = requestIdHeaderValue;

        if (properties.isPreferJwtClaims()) {
            Jwt jwt = resolveJwt();
            if (jwt != null) {
                String jwtTenant = firstNonBlank(
                        claimAsString(jwt, properties.getTenantIdClaim()),
                        claimAsString(jwt, properties.getTenantIdClaimFallback()));
                String jwtCountry = firstNonBlank(
                        claimAsString(jwt, properties.getCountryClaim()),
                        claimAsString(jwt, properties.getCountryClaimFallback()));
                String jwtUserId = firstNonBlank(
                        claimAsString(jwt, properties.getUserIdClaim()),
                        claimAsString(jwt, properties.getUserIdClaimFallback()));

                if (properties.isEnforceHeaderJwtMatch()) {
                    if (tenantIdHeaderValue != null
                            && !tenantIdHeaderValue.isBlank()
                            && jwtTenant != null
                            && !jwtTenant.equals(tenantIdHeaderValue)) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                "X-Tenant-Id does not match authenticated tenant");
                        return;
                    }
                    if (countryHeaderValue != null
                            && !countryHeaderValue.isBlank()
                            && jwtCountry != null
                            && !jwtCountry.equals(countryHeaderValue)) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                "X-Country does not match authenticated country");
                        return;
                    }
                    if (userIdHeaderValue != null
                            && !userIdHeaderValue.isBlank()
                            && jwtUserId != null
                            && !jwtUserId.equals(userIdHeaderValue)) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                                "X-User-Id does not match authenticated user");
                        return;
                    }
                }

                if (jwtTenant != null && !jwtTenant.isBlank()) {
                    tenantId = jwtTenant;
                }
                if (jwtCountry != null && !jwtCountry.isBlank()) {
                    country = jwtCountry;
                }
                if (jwtUserId != null && !jwtUserId.isBlank()) {
                    userId = jwtUserId;
                }
            }
        }

        if (properties.isRequireCountry() && (country == null || country.isBlank())) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required country context");
            return;
        }

        if (properties.isRequireTenantId() && (tenantId == null || tenantId.isBlank())) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required tenant context");
            return;
        }

        if (properties.isRequireUserId() && (userId == null || userId.isBlank())) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing required user context");
            return;
        }

        // Validate requestId format if provided
        if (requestId != null && !requestId.isBlank()) {
            try {
                UUID.fromString(requestId);
            } catch (IllegalArgumentException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "X-Request-Id must be a valid UUID");
                return;
            }
        }

        RequestContextHolder.set(new RequestContext(
                correlationId, country, tenantId, userId, requestId));
        MDC.put("correlationId", correlationId);
        if (tenantId != null && !tenantId.isBlank()) {
            MDC.put("tenantId", tenantId);
        }
        if (country != null && !country.isBlank()) {
            MDC.put("country", country);
        }
        if (userId != null && !userId.isBlank()) {
            MDC.put("userId", userId);
        }
        if (requestId != null && !requestId.isBlank()) {
            MDC.put("requestId", requestId);
        }
        response.setHeader(correlationIdHeader, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove("correlationId");
            MDC.remove("tenantId");
            MDC.remove("country");
            MDC.remove("userId");
            MDC.remove("requestId");
            RequestContextHolder.clear();
        }
    }

    /**
     * Extracts a header value from the HTTP request.
     *
     * @param request the HTTP request
     * @param headerName the name of the header to extract
     * @return the header value or null if not found
     */
    private static String headerValue(final HttpServletRequest request, final String headerName) {
        if (headerName == null || headerName.isBlank()) {
            return null;
        }
        return request.getHeader(headerName);
    }

    /**
     * Resolves the JWT from the Spring Security context.
     *
     * @return the JWT or null if not available
     */
    private static Jwt resolveJwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt;
        }
        Object credentials = authentication.getCredentials();
        if (credentials instanceof Jwt jwt) {
            return jwt;
        }
        return null;
    }

    /**
     * Extracts a claim value from the JWT as a string.
     *
     * @param jwt the JWT token
     * @param claimName the name of the claim to extract
     * @return the claim value as a string or null if not found
     */
    private static String claimAsString(final Jwt jwt, final String claimName) {
        if (jwt == null || claimName == null || claimName.isBlank()) {
            return null;
        }
        Object value = jwt.getClaims().get(claimName);
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    /**
     * Returns the first non-blank string from the given options.
     *
     * @param first the first string to check
     * @param second the second string to check
     * @return the first non-blank string or null if both are blank
     */
    private static String firstNonBlank(final String first, final String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }
}
