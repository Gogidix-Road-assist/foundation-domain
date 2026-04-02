package com.gogidix.rapidassist.logging.aggregation.service;

import com.gogidix.rapidassist.logging.aggregation.service.application.LoggingRetentionProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(LoggingRetentionProperties.class)
public class LoggingAggregationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LoggingAggregationServiceApplication.class, args);
    }
}
