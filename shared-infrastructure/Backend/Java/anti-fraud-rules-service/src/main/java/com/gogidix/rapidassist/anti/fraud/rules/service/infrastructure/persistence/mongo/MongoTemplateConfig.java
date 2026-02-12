package com.gogidix.rapidassist.anti.fraud.rules.infrastructure.persistence.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import com.mongodb.client.MongoClient;

@Configuration
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.anti.fraud.rules.service.infrastructure.persistence.mongodb")
public class MongoTemplateConfig extends AbstractMongoClientConfiguration {
    @Override
    protected String getDatabaseName() {
        return "rapidassist";
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, getDatabaseName());
    }
}
