package com.gogidix.ai.dashboard;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * AI Services Dashboard Aggregation Service
 *
 * This service aggregates health and metrics data from all 27 AI services
 * and provides a unified API for the frontend dashboard.
 */
@SpringBootApplication
@EnableAsync
@EnableScheduling
public class AiDashboardApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiDashboardApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
