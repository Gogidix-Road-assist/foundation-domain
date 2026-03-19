package com.gogidix.rapidassist.ai.chatbot.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstrap class for chatbot AI Service
 *
 * This service is part of the RapidAssist AI Services suite.
 * It provides AI-powered capabilities.
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
