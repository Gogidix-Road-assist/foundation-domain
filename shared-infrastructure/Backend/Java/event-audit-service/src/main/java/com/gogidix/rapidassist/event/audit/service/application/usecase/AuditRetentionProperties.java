package com.gogidix.rapidassist.event.audit.service.application.usecase;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "gogidix.audit")
public class AuditRetentionProperties {

    private Duration retention = Duration.ofDays(90);

    public Duration getRetention() {
        return retention;
    }

    public void setRetention(Duration retention) {
        this.retention = retention;
    }
}
