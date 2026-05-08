package com.gogidix.rapidassist.alerting.service;

import com.gogidix.rapidassist.alerting.service.application.AlertingRetentionProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AlertingRetentionProperties.class)
public class AlertingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AlertingServiceApplication.class, args);
    }
}
