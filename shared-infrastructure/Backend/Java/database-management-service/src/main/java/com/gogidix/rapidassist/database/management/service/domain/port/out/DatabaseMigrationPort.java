package com.gogidix.rapidassist.database.management.service.domain.port.out;

import com.gogidix.rapidassist.database.management.service.domain.model.DatabaseConnection;
import com.gogidix.rapidassist.database.management.service.domain.model.MigrationInfo;

/**
 * Output port for database migration operations
 */
public interface DatabaseMigrationPort {

    /**
     * Get migration info for a connection
     */
    MigrationInfo getMigrationInfo(DatabaseConnection connection);

    /**
     * Run pending migrations
     */
    MigrationInfo runMigrations(DatabaseConnection connection);

    /**
     * Validate migration scripts
     */
    boolean validateMigrations(DatabaseConnection connection);

    /**
     * Rollback last migration
     */
    boolean rollbackLastMigration(DatabaseConnection connection);
}
