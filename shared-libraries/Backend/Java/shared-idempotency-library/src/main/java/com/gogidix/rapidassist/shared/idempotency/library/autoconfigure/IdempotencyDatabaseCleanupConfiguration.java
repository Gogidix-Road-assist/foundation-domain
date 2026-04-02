package com.gogidix.rapidassist.shared.idempotency.library.autoconfigure;

import com.gogidix.rapidassist.shared.idempotency.library.infrastructure.database.DatabaseIdempotencyStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduled cleanup configuration for database idempotency store.
 * Automatically removes expired idempotency records.
 */
@Component
@EnableScheduling
@ConditionalOnBean(DatabaseIdempotencyStore.class)
@ConditionalOnProperty(prefix = "gogidix.idempotency.database", name = "auto-cleanup", havingValue = "true", matchIfMissing = true)
public class IdempotencyDatabaseCleanupConfiguration {

    private static final Logger log = LoggerFactory.getLogger(IdempotencyDatabaseCleanupConfiguration.class);

    private final DatabaseIdempotencyStore databaseIdempotencyStore;

    public IdempotencyDatabaseCleanupConfiguration(DatabaseIdempotencyStore databaseIdempotencyStore) {
        this.databaseIdempotencyStore = databaseIdempotencyStore;
        log.info("Idempotency database cleanup scheduled task enabled");
    }

    /**
     * Scheduled cleanup task that runs every hour.
     * Removes expired idempotency keys from the database.
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void cleanupExpiredKeys() {
        try {
            long deleted = databaseIdempotencyStore.deleteExpired();
            if (deleted > 0) {
                log.info("Cleaned up {} expired idempotency keys", deleted);
            }
        } catch (Exception e) {
            log.error("Failed to cleanup expired idempotency keys", e);
        }
    }
}
