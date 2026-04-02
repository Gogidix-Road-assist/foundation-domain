package com.gogidix.rapidassist.ai.report.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Bootstrap class for AI Report Generation Service
 *
 * This service is part of the RapidAssist AI Services suite.
 * It provides AI-powered report generation capabilities with template-based
 * reporting, multiple output formats, and scheduled report generation.
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
