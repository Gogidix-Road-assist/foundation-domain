package com.gogidix.rapidassist.tenant.org.service.infrastructure.persistence.mongodb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.tenant.org.service.infrastructure.persistence.mongodb")
public class MongoConfig {

    @Bean
    public MongoTemplate mongoTemplate(com.mongodb.client.MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, "rapid_assist_tenant_org");
    }
}
