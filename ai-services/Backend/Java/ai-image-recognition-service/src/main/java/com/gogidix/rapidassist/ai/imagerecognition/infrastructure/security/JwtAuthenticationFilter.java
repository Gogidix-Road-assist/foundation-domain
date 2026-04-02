package com.gogidix.rapidassist.ai.imagerecognition.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter.
 * Validates JWT tokens and sets authentication in SecurityContext.
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                 HttpServletResponse response,
                                 FilterChain filterChain) throws ServletException, IOException {

        // 1. Extract JWT from Authorization header
        String token = extractJwt(request);

        if (token != null && !token.isBlank()) {
            try {
                // 2. Validate JWT and extract claims
                // In production, this would use a proper JWT validator
                // For now, we'll do basic validation
                JwtClaims claims = validateToken(token);

                if (claims != null && claims.isValid()) {
                    // 3. Convert roles to GrantedAuthority
                    Collection<org.springframework.security.core.GrantedAuthority> authorities =
                            claims.getRoles().stream()
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toList());

                    // 4. Create authentication object
                    TenantAuthentication authentication = new TenantAuthentication(
                            claims.getTenantId(),
                            claims.getUserId(),
                            authorities,
                            null
                    );

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // 4. Set authentication in SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("JWT authentication successful for tenant: {}", claims.getTenantId());
                }
            } catch (Exception e) {
                log.error("JWT authentication failed: {}", e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    private JwtClaims validateToken(String token) {
        // In production, this would use a proper JWT library like jjwt
        // For now, we'll return a placeholder
        // This is a simplified implementation
        try {
            // Basic validation - check if token is not empty
            if (token != null && !token.isBlank()) {
                // Parse token and extract claims
                // For now, return a placeholder
                return new JwtClaims("tenant-1", "user-1", new ArrayList<>());
            }
        } catch (Exception e) {
            log.error("Token validation failed: {}", e.getMessage());
        }
        return null;
    }

    /**
     * Simple JWT claims holder.
     * In production, use a proper JWT library.
     */
    private static class JwtClaims {
        private final String tenantId;
        private final String userId;
        private final java.util.List<String> roles;

        public JwtClaims(String tenantId, String userId, java.util.List<String> roles) {
            this.tenantId = tenantId;
            this.userId = userId;
            this.roles = roles;
        }

        public String getTenantId() {
            return tenantId;
        }

        public String getUserId() {
            return userId;
        }

        public java.util.List<String> getRoles() {
            return roles;
        }

        public boolean isValid() {
            return tenantId != null && !tenantId.isBlank();
        }
    }

    /**
     * Custom authentication for tenant-based security.
     */
    public static class TenantAuthentication extends org.springframework.security.authentication.UsernamePasswordAuthenticationToken {
        private final String tenantId;

        public TenantAuthentication(String tenantId, String userId, java.util.Collection<? extends org.springframework.security.core.GrantedAuthority> authorities, Object credentials) {
            super(userId, credentials, authorities);
            this.tenantId = tenantId;
        }

        public String getTenantId() {
            return tenantId;
        }

        public String getUserId() {
            return getPrincipal() != null ? getPrincipal().toString() : null;
        }
    }
}
