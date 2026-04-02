package com.gogidix.rapidassist.ai.computervision.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

/**
 * Bootstrap class for Computer Vision AI Service
 *
 * This service is part of the RapidAssist AI Services suite.
 * It provides AI-powered computer vision capabilities including:
 * - Image Analysis
 * - Object Detection
 * - Face Detection
 * - Text Recognition (OCR)
 * - Image Classification
 */
@EnableKafka
@SpringBootApplication(scanBasePackages = {
    "com.gogidix.rapidassist.ai.computervision",
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
