package com.gogidix.rapidassist.api.gateway.infrastructure.web;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gogidix.gateway")
public class GatewayRequestContextProperties {

    private String correlationIdHeader = "X-Correlation-Id";

    public String getCorrelationIdHeader() {
        return correlationIdHeader;
    }

    public void setCorrelationIdHeader(String correlationIdHeader) {
        this.correlationIdHeader = correlationIdHeader;
    }
}
