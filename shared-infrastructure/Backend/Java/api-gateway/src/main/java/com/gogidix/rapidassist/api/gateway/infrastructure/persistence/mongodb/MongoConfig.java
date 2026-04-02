package com.gogidix.rapidassist.api.gateway.infrastructure.persistence.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.api.gateway.infrastructure.persistence.mongodb")
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.data.mongodb.database:rapid_assist_gateway}")
    private String databaseName;

    @Override
    protected String getDatabaseName() {
        return databaseName;
    }

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(databaseName);
    }
}
