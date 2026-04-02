package com.gogidix.rapidassist.ai.dataquality.infrastructure.rest.filter;

// Tenant context handled by RequestContextHolder
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to extract and set tenant context from X-Tenant-ID header
 * This is a fallback filter for when JWT/API key authentication is not used
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(TenantFilter.class);
    private static final String TENANT_HEADER = "X-Tenant-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Only set tenant context if not already set by JWT/API key filters
        String tenantId = request.getHeader(TENANT_HEADER);

        if (tenantId != null && !tenantId.isBlank()) {
            // Set tenant context via RequestContextHolder
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.set(
                com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext.builder()
                    .tenantId(tenantId)
                    .requestId(java.util.UUID.randomUUID().toString())
                    .build()
            );
            log.debug("Set tenant context from header: {}", tenantId);
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder.clear();
            log.debug("Cleared tenant context");
        }
    }
}
