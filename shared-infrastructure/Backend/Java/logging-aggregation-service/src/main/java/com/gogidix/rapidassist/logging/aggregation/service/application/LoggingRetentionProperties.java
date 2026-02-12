package com.gogidix.rapidassist.logging.aggregation.service.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "gogidix.logging")
public class LoggingRetentionProperties {

    private Duration retention = Duration.ofDays(30);

    public Duration getRetention() {
        return retention;
    }

    public void setRetention(Duration retention) {
        this.retention = retention;
    }
}
