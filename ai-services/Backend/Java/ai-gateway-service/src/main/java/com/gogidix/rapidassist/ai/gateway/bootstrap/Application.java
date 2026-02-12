package com.gogidix.rapidassist.ai.gateway.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * Bootstrap class for AI Gateway Service
 *
 * This service is part of the RapidAssist AI Services suite.
 * It provides API gateway functionality for routing AI service requests.
 */
@SpringBootApplication
@EnableCaching
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
