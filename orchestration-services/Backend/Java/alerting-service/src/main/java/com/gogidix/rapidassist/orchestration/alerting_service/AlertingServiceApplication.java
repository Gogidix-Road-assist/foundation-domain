package com.gogidix.rapidassist.orchestration.alerting_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for Alerting Service
 */
@SpringBootApplication(scanBasePackages = {
    "com.gogidix.rapidassist.orchestration.alerting_service",
    "com.gogidix.rapidassist.orchestration.alerting_service.shared"
})
@EnableKafka
@EnableScheduling
public class AlertingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertingServiceApplication.class, args);
    }
}
