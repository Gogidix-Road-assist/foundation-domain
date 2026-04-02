package com.gogidix.rapidassist.ai.analytics.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * Bootstrap class for AI BI Analytics Service
 *
 * This service is part of the RapidAssist AI Services suite.
 * It provides business intelligence and analytics capabilities.
 */
@SpringBootApplication
@EnableMongoAuditing
@EnableAsync
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
