package com.gogidix.rapidassist.ai.tagging.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Bootstrap class for AI Automated Tagging Service
 *
 * This service is part of the RapidAssist AI Services suite.
 * It provides AI-powered automated content tagging capabilities.
 */
@SpringBootApplication
@EnableMongoRepositories
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
