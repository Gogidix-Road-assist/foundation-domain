package com.gogidix.rapidassist.database.management.service.domain.port.in;

import com.gogidix.rapidassist.database.management.service.domain.model.BackupInfo;
import com.gogidix.rapidassist.database.management.service.domain.model.DatabaseConnection;
import com.gogidix.rapidassist.database.management.service.domain.model.HealthCheckResult;
import com.gogidix.rapidassist.database.management.service.domain.model.MigrationInfo;

import java.util.List;
import java.util.Optional;

/**
 * Input port for database management queries.
 * All methods are tenant-scoped to ensure proper multi-tenancy isolation.
 */
public interface DatabaseManagementQuery {

    /**
     * Get all database connections for the specified tenant.
     */
    List<DatabaseConnection> getAllConnections(String tenantId);

    /**
     * Get connection by ID for the specified tenant.
     */
    Optional<DatabaseConnection> getConnectionById(String tenantId, String id);

    /**
     * Get connection by name for the specified tenant.
     */
    Optional<DatabaseConnection> getConnectionByName(String tenantId, String name);

    /**
     * Perform health check on a connection for the specified tenant.
     */
    HealthCheckResult performHealthCheck(String tenantId, String connectionId);

    /**
     * Perform health check on all connections for the specified tenant.
     */
    List<HealthCheckResult> performHealthCheckOnAll(String tenantId);

    /**
     * Get migration info for a connection for the specified tenant.
     */
    MigrationInfo getMigrationInfo(String tenantId, String connectionId);

    /**
     * Get all backups for a connection for the specified tenant.
     */
    List<BackupInfo> getBackups(String tenantId, String connectionId);

    /**
     * Get backup by ID for the specified tenant.
     */
    Optional<BackupInfo> getBackupById(String tenantId, String backupId);

    /**
     * Get connection statistics for the specified tenant.
     */
    DatabaseStatistics getStatistics(String tenantId, String connectionId);

    /**
     * Get overall statistics for all connections for the specified tenant.
     */
    OverallStatistics getOverallStatistics(String tenantId);

    /**
     * DTO for database statistics
     */
    record DatabaseStatistics(
        String connectionId,
        int totalConnections,
        int activeConnections,
        int idleConnections,
        long totalQueries,
        long failedQueries,
        double averageQueryTime,
        double successRate,
        long totalRows,
        int totalTables
    ) {}

    /**
     * DTO for overall statistics
     */
    record OverallStatistics(
        int totalDatabases,
        int healthyDatabases,
        int degradedDatabases,
        int downDatabases,
        long totalConnections,
        long totalQueries,
        double averageResponseTime,
        List<ConnectionSummary> connectionSummaries
    ) {}

    record ConnectionSummary(
        String connectionId,
        String name,
        DatabaseConnection.ConnectionStatus status,
        double utilization,
        long responseTime
    ) {}
}
