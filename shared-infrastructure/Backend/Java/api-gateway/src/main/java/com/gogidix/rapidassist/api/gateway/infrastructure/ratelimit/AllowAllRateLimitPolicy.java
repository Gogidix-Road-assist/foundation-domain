package com.gogidix.rapidassist.api.gateway.infrastructure.ratelimit;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class AllowAllRateLimitPolicy implements RateLimitPolicy {

    @Override
    public Mono<RateLimitDecision> evaluate(ServerWebExchange exchange) {
        return Mono.just(RateLimitDecision.allow());
    }
}
