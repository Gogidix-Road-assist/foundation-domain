package com.gogidix.rapidassist.config.service.infrastructure.persistence.mongo;

import org.springframework.context.annotation.Configuration;

/**
 * MongoDB configuration placeholder.
 *
 * <p>Note: MongoDB repositories and MongoTemplate are configured by MongoConfig
 * in the infrastructure.persistence.mongodb package which provides:
 * - @EnableMongoRepositories annotation
 * - MongoTemplate bean (via AbstractMongoClientConfiguration)
 * - Custom converters for MongoDB
 *
 * This class is kept for package consistency but delegates to MongoConfig.
 *
 * @see com.gogidix.rapidassist.config.service.infrastructure.persistence.mongodb.MongoConfig
 */
@Configuration
public class MongoTemplateConfig {
    // MongoDB configuration is handled by MongoConfig
    // This class exists for package structure consistency
}
