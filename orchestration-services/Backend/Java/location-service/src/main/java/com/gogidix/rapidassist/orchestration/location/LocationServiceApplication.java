package com.gogidix.rapidassist.orchestration.location;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Location Service
 * Port: 8089
 * Database: orchestration_location_service_db
 */
@SpringBootApplication(scanBasePackages = {
        "com.gogidix.rapidassist.orchestration.location",
        "com.gogidix.rapidassist.shared.requestcontext"
})
@EnableMongoAuditing
@EnableKafka
@EnableScheduling
public class LocationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocationServiceApplication.class, args);
    }
}
