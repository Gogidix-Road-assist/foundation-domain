package com.gogidix.rapidassist.dashboard.configuration.service.security;

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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT authentication filter for processing JWT tokens in HTTP requests.
 *
 * <p>This filter extracts JWT tokens from the Authorization header, validates them,
 * and sets the authentication in the SecurityContext if valid.</p>
 *
 * <p>Note: This is a basic implementation for development purposes. For production:</p>
 * <ul>
 *   <li>Implement proper JWT validation with JwtTokenProvider</li>
 *   <li>Load user details from a user service</li>
 *   <li>Add proper error handling for invalid/expired tokens</li>
 *   <li>Implement token refresh logic</li>
 *   <li>Add proper logging and monitoring</li>
 * </ul>
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt)) {
                // TODO: Implement proper JWT validation with JwtTokenProvider
                // For now, this is a placeholder that logs the token
                logger.debug("Processing JWT token for request: {}", request.getRequestURI());

                // Placeholder authentication - replace with actual JWT validation
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        "user", // principal - should be extracted from JWT
                        null,   // credentials
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // authorities - should be extracted from JWT
                    );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                logger.debug("Set authentication for user: {}", authentication.getName());
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Extracts the JWT token from the Authorization header.
     *
     * @param request the HTTP request
     * @return the JWT token, or null if not present
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }
}
