package com.gogidix.rapidassist.api.gateway.infrastructure.web;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

public class CorrelationIdGlobalFilter implements GlobalFilter, Ordered {

    public static final String ATTRIBUTE_CORRELATION_ID = "gogidix.correlationId";

    private final GatewayRequestContextProperties properties;

    public CorrelationIdGlobalFilter(GatewayRequestContextProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String headerName = properties.getCorrelationIdHeader();
        if (!StringUtils.hasText(headerName)) {
            return chain.filter(exchange);
        }

        String correlationId = exchange.getRequest().getHeaders().getFirst(headerName);
        if (!StringUtils.hasText(correlationId)) {
            correlationId = UUID.randomUUID().toString();
        }

        final String correlationIdFinal = correlationId;

        exchange.getAttributes().put(ATTRIBUTE_CORRELATION_ID, correlationIdFinal);

        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .headers(headers -> headers.set(headerName, correlationIdFinal))
                .build();

        ServerHttpResponse response = exchange.getResponse();
        response.getHeaders().set(headerName, correlationIdFinal);

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return -200;
    }
}
