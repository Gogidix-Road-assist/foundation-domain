package com.gogidix.rapidassist.ai.imagerecognition.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot Application class for AI Image Recognition Service.
 */
@SpringBootApplication(scanBasePackages = {
        "com.gogidix.rapidassist.ai.imagerecognition",
        "com.gogidix.rapidassist.shared"
})
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
