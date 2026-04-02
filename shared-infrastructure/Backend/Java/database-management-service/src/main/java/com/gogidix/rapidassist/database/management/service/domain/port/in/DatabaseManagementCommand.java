package com.gogidix.rapidassist.database.management.service.domain.port.in;

import com.gogidix.rapidassist.database.management.service.domain.model.BackupInfo;
import com.gogidix.rapidassist.database.management.service.domain.model.DatabaseConnection;
import com.gogidix.rapidassist.database.management.service.domain.model.MigrationInfo;

import java.util.List;

/**
 * Input port for database management commands.
 * All methods are tenant-scoped to ensure proper multi-tenancy isolation.
 */
public interface DatabaseManagementCommand {

    /**
     * Register a new database connection for the specified tenant.
     */
    DatabaseConnection registerConnection(String tenantId, RegisterConnectionRequest request);

    /**
     * Update an existing connection for the specified tenant.
     */
    DatabaseConnection updateConnection(String tenantId, String id, UpdateConnectionRequest request);

    /**
     * Remove a connection for the specified tenant.
     */
    void removeConnection(String tenantId, String id);

    /**
     * Test a connection for the specified tenant.
     */
    TestConnectionResult testConnection(String tenantId, String id);

    /**
     * Run migrations for a connection for the specified tenant.
     */
    MigrationInfo runMigrations(String tenantId, String connectionId);

    /**
     * Create a backup for the specified tenant.
     */
    BackupInfo createBackup(String tenantId, CreateBackupRequest request);

    /**
     * Restore from a backup for the specified tenant.
     */
    RestoreResult restoreFromBackup(String tenantId, String backupId);

    /**
     * Cancel an ongoing backup for the specified tenant.
     */
    void cancelBackup(String tenantId, String backupId);

    /**
     * Execute a query for the specified tenant.
     */
    QueryResult executeQuery(String tenantId, ExecuteQueryRequest request);

    // Request DTOs
    record RegisterConnectionRequest(
        String name,
        DatabaseConnection.DatabaseType type,
        String host,
        int port,
        String database,
        String username,
        String password,
        int poolSize,
        java.util.Map<String, Object> properties
    ) {}

    record UpdateConnectionRequest(
        String name,
        String host,
        int port,
        int poolSize,
        java.util.Map<String, Object> properties
    ) {}

    record TestConnectionResult(
        boolean success,
        String message,
        long responseTime
    ) {}

    record CreateBackupRequest(
        String connectionId,
        String backupName,
        BackupInfo.BackupType type,
        String storageLocation
    ) {}

    record RestoreResult(
        boolean success,
        String message,
        long duration
    ) {}

    record ExecuteQueryRequest(
        String connectionId,
        String query,
        java.util.Map<String, Object> parameters
    ) {}

    record QueryResult(
        boolean success,
        List<java.util.Map<String, Object>> rows,
        int rowCount,
        long executionTime,
        String errorMessage
    ) {}
}
