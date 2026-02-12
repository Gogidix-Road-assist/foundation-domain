package com.gogidix.rapidassist.orchestration.fleetorganization.config;

import com.gogidix.rapidassist.orchestration.fleetorganization.shared.requestcontext.RequestContext;
import com.gogidix.rapidassist.orchestration.fleetorganization.shared.requestcontext.RequestContextHolder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filter to extract tenant context from HTTP headers
 * For production, this should validate JWT tokens
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String CORRELATION_HEADER = "X-Correlation-ID";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {

        // Extract tenant ID from header (in production, validate from JWT)
        String tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId == null || tenantId.isBlank()) {
            // For testing, allow requests without tenant
            tenantId = "default-tenant";
            log.debug("No tenant ID provided, using default: {}", tenantId);
        }

        // Extract correlation ID
        String correlationId = request.getHeader(CORRELATION_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = java.util.UUID.randomUUID().toString();
        }

        // Build and set request context
        RequestContext context = RequestContext.builder()
                .tenantId(tenantId)
                .userId(null) // Extract from JWT in production
                .correlationId(correlationId)
                .build();

        RequestContextHolder.set(context);

        // Add correlation ID to response
        response.setHeader(CORRELATION_HEADER, correlationId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Always clear context to prevent memory leaks
            RequestContextHolder.clear();
        }
    }
}
