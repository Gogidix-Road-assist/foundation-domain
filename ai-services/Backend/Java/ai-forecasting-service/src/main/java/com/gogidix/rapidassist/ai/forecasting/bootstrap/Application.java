package com.gogidix.rapidassist.ai.forecasting.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Bootstrap class for AI Forecasting Service
 *
 * This service is part of the RapidAssist AI Services suite.
 * It provides AI-powered forecasting and predictive analytics capabilities.
 */
@SpringBootApplication(scanBasePackages = {
    "com.gogidix.rapidassist.ai.forecasting",
    "com.gogidix.rapidassist.shared"
})
@EnableKafka
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
