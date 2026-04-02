package com.gogidix.rapidassist.idempotency.service.infrastructure.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.idempotency.service.domain.port.out.IdempotencyStore;
import com.gogidix.rapidassist.idempotency.service.infrastructure.persistence.memory.InMemoryIdempotencyStore;
import com.gogidix.rapidassist.idempotency.service.infrastructure.persistence.redis.RedisIdempotencyStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class IdempotencyPersistenceConfiguration {

    @Bean
    @ConditionalOnClass(RedisTemplate.class)
    @ConditionalOnProperty(name = "gogidix.idempotency.persistence.type", havingValue = "redis", matchIfMissing = true)
    public IdempotencyStore redisIdempotencyStore(RedisTemplate<String, String> redisTemplate,
                                                  ObjectMapper objectMapper) {
        return new RedisIdempotencyStore(redisTemplate, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(IdempotencyStore.class)
    @ConditionalOnProperty(name = "gogidix.idempotency.persistence.type", havingValue = "memory")
    public IdempotencyStore inMemoryIdempotencyStore() {
        return new InMemoryIdempotencyStore();
    }
}