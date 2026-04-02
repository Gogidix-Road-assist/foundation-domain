package com.gogidix.rapidassist.database.management.service.adapters.persistence;

import com.gogidix.rapidassist.database.management.service.domain.model.DatabaseConnection;
import com.gogidix.rapidassist.database.management.service.domain.port.out.DatabaseConnectionRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of DatabaseConnectionRepository with tenant isolation.
 * For production, replace with MongoDB or JPA implementation.
 * All operations are tenant-scoped to ensure proper multi-tenancy isolation.
 */
@Repository
public class InMemoryDatabaseConnectionRepository implements DatabaseConnectionRepository {

    // Structure: Map<tenantId, Map<connectionId, DatabaseConnection>>
    private final Map<String, Map<String, DatabaseConnection>> tenantConnections = new ConcurrentHashMap<>();

    // Structure: Map<tenantId, Map<name, connectionId>>
    private final Map<String, Map<String, String>> tenantNameToIdMap = new ConcurrentHashMap<>();

    @Override
    public DatabaseConnection save(DatabaseConnection connection) {
        if (connection.getId() == null) {
            connection.setId(UUID.randomUUID().toString());
        }

        String tenantId = connection.getTenantId();
        if (tenantId == null) {
            throw new IllegalArgumentException("TenantId is required");
        }

        // Get or create tenant-specific maps
        Map<String, DatabaseConnection> connections = tenantConnections.computeIfAbsent(
            tenantId, k -> new ConcurrentHashMap<>());
        Map<String, String> nameToIdMap = tenantNameToIdMap.computeIfAbsent(
            tenantId, k -> new ConcurrentHashMap<>());

        // Update name index
        if (connection.getName() != null) {
            String existingId = nameToIdMap.get(connection.getName());
            if (existingId != null && !existingId.equals(connection.getId())) {
                // Name exists with different ID
                throw new IllegalArgumentException(
                    "Connection with name '" + connection.getName() + "' already exists for tenant '" + tenantId + "'");
            }
            nameToIdMap.put(connection.getName(), connection.getId());
        }

        connections.put(connection.getId(), connection);
        return connection;
    }

    @Override
    public Optional<DatabaseConnection> findByIdAndTenantId(String id, String tenantId) {
        Map<String, DatabaseConnection> connections = tenantConnections.get(tenantId);
        if (connections == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(connections.get(id));
    }

    @Override
    public List<DatabaseConnection> findByTenantId(String tenantId) {
        Map<String, DatabaseConnection> connections = tenantConnections.get(tenantId);
        if (connections == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(connections.values());
    }

    @Override
    public Optional<DatabaseConnection> findByTenantIdAndName(String tenantId, String name) {
        Map<String, String> nameToIdMap = tenantNameToIdMap.get(tenantId);
        if (nameToIdMap == null) {
            return Optional.empty();
        }
        String id = nameToIdMap.get(name);
        if (id != null) {
            Map<String, DatabaseConnection> connections = tenantConnections.get(tenantId);
            if (connections != null) {
                return Optional.ofNullable(connections.get(id));
            }
        }
        return Optional.empty();
    }

    @Override
    public List<DatabaseConnection> findByTenantIdAndType(String tenantId, DatabaseConnection.DatabaseType type) {
        Map<String, DatabaseConnection> connections = tenantConnections.get(tenantId);
        if (connections == null) {
            return new ArrayList<>();
        }
        return connections.values().stream()
            .filter(c -> c.getType() == type)
            .collect(Collectors.toList());
    }

    @Override
    public void deleteByIdAndTenantId(String id, String tenantId) {
        Map<String, DatabaseConnection> connections = tenantConnections.get(tenantId);
        Map<String, String> nameToIdMap = tenantNameToIdMap.get(tenantId);

        if (connections != null) {
            DatabaseConnection connection = connections.remove(id);
            if (connection != null && connection.getName() != null && nameToIdMap != null) {
                nameToIdMap.remove(connection.getName());
            }
        }
    }

    @Override
    public boolean existsByTenantIdAndName(String tenantId, String name) {
        Map<String, String> nameToIdMap = tenantNameToIdMap.get(tenantId);
        return nameToIdMap != null && nameToIdMap.containsKey(name);
    }

    @Override
    public List<DatabaseConnection> findByTenantIdAndStatus(String tenantId, DatabaseConnection.ConnectionStatus status) {
        Map<String, DatabaseConnection> connections = tenantConnections.get(tenantId);
        if (connections == null) {
            return new ArrayList<>();
        }
        return connections.values().stream()
            .filter(c -> c.getStatus() == status)
            .collect(Collectors.toList());
    }

    @Override
    public long countByTenantIdAndType(String tenantId, DatabaseConnection.DatabaseType type) {
        Map<String, DatabaseConnection> connections = tenantConnections.get(tenantId);
        if (connections == null) {
            return 0;
        }
        return connections.values().stream()
            .filter(c -> c.getType() == type)
            .count();
    }

    @Override
    public long countByTenantId(String tenantId) {
        Map<String, DatabaseConnection> connections = tenantConnections.get(tenantId);
        return connections == null ? 0 : connections.size();
    }

    /**
     * Clear all data for testing purposes.
     */
    public void clear() {
        tenantConnections.clear();
        tenantNameToIdMap.clear();
    }

    /**
     * Clear all data for a specific tenant.
     */
    public void clearByTenantId(String tenantId) {
        tenantConnections.remove(tenantId);
        tenantNameToIdMap.remove(tenantId);
    }
}
