package com.gogidix.rapidassist.orchestration.location.infrastructure.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import jakarta.annotation.PostConstruct;

/**
 * MongoDB configuration for Location Service
 * Creates geospatial indexes for Location, LocationHistory, and Geofence collections
 */
@Slf4j
@Configuration
@EnableMongoRepositories(basePackages = {
        "com.gogidix.rapidassist.orchestration.location.infrastructure.persistence.mongo"
})
@RequiredArgsConstructor
public class MongoConfig extends AbstractMongoClientConfiguration {

    private final MongoClient mongoClient;

    @Override
    protected String getDatabaseName() {
        return "orchestration_location_service_db";
    }

    @PostConstruct
    public void createGeospatialIndexes() {
        log.info("Creating geospatial indexes for Location Service");

        MongoDatabase database = mongoClient.getDatabase(getDatabaseName());

        // Locations collection - coordinates field (2dsphere index)
        try {
            database.runCommand(org.bson.Document.parse(
                    "{ createIndexes: 'locations', indexes: [{ key: { coordinates: '2dsphere' }, name: 'coordinates_2dsphere' }] }"
            ));
            log.info("Created 2dsphere index on locations.coordinates");
        } catch (Exception e) {
            log.debug("Index may already exist: {}", e.getMessage());
        }

        // Location history collection
        try {
            database.runCommand(org.bson.Document.parse(
                    "{ createIndexes: 'location_history', indexes: [{ key: { coordinates: '2dsphere' }, name: 'coordinates_2dsphere' }] }"
            ));
            log.info("Created 2dsphere index on location_history.coordinates");
        } catch (Exception e) {
            log.debug("Index may already exist: {}", e.getMessage());
        }

        // Geofences collection - center field for circle geofences
        try {
            database.runCommand(org.bson.Document.parse(
                    "{ createIndexes: 'geofences', indexes: [{ key: { center: '2dsphere' }, name: 'center_2dsphere' }] }"
            ));
            log.info("Created 2dsphere index on geofences.center");
        } catch (Exception e) {
            log.debug("Index may already exist: {}", e.getMessage());
        }

        // Geofences collection - geometry field for polygon geofences
        try {
            database.runCommand(org.bson.Document.parse(
                    "{ createIndexes: 'geofences', indexes: [{ key: { geometry: '2dsphere' }, name: 'geometry_2dsphere' }] }"
            ));
            log.info("Created 2dsphere index on geofences.geometry");
        } catch (Exception e) {
            log.debug("Index may already exist: {}", e.getMessage());
        }

        log.info("Geospatial indexes created successfully");
    }
}
