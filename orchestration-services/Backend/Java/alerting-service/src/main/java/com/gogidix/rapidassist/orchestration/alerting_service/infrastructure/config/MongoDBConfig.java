package com.gogidix.rapidassist.orchestration.alerting_service.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB configuration
 */
@Configuration
@EnableMongoRepositories(basePackages = {
    "com.gogidix.rapidassist.orchestration.alerting_service.infrastructure.persistence.mongo"
})
public class MongoDBConfig {
}
