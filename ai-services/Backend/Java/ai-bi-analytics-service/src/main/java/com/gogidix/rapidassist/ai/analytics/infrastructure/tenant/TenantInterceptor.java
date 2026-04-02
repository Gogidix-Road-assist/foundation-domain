package com.gogidix.rapidassist.ai.analytics.infrastructure.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor that extracts tenant information from the JWT token
 * and sets it in the RequestContextHolder for the duration of the request
 */
@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static final String TENANT_HEADER = "X-Tenant-ID";
    private static final String USER_HEADER = "X-User-ID";
    private static final String CORRELATION_HEADER = "X-Correlation-ID";

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request,
                           @NonNull HttpServletResponse response,
                           @NonNull Object handler) {

        try {
            // Extract tenant ID from header (in production, this would come from JWT)
            String tenantId = request.getHeader(TENANT_HEADER);
            String userId = request.getHeader(USER_HEADER);
            String correlationId = request.getHeader(CORRELATION_HEADER);

            // If no correlation ID, generate one
            if (correlationId == null || correlationId.isEmpty()) {
                correlationId = java.util.UUID.randomUUID().toString();
            }

            // Set the context
            TenantContext context = new TenantContext(tenantId, userId, correlationId);
            RequestContextHolder.setContext(context);

            log.debug("Tenant context set - Tenant: {}, User: {}, Correlation: {}",
                    tenantId, userId, correlationId);

        } catch (Exception e) {
            log.error("Error setting tenant context", e);
            // Don't fail the request if tenant context setup fails
            // Security filter will handle authentication
        }

        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request,
                                @NonNull HttpServletResponse response,
                                @NonNull Object handler,
                                Exception ex) {
        // Clean up the thread-local context
        RequestContextHolder.clearContext();
        log.debug("Tenant context cleared");
    }
}
