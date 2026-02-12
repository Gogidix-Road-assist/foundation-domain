package com.gogidix.rapidassist.ai.chatbot.domain.tenant;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Thread-local holder for current tenant context.
 * Used throughout the application for multi-tenancy support.
 */
@Slf4j
public class TenantContext {

    private static final ThreadLocal<String> CURRENT_TENANT = new ThreadLocal<>();
    private static final ThreadLocal<String> CORRELATION_ID = new ThreadLocal<>();

    /**
     * Set the current tenant ID for this thread
     */
    public static void setTenantId(String tenantId) {
        log.info("Setting tenant context: {}", tenantId);
        CURRENT_TENANT.set(tenantId);
    }

    /**
     * Get the current tenant ID for this thread
     */
    public static String getTenantId() {
        String tenantId = CURRENT_TENANT.get();
        if (tenantId == null) {
            throw new IllegalStateException("Tenant context not set. Please set tenant ID before proceeding.");
        }
        return tenantId;
    }

    /**
     * Get the current tenant ID as Optional
     */
    public static Optional<String> getTenantIdOptional() {
        return Optional.ofNullable(CURRENT_TENANT.get());
    }

    /**
     * Clear the current tenant context
     */
    public static void clear() {
        log.info("Clearing tenant context");
        CURRENT_TENANT.remove();
        CORRELATION_ID.remove();
    }

    /**
     * Set correlation ID for tracing
     */
    public static void setCorrelationId(String correlationId) {
        CORRELATION_ID.set(correlationId);
    }

    /**
     * Get correlation ID
     */
    public static String getCorrelationId() {
        return CORRELATION_ID.get();
    }

    /**
     * Check if tenant context is set
     */
    public static boolean isTenantContextSet() {
        return CURRENT_TENANT.get() != null;
    }
}
