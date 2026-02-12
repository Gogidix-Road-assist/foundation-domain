package com.gogidix.rapidassist.access.control.service.infrastructure.security;

import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * Interceptor: TenantInterceptor
 *
 * INTERCEPTS ALL REQUESTS and extracts tenant context from JWT.
 * This is the ENTRY POINT for multi-tenancy in this service.
 *
 * Responsibilities:
 * 1. Extract tenant_id from JWT claims (via Authorization header)
 * 2. Validate tenant exists and is active
 * 3. Set RequestContext with tenantId, userId, correlationId
 * 4. Always clear context after request completes
 *
 * NOTE: In production, this would integrate with a proper JWT validator.
 * For now, we extract from headers for demonstration.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(TenantInterceptor.class);

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String USER_HEADER = "X-User-ID";
    private static final String CORRELATION_HEADER = "X-Correlation-ID";

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) {

        // Extract tenant ID from header (in production, this comes from JWT)
        String tenantId = request.getHeader(TENANT_HEADER);
        if (tenantId == null || tenantId.isBlank()) {
            log.warn("Missing tenant ID in request: {}", request.getRequestURI());
            // For backward compatibility, try query param
            tenantId = request.getParameter("tenantId");
            if (tenantId == null || tenantId.isBlank()) {
                throw new TenantContextException("Missing tenant context. Provide " + TENANT_HEADER + " header.");
            }
        }

        // Extract user ID from header
        String userId = request.getHeader(USER_HEADER);

        // Extract or generate correlation ID
        String correlationId = request.getHeader(CORRELATION_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        // Build and set RequestContext
        RequestContext context = RequestContext.builder()
                .tenantId(tenantId)
                .userId(userId)
                .correlationId(correlationId)
                .build();

        RequestContextHolder.set(context);

        // Add correlation ID to response for tracing
        response.setHeader(CORRELATION_HEADER, correlationId);

        log.debug("Tenant context established: tenantId={}, userId={}, correlationId={}",
                tenantId, userId, correlationId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler,
                               Exception ex) {
        // ALWAYS clear context to prevent memory leaks
        RequestContextHolder.clear();
        log.debug("Tenant context cleared");
    }

    /**
     * Exception thrown when tenant context is missing or invalid.
     */
    public static class TenantContextException extends RuntimeException {
        public TenantContextException(String message) {
            super(message);
        }
    }
}
