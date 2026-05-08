package com.gogidix.rapidassist.event.audit.service.infrastructure.persistence;

import com.gogidix.rapidassist.event.audit.service.domain.port.out.AuditEventStore;
import com.gogidix.rapidassist.event.audit.service.infrastructure.persistence.mongo.MongoAuditEventStore;
import com.gogidix.rapidassist.event.audit.service.infrastructure.persistence.noop.NoOpAuditEventStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class AuditPersistenceConfiguration {

    @Bean
    @ConditionalOnClass(MongoTemplate.class)
    @ConditionalOnProperty(prefix = "gogidix.audit", name = "mongo-enabled", havingValue = "true", matchIfMissing = false)
    public AuditEventStore mongoAuditEventStore(MongoTemplate template) {
        return new MongoAuditEventStore(template);
    }

    @Bean
    @ConditionalOnMissingBean
    public AuditEventStore noOpAuditEventStore() {
        return new NoOpAuditEventStore();
    }
}
