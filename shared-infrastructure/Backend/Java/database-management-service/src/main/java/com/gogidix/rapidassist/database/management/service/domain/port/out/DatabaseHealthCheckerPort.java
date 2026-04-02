package com.gogidix.rapidassist.database.management.service.domain.port.out;

import com.gogidix.rapidassist.database.management.service.domain.model.DatabaseConnection;
import com.gogidix.rapidassist.database.management.service.domain.model.HealthCheckResult;

/**
 * Output port for database health checking operations
 */
public interface DatabaseHealthCheckerPort {

    /**
     * Check health of a database connection
     */
    HealthCheckResult checkHealth(DatabaseConnection connection);

    /**
     * Get connection pool statistics
     */
    PoolStatistics getPoolStatistics(String connectionId);

    /**
     * Test connectivity without throwing exceptions
     */
    boolean testConnectivity(DatabaseConnection connection);

    record PoolStatistics(
        int totalConnections,
        int activeConnections,
        int idleConnections,
        int waitingThreads,
        long maxWaitTime,
        double averageWaitTime
    ) {}
}
