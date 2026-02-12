package com.gogidix.rapidassist.orchestration.fleetorganization.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration for Fleet Organization Service
 */
@Configuration
@EnableMongoRepositories(basePackages = {
    "com.gogidix.rapidassist.orchestration.fleetorganization.infrastructure.persistence.mongo"
})
@EnableMongoAuditing
public class MongoDBConfig {
    // MongoDB configuration is primarily in application.yml
}
