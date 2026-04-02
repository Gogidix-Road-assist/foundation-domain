package com.gogidix.rapidassist.database.management.service.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

/**
 * Domain model representing a database connection configuration.
 * This entity is tenant-scoped to ensure proper multi-tenancy isolation.
 */
@Entity
@Table(name = "database_connections",
    indexes = {
        @Index(name = "idx_tenant_id", columnList = "tenant_id"),
        @Index(name = "idx_tenant_name", columnList = "tenant_id,name"),
        @Index(name = "idx_tenant_type", columnList = "tenant_id,type"),
        @Index(name = "idx_status", columnList = "status")
    }
)
public class DatabaseConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private String id;

    @Column(name = "tenant_id", nullable = false)
    private String tenantId;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private DatabaseType type;

    @Column(name = "host", nullable = false)
    private String host;

    @Column(name = "port", nullable = false)
    private int port;

    @Column(name = "database", nullable = false)
    private String database;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ConnectionStatus status;

    @Column(name = "pool_size")
    private int poolSize;

    @Column(name = "active_connections")
    private int activeConnections;

    @Column(name = "idle_connections")
    private int idleConnections;

    @Column(name = "max_wait_time")
    private long maxWaitTime;

    @Column(name = "average_query_time")
    private double averageQueryTime;

    @Column(name = "last_checked")
    private LocalDateTime lastChecked;

    @Column(name = "properties", length = 2000)
    private String propertiesJson;

    @Transient
    private Map<String, Object> properties;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum DatabaseType {
        POSTGRESQL,
        MYSQL,
        MONGODB,
        REDIS,
        ORACLE,
        SQLSERVER,
        MARIADB
    }

    public enum ConnectionStatus {
        HEALTHY,
        DEGRADED,
        DOWN,
        UNKNOWN
    }

    public DatabaseConnection() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ConnectionStatus.UNKNOWN;
    }

    public DatabaseConnection(String id, String tenantId, String name, DatabaseType type, String host, int port, String database) {
        this();
        this.id = id;
        this.tenantId = tenantId;
        this.name = name;
        this.type = type;
        this.host = host;
        this.port = port;
        this.database = database;
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Business logic
    public void markAsHealthy() {
        this.status = ConnectionStatus.HEALTHY;
        this.lastChecked = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDegraded() {
        this.status = ConnectionStatus.DEGRADED;
        this.lastChecked = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDown() {
        this.status = ConnectionStatus.DOWN;
        this.lastChecked = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updatePoolMetrics(int activeConnections, int idleConnections, long maxWaitTime) {
        this.activeConnections = activeConnections;
        this.idleConnections = idleConnections;
        this.maxWaitTime = maxWaitTime;
        this.updatedAt = LocalDateTime.now();
    }

    public double getUtilizationPercentage() {
        if (poolSize == 0) return 0;
        return ((double) activeConnections / poolSize) * 100;
    }

    public boolean isOverThreshold(double threshold) {
        return getUtilizationPercentage() > threshold;
    }

    public String getJdbcUrl() {
        return switch (type) {
            case POSTGRESQL -> "jdbc:postgresql://" + host + ":" + port + "/" + database;
            case MYSQL -> "jdbc:mysql://" + host + ":" + port + "/" + database;
            case MONGODB -> "mongodb://" + host + ":" + port + "/" + database;
            case REDIS -> "redis://" + host + ":" + port + "/" + database;
            default -> throw new UnsupportedOperationException("Unsupported database type: " + type);
        };
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTenantId() { return tenantId; }
    public void setTenantId(String tenantId) { this.tenantId = tenantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public DatabaseType getType() { return type; }
    public void setType(DatabaseType type) { this.type = type; }

    public String getHost() { return host; }
    public void setHost(String host) { this.host = host; }

    public int getPort() { return port; }
    public void setPort(int port) { this.port = port; }

    public String getDatabase() { return database; }
    public void setDatabase(String database) { this.database = database; }

    public ConnectionStatus getStatus() { return status; }
    public void setStatus(ConnectionStatus status) { this.status = status; }

    public int getPoolSize() { return poolSize; }
    public void setPoolSize(int poolSize) { this.poolSize = poolSize; }

    public int getActiveConnections() { return activeConnections; }
    public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }

    public int getIdleConnections() { return idleConnections; }
    public void setIdleConnections(int idleConnections) { this.idleConnections = idleConnections; }

    public long getMaxWaitTime() { return maxWaitTime; }
    public void setMaxWaitTime(long maxWaitTime) { this.maxWaitTime = maxWaitTime; }

    public double getAverageQueryTime() { return averageQueryTime; }
    public void setAverageQueryTime(double averageQueryTime) { this.averageQueryTime = averageQueryTime; }

    public LocalDateTime getLastChecked() { return lastChecked; }
    public void setLastChecked(LocalDateTime lastChecked) { this.lastChecked = lastChecked; }

    public Map<String, Object> getProperties() { return properties; }
    public void setProperties(Map<String, Object> properties) { this.properties = properties; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getPropertiesJson() { return propertiesJson; }
    public void setPropertiesJson(String propertiesJson) { this.propertiesJson = propertiesJson; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DatabaseConnection that = (DatabaseConnection) o;
        return Objects.equals(id, that.id) && Objects.equals(tenantId, that.tenantId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tenantId);
    }

    @Override
    public String toString() {
        return "DatabaseConnection{" +
            "id='" + id + '\'' +
            ", tenantId='" + tenantId + '\'' +
            ", name='" + name + '\'' +
            ", type=" + type +
            ", status=" + status +
            '}';
    }
}
