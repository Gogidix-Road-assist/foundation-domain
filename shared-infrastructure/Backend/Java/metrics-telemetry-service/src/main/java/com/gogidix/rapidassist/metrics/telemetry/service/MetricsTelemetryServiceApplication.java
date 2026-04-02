package com.gogidix.rapidassist.metrics.telemetry.service;

import com.gogidix.rapidassist.metrics.telemetry.service.application.TelemetryRetentionProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(TelemetryRetentionProperties.class)
public class MetricsTelemetryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetricsTelemetryServiceApplication.class, args);
    }
}
