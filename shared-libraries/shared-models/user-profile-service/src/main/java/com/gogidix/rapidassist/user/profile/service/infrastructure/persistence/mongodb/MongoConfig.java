package com.gogidix.rapidassist.user.profile.service.infrastructure.persistence.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.user.profile.service.infrastructure.persistence.mongodb")
public class MongoConfig {

    @Value("${spring.data.mongodb.database:rapid_assist_user_profile}")
    private String databaseName;

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(databaseName);
    }
}
