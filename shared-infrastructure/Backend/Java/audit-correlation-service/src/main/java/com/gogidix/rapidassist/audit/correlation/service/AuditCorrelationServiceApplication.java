package com.gogidix.rapidassist.audit.correlation.service;

import com.gogidix.rapidassist.audit.correlation.service.application.CorrelationRetentionProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(CorrelationRetentionProperties.class)
public class AuditCorrelationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuditCorrelationServiceApplication.class, args);
    }
}
