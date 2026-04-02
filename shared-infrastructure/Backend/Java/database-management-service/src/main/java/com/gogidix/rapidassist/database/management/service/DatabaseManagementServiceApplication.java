package com.gogidix.rapidassist.database.management.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Main Spring Boot application for Database Management Service
 */
@SpringBootApplication
public class DatabaseManagementServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatabaseManagementServiceApplication.class, args);
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }
}
