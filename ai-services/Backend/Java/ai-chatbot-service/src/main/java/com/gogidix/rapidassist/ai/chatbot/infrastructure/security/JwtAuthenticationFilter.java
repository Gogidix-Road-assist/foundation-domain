package com.gogidix.rapidassist.ai.chatbot.infrastructure.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
 * JWT Authentication Filter
 * Validates JWT token and sets authentication context
 */
@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String TENANT_CLAIM = "tenantId";
    private static final String USER_CLAIM = "userId";
    private static final String ROLE_CLAIM = "role";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            // 1. Extract token from header
            String token = extractToken(request);

            if (token != null && !token.isBlank()) {
                // 2. Validate token and extract claims
                JwtClaims claims = validateToken(token);

                if (claims != null && claims.isValid()) {
                    // 3. Create authentication object
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            claims.getUserId(),
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_" + claims.getRole()))
                        );

                    authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // 4. Set security context
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    log.debug("JWT authenticated: userId={}, tenantId={}, role={}",
                        claims.getUserId(), claims.getTenantId(), claims.getRole());
                }
            }

        } catch (Exception e) {
            log.error("JWT authentication failed: {}", e.getMessage());
            // Continue filter chain even if authentication fails
            // Let the endpoint return 401
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // Skip filter for public endpoints
        String path = request.getRequestURI();
        return path.startsWith("/actuator") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/api-docs") ||
               path.endsWith("/health");
    }

    /**
     * Extract JWT token from Authorization header
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (bearerToken != null && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    /**
     * Validate JWT token and extract claims
     * In production, use proper JWT library (jjwt, nimbus, etc.)
     */
    private JwtClaims validateToken(String token) {
        // TODO: Implement proper JWT validation with signature verification
        // For now, simplified implementation

        try {
            // Parse token (simplified - use proper JWT library in production)
            String[] parts = token.split("\\.");

            if (parts.length != 3) {
                log.warn("Invalid JWT format");
                return null;
            }

            // Extract claims from payload (part 1)
            String payload = parts[1];

            // Decode Base64 (simplified)
            java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
            String decoded = new String(decoder.decode(payload));

            // Parse JSON (simplified - use Jackson/JwtParser in production)
            String tenantId = extractClaim(decoded, TENANT_CLAIM);
            String userId = extractClaim(decoded, USER_CLAIM);
            String role = extractClaim(decoded, ROLE_CLAIM);

            return new JwtClaims(tenantId, userId, role, true);

        } catch (Exception e) {
            log.error("Failed to validate JWT: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Simple claim extraction (use proper JWT library in production)
     */
    private String extractClaim(String payload, String claim) {
        String pattern = "\"" + claim + "\":\"?([^,\"}]+)\"?";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
        java.util.regex.Matcher m = p.matcher(payload);

        if (m.find()) {
            return m.group(1).replaceAll("\"", "");
        }

        return null;
    }

    /**
     * JWT Claims holder
     */
    private record JwtClaims(
        String tenantId,
        String userId,
        String role,
        boolean valid
    ) {
        public boolean isValid() {
            return valid && tenantId != null && userId != null;
        }

        public String getTenantId() {
            return tenantId;
        }

        public String getUserId() {
            return userId;
        }

        public String getRole() {
            return role != null ? role : "USER";
        }
    }
}
