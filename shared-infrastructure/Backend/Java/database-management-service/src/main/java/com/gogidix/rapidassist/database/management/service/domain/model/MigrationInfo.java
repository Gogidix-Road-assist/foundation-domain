package com.gogidix.rapidassist.database.management.service.domain.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Domain model for database migration information
 */
public class MigrationInfo {

    private String connectionId;
    private String currentVersion;
    private List<Migration> migrations;
    private boolean pendingMigrations;
    private int pendingCount;
    private LocalDateTime lastMigration;
    private MigrationStatus status;

    public enum MigrationStatus {
        UP_TO_DATE,
        PENDING,
        FAILED,
        IN_PROGRESS
    }

    public MigrationInfo() {
        this.status = MigrationStatus.UP_TO_DATE;
    }

    // Business logic
    public void calculateStatus() {
        if (pendingMigrations && pendingCount > 0) {
            this.status = MigrationStatus.PENDING;
        } else if (migrations != null && migrations.stream().anyMatch(m -> m.state() == Migration.State.FAILED)) {
            this.status = MigrationStatus.FAILED;
        } else if (migrations != null && migrations.stream().anyMatch(m -> m.state() == Migration.State.RUNNING)) {
            this.status = MigrationStatus.IN_PROGRESS;
        } else {
            this.status = MigrationStatus.UP_TO_DATE;
        }
    }

    public record Migration(
        String version,
        String description,
        String script,
        Migration.State state,
        LocalDateTime installedOn,
        long executionTime
    ) {
        public enum State {
            PENDING,
            RUNNING,
            SUCCESS,
            FAILED,
            UNDONE
        }
    }

    // Getters and Setters
    public String getConnectionId() { return connectionId; }
    public void setConnectionId(String connectionId) { this.connectionId = connectionId; }

    public String getCurrentVersion() { return currentVersion; }
    public void setCurrentVersion(String currentVersion) { this.currentVersion = currentVersion; }

    public List<Migration> getMigrations() { return migrations; }
    public void setMigrations(List<Migration> migrations) { this.migrations = migrations; }

    public boolean isPendingMigrations() { return pendingMigrations; }
    public void setPendingMigrations(boolean pendingMigrations) { this.pendingMigrations = pendingMigrations; }

    public int getPendingCount() { return pendingCount; }
    public void setPendingCount(int pendingCount) { this.pendingCount = pendingCount; }

    public LocalDateTime getLastMigration() { return lastMigration; }
    public void setLastMigration(LocalDateTime lastMigration) { this.lastMigration = lastMigration; }

    public MigrationStatus getStatus() { return status; }
    public void setStatus(MigrationStatus status) { this.status = status; }
}
