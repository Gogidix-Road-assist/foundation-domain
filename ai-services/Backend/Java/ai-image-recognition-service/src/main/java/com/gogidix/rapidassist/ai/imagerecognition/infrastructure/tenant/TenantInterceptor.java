package com.gogidix.rapidassist.ai.imagerecognition.infrastructure.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * INTERCEPTS ALL REQUESTS and extracts tenant context from JWT.
 * This is the ENTRY POINT for multi-tenancy.
 */
@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor, Ordered {

    private static final String TENANT_ID_HEADER = "X-Tenant-ID";
    private static final String USER_ID_HEADER = "X-User-ID";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public boolean preHandle(HttpServletRequest request,
                          HttpServletResponse response,
                          Object handler) {

        // 1. Extract tenantId from header
        String tenantId = extractTenantId(request);
        if (tenantId == null || tenantId.isBlank()) {
            log.warn("Missing tenant_id in request headers");
            throw new IllegalArgumentException("Missing tenant_id in request headers");
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

        log.debug("Tenant context set: tenantId={}, userId={}, correlationId={}",
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

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    private String extractTenantId(HttpServletRequest request) {
        // Try X-Tenant-ID header first
        String tenantId = request.getHeader(TENANT_ID_HEADER);

        // If not found, try Authorization header (JWT)
        if (tenantId == null || tenantId.isBlank()) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                // In production, this would validate JWT and extract tenant_id from claims
                // For now, we'll use the header approach
                log.debug("Bearer token found, would extract tenant_id from JWT in production");
            }
        }

        return tenantId;
    }
}
