package com.gogidix.rapidassist.ai.recommendation.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Application class for AI Recommendation Service.
 * Bootstraps the Spring Boot application with required configurations.
 */
@EnableKafka
@EnableScheduling
@SpringBootApplication(scanBasePackages = {
    "com.gogidix.rapidassist.ai.recommendation",
    "com.gogidix.rapidassist.shared"
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
