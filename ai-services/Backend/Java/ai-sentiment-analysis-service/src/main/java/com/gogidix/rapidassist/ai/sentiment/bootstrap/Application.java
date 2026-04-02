package com.gogidix.rapidassist.ai.sentiment.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstrap class for AI Sentiment Analysis Service
 *
 * This service is part of the RapidAssist AI Services suite.
 * It provides AI-powered sentiment analysis capabilities.
 */
@SpringBootApplication
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
