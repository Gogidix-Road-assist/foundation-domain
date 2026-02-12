package com.gogidix.rapidassist.service.health.monitor.service.infrastructure.persistence;

import com.gogidix.rapidassist.service.health.monitor.service.domain.port.out.ServiceHealthStore;
import com.gogidix.rapidassist.service.health.monitor.service.infrastructure.persistence.mongo.MongoServiceHealthStore;
import com.gogidix.rapidassist.service.health.monitor.service.infrastructure.persistence.noop.NoOpServiceHealthStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class ServiceHealthPersistenceConfiguration {

    @Bean
    @ConditionalOnClass(MongoTemplate.class)
    @ConditionalOnProperty(prefix = "gogidix.service-health", name = "mongo-enabled", havingValue = "true", matchIfMissing = false)
    public ServiceHealthStore mongoServiceHealthStore(MongoTemplate template) {
        return new MongoServiceHealthStore(template);
    }

    @Bean
    @ConditionalOnMissingBean
    public ServiceHealthStore noOpServiceHealthStore() {
        return new NoOpServiceHealthStore();
    }
}
