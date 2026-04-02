package com.gogidix.rapidassist.rate.limiting.service.infrastructure.persistence;

import com.gogidix.rapidassist.rate.limiting.service.domain.port.out.RateLimitCounterStore;
import com.gogidix.rapidassist.rate.limiting.service.infrastructure.persistence.comprehensive.ComprehensiveRateLimitCounterStore;
import com.gogidix.rapidassist.rate.limiting.service.infrastructure.persistence.mongo.MongoRateLimitCounterStore;
import com.gogidix.rapidassist.rate.limiting.service.infrastructure.persistence.noop.NoOpRateLimitCounterStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableConfigurationProperties(RateLimitingProperties.class)
public class RateLimitingPersistenceConfiguration {

    @Bean
    @ConditionalOnProperty(name = "gogidix.rate-limiting.provider.type", havingValue = "noop", matchIfMissing = false)
    public RateLimitCounterStore noOpRateLimitCounterStore() {
        return new NoOpRateLimitCounterStore();
    }

    @Bean
    @ConditionalOnClass(RedisTemplate.class)
    @ConditionalOnProperty(name = "gogidix.rate-limiting.provider.type", havingValue = "comprehensive", matchIfMissing = true)
    public RateLimitCounterStore comprehensiveRateLimitCounterStore(RedisTemplate<String, String> redisTemplate,
                                                                   RateLimitingProperties properties) {
        return new ComprehensiveRateLimitCounterStore(redisTemplate, properties);
    }

    @Bean
    @ConditionalOnClass(MongoTemplate.class)
    @ConditionalOnProperty(name = "gogidix.rate-limiting.mongo-enabled", havingValue = "true")
    public RateLimitCounterStore mongoRateLimitCounterStore(MongoTemplate mongoTemplate) {
        return new MongoRateLimitCounterStore(mongoTemplate);
    }
}
