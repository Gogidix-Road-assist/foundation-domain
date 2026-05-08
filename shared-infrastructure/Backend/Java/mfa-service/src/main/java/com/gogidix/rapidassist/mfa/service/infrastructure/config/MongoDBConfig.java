package com.gogidix.rapidassist.mfa.service.infrastructure.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;

@Configuration
public class MongoDBConfig extends AbstractMongoClientConfiguration {
    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017/rapidassist}")
    private String mongoUri;

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
