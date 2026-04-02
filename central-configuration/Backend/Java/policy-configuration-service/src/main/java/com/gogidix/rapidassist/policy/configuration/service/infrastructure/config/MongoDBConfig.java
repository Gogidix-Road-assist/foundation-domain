package com.gogidix.rapidassist.policy.configuration.service.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration for the policy configuration service.
 *
 * <p>Enables MongoDB repositories with tenant-aware indexing.
 * Connection settings are auto-configured by Spring Boot.
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.policy.configuration.service.infrastructure.persistence")
public class MongoDBConfig {
    // Spring Boot auto-configures MongoClient, MongoTemplate, etc.
    // from application.yml properties
}
