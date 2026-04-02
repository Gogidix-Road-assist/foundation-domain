package com.gogidix.rapidassist.ai.tagging.infrastructure.security;

import com.gogidix.rapidassist.ai.tagging.infrastructure.tenant.RequestContext;
import com.gogidix.rapidassist.ai.tagging.infrastructure.tenant.RequestContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * JWT Authentication Filter
 * Validates JWT tokens and sets SecurityContext
 * Works in conjunction with TenantInterceptor
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TENANT_ID_CLAIM = "tenant_id";
    private static final String USER_ID_CLAIM = "sub";
    private static final String ROLE_PREFIX = "ROLE_";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        // Extract JWT from header
        String token = extractJwt(request);

        if (token != null && isValidToken(token)) {
            try {
                // In production, use proper JWT validation library (e.g., jjwt)
                // For now, we'll do basic validation and extraction

                // Extract claims from token (simplified - production should use JWT library)
                JwtClaims claims = extractClaims(token);

                // Create authentication object
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        claims.getUserId(),
                        token,
                        claims.getRoles()
                    );

                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Set security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.debug("Authenticated user: {} for tenant: {}",
                    claims.getUserId(), claims.getTenantId());

            } catch (Exception e) {
                log.error("Failed to authenticate user", e);
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extract JWT from Authorization header
     */
    private String extractJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    /**
     * Basic token validation
     * In production, use proper JWT library with signature verification
     */
    private boolean isValidToken(String token) {
        // Basic validation - token should not be empty
        if (token == null || token.isBlank()) {
            return false;
        }

        // In production, validate:
        // 1. Signature using JWT library
        // 2. Expiration time
        // 3. Issuer
        // 4. Audience

        return token.length() > 20; // Simple validation for now
    }

    /**
     * Extract claims from JWT token
     * In production, use JWT library (e.g., jjwt)
     */
    private JwtClaims extractClaims(String token) {
        // For production, use: Jwts.parserBuilder()...
        // For now, return default claims

        // In production, extract from JWT:
        // String tenantId = claims.get(TENANT_ID_CLAIM, String.class);
        // String userId = claims.get(USER_ID_CLAIM, String.class);
        // List<String> roles = claims.get("roles", List.class);

        return JwtClaims.builder()
            .tenantId("default-tenant")
            .userId(UUID.randomUUID().toString())
            .roles(List.of(new SimpleGrantedAuthority(ROLE_PREFIX + "USER")))
            .build();
    }

    /**
     * Inner class representing JWT claims
     */
    @lombok.Builder
    @lombok.Data
    public static class JwtClaims {
        private String tenantId;
        private String userId;
        private List<SimpleGrantedAuthority> roles;
    }
}
