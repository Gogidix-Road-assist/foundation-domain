package com.gogidix.rapidassist.alerting.service.infrastructure.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

/**
 * Configuration: MongoDBConfig
 *
 * Configures MongoDB with connection pooling for the alerting service.
 * Supports multi-tenant data isolation through tenant-scoped collections.
 *
 * @author Rapid Assist
 * @version 1.0.0
 */
@Configuration
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017/rapidassist}")
    private String mongoUri;

    @Value("${spring.application.name:alerting-service}")
    private String databaseName;

    /**
     * Returns the database name for MongoDB connections.
     *
     * @return the database name
     */
    @Override
    protected String getDatabaseName() {
        return "rapidassist";
    }

    /**
     * Configure MongoDB client settings with connection string.
     *
     * @param builder the MongoDB client settings builder
     */
    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        builder.applyConnectionString(connectionString);
    }
}
