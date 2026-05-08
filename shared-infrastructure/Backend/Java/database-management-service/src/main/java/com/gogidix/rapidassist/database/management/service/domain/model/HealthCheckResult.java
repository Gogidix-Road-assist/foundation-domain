package com.gogidix.rapidassist.database.management.service.domain.model;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Domain model for database health check results
 */
public class HealthCheckResult {

    private String connectionId;
    private boolean healthy;
    private long responseTime;
    private String message;
    private Map<String, Object> details;
    private LocalDateTime timestamp;
    private int totalConnections;
    private int activeConnections;
    private int idleConnections;
    private long totalQueryCount;
    private double averageQueryTime;
    private long failedQueryCount;
    private double successRate;

    public HealthCheckResult() {
        this.timestamp = LocalDateTime.now();
    }

    public HealthCheckResult(String connectionId, boolean healthy, long responseTime, String message) {
        this();
        this.connectionId = connectionId;
        this.healthy = healthy;
        this.responseTime = responseTime;
        this.message = message;
    }

    // Business logic
    public boolean isDegraded() {
        return !healthy && responseTime > 1000;
    }

    public boolean isSlow() {
        return responseTime > 500;
    }

    public boolean isOverloaded() {
        if (totalConnections == 0) return false;
        return (double) activeConnections / totalConnections > 0.8;
    }

    public HealthStatus getHealthStatus() {
        if (!healthy) {
            return isDegraded() ? HealthStatus.DEGRADED : HealthStatus.DOWN;
        }
        if (isSlow() || isOverloaded()) {
            return HealthStatus.DEGRADED;
        }
        return HealthStatus.HEALTHY;
    }

    public enum HealthStatus {
        HEALTHY,
        DEGRADED,
        DOWN
    }

    // Getters and Setters
    public String getConnectionId() { return connectionId; }
    public void setConnectionId(String connectionId) { this.connectionId = connectionId; }

    public boolean isHealthy() { return healthy; }
    public void setHealthy(boolean healthy) { this.healthy = healthy; }

    public long getResponseTime() { return responseTime; }
    public void setResponseTime(long responseTime) { this.responseTime = responseTime; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Map<String, Object> getDetails() { return details; }
    public void setDetails(Map<String, Object> details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getTotalConnections() { return totalConnections; }
    public void setTotalConnections(int totalConnections) { this.totalConnections = totalConnections; }

    public int getActiveConnections() { return activeConnections; }
    public void setActiveConnections(int activeConnections) { this.activeConnections = activeConnections; }

    public int getIdleConnections() { return idleConnections; }
    public void setIdleConnections(int idleConnections) { this.idleConnections = idleConnections; }

    public long getTotalQueryCount() { return totalQueryCount; }
    public void setTotalQueryCount(long totalQueryCount) { this.totalQueryCount = totalQueryCount; }

    public double getAverageQueryTime() { return averageQueryTime; }
    public void setAverageQueryTime(double averageQueryTime) { this.averageQueryTime = averageQueryTime; }

    public long getFailedQueryCount() { return failedQueryCount; }
    public void setFailedQueryCount(long failedQueryCount) { this.failedQueryCount = failedQueryCount; }

    public double getSuccessRate() { return successRate; }
    public void setSuccessRate(double successRate) { this.successRate = successRate; }
}
