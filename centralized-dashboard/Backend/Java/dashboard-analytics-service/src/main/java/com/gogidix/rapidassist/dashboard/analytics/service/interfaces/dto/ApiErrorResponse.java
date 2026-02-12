package com.gogidix.rapidassist.dashboard.analytics.service.interfaces.dto;
import java.time.Instant;
public record ApiErrorResponse(
        int status, String error, String message, String path,
        Instant timestamp, String tenantId, String correlationId
) {
    public static ApiErrorResponse of(int status, String error, String message, String path) {
        return new ApiErrorResponse(status, error, message, path, Instant.now(), null, null);
    }
    public ApiErrorResponse withTenantId(String tenantId) {
        return new ApiErrorResponse(status, error, message, path, timestamp, tenantId, correlationId);
    }
    public ApiErrorResponse withCorrelationId(String correlationId) {
        return new ApiErrorResponse(status, error, message, path, timestamp, tenantId, correlationId);
    }
}
