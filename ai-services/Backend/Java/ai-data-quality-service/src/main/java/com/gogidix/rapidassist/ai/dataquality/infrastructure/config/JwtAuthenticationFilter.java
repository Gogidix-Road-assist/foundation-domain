package com.gogidix.rapidassist.ai.dataquality.infrastructure.config;

import com.gogidix.rapidassist.shared.security.library.jwt.JwtTokenUtil;
// Tenant context handled by RequestContextHolder
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
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter
 * Validates JWT tokens from Authorization header
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String jwt = extractJwtFromRequest(request);

            if (StringUtils.hasText(jwt) && !jwtTokenUtil.isTokenExpired(jwt)) {
                String userId = jwtTokenUtil.extractUsername(jwt);
                String tenantId = jwtTokenUtil.extractTenantId(jwt);

                log.debug("Authenticated user {} for tenant {}", userId, tenantId);

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Set tenant context via RequestContextHolder
                com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
                    com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                        .tenantId(tenantId)
                        .userId(userId)
                        .requestId(java.util.UUID.randomUUID().toString())
                        .build()
                );
            }
        } catch (Exception ex) {
            log.error("Could not set user authentication in security context", ex);
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/actuator/health") ||
               path.startsWith("/actuator/info") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/v3/api-docs");
    }
}
