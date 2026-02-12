package com.gogidix.rapidassist.ai.contentanalysis.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Bootstrap class for Content Analysis AI Service
 *
 * This service is part of the RapidAssist AI Services suite.
 * It provides AI-powered content quality, readability, sentiment, and SEO analysis.
 */
@EnableKafka
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
