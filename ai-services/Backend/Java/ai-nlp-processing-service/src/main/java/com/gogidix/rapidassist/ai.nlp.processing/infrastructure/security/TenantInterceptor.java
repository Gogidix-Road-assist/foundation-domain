package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.security;

import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContext;
import com.gogidix.rapidassist.shared.request.context.library.domain.RequestContextHolder;
import com.gogidix.rapidassist.shared.security.library.exception.InvalidTokenException;
import com.gogidix.rapidassist.shared.security.library.jwt.JwtTokenUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

/**
 * INTERCEPTS ALL REQUESTS and extracts tenant context from JWT
 * This is the ENTRY POINT for multi-tenancy
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class TenantInterceptor implements HandlerInterceptor, Ordered {

    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                            HttpServletResponse response,
                            Object handler) {

        try {
            // 1. Extract JWT from Authorization header or X-Tenant-ID header
            String token = extractJwt(request);
            String tenantId = request.getHeader("X-Tenant-ID");
            String userId = null;
            String correlationId = request.getHeader("X-Correlation-ID");

            // Generate correlationId if not present
            if (correlationId == null || correlationId.isBlank()) {
                correlationId = UUID.randomUUID().toString();
            }

            // 2. If token is present, validate and extract claims
            if (token != null && !token.isBlank()) {
                try {
                    if (jwtTokenUtil.validateToken(token)) {
                        tenantId = jwtTokenUtil.extractTenantId(token);
                        userId = jwtTokenUtil.extractUserId(token);
                    }
                } catch (InvalidTokenException e) {
                    log.warn("Invalid JWT token: {}", e.getMessage());
                    // Continue with X-Tenant-ID if available
                }
            }

            // 3. Validate tenantId exists (from JWT or header)
            if (tenantId == null || tenantId.isBlank()) {
                log.warn("Missing tenant ID in request to: {}", request.getRequestURI());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\":\"Missing tenant ID\",\"message\":\"X-Tenant-ID header or valid JWT token required\"}");
                return false;
            }

            // 4. Extract userId from JWT or use system default
            if (userId == null || userId.isBlank()) {
                userId = "system";
            }

            // 5. Build and set RequestContext
            RequestContext context = RequestContext.builder()
                .tenantId(tenantId)
                .userId(userId)
                .correlationId(correlationId)
                .requestId(UUID.randomUUID().toString())
                .build();

            RequestContextHolder.set(context);

            // 6. Add correlationId to response for tracing
            response.setHeader("X-Correlation-ID", correlationId);
            response.setHeader("X-Tenant-ID", tenantId);

            log.debug("Tenant context set: tenantId={}, userId={}, correlationId={}",
                tenantId, userId, correlationId);

            return true;
        } catch (Exception e) {
            log.error("Error setting tenant context", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                               HttpServletResponse response,
                               Object handler,
                               Exception ex) {
        // ALWAYS clear context to prevent memory leaks
        RequestContextHolder.clear();
        log.debug("Tenant context cleared for request: {}", request.getRequestURI());
    }

    /**
     * Extract JWT from Authorization header
     */
    private String extractJwt(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
