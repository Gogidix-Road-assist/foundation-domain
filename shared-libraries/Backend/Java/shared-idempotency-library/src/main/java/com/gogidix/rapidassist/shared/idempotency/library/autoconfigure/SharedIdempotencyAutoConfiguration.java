package com.gogidix.rapidassist.shared.idempotency.library.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.shared.idempotency.library.domain.policy.IdempotencyPolicy;
import com.gogidix.rapidassist.shared.idempotency.library.domain.policy.IdempotencyRequestHasher;
import com.gogidix.rapidassist.shared.idempotency.library.domain.port.out.IdempotencyStore;
import com.gogidix.rapidassist.shared.idempotency.library.infrastructure.database.DatabaseIdempotencyStore;
import com.gogidix.rapidassist.shared.idempotency.library.infrastructure.hash.DefaultIdempotencyRequestHasher;
import com.gogidix.rapidassist.shared.idempotency.library.infrastructure.metrics.IdempotencyMetrics;
import com.gogidix.rapidassist.shared.idempotency.library.infrastructure.noop.NoOpIdempotencyStore;
import com.gogidix.rapidassist.shared.idempotency.library.infrastructure.policy.DefaultIdempotencyPolicy;
import com.gogidix.rapidassist.shared.idempotency.library.infrastructure.redis.RedisIdempotencyStore;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;

@AutoConfiguration
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
@EnableConfigurationProperties(IdempotencyProperties.class)
public class SharedIdempotencyAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "gogidix.idempotency", name = "store", havingValue = "redis", matchIfMissing = false)
    @ConditionalOnClass(RedisTemplate.class)
    public IdempotencyStore redisIdempotencyStore(
            RedisTemplate<String, Object> redisTemplate,
            ObjectMapper objectMapper,
            IdempotencyProperties properties) {
        return new RedisIdempotencyStore(redisTemplate, objectMapper, properties.getTtl());
    }

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "gogidix.idempotency", name = "store", havingValue = "database", matchIfMissing = false)
    public IdempotencyStore databaseIdempotencyStore(
            Object repository,
            IdempotencyProperties properties) {
        // Cast to IdempotencyKeyRepository - this only executes when the bean is created
        // (after @ConditionalOnProperty check passes, meaning JPA is on the classpath)
        com.gogidix.rapidassist.shared.idempotency.library.infrastructure.database.IdempotencyKeyRepository repo =
                (com.gogidix.rapidassist.shared.idempotency.library.infrastructure.database.IdempotencyKeyRepository) repository;
        return new DatabaseIdempotencyStore(repo, properties.getTtl());
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotencyStore idempotencyStore() {
        return new NoOpIdempotencyStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotencyRequestHasher idempotencyRequestHasher() {
        return new DefaultIdempotencyRequestHasher();
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotencyMetrics idempotencyMetrics(MeterRegistry meterRegistry) {
        return new IdempotencyMetrics(meterRegistry);
    }

    @Bean
    @ConditionalOnMissingBean
    public IdempotencyPolicy idempotencyPolicy(IdempotencyProperties properties) {
        return new DefaultIdempotencyPolicy(properties);
    }

    @Bean
    public FilterRegistrationBean<IdempotencyKeyFilter> gogidixIdempotencyKeyFilter(IdempotencyProperties properties) {
        FilterRegistrationBean<IdempotencyKeyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new IdempotencyKeyFilter(properties));
        registrationBean.setOrder(-90);
        return registrationBean;
    }
}
