package com.gogidix.rapidassist.database.management.service.domain.port.out;

import com.gogidix.rapidassist.database.management.service.domain.model.BackupInfo;

import java.util.List;
import java.util.Optional;

/**
 * Output port for backup persistence.
 * All methods are tenant-scoped to ensure proper multi-tenancy isolation.
 */
public interface BackupRepository {

    /**
     * Save a backup.
     * The tenantId must be set on the entity before saving.
     */
    BackupInfo save(BackupInfo backup);

    /**
     * Find backup by ID within the specified tenant.
     * This ensures tenant isolation by requiring tenantId parameter.
     */
    Optional<BackupInfo> findByIdAndTenantId(String id, String tenantId);

    /**
     * Find backups by tenant and connection ID.
     */
    List<BackupInfo> findByTenantIdAndConnectionId(String tenantId, String connectionId);

    /**
     * Find recent backups for a tenant and connection.
     */
    List<BackupInfo> findRecentBackupsByTenantIdAndConnectionId(String tenantId, String connectionId, int limit);

    /**
     * Find all backups for a specific tenant.
     */
    List<BackupInfo> findByTenantId(String tenantId);

    /**
     * Find backups by tenant and status.
     */
    List<BackupInfo> findByTenantIdAndStatus(String tenantId, BackupInfo.BackupStatus status);

    /**
     * Find backups by tenant and type.
     */
    List<BackupInfo> findByTenantIdAndType(String tenantId, BackupInfo.BackupType type);

    /**
     * Delete backup by ID within the specified tenant.
     */
    void deleteByIdAndTenantId(String id, String tenantId);

    /**
     * Count backups by tenant and connection ID.
     */
    long countByTenantIdAndConnectionId(String tenantId, String connectionId);

    /**
     * Count all backups for a tenant.
     */
    long countByTenantId(String tenantId);

    /**
     * Count backups by tenant and status.
     */
    long countByTenantIdAndStatus(String tenantId, BackupInfo.BackupStatus status);
}
