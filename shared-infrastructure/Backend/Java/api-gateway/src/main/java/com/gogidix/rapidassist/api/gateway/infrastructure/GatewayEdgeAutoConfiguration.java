package com.gogidix.rapidassist.api.gateway.infrastructure;

import com.gogidix.rapidassist.api.gateway.infrastructure.ratelimit.AllowAllRateLimitPolicy;
import com.gogidix.rapidassist.api.gateway.infrastructure.ratelimit.RateLimitPolicy;
import com.gogidix.rapidassist.api.gateway.infrastructure.ratelimit.RateLimitingGlobalFilter;
import com.gogidix.rapidassist.api.gateway.infrastructure.web.CorrelationIdGlobalFilter;
import com.gogidix.rapidassist.api.gateway.infrastructure.web.GatewayRequestContextProperties;
import com.gogidix.rapidassist.api.gateway.infrastructure.web.RequestResponseLoggingGlobalFilter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(GatewayRequestContextProperties.class)
public class GatewayEdgeAutoConfiguration {

    @Bean
    public CorrelationIdGlobalFilter correlationIdGlobalFilter(GatewayRequestContextProperties properties) {
        return new CorrelationIdGlobalFilter(properties);
    }

    @Bean
    public RequestResponseLoggingGlobalFilter requestResponseLoggingGlobalFilter() {
        return new RequestResponseLoggingGlobalFilter();
    }

    @Bean
    public RateLimitPolicy rateLimitPolicy() {
        return new AllowAllRateLimitPolicy();
    }

    @Bean
    public RateLimitingGlobalFilter rateLimitingGlobalFilter(RateLimitPolicy policy) {
        return new RateLimitingGlobalFilter(policy);
    }
}
