package com.gogidix.rapidassist.logging.aggregation.service.infrastructure.persistence;

import com.gogidix.rapidassist.logging.aggregation.service.domain.port.out.LogEventStore;
import com.gogidix.rapidassist.logging.aggregation.service.infrastructure.persistence.mongo.MongoLogEventStore;
import com.gogidix.rapidassist.logging.aggregation.service.infrastructure.persistence.noop.NoOpLogEventStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class LoggingPersistenceConfiguration {

    @Bean
    @ConditionalOnClass(MongoTemplate.class)
    @ConditionalOnProperty(prefix = "gogidix.logging", name = "mongo-enabled", havingValue = "true", matchIfMissing = false)
    public LogEventStore mongoLogEventStore(MongoTemplate template) {
        return new MongoLogEventStore(template);
    }

    @Bean
    @ConditionalOnMissingBean
    public LogEventStore noOpLogEventStore() {
        return new NoOpLogEventStore();
    }
}
