package com.gogidix.rapidassist.access.control.service.infrastructure.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

/**
 * Configuration: MongoDBConfig
 *
 * Configures MongoDB with connection pooling.
 */
@Configuration
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017/rapidassist}")
    private String mongoUri;

    @Value("${spring.application.name:access-control-service}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return "rapidassist";
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        builder.applyConnectionString(connectionString);
    }
}
