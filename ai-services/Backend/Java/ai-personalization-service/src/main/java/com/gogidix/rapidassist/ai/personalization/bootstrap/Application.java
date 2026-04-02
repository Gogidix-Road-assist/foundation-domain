package com.gogidix.rapidassist.ai.personalization.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

/**
 * Bootstrap class for AI Personalization Service
 *
 * This service is part of the RapidAssist AI Services suite.
 * It provides AI-powered personalization capabilities including:
 * - User preference learning and tracking
 * - Personalized content recommendations
 * - User segmentation and profiling
 * - Personalization rule management
 * - A/B testing for personalization
 * - Real-time personalization
 * - Campaign personalization
 */
@EnableMongoAuditing
@SpringBootApplication(scanBasePackages = {
    "com.gogidix.rapidassist.ai.personalization",
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
