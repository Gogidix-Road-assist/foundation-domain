package com.gogidix.rapidassist.database.management.service.domain.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Document(collection = "database_connections")
@CompoundIndex(name = "idx_tenant_name", def = "{'tenantId': 1, 'name': 1}")
@CompoundIndex(name = "idx_tenant_type", def = "{'tenantId': 1, 'type': 1}")
public class DatabaseConnection {

    @Id
    private String id;

    @Indexed
    @Field("tenant_id")
    private String tenantId;

    @Field("name")
    private String name;

    @Field("type")
    private DatabaseType type;

    @Field("host")
    private String host;

    @Field("port")
    private int port;

    @Field("database")
    private String database;

    @Indexed
    @Field("status")
    private ConnectionStatus status;

    @Field("pool_size")
    private int poolSize;

    @Field("active_connections")
    private int activeConnections;

    @Field("idle_connections")
    private int idleConnections;

    @Field("max_wait_time")
    private long maxWaitTime;

    @Field("average_query_time")
    private double averageQueryTime;

    @Field("last_checked")
    private LocalDateTime lastChecked;

    @Field("properties")
    private String propertiesJson;

    @org.springframework.data.annotation.Transient
    private Map<String, Object> properties;

    @Field("created_at")
    private LocalDateTime createdAt;

    @Field("updated_at")
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
        this.id = UUID.randomUUID().toString();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ConnectionStatus.UNKNOWN;
    }

    public DatabaseConnection(String id, String tenantId, String name, DatabaseType type, String host, int port, String database) {
        this();
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.tenantId = tenantId;
        this.name = name;
        this.type = type;
        this.host = host;
        this.port = port;
        this.database = database;
    }

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
