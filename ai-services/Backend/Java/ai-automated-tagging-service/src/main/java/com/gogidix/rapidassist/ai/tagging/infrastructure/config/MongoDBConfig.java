package com.gogidix.rapidassist.ai.tagging.infrastructure.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration for AI Automated Tagging Service
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.ai.tagging.infrastructure.persistence.repository")
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${spring.data.mongodb.database:rapid_assist_ai_automated_tagging_service}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        // Extract database name from URI
        ConnectionString connectionString = new ConnectionString(mongoUri);
        String db = connectionString.getDatabase();
        return db != null ? db : databaseName;
    }

    @Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        ConnectionString connectionString = new ConnectionString(mongoUri);
        builder.applyConnectionString(connectionString);
    }
}
