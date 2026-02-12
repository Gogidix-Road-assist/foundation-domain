package com.gogidix.rapidassist.billing.service.infrastructure.persistence;

import com.gogidix.rapidassist.billing.service.domain.port.out.BillingAccountStore;
import com.gogidix.rapidassist.billing.service.infrastructure.persistence.memory.InMemoryBillingAccountStore;
import com.gogidix.rapidassist.billing.service.infrastructure.persistence.mongodb.MongoBillingAccountStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class BillingPersistenceConfiguration {

    @Bean
    @ConditionalOnClass({RedisTemplate.class, MongoRepository.class})
    @ConditionalOnProperty(name = "gogidix.billing.persistence.type", havingValue = "mongodb", matchIfMissing = true)
    public BillingAccountStore mongoBillingAccountStore(com.gogidix.rapidassist.billing.service.infrastructure.persistence.mongodb.MongoBillingAccountRepository repository,
                                                        RedisTemplate<String, String> redisTemplate) {
        return new MongoBillingAccountStore(repository, redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(BillingAccountStore.class)
    @ConditionalOnProperty(name = "gogidix.billing.persistence.type", havingValue = "memory")
    public BillingAccountStore inMemoryBillingAccountStore() {
        return new InMemoryBillingAccountStore();
    }
}