package com.gogidix.rapidassist.ai.dataquality.infrastructure.config;

// Tenant context handled by RequestContextHolder
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * API Key Authentication Filter
 * Validates API keys from X-API-Key header
 */
@Slf4j
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    @Value("${security.api-key.enabled:true}")
    private boolean apiKeyEnabled;

    @Value("${security.api_key.secret:}")
    private String apiKeySecret;

    private static final String API_KEY_HEADER = "X-API-Key";
    private static final String TENANT_ID_HEADER = "X-Tenant-ID";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        if (!apiKeyEnabled) {
            filterChain.doFilter(request, response);
            return;
        }

        String apiKey = request.getHeader(API_KEY_HEADER);

        if (apiKey != null && !apiKey.isEmpty()) {
            if (apiKeySecret != null && apiKey.equals(apiKeySecret)) {
                String tenantId = request.getHeader(TENANT_ID_HEADER);

                log.debug("Authenticated via API Key for tenant {}", tenantId);

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        "api-key-user",
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_API_USER"))
                    );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Set tenant context via RequestContextHolder
                if (tenantId != null) {
                    com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
                        com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                            .tenantId(tenantId)
                            .requestId(java.util.UUID.randomUUID().toString())
                            .build()
                    );
                }
            } else {
                log.warn("Invalid API Key provided");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        filterChain.doFilter(request, response);
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
