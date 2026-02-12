package com.gogidix.rapidassist.orchestration.matching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Application class for the Matching Service
 * This service handles provider matching operations using multiple algorithms
 * Port: 8090
 * Database: orchestration_matching_service_db
 */
@SpringBootApplication(scanBasePackages = {
    "com.gogidix.rapidassist.orchestration.matching"
})
@EnableMongoAuditing
@EnableKafka
@EnableScheduling
public class MatchingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchingServiceApplication.class, args);
    }
}
