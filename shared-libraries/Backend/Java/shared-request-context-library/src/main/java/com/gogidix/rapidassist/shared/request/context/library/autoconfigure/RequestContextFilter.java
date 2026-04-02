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

public class RequestContextFilter extends OncePerRequestFilter {

    private final RequestContextProperties properties;

    public RequestContextFilter(RequestContextProperties properties) {
        this.properties = properties;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        // Bypass tenant context validation for actuator endpoints and error pages
        return path.startsWith("/actuator") || path.startsWith("/error");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
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
                String jwtTenant = firstNonBlank(claimAsString(jwt, properties.getTenantIdClaim()), claimAsString(jwt, properties.getTenantIdClaimFallback()));
                String jwtCountry = firstNonBlank(claimAsString(jwt, properties.getCountryClaim()), claimAsString(jwt, properties.getCountryClaimFallback()));
                String jwtUserId = firstNonBlank(claimAsString(jwt, properties.getUserIdClaim()), claimAsString(jwt, properties.getUserIdClaimFallback()));

                if (properties.isEnforceHeaderJwtMatch()) {
                    if (tenantIdHeaderValue != null && !tenantIdHeaderValue.isBlank() && jwtTenant != null && !jwtTenant.equals(tenantIdHeaderValue)) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "X-Tenant-Id does not match authenticated tenant");
                        return;
                    }
                    if (countryHeaderValue != null && !countryHeaderValue.isBlank() && jwtCountry != null && !jwtCountry.equals(countryHeaderValue)) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "X-Country does not match authenticated country");
                        return;
                    }
                    if (userIdHeaderValue != null && !userIdHeaderValue.isBlank() && jwtUserId != null && !jwtUserId.equals(userIdHeaderValue)) {
                        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "X-User-Id does not match authenticated user");
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
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "X-Request-Id must be a valid UUID");
                return;
            }
        }

        RequestContextHolder.set(new RequestContext(correlationId, country, tenantId, userId, requestId));
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

    private static String headerValue(HttpServletRequest request, String headerName) {
        if (headerName == null || headerName.isBlank()) {
            return null;
        }
        return request.getHeader(headerName);
    }

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

    private static String claimAsString(Jwt jwt, String claimName) {
        if (jwt == null || claimName == null || claimName.isBlank()) {
            return null;
        }
        Object value = jwt.getClaims().get(claimName);
        if (value == null) {
            return null;
        }
        return String.valueOf(value);
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }
}
