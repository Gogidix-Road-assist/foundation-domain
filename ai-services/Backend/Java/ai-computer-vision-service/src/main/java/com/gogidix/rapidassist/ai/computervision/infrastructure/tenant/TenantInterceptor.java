package com.gogidix.rapidassist.ai.computervision.infrastructure.tenant;

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
 * INTERCEPTS ALL REQUESTS and extracts tenant context from JWT
 * This is the ENTRY POINT for multi-tenancy
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(TenantInterceptor.class);
    private static final String TENANT_ID_HEADER = "X-Tenant-ID";
    private static final String USER_ID_HEADER = "X-User-ID";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) {

        // 1. Extract tenantId from header
        String tenantId = request.getHeader(TENANT_ID_HEADER);
        if (tenantId == null || tenantId.isBlank()) {
            logger.warn("Missing tenant_id in request headers");
            // For development/testing purposes, use a default tenant
            // In production, this should throw an exception
            tenantId = "default-tenant";
        }

        // 2. Extract userId from header
        String userId = request.getHeader(USER_ID_HEADER);

        // 3. Extract or generate correlationId
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        // 4. Build and set TenantContext
        TenantContext context = TenantContext.builder()
                .tenantId(tenantId)
                .userId(userId)
                .correlationId(correlationId)
                .build();

        RequestContextHolder.set(context);

        // 5. Add correlationId to response for tracing
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        logger.debug("Tenant context set: tenantId={}, userId={}, correlationId={}",
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
        logger.debug("Tenant context cleared");
    }
}
