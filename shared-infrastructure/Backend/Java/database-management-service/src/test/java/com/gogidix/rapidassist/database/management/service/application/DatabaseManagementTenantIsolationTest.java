package com.gogidix.rapidassist.database.management.service.application;

import com.gogidix.rapidassist.database.management.service.adapters.persistence.InMemoryDatabaseConnectionRepository;
import com.gogidix.rapidassist.database.management.service.domain.model.DatabaseConnection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tenant Isolation Tests for DatabaseConnectionRepository.
 * Verifies that tenant data is properly isolated.
 */
class DatabaseManagementTenantIsolationTest {

    private InMemoryDatabaseConnectionRepository repository;

    private DatabaseConnection connection1;
    private DatabaseConnection connection2;
    private DatabaseConnection connection3;

    @BeforeEach
    void setUp() {
        repository = new InMemoryDatabaseConnectionRepository();
        repository.clear();

        // Create test connections for different tenants
        connection1 = new DatabaseConnection(
            "conn-1", "tenant-1", "db-primary",
            DatabaseConnection.DatabaseType.POSTGRESQL,
            "localhost", 5432, "rapid_assist"
        );

        connection2 = new DatabaseConnection(
            "conn-2", "tenant-2", "db-secondary",
            DatabaseConnection.DatabaseType.MYSQL,
            "localhost", 3306, "rapid_assist"
        );

        connection3 = new DatabaseConnection(
            "conn-3", "tenant-1", "db-replica",
            DatabaseConnection.DatabaseType.POSTGRESQL,
            "localhost", 5433, "rapid_assist"
        );

        connection1 = repository.save(connection1);
        connection2 = repository.save(connection2);
        connection3 = repository.save(connection3);
    }

    @Test
    void testTenantIsolation_FindByTenantId() {
        // When finding connections by tenant-1
        List<DatabaseConnection> tenant1Connections = repository.findByTenantId("tenant-1");

        // Then should only return tenant-1 connections
        assertThat(tenant1Connections).hasSize(2);
        assertThat(tenant1Connections)
            .allMatch(c -> "tenant-1".equals(c.getTenantId()));

        // When finding connections by tenant-2
        List<DatabaseConnection> tenant2Connections = repository.findByTenantId("tenant-2");

        // Then should only return tenant-2 connections
        assertThat(tenant2Connections).hasSize(1);
        assertThat(tenant2Connections.get(0).getTenantId()).isEqualTo("tenant-2");
    }

    @Test
    void testTenantIsolation_FindByIdAndTenantId() {
        // When finding connection1 by tenant-1
        Optional<DatabaseConnection> found1 = repository.findByIdAndTenantId(
            "conn-1", "tenant-1"
        );

        // Then should find it
        assertThat(found1).isPresent();
        assertThat(found1.get().getId()).isEqualTo("conn-1");
        assertThat(found1.get().getTenantId()).isEqualTo("tenant-1");

        // When finding connection1 by tenant-2 (wrong tenant)
        Optional<DatabaseConnection> notFound = repository.findByIdAndTenantId(
            "conn-1", "tenant-2"
        );

        // Then should NOT find it
        assertThat(notFound).isEmpty();
    }

    @Test
    void testTenantIsolation_FindByTenantIdAndName() {
        // When finding by tenant-1 and name "db-primary"
        Optional<DatabaseConnection> found1 = repository.findByTenantIdAndName(
            "tenant-1", "db-primary"
        );

        // Then should find it
        assertThat(found1).isPresent();
        assertThat(found1.get().getName()).isEqualTo("db-primary");
        assertThat(found1.get().getTenantId()).isEqualTo("tenant-1");

        // When finding by tenant-2 and name "db-primary" (wrong tenant)
        Optional<DatabaseConnection> notFound = repository.findByTenantIdAndName(
            "tenant-2", "db-primary"
        );

        // Then should NOT find it
        assertThat(notFound).isEmpty();
    }

    @Test
    void testTenantIsolation_FindByTenantIdAndType() {
        // When finding PostgreSQL connections for tenant-1
        List<DatabaseConnection> tenant1Postgres = repository.findByTenantIdAndType(
            "tenant-1", DatabaseConnection.DatabaseType.POSTGRESQL
        );

        // Then should find 2 connections
        assertThat(tenant1Postgres).hasSize(2);
        assertThat(tenant1Postgres)
            .allMatch(c -> "tenant-1".equals(c.getTenantId()));
        assertThat(tenant1Postgres)
            .allMatch(c -> c.getType() == DatabaseConnection.DatabaseType.POSTGRESQL);

        // When finding PostgreSQL connections for tenant-2
        List<DatabaseConnection> tenant2Postgres = repository.findByTenantIdAndType(
            "tenant-2", DatabaseConnection.DatabaseType.POSTGRESQL
        );

        // Then should find 0 connections
        assertThat(tenant2Postgres).isEmpty();
    }

    @Test
    void testTenantIsolation_DeleteByIdAndTenantId() {
        // When deleting connection1 with tenant-1
        repository.deleteByIdAndTenantId("conn-1", "tenant-1");

        // Then connection1 should be gone for tenant-1
        Optional<DatabaseConnection> deleted = repository.findByIdAndTenantId(
            "conn-1", "tenant-1"
        );
        assertThat(deleted).isEmpty();

        // Other connections should still exist
        assertThat(repository.findByTenantId("tenant-1")).hasSize(1); // connection3
        assertThat(repository.findByTenantId("tenant-2")).hasSize(1); // connection2
    }

    @Test
    void testTenantIsolation_ExistsByTenantIdAndName() {
        // When checking if "db-primary" exists for tenant-1
        boolean existsForTenant1 = repository.existsByTenantIdAndName("tenant-1", "db-primary");

        // Then should be true
        assertThat(existsForTenant1).isTrue();

        // When checking if "db-primary" exists for tenant-2 (wrong tenant)
        boolean existsForTenant2 = repository.existsByTenantIdAndName("tenant-2", "db-primary");

        // Then should be false
        assertThat(existsForTenant2).isFalse();

        // When checking if "db-secondary" exists for tenant-2
        boolean existsForTenant2Db2 = repository.existsByTenantIdAndName("tenant-2", "db-secondary");

        // Then should be true
        assertThat(existsForTenant2Db2).isTrue();
    }

    @Test
    void testTenantIsolation_CountByTenantId() {
        // When counting connections for tenant-1
        long tenant1Count = repository.countByTenantId("tenant-1");

        // Then should be 2
        assertThat(tenant1Count).isEqualTo(2);

        // When counting connections for tenant-2
        long tenant2Count = repository.countByTenantId("tenant-2");

        // Then should be 1
        assertThat(tenant2Count).isEqualTo(1);

        // When counting connections for non-existent tenant
        long tenant3Count = repository.countByTenantId("tenant-3");

        // Then should be 0
        assertThat(tenant3Count).isEqualTo(0);
    }

    @Test
    void testTenantIsolation_CountByTenantIdAndType() {
        // When counting PostgreSQL connections for tenant-1
        long tenant1PostgresCount = repository.countByTenantIdAndType(
            "tenant-1", DatabaseConnection.DatabaseType.POSTGRESQL
        );

        // Then should be 2
        assertThat(tenant1PostgresCount).isEqualTo(2);

        // When counting MySQL connections for tenant-2
        long tenant2MysqlCount = repository.countByTenantIdAndType(
            "tenant-2", DatabaseConnection.DatabaseType.MYSQL
        );

        // Then should be 1
        assertThat(tenant2MysqlCount).isEqualTo(1);
    }

    @Test
    void testTenantIsolation_CrossTenantDataLeakPrevention() {
        // This test verifies that tenants cannot access each other's data
        // even when using the same connection names

        // Create connection with same name for different tenants
        DatabaseConnection tenant1Db = new DatabaseConnection(
            "conn-shared-1", "tenant-1", "shared-db-name",
            DatabaseConnection.DatabaseType.POSTGRESQL,
            "host1", 5432, "db1"
        );
        DatabaseConnection tenant2Db = new DatabaseConnection(
            "conn-shared-2", "tenant-2", "shared-db-name",
            DatabaseConnection.DatabaseType.MYSQL,
            "host2", 3306, "db2"
        );

        repository.save(tenant1Db);
        repository.save(tenant2Db);

        // Verify they have different IDs
        assertThat(tenant1Db.getId()).isNotEqualTo(tenant2Db.getId());

        // Verify each tenant only sees their own
        Optional<DatabaseConnection> tenant1View = repository.findByTenantIdAndName(
            "tenant-1", "shared-db-name"
        );
        Optional<DatabaseConnection> tenant2View = repository.findByTenantIdAndName(
            "tenant-2", "shared-db-name"
        );

        assertThat(tenant1View).isPresent();
        assertThat(tenant2View).isPresent();
        assertThat(tenant1View.get().getId()).isNotEqualTo(tenant2View.get().getId());
        assertThat(tenant1View.get().getType()).isEqualTo(DatabaseConnection.DatabaseType.POSTGRESQL);
        assertThat(tenant2View.get().getType()).isEqualTo(DatabaseConnection.DatabaseType.MYSQL);
    }

    @Test
    void testTenantIsolation_ClearByTenantId() {
        // When clearing data for tenant-1
        repository.clearByTenantId("tenant-1");

        // Then tenant-1 connections should be gone
        assertThat(repository.findByTenantId("tenant-1")).isEmpty();

        // But tenant-2 connections should still exist
        assertThat(repository.findByTenantId("tenant-2")).hasSize(1);
    }
}
