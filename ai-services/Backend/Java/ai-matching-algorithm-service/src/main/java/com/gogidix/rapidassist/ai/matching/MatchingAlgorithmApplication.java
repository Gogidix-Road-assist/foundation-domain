package com.gogidix.rapidassist.ai.matching;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Application class for AI Matching Algorithm Service.
 * Handles intelligent matching, similarity scoring, rule-based matching, and ML-based matching operations.
 */
@SpringBootApplication(scanBasePackages = {
    "com.gogidix.rapidassist.ai.matching",
    "com.gogidix.rapidassist.shared"
})
@EnableMongoAuditing
@EnableKafka
@EnableScheduling
public class MatchingAlgorithmApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatchingAlgorithmApplication.class, args);
    }
}
