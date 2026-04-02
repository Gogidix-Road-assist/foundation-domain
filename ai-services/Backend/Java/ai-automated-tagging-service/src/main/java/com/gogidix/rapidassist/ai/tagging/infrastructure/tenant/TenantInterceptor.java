package com.gogidix.rapidassist.ai.tagging.infrastructure.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
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

    private static final String TENANT_ID_HEADER = "X-Tenant-ID";
    private static final String USER_ID_HEADER = "X-User-ID";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) throws Exception {

        // 1. Extract tenantId from header (in production, this would come from JWT)
        String tenantId = request.getHeader(TENANT_ID_HEADER);
        if (tenantId == null || tenantId.isBlank()) {
            // For development/testing, use a default tenant
            tenantId = "default-tenant";
        }

        // 2. Extract userId from header (in production, this would come from JWT)
        String userId = request.getHeader(USER_ID_HEADER);
        if (userId == null || userId.isBlank()) {
            userId = "system-user";
        }

        // 3. Extract or generate correlationId
        String correlationId = request.getHeader(CORRELATION_ID_HEADER);
        if (correlationId == null || correlationId.isBlank()) {
            correlationId = UUID.randomUUID().toString();
        }

        // 4. Build and set RequestContext
        RequestContext context = RequestContext.builder()
            .tenantId(tenantId)
            .userId(userId)
            .correlationId(correlationId)
            .build();

        RequestContextHolder.set(context);

        // 5. Add to MDC for logging
        MDC.put("tenantId", tenantId);
        MDC.put("correlationId", correlationId);

        // 6. Add correlationId to response for tracing
        response.setHeader(CORRELATION_ID_HEADER, correlationId);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler,
                               Exception ex) throws Exception {
        // ALWAYS clear context to prevent memory leaks
        RequestContextHolder.clear();
        MDC.clear();
    }
}
