package com.gogidix.rapidassist.config.service.config;

import com.gogidix.rapidassist.config.service.domain.port.out.ConfigurationCacheStore;
import com.gogidix.rapidassist.config.service.infrastructure.persistence.noop.NoOpConfigurationCacheStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration for cache store when Redis is not available.
 * This configuration provides a NoOp cache implementation as a fallback for the
 * redisConfigurationCacheStore bean that would normally be provided by RedisAutoConfiguration.
 *
 * <p>When gogidix.config.cache.provider is set to "noop", the RedisConfigurationCacheStore
 * is not created due to its @ConditionalOnProperty annotation. This configuration provides
 * a fallback bean with the same name that delegates to NoOpConfigurationCacheStore.
 */
@Configuration
@Profile("test")
public class TestCacheConfiguration {

    /**
     * Provides a fallback cache store bean named "redisConfigurationCacheStore" when
     * Redis is disabled (gogidix.config.cache.provider=noop).
     * This allows services that @Autowired the redisConfigurationCacheStore by name to work.
     *
     * @param noOpCacheStore the NoOp implementation to delegate to
     * @return a wrapper that delegates to NoOpConfigurationCacheStore
     */
    @Bean
    @Primary
    @org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
        name = "gogidix.config.cache.provider",
        havingValue = "noop"
    )
    public ConfigurationCacheStore redisConfigurationCacheStore(NoOpConfigurationCacheStore noOpCacheStore) {
        // Return the NoOp cache store as a substitute for Redis cache
        return noOpCacheStore;
    }
}
