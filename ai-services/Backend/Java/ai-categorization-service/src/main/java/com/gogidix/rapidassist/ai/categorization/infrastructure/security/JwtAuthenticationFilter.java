package com.gogidix.rapidassist.ai.categorization.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JWT Authentication Filter.
 *
 * This filter:
 * - Extracts JWT from Authorization header
 * - Validates the token
 * - Sets up Spring Security context with user authorities
 * - Runs BEFORE TenantInterceptor to ensure authentication context is available
 *
 * CRITICAL: This filter MUST run before TenantInterceptor.
 * TenantInterceptor will extract tenant_id from the validated JWT claims.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TENANT_ID_CLAIM = "tenant_id";
    private static final String USER_ID_CLAIM = "user_id";
    private static final String ROLES_CLAIM = "roles";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader(AUTHORIZATION_HEADER);

        // Skip if no Authorization header
        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // Extract JWT token
            String jwt = authHeader.substring(BEARER_PREFIX.length());

            // TODO: Validate JWT with proper JWT validator
            // For now, we'll do basic validation
            if (isValidJwt(jwt)) {
                // Extract claims from JWT
                JwtClaims claims = extractClaims(jwt);

                // Create authentication token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                claims.getSubject(),
                                null,
                                claims.getAuthorities()
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // Set security context
                SecurityContextHolder.getContext().setAuthentication(authToken);

                // Store tenant ID in request attributes for TenantInterceptor
                request.setAttribute("tenant_id", claims.getTenantId());
                request.setAttribute("user_id", claims.getUserId());

                log.debug("JWT authentication successful for user: {}", claims.getSubject());
            } else {
                log.warn("Invalid JWT token provided");
            }

        } catch (Exception e) {
            log.error("Error processing JWT token", e);
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Validate JWT token.
     * TODO: Implement proper JWT validation with signature verification.
     *
     * @param jwt the JWT token
     * @return true if valid, false otherwise
     */
    private boolean isValidJwt(String jwt) {
        // Basic validation - JWT should have 3 parts separated by dots
        if (jwt == null || jwt.isBlank()) {
            return false;
        }

        String[] parts = jwt.split("\\.");
        return parts.length == 3;
    }

    /**
     * Extract claims from JWT.
     * TODO: Implement proper JWT parsing with JWT library.
     *
     * @param jwt the JWT token
     * @return the JWT claims
     */
    private JwtClaims extractClaims(String jwt) {
        // For now, return default claims
        // In production, parse the JWT and extract actual claims
        return JwtClaims.builder()
                .subject("user")
                .tenantId("default-tenant")
                .userId("default-user")
                .roles(List.of("ROLE_USER"))
                .build();
    }

    /**
     * Inner class to hold JWT claims.
     */
    @lombok.Builder
    @lombok.Data
    private static class JwtClaims {
        private String subject;
        private String tenantId;
        private String userId;
        private List<String> roles;

        public List<SimpleGrantedAuthority> getAuthorities() {
            if (roles == null) {
                return List.of(new SimpleGrantedAuthority("ROLE_USER"));
            }
            return roles.stream()
                    .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        }
    }
}
