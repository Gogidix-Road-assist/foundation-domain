package com.gogidix.rapidassist.alerting.service.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@ConfigurationProperties(prefix = "gogidix.alerting")
public class AlertingRetentionProperties {

    private Duration rulesRetention = Duration.ofDays(365);
    private Duration eventsRetention = Duration.ofDays(90);

    public Duration getRulesRetention() {
        return rulesRetention;
    }

    public void setRulesRetention(Duration rulesRetention) {
        this.rulesRetention = rulesRetention;
    }

    public Duration getEventsRetention() {
        return eventsRetention;
    }

    public void setEventsRetention(Duration eventsRetention) {
        this.eventsRetention = eventsRetention;
    }
}
