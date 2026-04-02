package com.gogidix.rapidassist.idempotency.service.application.service;

import com.gogidix.rapidassist.idempotency.service.domain.model.EnhancedIdempotencyRecord;
import com.gogidix.rapidassist.idempotency.service.infrastructure.persistence.redis.RedisIdempotencyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class ComprehensiveIdempotencyService {

    private static final Logger logger = LoggerFactory.getLogger(ComprehensiveIdempotencyService.class);
    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final Duration DEFAULT_RESERVE_TTL = Duration.ofHours(1);

    private final RedisIdempotencyStore idempotencyStore;

    public ComprehensiveIdempotencyService(RedisIdempotencyStore idempotencyStore) {
        this.idempotencyStore = idempotencyStore;
    }

    /**
     * Reserve an idempotency key for an operation
     */
    public IdempotencyResult reserve(String tenantId, String key, String operation) {
        return reserve(tenantId, key, operation, DEFAULT_RESERVE_TTL);
    }

    public IdempotencyResult reserve(String tenantId, String key, String operation, Duration ttl) {
        String requestId = UUID.randomUUID().toString();

        // Check if record already exists
        Optional<EnhancedIdempotencyRecord> existing = idempotencyStore.getEnhancedRecord(tenantId, key);
        if (existing.isPresent()) {
            EnhancedIdempotencyRecord record = existing.get();

            if (record.status() == EnhancedIdempotencyRecord.IdempotencyStatus.COMPLETED) {
                logger.info("Operation already completed: tenant={}, key={}, requestId={}",
                    tenantId, key, record.requestId());
                return IdempotencyResult.completed(record);
            } else if (record.status() == EnhancedIdempotencyRecord.IdempotencyStatus.IN_PROGRESS) {
                logger.warn("Operation already in progress: tenant={}, key={}", tenantId, key);
                return IdempotencyResult.inProgress(record);
            } else if (record.canRetry(DEFAULT_MAX_RETRIES)) {
                logger.info("Retrying failed operation: tenant={}, key={}, attempt={}",
                    tenantId, key, record.retryCount() + 1);
                EnhancedIdempotencyRecord retryRecord = record.withRetry();
                idempotencyStore.saveEnhancedRecord(retryRecord);
                return IdempotencyResult.reserved(retryRecord);
            } else {
                logger.warn("Max retries exceeded: tenant={}, key={}", tenantId, key);
                return IdempotencyResult.failed("Maximum retry attempts exceeded", record);
            }
        }

        // Reserve new key
        boolean reserved = idempotencyStore.reserveIdempotencyKey(tenantId, key, requestId, ttl);
        if (reserved) {
            EnhancedIdempotencyRecord record = EnhancedIdempotencyRecord.createReserved(
                tenantId, key, operation, requestId);
            idempotencyStore.saveEnhancedRecord(record);
            logger.info("Reserved idempotency key: tenant={}, key={}, requestId={}",
                tenantId, key, requestId);
            return IdempotencyResult.reserved(record);
        }

        logger.warn("Failed to reserve idempotency key: tenant={}, key={}", tenantId, key);
        return IdempotencyResult.failed("Failed to reserve key", null);
    }

    /**
     * Mark an operation as started
     */
    public boolean markInProgress(String tenantId, String key, String requestId,
                                 Object requestPayload) {
        Optional<EnhancedIdempotencyRecord> optRecord = idempotencyStore.getEnhancedRecord(tenantId, key);
        if (optRecord.isEmpty() || !optRecord.get().requestId().equals(requestId)) {
            logger.warn("Invalid attempt to mark in progress: tenant={}, key={}", tenantId, key);
            return false;
        }

        EnhancedIdempotencyRecord record = optRecord.get();
        EnhancedIdempotencyRecord inProgress = new EnhancedIdempotencyRecord(
            record.tenantId(),
            record.key(),
            EnhancedIdempotencyRecord.IdempotencyStatus.IN_PROGRESS,
            record.createdAt(),
            Instant.now(),
            record.expiresAt(),
            record.requestId(),
            record.operation(),
            Map.of("payload", requestPayload),
            null,
            null,
            null,
            record.retryCount(),
            record.executionTimeMs(),
            record.metadata()
        );

        return idempotencyStore.saveEnhancedRecord(inProgress);
    }

    /**
     * Mark an operation as completed
     */
    public boolean markCompleted(String tenantId, String key, String requestId,
                               Object requestPayload, Object responsePayload,
                               String responseStatus, long executionTimeMs) {
        Optional<EnhancedIdempotencyRecord> optRecord = idempotencyStore.getEnhancedRecord(tenantId, key);
        if (optRecord.isEmpty() || !optRecord.get().requestId().equals(requestId)) {
            logger.warn("Invalid attempt to mark completed: tenant={}, key={}", tenantId, key);
            return false;
        }

        EnhancedIdempotencyRecord completed = EnhancedIdempotencyRecord.createCompleted(
            tenantId,
            key,
            requestId,
            optRecord.get().operation(),
            Map.of("payload", requestPayload),
            Map.of("payload", responsePayload),
            responseStatus,
            executionTimeMs
        );

        boolean saved = idempotencyStore.saveEnhancedRecord(completed);
        if (saved) {
            logger.info("Operation completed: tenant={}, key={}, requestId={}, duration={}ms",
                tenantId, key, requestId, executionTimeMs);
        }
        return saved;
    }

    /**
     * Mark an operation as failed
     */
    public boolean markFailed(String tenantId, String key, String requestId,
                             Object requestPayload, String errorMessage,
                             long executionTimeMs) {
        Optional<EnhancedIdempotencyRecord> optRecord = idempotencyStore.getEnhancedRecord(tenantId, key);
        if (optRecord.isEmpty() || !optRecord.get().requestId().equals(requestId)) {
            logger.warn("Invalid attempt to mark failed: tenant={}, key={}", tenantId, key);
            return false;
        }

        EnhancedIdempotencyRecord failed = EnhancedIdempotencyRecord.createFailed(
            tenantId,
            key,
            requestId,
            optRecord.get().operation(),
            Map.of("payload", requestPayload),
            errorMessage,
            executionTimeMs
        );

        boolean saved = idempotencyStore.saveEnhancedRecord(failed);
        if (saved) {
            logger.error("Operation failed: tenant={}, key={}, requestId={}, error={}",
                tenantId, key, requestId, errorMessage);
        }
        return saved;
    }

    /**
     * Get the cached response for a completed operation
     */
    public Optional<CachedResponse> getCachedResponse(String tenantId, String key) {
        Optional<EnhancedIdempotencyRecord> optRecord = idempotencyStore.getEnhancedRecord(tenantId, key);
        if (optRecord.isEmpty() || optRecord.get().status() != EnhancedIdempotencyRecord.IdempotencyStatus.COMPLETED) {
            return Optional.empty();
        }

        EnhancedIdempotencyRecord record = optRecord.get();
        return Optional.of(new CachedResponse(
            record.responsePayload(),
            record.responseStatus(),
            record.executionTimeMs(),
            record.createdAt()
        ));
    }

    /**
     * Cleanup expired records (scheduled task)
     */
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void cleanupExpiredRecords() {
        logger.debug("Cleaning up expired idempotency records");
        // Redis automatically handles expiration with TTL
        // This could be enhanced to scan for records that need manual cleanup
    }

    /**
     * Cancel an operation
     */
    public boolean cancelOperation(String tenantId, String key, String requestId, String reason) {
        Optional<EnhancedIdempotencyRecord> optRecord = idempotencyStore.getEnhancedRecord(tenantId, key);
        if (optRecord.isEmpty() || !optRecord.get().requestId().equals(requestId)) {
            logger.warn("Invalid attempt to cancel operation: tenant={}, key={}", tenantId, key);
            return false;
        }

        EnhancedIdempotencyRecord record = optRecord.get();
        EnhancedIdempotencyRecord cancelled = new EnhancedIdempotencyRecord(
            record.tenantId(),
            record.key(),
            EnhancedIdempotencyRecord.IdempotencyStatus.CANCELLED,
            record.createdAt(),
            Instant.now(),
            record.expiresAt(),
            record.requestId(),
            record.operation(),
            record.requestPayload(),
            null,
            "CANCELLED",
            reason,
            record.retryCount(),
            record.executionTimeMs(),
            record.metadata()
        );

        return idempotencyStore.saveEnhancedRecord(cancelled);
    }

    public record IdempotencyResult(
        ResultType type,
        EnhancedIdempotencyRecord record,
        String message
    ) {
        public static IdempotencyResult reserved(EnhancedIdempotencyRecord record) {
            return new IdempotencyResult(ResultType.RESERVED, record, null);
        }

        public static IdempotencyResult completed(EnhancedIdempotencyRecord record) {
            return new IdempotencyResult(ResultType.COMPLETED, record, null);
        }

        public static IdempotencyResult inProgress(EnhancedIdempotencyRecord record) {
            return new IdempotencyResult(ResultType.IN_PROGRESS, record, "Operation already in progress");
        }

        public static IdempotencyResult failed(String message, EnhancedIdempotencyRecord record) {
            return new IdempotencyResult(ResultType.FAILED, record, message);
        }

        public enum ResultType {
            RESERVED,
            COMPLETED,
            IN_PROGRESS,
            FAILED
        }
    }

    public record CachedResponse(
        Object response,
        String status,
        long executionTimeMs,
        Instant completedAt
    ) {}
}