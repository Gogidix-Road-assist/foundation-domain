package com.gogidix.rapidassist.ai.categorization;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Main application class for AI Categorization Service.
 * This service handles content categorization, taxonomy management,
 * and AI-based category prediction for the Rapid Assist platform.
 */
@SpringBootApplication
@EnableMongoAuditing
@EnableKafka
public class AiCategorizationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCategorizationServiceApplication.class, args);
    }
}
