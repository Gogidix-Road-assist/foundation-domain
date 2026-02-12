package com.gogidix.rapidassist.api.gateway.infrastructure.ratelimit;

import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public interface RateLimitPolicy {

    Mono<RateLimitDecision> evaluate(ServerWebExchange exchange);
}
