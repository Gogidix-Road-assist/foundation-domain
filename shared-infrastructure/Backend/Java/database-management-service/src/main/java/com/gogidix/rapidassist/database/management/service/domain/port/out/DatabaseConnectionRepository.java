package com.gogidix.rapidassist.database.management.service.domain.port.out;

import com.gogidix.rapidassist.database.management.service.domain.model.DatabaseConnection;

import java.util.List;
import java.util.Optional;

/**
 * Output port for database connection persistence.
 * All methods are tenant-scoped to ensure proper multi-tenancy isolation.
 */
public interface DatabaseConnectionRepository {

    /**
     * Save a database connection.
     * The tenantId must be set on the entity before saving.
     */
    DatabaseConnection save(DatabaseConnection connection);

    /**
     * Find connection by ID within the specified tenant.
     * This ensures tenant isolation by requiring tenantId parameter.
     */
    Optional<DatabaseConnection> findByIdAndTenantId(String id, String tenantId);

    /**
     * Find all connections for a specific tenant.
     */
    List<DatabaseConnection> findByTenantId(String tenantId);

    /**
     * Find connection by tenant and name.
     */
    Optional<DatabaseConnection> findByTenantIdAndName(String tenantId, String name);

    /**
     * Find connections by tenant and type.
     */
    List<DatabaseConnection> findByTenantIdAndType(String tenantId, DatabaseConnection.DatabaseType type);

    /**
     * Delete connection by ID within the specified tenant.
     */
    void deleteByIdAndTenantId(String id, String tenantId);

    /**
     * Check if connection exists by tenant and name.
     */
    boolean existsByTenantIdAndName(String tenantId, String name);

    /**
     * Find connections by tenant and status.
     */
    List<DatabaseConnection> findByTenantIdAndStatus(String tenantId, DatabaseConnection.ConnectionStatus status);

    /**
     * Count connections by tenant and type.
     */
    long countByTenantIdAndType(String tenantId, DatabaseConnection.DatabaseType type);

    /**
     * Count all connections for a tenant.
     */
    long countByTenantId(String tenantId);
}
