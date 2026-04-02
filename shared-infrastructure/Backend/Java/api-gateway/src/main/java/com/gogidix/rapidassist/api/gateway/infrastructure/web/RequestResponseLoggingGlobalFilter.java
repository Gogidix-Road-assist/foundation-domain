package com.gogidix.rapidassist.api.gateway.infrastructure.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class RequestResponseLoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestResponseLoggingGlobalFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startNanos = System.nanoTime();

        ServerHttpRequest request = exchange.getRequest();
        String method = request.getMethod() == null ? "" : request.getMethod().name();
        String path = request.getURI().getRawPath();
        String correlationId = String.valueOf(exchange.getAttributes().getOrDefault(
                CorrelationIdGlobalFilter.ATTRIBUTE_CORRELATION_ID,
                ""
        ));

        return chain.filter(exchange)
                .doOnSuccess((v) -> logCompletion(exchange, startNanos, method, path, correlationId, null))
                .doOnError((ex) -> logCompletion(exchange, startNanos, method, path, correlationId, ex));
    }

    private void logCompletion(ServerWebExchange exchange, long startNanos, String method, String path, String correlationId, Throwable ex) {
        int status = exchange.getResponse().getStatusCode() == null ? 0 : exchange.getResponse().getStatusCode().value();
        long durationMs = (System.nanoTime() - startNanos) / 1_000_000L;

        if (ex == null) {
            log.info("gateway_request method={} path={} status={} durationMs={} correlationId={}", method, path, status, durationMs, correlationId);
            return;
        }

        log.warn("gateway_request_failed method={} path={} status={} durationMs={} correlationId={} error={}", method, path, status, durationMs, correlationId, ex.toString());
    }

    @Override
    public int getOrder() {
        return -150;
    }
}
