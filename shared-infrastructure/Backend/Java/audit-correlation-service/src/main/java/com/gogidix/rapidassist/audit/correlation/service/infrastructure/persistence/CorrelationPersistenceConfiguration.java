package com.gogidix.rapidassist.audit.correlation.service.infrastructure.persistence;

import com.gogidix.rapidassist.audit.correlation.service.domain.port.out.CorrelationStore;
import com.gogidix.rapidassist.audit.correlation.service.infrastructure.persistence.mongo.MongoCorrelationStore;
import com.gogidix.rapidassist.audit.correlation.service.infrastructure.persistence.noop.NoOpCorrelationStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class CorrelationPersistenceConfiguration {

    @Bean
    @ConditionalOnClass(MongoTemplate.class)
    @ConditionalOnProperty(prefix = "gogidix.correlation", name = "mongo-enabled", havingValue = "true", matchIfMissing = false)
    public CorrelationStore mongoCorrelationStore(MongoTemplate template) {
        return new MongoCorrelationStore(template);
    }

    @Bean
    @ConditionalOnMissingBean
    public CorrelationStore noOpCorrelationStore() {
        return new NoOpCorrelationStore();
    }
}
