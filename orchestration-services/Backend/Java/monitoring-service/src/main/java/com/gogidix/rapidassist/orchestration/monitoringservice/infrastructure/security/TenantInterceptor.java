package com.gogidix.rapidassist.orchestration.monitoringservice.infrastructure.security;

import com.gogidix.rapidassist.orchestration.monitoringservice.shared.requestcontext.RequestContext;
import com.gogidix.rapidassist.orchestration.monitoringservice.shared.requestcontext.RequestContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * INTERCEPTS ALL REQUESTS and extracts tenant context
 * This is the ENTRY POINT for multi-tenancy
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantInterceptor implements HandlerInterceptor {

    private static final String TENANT_ID_HEADER = "X-Tenant-ID";
    private static final String USER_ID_HEADER = "X-User-ID";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) {

        // 1. Extract tenant ID from header
        String tenantId = request.getHeader(TENANT_ID_HEADER);
        if (tenantId == null || tenantId.isBlank()) {
            throw new SecurityException("Missing required header: " + TENANT_ID_HEADER);
        }

        // 2. Extract user ID (optional for system-to-system calls)
        String userId = request.getHeader(USER_ID_HEADER);

        // 3. Extract or generate correlation ID
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

        // 5. Add correlation ID to response for tracing
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
        log.debug("Tenant context cleared");
    }
}
