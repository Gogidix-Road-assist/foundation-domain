package com.gogidix.rapidassist.ai.dataquality.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Bootstrap class for AI Data Quality Service
 *
 * This service is part of the RapidAssist AI Services suite.
 * It provides data quality monitoring, validation, and improvement capabilities.
 */
@EnableKafka
@EnableMongoRepositories
@SpringBootApplication(scanBasePackages = {
    "com.gogidix.rapidassist.ai.dataquality",
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
