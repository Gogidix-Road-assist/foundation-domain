package com.gogidix.rapidassist.ai.forecasting.infrastructure.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * INTERCEPTS ALL REQUESTS and extracts tenant context from headers
 * This is the ENTRY POINT for multi-tenancy
 */
@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor, Ordered {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String CORRELATION_HEADER = "X-Correlation-ID";
    private static final String USER_HEADER = "X-User-ID";

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) {

        // 1. Extract tenantId from header
        String tenantId = extractTenantId(request);
        if (tenantId == null || tenantId.isBlank()) {
            log.warn("Missing tenant ID in request headers");
            throw new IllegalArgumentException("Missing X-Tenant-ID header");
        }

        // 2. Extract userId from header (optional for now)
        String userId = request.getHeader(USER_HEADER);

        // 3. Extract or generate correlationId
        String correlationId = request.getHeader(CORRELATION_HEADER);
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

        // 5. Add correlationId to response for tracing
        response.setHeader(CORRELATION_HEADER, correlationId);

        log.debug("Set tenant context - tenantId: {}, userId: {}, correlationId: {}",
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
    }

    private String extractTenantId(HttpServletRequest request) {
        return request.getHeader(TENANT_HEADER);
    }
}
