package com.gogidix.rapidassist.ai.modelmanagement.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Bootstrap class for AI Model Management Service.
 *
 * This service handles ML model lifecycle management, versioning, deployment,
 * performance tracking, A/B testing, and training job management.
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
