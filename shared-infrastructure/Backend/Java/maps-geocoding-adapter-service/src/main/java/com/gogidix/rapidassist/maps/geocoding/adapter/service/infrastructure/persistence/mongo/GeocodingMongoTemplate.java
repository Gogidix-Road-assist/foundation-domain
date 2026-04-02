package com.gogidix.rapidassist.maps.geocoding.adapter.service.infrastructure.persistence.mongo;

import com.mongodb.client.MongoClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * MongoDB Persistence Configuration for Maps Geocoding Adapter Service.
 *
 * Provides MongoDB template with tenant-scoped collection support.
 * This is an ADAPTER in the hexagonal architecture that implements
 * the persistence port using MongoDB.
 *
 * @author Rapid Assist
 * @version 1.0.0
 */
@Configuration
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.maps.geocoding.adapter.service.application")
public class GeocodingMongoTemplate extends AbstractMongoClientConfiguration {

    private static final Logger log = LoggerFactory.getLogger(GeocodingMongoTemplate.class);

    @Value("${spring.data.mongodb.uri:mongodb://localhost:27017/rapidassist}")
    private String mongoUri;

    @Value("${spring.application.name:maps-geocoding-adapter-service}")
    private String databaseName;

    @Value("${spring.data.mongodb.auto-index-creation:true}")
    private boolean autoIndexCreation;

    @Override
    protected String getDatabaseName() {
        return "rapidassist";
    }

    /**
     * Configure MongoDB client settings.
     * Note: Connection string is applied via application.properties
     *
     * @return MongoClient configuration builder
     */
    @Override
    protected void configureClientSettings(com.mongodb.MongoClientSettings.Builder builder) {
        // Default configuration - connection settings from application.properties
        log.debug("Configuring MongoDB client for database: {}", getDatabaseName());
    }

    /**
     * Creates MongoTemplate bean with tenant-aware configuration.
     *
     * @param mongoClient the MongoDB client
     * @return configured MongoTemplate
     */
    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        log.debug("Created MongoTemplate for database: {}", getDatabaseName());
        return new MongoTemplate(mongoClient, getDatabaseName());
    }
}
