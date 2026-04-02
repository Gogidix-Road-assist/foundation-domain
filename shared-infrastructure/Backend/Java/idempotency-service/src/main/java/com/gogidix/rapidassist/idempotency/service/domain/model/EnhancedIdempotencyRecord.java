package com.gogidix.rapidassist.idempotency.service.domain.model;

import java.time.Instant;
import java.util.Map;

public record EnhancedIdempotencyRecord(
    String tenantId,
    String key,
    IdempotencyStatus status,
    Instant createdAt,
    Instant updatedAt,
    Instant expiresAt,
    String requestId,
    String operation,
    Map<String, Object> requestPayload,
    Map<String, Object> responsePayload,
    String responseStatus,
    String errorMessage,
    int retryCount,
    long executionTimeMs,
    Map<String, Object> metadata
) {

    public static EnhancedIdempotencyRecord createReserved(String tenantId, String key, String operation, String requestId) {
        return new EnhancedIdempotencyRecord(
            tenantId,
            key,
            IdempotencyStatus.RESERVED,
            Instant.now(),
            Instant.now(),
            Instant.now().plusSeconds(3600), // Default 1 hour expiry
            requestId,
            operation,
            Map.of(),
            null,
            null,
            null,
            0,
            0,
            Map.of()
        );
    }

    public static EnhancedIdempotencyRecord createCompleted(
            String tenantId,
            String key,
            String requestId,
            String operation,
            Map<String, Object> requestPayload,
            Map<String, Object> responsePayload,
            String responseStatus,
            long executionTimeMs) {
        return new EnhancedIdempotencyRecord(
            tenantId,
            key,
            IdempotencyStatus.COMPLETED,
            Instant.now(),
            Instant.now(),
            Instant.now().plusSeconds(86400), // Keep completed records for 24 hours
            requestId,
            operation,
            requestPayload,
            responsePayload,
            responseStatus,
            null,
            0,
            executionTimeMs,
            Map.of()
        );
    }

    public static EnhancedIdempotencyRecord createFailed(
            String tenantId,
            String key,
            String requestId,
            String operation,
            Map<String, Object> requestPayload,
            String errorMessage,
            long executionTimeMs) {
        return new EnhancedIdempotencyRecord(
            tenantId,
            key,
            IdempotencyStatus.FAILED,
            Instant.now(),
            Instant.now(),
            Instant.now().plusSeconds(3600), // Keep failed records for 1 hour
            requestId,
            operation,
            requestPayload,
            null,
            "ERROR",
            errorMessage,
            0,
            executionTimeMs,
            Map.of()
        );
    }

    public EnhancedIdempotencyRecord withRetry() {
        return new EnhancedIdempotencyRecord(
            tenantId,
            key,
            status,
            createdAt,
            Instant.now(),
            expiresAt,
            requestId,
            operation,
            requestPayload,
            responsePayload,
            responseStatus,
            errorMessage,
            retryCount + 1,
            executionTimeMs,
            metadata
        );
    }

    public boolean isExpired() {
        return Instant.now().isAfter(expiresAt);
    }

    public boolean canRetry(int maxRetries) {
        return retryCount < maxRetries && (status == IdempotencyStatus.FAILED || status == IdempotencyStatus.TIMEOUT);
    }

    public enum IdempotencyStatus {
        RESERVED,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        TIMEOUT,
        CANCELLED
    }
}