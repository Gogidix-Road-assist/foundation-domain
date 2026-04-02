package com.gogidix.rapidassist.database.management.service.domain.port.out;

import com.gogidix.rapidassist.database.management.service.domain.model.BackupInfo;
import com.gogidix.rapidassist.database.management.service.domain.model.DatabaseConnection;

import java.util.function.Consumer;

/**
 * Output port for database backup operations
 */
public interface DatabaseBackupPort {

    /**
     * Create a backup
     */
    BackupInfo createBackup(DatabaseConnection connection, BackupInfo backupInfo, Consumer<BackupInfo> progressCallback);

    /**
     * Restore from a backup
     */
    RestoreResult restoreFromBackup(BackupInfo backup);

    /**
     * Cancel an ongoing backup
     */
    boolean cancelBackup(String backupId);

    /**
     * Get backup storage location info
     */
    StorageInfo getStorageInfo(String location);

    record RestoreResult(
        boolean success,
        String message,
        long duration
    ) {}

    record StorageInfo(
        String location,
        long totalSpace,
        long usedSpace,
        long availableSpace,
        boolean accessible
    ) {}
}
