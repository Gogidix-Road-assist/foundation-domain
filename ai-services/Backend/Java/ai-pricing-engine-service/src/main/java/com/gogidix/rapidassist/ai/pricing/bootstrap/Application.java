package com.gogidix.rapidassist.ai.pricing.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Bootstrap class for AI Pricing Engine Service
 *
 * This service is part of the RapidAssist AI Services suite.
 * It provides AI-powered pricing capabilities including dynamic pricing,
 * price optimization, discount management, and price elasticity analysis.
 */
@SpringBootApplication
@EnableMongoRepositories(basePackages = "com.gogidix.rapidassist.ai.pricing.infrastructure.persistence.repository")
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
