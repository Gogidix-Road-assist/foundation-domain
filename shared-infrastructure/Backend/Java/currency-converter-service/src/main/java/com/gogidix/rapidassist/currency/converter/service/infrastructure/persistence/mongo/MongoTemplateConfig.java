package com.gogidix.rapidassist.currency.converter.service.infrastructure.persistence.mongo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import com.mongodb.client.MongoClient;

@Configuration
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.currency.converter.service.domain")
public class MongoTemplateConfig extends AbstractMongoClientConfiguration {
    @Override
    protected String getDatabaseName() {
        return "rapidassist";
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient, MappingMongoConverter converter) {
        return new MongoTemplate(mongoClient, getDatabaseName());
    }
}
