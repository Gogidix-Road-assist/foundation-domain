package com.gogidix.rapidassist.service.health.monitor.service.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "gogidix.service-health")
public class ServiceHealthRetentionProperties {

    private Duration retention = Duration.ofDays(30);

    public Duration getRetention() {
        return retention;
    }

    public void setRetention(Duration retention) {
        this.retention = retention;
    }
}
