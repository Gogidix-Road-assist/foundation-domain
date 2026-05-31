package com.gogidix.rapidassist.api.keys.service.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.api.keys.service.infrastructure.persistence")
public class MongoDBConfig {
}
