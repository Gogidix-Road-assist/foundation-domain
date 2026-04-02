package com.gogidix.rapidassist.service.registry.discovery.infrastructure.persistence.mongodb;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.service.registry.discovery.infrastructure.persistence.mongodb")
public class MongoConfig {
}
