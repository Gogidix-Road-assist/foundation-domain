package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.security;

import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import com.gogidix.rapidassist.shared.security.library.exception.InvalidTokenException;
import com.gogidix.rapidassist.shared.security.library.jwt.JwtTokenUtil;
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
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter
 * Validates JWT tokens and sets security context
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                HttpServletResponse response,
                                FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = extractJwt(request);

            if (token != null && jwtTokenUtil.validateToken(token)) {
                // Extract user details from token
                String username = jwtTokenUtil.extractUsername(token);
                String userId = jwtTokenUtil.extractUserId(token);
                String tenantId = jwtTokenUtil.extractTenantId(token);

                log.debug("Authenticated user: {}, tenant: {}", username, tenantId);

                // Create authentication token
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        username,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Set security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Ensure RequestContext is set (may already be set by TenantInterceptor)
                if (RequestContextHolder.get().isEmpty()) {
                    RequestContext context = RequestContext.builder()
                        .tenantId(tenantId)
                        .userId(userId)
                        .requestId(java.util.UUID.randomUUID().toString())
                        .build();
                    RequestContextHolder.set(context);
                }
            }

            filterChain.doFilter(request, response);
        } catch (InvalidTokenException e) {
            log.error("JWT token validation failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Invalid token\",\"message\":\"" + e.getMessage() + "\"}");
        } catch (Exception e) {
            log.error("Unexpected error in JWT filter", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Extract JWT from Authorization header
     */
    private String extractJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
