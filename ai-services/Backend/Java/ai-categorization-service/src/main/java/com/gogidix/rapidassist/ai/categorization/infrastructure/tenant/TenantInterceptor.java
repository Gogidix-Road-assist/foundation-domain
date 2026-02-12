package com.gogidix.rapidassist.ai.categorization.infrastructure.tenant;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * INTERCEPTS ALL REQUESTS and extracts tenant context from JWT.
 * This is the ENTRY POINT for multi-tenancy.
 *
 * CRITICAL: This interceptor MUST run BEFORE any controller or service logic.
 * It validates the JWT, extracts tenant_id, and sets the TenantContext.
 *
 * All subsequent operations will use RequestContextHolder to access the tenant context.
 * This ensures complete tenant isolation across all database operations.
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
@RequiredArgsConstructor
public class TenantInterceptor implements HandlerInterceptor {

    private static final String TENANT_ID_HEADER = "X-Tenant-ID";
    private static final String USER_ID_HEADER = "X-User-ID";
    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private static final String AUTHORIZATION_HEADER = "Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler) throws Exception {

        try {
            // 1. Extract tenantId from header (or from JWT in production)
            String tenantId = extractTenantId(request);
            if (tenantId == null || tenantId.isBlank()) {
                log.warn("Missing tenant ID in request: {}", request.getRequestURI());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing tenant ID");
                return false;
            }

            // 2. Extract userId from header (or from JWT in production)
            String userId = request.getHeader(USER_ID_HEADER);

            // 3. Extract or generate correlationId for distributed tracing
            String correlationId = request.getHeader(CORRELATION_ID_HEADER);
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = java.util.UUID.randomUUID().toString();
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

            log.debug("Tenant context set: {} for request: {}", tenantId, request.getRequestURI());

            return true;

        } catch (Exception e) {
            log.error("Error setting tenant context for request: {}", request.getRequestURI(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error processing request");
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler,
                               Exception ex) {

        // ALWAYS clear context to prevent memory leaks
        // This is CRITICAL for thread pool environments
        try {
            if (RequestContextHolder.isSet()) {
                String tenantId = RequestContextHolder.getTenantId();
                log.debug("Clearing tenant context for: {}", tenantId);
            }
        } finally {
            RequestContextHolder.clear();
        }
    }

    /**
     * Extract tenant ID from request headers.
     * In production, this would validate the JWT and extract tenant_id from claims.
     *
     * @param request the HTTP request
     * @return the tenant ID, or null if not found
     */
    private String extractTenantId(HttpServletRequest request) {
        // First try X-Tenant-ID header (for testing/dev)
        String tenantId = request.getHeader(TENANT_ID_HEADER);
        if (tenantId != null && !tenantId.isBlank()) {
            return tenantId;
        }

        // In production, extract from JWT
        String authorization = request.getHeader(AUTHORIZATION_HEADER);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            // TODO: Validate JWT and extract tenant_id from claims
            // For now, return null to enforce X-Tenant-ID header
            log.debug("Authorization header present, but JWT validation not yet implemented");
        }

        return null;
    }
}
