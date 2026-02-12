package com.gogidix.rapidassist.api.gateway.infrastructure.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

/**
 * Configuration: MongoDBConfig
 *
 * Configures MongoDB for the API Gateway service.
 * The gateway may store route history, rate limit data, or audit logs.
 *
 * Note: This configuration is conditional on MongoDB being enabled.
 *
 * @author Rapid Assist
 * @version 1.0.0
 */
@Configuration
@ConditionalOnProperty(name = "spring.data.mongodb.enabled", havingValue = "true", matchIfMissing = false)
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017/rapidassist}")
    private String mongoUri;

    @Value("${spring.application.name:api-gateway}")
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
