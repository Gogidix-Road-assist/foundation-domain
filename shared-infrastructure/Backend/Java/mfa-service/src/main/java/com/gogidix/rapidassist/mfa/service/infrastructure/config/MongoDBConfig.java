package com.gogidix.rapidassist.mfa.service.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.mfa.service.infrastructure.persistence")
public class MongoDBConfig {
}
