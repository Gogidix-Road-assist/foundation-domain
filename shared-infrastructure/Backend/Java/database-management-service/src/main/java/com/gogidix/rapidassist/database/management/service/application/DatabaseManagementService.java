package com.gogidix.rapidassist.database.management.service.application;

import com.gogidix.rapidassist.database.management.service.domain.model.BackupInfo;
import com.gogidix.rapidassist.database.management.service.domain.model.DatabaseConnection;
import com.gogidix.rapidassist.database.management.service.domain.model.HealthCheckResult;
import com.gogidix.rapidassist.database.management.service.domain.model.MigrationInfo;
import com.gogidix.rapidassist.database.management.service.domain.port.in.DatabaseManagementCommand;
import com.gogidix.rapidassist.database.management.service.domain.port.in.DatabaseManagementQuery;
import com.gogidix.rapidassist.database.management.service.domain.port.out.BackupRepository;
import com.gogidix.rapidassist.database.management.service.domain.port.out.DatabaseBackupPort;
import com.gogidix.rapidassist.database.management.service.domain.port.out.DatabaseConnectionRepository;
import com.gogidix.rapidassist.database.management.service.domain.port.out.DatabaseHealthCheckerPort;
import com.gogidix.rapidassist.database.management.service.domain.port.out.DatabaseMigrationPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Application service for database management
 * Implements both query and command ports
 */
@Service
@Transactional
public class DatabaseManagementService implements DatabaseManagementQuery, DatabaseManagementCommand {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseManagementService.class);

    private final DatabaseConnectionRepository connectionRepository;
    private final BackupRepository backupRepository;
    private final DatabaseHealthCheckerPort healthChecker;
    private final DatabaseMigrationPort migrationPort;
    private final DatabaseBackupPort backupPort;

    public DatabaseManagementService(
            DatabaseConnectionRepository connectionRepository,
            BackupRepository backupRepository,
            DatabaseHealthCheckerPort healthChecker,
            DatabaseMigrationPort migrationPort,
            DatabaseBackupPort backupPort) {
        this.connectionRepository = connectionRepository;
        this.backupRepository = backupRepository;
        this.healthChecker = healthChecker;
        this.migrationPort = migrationPort;
        this.backupPort = backupPort;
    }

    // Query implementation

    @Override
    public List<DatabaseConnection> getAllConnections(String tenantId) {
        return connectionRepository.findByTenantId(tenantId);
    }

    @Override
    public Optional<DatabaseConnection> getConnectionById(String tenantId, String id) {
        return connectionRepository.findByIdAndTenantId(id, tenantId);
    }

    @Override
    public Optional<DatabaseConnection> getConnectionByName(String tenantId, String name) {
        return connectionRepository.findByTenantIdAndName(tenantId, name);
    }

    @Override
    public HealthCheckResult performHealthCheck(String tenantId, String connectionId) {
        Optional<DatabaseConnection> connectionOpt = connectionRepository.findByIdAndTenantId(connectionId, tenantId);
        if (connectionOpt.isEmpty()) {
            return new HealthCheckResult(connectionId, false, 0, "Connection not found for tenant: " + tenantId);
        }

        DatabaseConnection connection = connectionOpt.get();
        HealthCheckResult result = healthChecker.checkHealth(connection);

        // Update connection status based on health check
        if (result.isHealthy()) {
            connection.markAsHealthy();
        } else if (result.isDegraded()) {
            connection.markAsDegraded();
        } else {
            connection.markAsDown();
        }
        connectionRepository.save(connection);

        return result;
    }

    @Override
    public List<HealthCheckResult> performHealthCheckOnAll(String tenantId) {
        List<DatabaseConnection> connections = connectionRepository.findByTenantId(tenantId);
        List<HealthCheckResult> results = new ArrayList<>();

        for (DatabaseConnection connection : connections) {
            results.add(performHealthCheck(tenantId, connection.getId()));
        }

        return results;
    }

    @Override
    public MigrationInfo getMigrationInfo(String tenantId, String connectionId) {
        Optional<DatabaseConnection> connectionOpt = connectionRepository.findByIdAndTenantId(connectionId, tenantId);
        if (connectionOpt.isEmpty()) {
            throw new IllegalArgumentException("Connection not found for tenant '" + tenantId + "' and id: " + connectionId);
        }

        DatabaseConnection connection = connectionOpt.get();
        MigrationInfo info = migrationPort.getMigrationInfo(connection);
        info.setConnectionId(connectionId);
        return info;
    }

    @Override
    public List<BackupInfo> getBackups(String tenantId, String connectionId) {
        return backupRepository.findByTenantIdAndConnectionId(tenantId, connectionId);
    }

    @Override
    public Optional<BackupInfo> getBackupById(String tenantId, String backupId) {
        return backupRepository.findByIdAndTenantId(backupId, tenantId);
    }

    @Override
    public DatabaseStatistics getStatistics(String tenantId, String connectionId) {
        Optional<DatabaseConnection> connectionOpt = connectionRepository.findByIdAndTenantId(connectionId, tenantId);
        if (connectionOpt.isEmpty()) {
            throw new IllegalArgumentException("Connection not found for tenant '" + tenantId + "' and id: " + connectionId);
        }

        DatabaseConnection connection = connectionOpt.get();
        DatabaseHealthCheckerPort.PoolStatistics poolStats = healthChecker.getPoolStatistics(connectionId);

        return new DatabaseStatistics(
            connectionId,
            poolStats.totalConnections(),
            poolStats.activeConnections(),
            poolStats.idleConnections(),
            0, // totalQueries - would be tracked separately
            0, // failedQueries
            poolStats.averageWaitTime(),
            calculateSuccessRate(poolStats),
            0, // totalRows
            0  // totalTables
        );
    }

    @Override
    public OverallStatistics getOverallStatistics(String tenantId) {
        List<DatabaseConnection> connections = connectionRepository.findByTenantId(tenantId);
        List<ConnectionSummary> summaries = new ArrayList<>();

        int healthy = 0, degraded = 0, down = 0;
        long totalConnections = 0;
        double totalResponseTime = 0;

        for (DatabaseConnection conn : connections) {
            DatabaseConnection.ConnectionStatus status = conn.getStatus();
            switch (status) {
                case HEALTHY -> healthy++;
                case DEGRADED -> degraded++;
                case DOWN -> down++;
            }

            totalConnections += conn.getActiveConnections();
            HealthCheckResult health = healthChecker.checkHealth(conn);
            totalResponseTime += health.getResponseTime();

            summaries.add(new ConnectionSummary(
                conn.getId(),
                conn.getName(),
                status,
                conn.getUtilizationPercentage(),
                health.getResponseTime()
            ));
        }

        return new OverallStatistics(
            connections.size(),
            healthy,
            degraded,
            down,
            totalConnections,
            0, // totalQueries
            connections.isEmpty() ? 0 : totalResponseTime / connections.size(),
            summaries
        );
    }

    // Command implementation

    @Override
    public DatabaseConnection registerConnection(String tenantId, RegisterConnectionRequest request) {
        logger.info("Registering new database connection for tenant {}: {}", tenantId, request.name());

        // Check if connection with same name exists for this tenant
        if (connectionRepository.existsByTenantIdAndName(tenantId, request.name())) {
            throw new IllegalArgumentException(
                "Connection with name '" + request.name() + "' already exists for tenant '" + tenantId + "'");
        }

        DatabaseConnection connection = new DatabaseConnection(
            UUID.randomUUID().toString(),
            tenantId,
            request.name(),
            request.type(),
            request.host(),
            request.port(),
            request.database()
        );

        connection.setPoolSize(request.poolSize() != 0 ? request.poolSize() : 10);
        connection.setProperties(request.properties());

        // Test the connection before saving
        if (!healthChecker.testConnectivity(connection)) {
            throw new IllegalStateException("Failed to connect to database: " + request.host());
        }

        connection.markAsHealthy();
        return connectionRepository.save(connection);
    }

    @Override
    public DatabaseConnection updateConnection(String tenantId, String id, UpdateConnectionRequest request) {
        DatabaseConnection connection = connectionRepository.findByIdAndTenantId(id, tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Connection not found for tenant '" + tenantId + "' and id: " + id));

        if (request.name() != null) {
            connection.setName(request.name());
        }
        if (request.poolSize() > 0) {
            connection.setPoolSize(request.poolSize());
        }
        if (request.properties() != null) {
            connection.setProperties(request.properties());
        }

        connection.setUpdatedAt(LocalDateTime.now());
        return connectionRepository.save(connection);
    }

    @Override
    public void removeConnection(String tenantId, String id) {
        logger.info("Removing database connection for tenant {}: {}", tenantId, id);
        connectionRepository.deleteByIdAndTenantId(id, tenantId);
    }

    @Override
    public TestConnectionResult testConnection(String tenantId, String id) {
        Optional<DatabaseConnection> connectionOpt = connectionRepository.findByIdAndTenantId(id, tenantId);
        if (connectionOpt.isEmpty()) {
            return new TestConnectionResult(false, "Connection not found for tenant: " + tenantId, 0);
        }

        DatabaseConnection connection = connectionOpt.get();
        long startTime = System.currentTimeMillis();

        try {
            boolean success = healthChecker.testConnectivity(connection);
            long responseTime = System.currentTimeMillis() - startTime;

            if (success) {
                connection.markAsHealthy();
                connectionRepository.save(connection);
                return new TestConnectionResult(true, "Connection successful", responseTime);
            } else {
                connection.markAsDown();
                connectionRepository.save(connection);
                return new TestConnectionResult(false, "Connection failed", responseTime);
            }
        } catch (Exception e) {
            connection.markAsDown();
            connectionRepository.save(connection);
            return new TestConnectionResult(false, "Connection error: " + e.getMessage(),
                System.currentTimeMillis() - startTime);
        }
    }

    @Override
    public MigrationInfo runMigrations(String tenantId, String connectionId) {
        Optional<DatabaseConnection> connectionOpt = connectionRepository.findByIdAndTenantId(connectionId, tenantId);
        if (connectionOpt.isEmpty()) {
            throw new IllegalArgumentException("Connection not found for tenant '" + tenantId + "' and id: " + connectionId);
        }

        DatabaseConnection connection = connectionOpt.get();
        logger.info("Running migrations for connection: {} for tenant: {}", connectionId, tenantId);

        MigrationInfo result = migrationPort.runMigrations(connection);
        result.setConnectionId(connectionId);

        return result;
    }

    @Override
    public BackupInfo createBackup(String tenantId, CreateBackupRequest request) {
        Optional<DatabaseConnection> connectionOpt = connectionRepository.findByIdAndTenantId(request.connectionId(), tenantId);
        if (connectionOpt.isEmpty()) {
            throw new IllegalArgumentException("Connection not found for tenant '" + tenantId + "' and id: " + request.connectionId());
        }

        DatabaseConnection connection = connectionOpt.get();
        logger.info("Creating backup for connection: {} for tenant: {}", request.connectionId(), tenantId);

        BackupInfo backupInfo = new BackupInfo(
            UUID.randomUUID().toString(),
            tenantId,
            request.connectionId(),
            request.backupName(),
            request.type()
        );

        // Save initial backup info
        BackupInfo savedBackup = backupRepository.save(backupInfo);

        // Execute backup asynchronously
        BackupInfo result = backupPort.createBackup(connection, savedBackup, (progress) -> {
            backupRepository.save(progress);
        });

        return backupRepository.save(result);
    }

    @Override
    public RestoreResult restoreFromBackup(String tenantId, String backupId) {
        BackupInfo backup = backupRepository.findByIdAndTenantId(backupId, tenantId)
            .orElseThrow(() -> new IllegalArgumentException("Backup not found for tenant '" + tenantId + "' and id: " + backupId));

        logger.info("Restoring from backup: {} for tenant: {}", backupId, tenantId);

        DatabaseBackupPort.RestoreResult result = backupPort.restoreFromBackup(backup);

        return new RestoreResult(
            result.success(),
            result.message(),
            result.duration()
        );
    }

    @Override
    public void cancelBackup(String tenantId, String backupId) {
        logger.info("Cancelling backup: {} for tenant: {}", backupId, tenantId);
        backupPort.cancelBackup(backupId);

        BackupInfo backup = backupRepository.findByIdAndTenantId(backupId, tenantId).orElse(null);
        if (backup != null) {
            backup.setStatus(BackupInfo.BackupStatus.CANCELLED);
            backup.setEndTime(LocalDateTime.now());
            backupRepository.save(backup);
        }
    }

    @Override
    public QueryResult executeQuery(String tenantId, ExecuteQueryRequest request) {
        Optional<DatabaseConnection> connectionOpt = connectionRepository.findByIdAndTenantId(request.connectionId(), tenantId);
        if (connectionOpt.isEmpty()) {
            throw new IllegalArgumentException("Connection not found for tenant '" + tenantId + "' and id: " + request.connectionId());
        }

        // This would execute the actual query
        // For now, return a placeholder result
        logger.warn("Query execution not yet implemented for query: {}", request.query());

        return new QueryResult(
            false,
            List.of(),
            0,
            0,
            "Query execution not yet implemented"
        );
    }

    // Helper methods

    private double calculateSuccessRate(DatabaseHealthCheckerPort.PoolStatistics poolStats) {
        if (poolStats.totalConnections() == 0) return 100.0;
        return 100.0 - (poolStats.waitingThreads() * 10.0); // Simplified calculation
    }
}
