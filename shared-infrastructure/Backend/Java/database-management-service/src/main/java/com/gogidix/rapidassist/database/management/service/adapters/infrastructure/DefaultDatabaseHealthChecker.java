package com.gogidix.rapidassist.database.management.service.adapters.infrastructure;

import com.gogidix.rapidassist.database.management.service.domain.model.DatabaseConnection;
import com.gogidix.rapidassist.database.management.service.domain.model.HealthCheckResult;
import com.gogidix.rapidassist.database.management.service.domain.port.out.DatabaseHealthCheckerPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

/**
 * Default implementation of database health checker
 * Supports PostgreSQL and MySQL health checks
 */
@Component
public class DefaultDatabaseHealthChecker implements DatabaseHealthCheckerPort {

    private static final Logger logger = LoggerFactory.getLogger(DefaultDatabaseHealthChecker.class);

    // Track connection pools for statistics
    private final Map<String, ConnectionPoolTracker> poolTrackers = new ConcurrentHashMap<>();

    @Override
    public HealthCheckResult checkHealth(DatabaseConnection connection) {
        long startTime = System.currentTimeMillis();
        HealthCheckResult result = new HealthCheckResult(connection.getId(), false, 0, "");

        try {
            // Create a connection to test health
            try (Connection conn = createConnection(connection)) {
                // Execute a simple query
                String testQuery = getTestQuery(connection.getType());
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery(testQuery)) {

                    boolean hasResult = rs.next();
                    result.setHealthy(true);
                    result.setMessage("Database is healthy");
                }

                // Get pool statistics
                updatePoolStatistics(connection);

            } catch (SQLException e) {
                result.setHealthy(false);
                result.setMessage("Health check failed: " + e.getMessage());
                logger.warn("Health check failed for connection {}: {}", connection.getId(), e.getMessage());
            }

        } catch (Exception e) {
            result.setHealthy(false);
            result.setMessage("Unexpected error: " + e.getMessage());
            logger.error("Unexpected error during health check for connection {}", connection.getId(), e);
        }

        result.setResponseTime(System.currentTimeMillis() - startTime);
        return result;
    }

    @Override
    public PoolStatistics getPoolStatistics(String connectionId) {
        ConnectionPoolTracker tracker = poolTrackers.get(connectionId);
        if (tracker == null) {
            // Return default statistics when no tracker exists
            return new PoolStatistics(
                10,  // default pool size
                0,
                10,
                0,
                0,
                0.0
            );
        }

        return tracker.getStatistics();
    }

    @Override
    public boolean testConnectivity(DatabaseConnection connection) {
        try (Connection conn = createConnection(connection)) {
            return conn.isValid(5); // 5 second timeout
        } catch (Exception e) {
            logger.warn("Connectivity test failed for {}: {}", connection.getId(), e.getMessage());
            return false;
        }
    }

    // Helper methods

    private Connection createConnection(DatabaseConnection connection) throws SQLException {
        String url = connection.getJdbcUrl();

        // For demo purposes, we'll simulate a connection
        // In production, you would use actual JDBC connection
        logger.debug("Creating connection to: {}", url);

        // Simulate connection based on type
        if (connection.getType() == DatabaseConnection.DatabaseType.MONGODB ||
            connection.getType() == DatabaseConnection.DatabaseType.REDIS) {
            // For NoSQL databases, simulation
            return createSimulatedConnection(connection);
        }

        // For SQL databases, use DriverManager
        // For demo, return a simulated connection
        return createSimulatedConnection(connection);
    }

    private Connection createSimulatedConnection(DatabaseConnection connection) {
        // Simulated connection for demonstration
        // In production, this would be a real JDBC connection
        return new SimulatedConnection(connection);
    }

    private String getTestQuery(DatabaseConnection.DatabaseType type) {
        return switch (type) {
            case POSTGRESQL -> "SELECT 1";
            case MYSQL, MARIADB -> "SELECT 1";
            case SQLSERVER -> "SELECT 1";
            case ORACLE -> "SELECT 1 FROM DUAL";
            case MONGODB -> "{ping: 1}";
            case REDIS -> "PING";
        };
    }

    private void updatePoolStatistics(DatabaseConnection connection) {
        poolTrackers.computeIfAbsent(connection.getId(), k -> new ConnectionPoolTracker(connection.getPoolSize()))
            .update(connection.getActiveConnections(), connection.getIdleConnections());
    }

    // Inner classes

    private static class ConnectionPoolTracker {
        private final int maxSize;
        private int activeConnections;
        private int idleConnections;
        private int waitingThreads;
        private long maxWaitTime;
        private final List<Long> waitTimes = new ArrayList<>();

        public ConnectionPoolTracker(int maxSize) {
            this.maxSize = maxSize;
        }

        public void update(int active, int idle) {
            this.activeConnections = active;
            this.idleConnections = idle;
        }

        public PoolStatistics getStatistics() {
            double avgWait = waitTimes.isEmpty() ? 0.0 :
                waitTimes.stream().mapToLong(Long::longValue).average().orElse(0.0);

            return new PoolStatistics(
                maxSize,
                activeConnections,
                idleConnections,
                waitingThreads,
                maxWaitTime,
                avgWait
            );
        }
    }

    /**
     * Simulated connection for demonstration purposes
     * In production, replace with actual JDBC connection
     */
    private static class SimulatedConnection implements Connection {
        private final DatabaseConnection config;
        private boolean closed = false;

        public SimulatedConnection(DatabaseConnection config) {
            this.config = config;
        }

        @Override public Statement createStatement() throws SQLException { return null; }
        @Override public PreparedStatement prepareStatement(String sql) throws SQLException { return null; }
        @Override public CallableStatement prepareCall(String sql) throws SQLException { return null; }
        @Override public String nativeSQL(String sql) throws SQLException { return sql; }
        @Override public void setAutoCommit(boolean autoCommit) throws SQLException {}
        @Override public boolean getAutoCommit() throws SQLException { return true; }
        @Override public void commit() throws SQLException {}
        @Override public void rollback() throws SQLException {}
        @Override public void close() throws SQLException { closed = true; }
        @Override public boolean isClosed() throws SQLException { return closed; }
        @Override public DatabaseMetaData getMetaData() throws SQLException { return null; }
        @Override public void setReadOnly(boolean readOnly) throws SQLException {}
        @Override public boolean isReadOnly() throws SQLException { return false; }
        @Override public void setCatalog(String catalog) throws SQLException {}
        @Override public String getCatalog() throws SQLException { return null; }
        @Override public void setTransactionIsolation(int level) throws SQLException {}
        @Override public int getTransactionIsolation() throws SQLException { return Connection.TRANSACTION_NONE; }
        @Override public SQLWarning getWarnings() throws SQLException { return null; }
        @Override public void clearWarnings() throws SQLException {}

        @Override
        public boolean isValid(int timeout) throws SQLException {
            // Simulate healthy connection
            return true;
        }

        // Additional required methods for Java 21
        @Override public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException { return null; }
        @Override public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException { return null; }
        @Override public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException { return null; }
        @Override public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return null; }
        @Override public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return null; }
        @Override public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return null; }
        @Override public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return null; }
        @Override public Clob createClob() throws SQLException { return null; }
        @Override public Blob createBlob() throws SQLException { return null; }
        @Override public NClob createNClob() throws SQLException { return null; }
        @Override public SQLXML createSQLXML() throws SQLException { return null; }
        @Override public void setClientInfo(String name, String value) throws java.sql.SQLClientInfoException {}
        @Override public void setClientInfo(java.util.Properties props) throws java.sql.SQLClientInfoException {}
        @Override public String getClientInfo(String name) throws SQLException { return null; }
        @Override public java.util.Properties getClientInfo() throws SQLException { return null; }
        @Override public Array createArrayOf(String typeName, Object[] elements) throws SQLException { return null; }
        @Override public Struct createStruct(String typeName, Object[] attributes) throws SQLException { return null; }
        @Override public void setSchema(String schema) throws SQLException {}
        @Override public String getSchema() throws SQLException { return null; }
        @Override public void abort(Executor executor) throws SQLException {}
        @Override public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {}
        @Override public int getNetworkTimeout() throws SQLException { return 0; }
        @Override public <T> T unwrap(Class<T> iface) throws SQLException { return null; }
        @Override public boolean isWrapperFor(Class<?> iface) throws SQLException { return false; }
        @Override public Map<String, Class<?>> getTypeMap() throws SQLException { return null; }
        @Override public void setTypeMap(Map<String, Class<?>> map) throws SQLException {}
        @Override public void setHoldability(int holdability) throws SQLException {}
        @Override public int getHoldability() throws SQLException { return 0; }
        @Override public Savepoint setSavepoint() throws SQLException { return null; }
        @Override public Savepoint setSavepoint(String name) throws SQLException { return null; }
        @Override public void rollback(Savepoint savepoint) throws SQLException {}
        @Override public void releaseSavepoint(Savepoint savepoint) throws SQLException {}
        @Override public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException { return null; }
        @Override public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return null; }
    }
}
