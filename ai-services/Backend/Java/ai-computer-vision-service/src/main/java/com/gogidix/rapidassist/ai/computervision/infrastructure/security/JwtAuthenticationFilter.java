package com.gogidix.rapidassist.ai.computervision.infrastructure.security;

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
 * JWT Authentication Filter
 * Validates JWT tokens and sets authentication context
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        String token = extractJwt(request);

        if (token != null && validateToken(token)) {
            // For now, we'll do a simple validation
            // In production, this should use a proper JWT validation library
            // like jjwt or Spring Security's JWT support

            String username = extractUsernameFromToken(token);

            if (username != null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                Collections.emptyList()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                logger.debug("Set authentication for user: {}", username);
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
     * Validate JWT token
     * For now, this is a simple validation
     * In production, use proper JWT validation
     */
    private boolean validateToken(String token) {
        // Basic validation - check if token is not empty
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        // In production, validate token signature and expiration
        // Example: Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)

        return true;
    }

    /**
     * Extract username from JWT token
     * For now, this is a simple implementation
     */
    private String extractUsernameFromToken(String token) {
        // In production, extract claims from JWT token
        // Example: Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject()

        // For development, return a default user
        return "system-user";
    }
}
