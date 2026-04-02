package com.gogidix.rapidassist.ai.moderation.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Bootstrap class for AI Content Moderation Service
 *
 * This service is part of the RapidAssist AI Services suite.
 * It provides AI-powered content moderation capabilities for policy compliance.
 */
@EnableKafka
@EnableMongoAuditing
@SpringBootApplication(scanBasePackages = {
    "com.gogidix.rapidassist.ai.moderation",
    "com.gogidix.rapidassist.shared"
})
public class Application {

    /**
     * Main entry point for the application
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
