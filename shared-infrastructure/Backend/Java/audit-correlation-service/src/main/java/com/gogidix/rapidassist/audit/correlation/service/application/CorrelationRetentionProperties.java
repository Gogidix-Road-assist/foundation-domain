package com.gogidix.rapidassist.audit.correlation.service.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "gogidix.correlation")
public class CorrelationRetentionProperties {

    private Duration retention = Duration.ofDays(90);

    public Duration getRetention() {
        return retention;
    }

    public void setRetention(Duration retention) {
        this.retention = retention;
    }
}
