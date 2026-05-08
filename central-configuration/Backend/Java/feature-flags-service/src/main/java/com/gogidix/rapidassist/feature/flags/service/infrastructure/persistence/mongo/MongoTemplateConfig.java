package com.gogidix.rapidassist.feature.flags.service.infrastructure.persistence.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.feature.flags.service.infrastructure.persistence.mongo")
public class MongoTemplateConfig {

    @Bean
    public MongoTemplate mongoTemplate(com.mongodb.client.MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "rapidassist");
    }
}
