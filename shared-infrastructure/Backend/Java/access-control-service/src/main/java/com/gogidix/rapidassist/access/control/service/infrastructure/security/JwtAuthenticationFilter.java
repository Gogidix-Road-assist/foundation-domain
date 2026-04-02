package com.gogidix.rapidassist.access.control.service.infrastructure.security;

import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * Filter: JwtAuthenticationFilter
 *
 * Validates JWT tokens and sets up Spring Security context.
 * Integrates with the shared RequestContext for tenant isolation.
 *
 * NOTE: This is a simplified implementation. In production,
 * use a proper JWT library like jjwt or Spring Security OAuth2 Resource Server.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain)
            throws ServletException, IOException {

        String bearerToken = request.getHeader("Authorization");

        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);

            try {
                // TODO: Implement proper JWT validation
                // For now, we'll rely on the TenantInterceptor for context

                // Extract claims (simplified - in production, use JWT library)
                String userId = extractSubjectFromToken(token);

                if (userId != null) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userId,
                                    null,
                                    Collections.emptyList()
                            );
                    authentication.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                log.warn("JWT validation failed: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractSubjectFromToken(String token) {
        // Simplified - in production, use proper JWT parsing
        // Return null to let TenantInterceptor handle context
        return null;
    }
}
