package com.gogidix.rapidassist.config.service.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * MongoDB configuration placeholder.
 *
 * <p>Note: MongoDB repositories are enabled by MongoConfig in the
 * infrastructure.persistence.mongodb package which also provides custom converters.
 * This configuration class is kept for potential future MongoDB-specific settings.
 *
 * @see com.gogidix.rapidassist.config.service.infrastructure.persistence.mongodb.MongoConfig
 */
@Configuration
public class MongoDBConfig {
    // Spring Boot auto-configures MongoClient, MongoTemplate, etc.
    // from application.yml properties
    // Repositories are enabled by MongoConfig with custom converters
}
