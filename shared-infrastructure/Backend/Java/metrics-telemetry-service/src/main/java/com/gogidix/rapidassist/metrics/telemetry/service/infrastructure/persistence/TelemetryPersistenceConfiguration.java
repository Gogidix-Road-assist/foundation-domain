package com.gogidix.rapidassist.metrics.telemetry.service.infrastructure.persistence;

import com.gogidix.rapidassist.metrics.telemetry.service.domain.port.out.TelemetryEventStore;
import com.gogidix.rapidassist.metrics.telemetry.service.infrastructure.persistence.mongo.MongoTelemetryEventStore;
import com.gogidix.rapidassist.metrics.telemetry.service.infrastructure.persistence.noop.NoOpTelemetryEventStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class TelemetryPersistenceConfiguration {

    @Bean
    @ConditionalOnClass(MongoTemplate.class)
    @ConditionalOnProperty(prefix = "gogidix.telemetry", name = "mongo-enabled", havingValue = "true", matchIfMissing = false)
    public TelemetryEventStore mongoTelemetryEventStore(MongoTemplate template) {
        return new MongoTelemetryEventStore(template);
    }

    @Bean
    @ConditionalOnMissingBean
    public TelemetryEventStore noOpTelemetryEventStore() {
        return new NoOpTelemetryEventStore();
    }
}
