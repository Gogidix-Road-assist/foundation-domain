package com.gogidix.rapidassist.api.gateway.infrastructure.ratelimit;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class RateLimitingGlobalFilter implements GlobalFilter, Ordered {

    private final RateLimitPolicy policy;

    public RateLimitingGlobalFilter(RateLimitPolicy policy) {
        this.policy = policy;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        return policy.evaluate(exchange)
                .flatMap(decision -> {
                    if (decision == null || decision.allowed()) {
                        return chain.filter(exchange);
                    }

                    exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    if (decision.retryAfter() != null) {
                        long seconds = Math.max(0, decision.retryAfter().toSeconds());
                        exchange.getResponse().getHeaders().set(HttpHeaders.RETRY_AFTER, String.valueOf(seconds));
                    }
                    return exchange.getResponse().setComplete();
                });
    }

    @Override
    public int getOrder() {
        return -175;
    }
}
