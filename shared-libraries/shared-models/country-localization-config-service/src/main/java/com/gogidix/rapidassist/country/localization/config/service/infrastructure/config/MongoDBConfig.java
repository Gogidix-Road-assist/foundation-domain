package com.gogidix.rapidassist.country.localization.config.service.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration for the country localization config service.
 *
 * <p>Enables MongoDB repositories with proper indexing.
 * Connection settings are auto-configured by Spring Boot.
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.country.localization.config.service.infrastructure.persistence")
public class MongoDBConfig {
    // Spring Boot auto-configures MongoClient, MongoTemplate, etc.
    // from application.yml properties
}
