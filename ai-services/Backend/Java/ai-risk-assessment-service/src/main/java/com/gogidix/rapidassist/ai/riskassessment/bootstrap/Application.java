package com.gogidix.rapidassist.ai.riskassessment.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Bootstrap class for AI Risk Assessment Service
 *
 * This service is part of the RapidAssist AI Services suite.
 * It provides AI-powered risk assessment capabilities including:
 * - Risk scoring and assessment
 * - Risk factor analysis
 * - Risk threshold management
 * - Alert generation for high risks
 * - Historical risk tracking
 * - Risk mitigation recommendations
 * - Batch risk assessment
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
