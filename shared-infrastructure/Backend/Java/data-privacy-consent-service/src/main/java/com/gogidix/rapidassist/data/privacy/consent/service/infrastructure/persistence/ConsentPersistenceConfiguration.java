package com.gogidix.rapidassist.data.privacy.consent.service.infrastructure.persistence;

import com.gogidix.rapidassist.data.privacy.consent.service.domain.port.out.ConsentStore;
import com.gogidix.rapidassist.data.privacy.consent.service.infrastructure.persistence.memory.InMemoryConsentStore;
import com.gogidix.rapidassist.data.privacy.consent.service.infrastructure.persistence.mongodb.MongoConsentStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class ConsentPersistenceConfiguration {

    @Bean
    @ConditionalOnClass({RedisTemplate.class, MongoRepository.class})
    @ConditionalOnProperty(name = "gogidix.consent.persistence.type", havingValue = "mongodb", matchIfMissing = true)
    public ConsentStore mongoConsentStore(com.gogidix.rapidassist.data.privacy.consent.service.infrastructure.persistence.mongodb.ConsentPreferencesRepository repository,
                                          RedisTemplate<String, String> redisTemplate) {
        return new MongoConsentStore(repository, redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(ConsentStore.class)
    @ConditionalOnProperty(name = "gogidix.consent.persistence.type", havingValue = "memory")
    public ConsentStore inMemoryConsentStore() {
        return new InMemoryConsentStore();
    }
}