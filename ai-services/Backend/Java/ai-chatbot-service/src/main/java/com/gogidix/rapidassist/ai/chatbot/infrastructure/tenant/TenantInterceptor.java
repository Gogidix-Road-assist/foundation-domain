package com.gogidix.rapidassist.ai.chatbot.infrastructure.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;
import java.util.UUID;

/**
 * INTERCEPTS ALL REQUESTS and extracts tenant context from JWT
 * This is the ENTRY POINT for multi-tenancy
 */
@Component
@Slf4j
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

        try {
            // 1. Extract tenantId from header (JWT validation happens at gateway)
            String tenantId = extractTenantId(request);
            if (tenantId == null || tenantId.isBlank()) {
                throw new IllegalStateException("Missing or blank tenant_id in header");
            }

            // 2. Extract userId from header
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

            log.debug("Tenant context set: tenantId={}, userId={}, correlationId={}",
                tenantId, userId, correlationId);

            return true;

        } catch (Exception e) {
            log.error("Failed to set tenant context: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to set tenant context: " + e.getMessage(), e);
        }
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) {
        // Optional: Add tenant info to model if needed
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler,
                               Exception ex) {
        // ALWAYS clear context to prevent memory leaks
        log.debug("Clearing tenant context");
        RequestContextHolder.clear();
    }

    /**
     * Extract tenant ID from request header
     */
    private String extractTenantId(HttpServletRequest request) {
        String tenantId = request.getHeader(TENANT_HEADER);

        if (tenantId == null || tenantId.isBlank()) {
            log.warn("Missing tenant header: {}", TENANT_HEADER);
            throw new IllegalStateException(
                "Missing required header: " + TENANT_HEADER
            );
        }

        return tenantId;
    }
}
