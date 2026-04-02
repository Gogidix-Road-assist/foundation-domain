package com.gogidix.rapidassist.payment.service.infrastructure.persistence;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gogidix.rapidassist.payment.service.domain.port.out.PaymentIntentStore;
import com.gogidix.rapidassist.payment.service.infrastructure.persistence.memory.InMemoryPaymentIntentStore;
import com.gogidix.rapidassist.payment.service.infrastructure.persistence.mongodb.MongoPaymentIntentStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class PaymentPersistenceConfiguration {

    @Bean
    @ConditionalOnClass({RedisTemplate.class, MongoRepository.class})
    @ConditionalOnProperty(name = "gogidix.payment.persistence.type", havingValue = "mongodb", matchIfMissing = true)
    public PaymentIntentStore mongoPaymentIntentStore(com.gogidix.rapidassist.payment.service.infrastructure.persistence.mongodb.PaymentIntentRepository repository,
                                                      RedisTemplate<String, String> redisTemplate,
                                                      ObjectMapper objectMapper) {
        return new MongoPaymentIntentStore(repository, redisTemplate, objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(PaymentIntentStore.class)
    @ConditionalOnProperty(name = "gogidix.payment.persistence.type", havingValue = "memory")
    public PaymentIntentStore inMemoryPaymentIntentStore() {
        return new InMemoryPaymentIntentStore();
    }
}