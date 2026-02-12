package com.gogidix.rapidassist.service.health.monitor.service;

import com.gogidix.rapidassist.service.health.monitor.service.application.ServiceHealthRetentionProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ServiceHealthRetentionProperties.class)
public class ServiceHealthMonitorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceHealthMonitorServiceApplication.class, args);
    }
}
