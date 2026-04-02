package com.gogidix.rapidassist.ai.contentanalysis.infrastructure.config;

import com.gogidix.rapidassist.ai.contentanalysis.domain.tenant.TenantContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Tenant Interceptor
 * Extracts and validates tenant ID from request headers
 */
@Slf4j
@Component
public class TenantInterceptor implements HandlerInterceptor {

    private static final String TENANT_ID_HEADER = "X-Tenant-ID";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tenantId = request.getHeader(TENANT_ID_HEADER);

        if (tenantId != null && !tenantId.isEmpty()) {
            log.debug("Setting tenant context: {}", tenantId);
            TenantContext.setTenantId(tenantId);
        } else {
            log.warn("Missing tenant ID header: {}", TENANT_ID_HEADER);
        }

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // Clear tenant context after request completes
        TenantContext.clear();
    }
}
