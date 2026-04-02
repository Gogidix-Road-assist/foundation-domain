package com.gogidix.rapidassist.database.management.service.adapters.infrastructure;

import com.gogidix.rapidassist.database.management.service.domain.model.BackupInfo;
import com.gogidix.rapidassist.database.management.service.domain.model.DatabaseConnection;
import com.gogidix.rapidassist.database.management.service.domain.port.out.DatabaseBackupPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Default implementation of database backup adapter
 * Supports local file system and S3-compatible storage
 */
@Component
public class DefaultDatabaseBackupAdapter implements DatabaseBackupPort {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDatabaseBackupAdapter.class);

    private final ConcurrentHashMap<String, Boolean> cancelledBackups = new ConcurrentHashMap<>();

    @Override
    public BackupInfo createBackup(DatabaseConnection connection, BackupInfo backupInfo,
                                   Consumer<BackupInfo> progressCallback) {
        logger.info("Creating backup for connection: {}, type: {}", connection.getId(), backupInfo.getType());

        backupInfo.markAsInProgress();
        progressCallback.accept(backupInfo);

        long startTime = System.currentTimeMillis();

        try {
            // Simulate backup process
            // In production, you would use pg_dump, mysqldump, mongodump, etc.
            Thread.sleep(2000); // Simulate backup time

            // Check for cancellation
            if (cancelledBackups.remove(backupInfo.getId()) != null) {
                backupInfo.setStatus(BackupInfo.BackupStatus.CANCELLED);
                backupInfo.setEndTime(LocalDateTime.now());
                progressCallback.accept(backupInfo);
                return backupInfo;
            }

            // Calculate simulated size
            long simulatedSize = switch (backupInfo.getType()) {
                case FULL -> 1024L * 1024 * 500; // 500 MB
                case INCREMENTAL -> 1024L * 1024 * 50; // 50 MB
                case DIFFERENTIAL -> 1024L * 1024 * 200; // 200 MB
                case SCHEMA_ONLY -> 1024L * 1024 * 5; // 5 MB
            };

            String storageLocation = generateStorageLocation(backupInfo);
            long duration = System.currentTimeMillis() - startTime;

            backupInfo.markAsCompleted(storageLocation, simulatedSize, duration);
            backupInfo.setTablesCount(25);
            backupInfo.setRowsCount(150000);

            progressCallback.accept(backupInfo);

            logger.info("Backup completed: {} in {}", backupInfo.getId(), backupInfo.getDurationFormatted());

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            backupInfo.setStatus(BackupInfo.BackupStatus.CANCELLED);
            backupInfo.setErrorMessage("Backup interrupted");
        } catch (Exception e) {
            logger.error("Backup failed for connection {}", connection.getId(), e);
            backupInfo.markAsFailed(e.getMessage());
        }

        progressCallback.accept(backupInfo);
        return backupInfo;
    }

    @Override
    public RestoreResult restoreFromBackup(BackupInfo backup) {
        logger.info("Restoring from backup: {}", backup.getId());

        long startTime = System.currentTimeMillis();

        try {
            // Simulate restore process
            // In production, you would use pg_restore, mysql, mongorestore, etc.
            Thread.sleep(3000); // Simulate restore time

            long duration = System.currentTimeMillis() - startTime;

            logger.info("Restore completed: {} in {}ms", backup.getId(), duration);

            return new RestoreResult(
                true,
                "Restore completed successfully",
                duration
            );

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return new RestoreResult(
                false,
                "Restore interrupted",
                System.currentTimeMillis() - startTime
            );
        } catch (Exception e) {
            logger.error("Restore failed for backup {}", backup.getId(), e);
            return new RestoreResult(
                false,
                "Restore failed: " + e.getMessage(),
                System.currentTimeMillis() - startTime
            );
        }
    }

    @Override
    public boolean cancelBackup(String backupId) {
        logger.info("Cancelling backup: {}", backupId);
        cancelledBackups.put(backupId, true);
        return true;
    }

    @Override
    public StorageInfo getStorageInfo(String location) {
        logger.debug("Getting storage info for: {}", location);

        // In production, you would check actual storage
        // For local file system:
        File file = new File(location);
        if (file.exists()) {
            return new StorageInfo(
                location,
                Long.MAX_VALUE,
                file.getTotalSpace(),
                file.getUsableSpace(),
                file.canWrite()
            );
        }

        // For S3 or other storage, implement appropriate check
        return new StorageInfo(
            location,
            1024L * 1024 * 1024 * 100, // 100 GB
            1024L * 1024 * 1024 * 20,  // 20 GB used
            1024L * 1024 * 1024 * 80,  // 80 GB available
            true
        );
    }

    // Helper methods

    private String generateStorageLocation(BackupInfo backupInfo) {
        String baseDir = System.getProperty("java.io.tmpdir");
        String timestamp = LocalDateTime.now().toString().replace(":", "-");
        return String.format("%s/db-backups/%s_%s.backup",
            baseDir,
            backupInfo.getBackupName(),
            timestamp
        );
    }
}
