package com.gogidix.rapidassist.shared.audit.library.scheduled;

import com.gogidix.rapidassist.shared.audit.library.autoconfigure.AuditProperties;
import com.gogidix.rapidassist.shared.audit.library.infrastructure.database.AuditLogRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Scheduled job for cleaning up old audit logs based on retention policy.
 *
 * <p>This job runs daily at midnight (by default) to delete audit logs older
 * than the configured retention period.
 *
 * <p>To enable this job, set:
 * <pre>
 * audit:
 *   retention:
 *     enabled: true
 *     period: 90d  # retention period
 * </pre>
 */
@Component
@ConditionalOnProperty(
    prefix = "audit.retention",
    name = "enabled",
    havingValue = "true"
)
public class AuditRetentionJob {

    private static final Logger log = LoggerFactory.getLogger(AuditRetentionJob.class);

    private final AuditLogRepository repository;
    private final AuditProperties properties;

    public AuditRetentionJob(AuditLogRepository repository, AuditProperties properties) {
        this.repository = repository;
        this.properties = properties;
    }

    /**
     * Scheduled method to delete old audit logs.
     * Runs daily at midnight (00:00) by default.
     */
    @Scheduled(cron = "${audit.retention.cron:0 0 0 * * ?}")
    public void cleanupOldAuditLogs() {
        try {
            Instant cutoffDate = Instant.now().minus(properties.getRetention().getPeriod());

            log.info("Starting audit log cleanup. Deleting logs created before: {}", cutoffDate);

            long deletedCount = repository.deleteByCreatedAtBefore(cutoffDate);

            log.info("Audit log cleanup completed. Deleted {} log(s)", deletedCount);

        } catch (Exception e) {
            log.error("Error during audit log cleanup", e);
        }
    }

    /**
     * Manual trigger for cleanup (useful for testing or ad-hoc cleanup).
     */
    public void cleanupNow() {
        cleanupOldAuditLogs();
    }
}
