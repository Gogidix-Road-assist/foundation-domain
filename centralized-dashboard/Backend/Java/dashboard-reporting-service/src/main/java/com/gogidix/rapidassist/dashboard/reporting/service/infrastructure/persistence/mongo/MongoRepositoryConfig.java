package com.gogidix.rapidassist.dashboard.reporting.service.infrastructure.persistence.mongo;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB Repository Configuration.
 * Note: The MongoTemplate bean is automatically created by Spring Boot's MongoAutoConfiguration.
 * We only need to enable MongoDB repositories here.
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.dashboard.reporting.service.infrastructure.persistence.mongo")
public class MongoRepositoryConfig {
}
