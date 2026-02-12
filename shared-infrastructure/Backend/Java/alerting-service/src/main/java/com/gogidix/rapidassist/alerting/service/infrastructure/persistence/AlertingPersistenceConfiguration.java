package com.gogidix.rapidassist.alerting.service.infrastructure.persistence;

import com.gogidix.rapidassist.alerting.service.domain.port.out.AlertEventStore;
import com.gogidix.rapidassist.alerting.service.domain.port.out.AlertRuleStore;
import com.gogidix.rapidassist.alerting.service.infrastructure.persistence.mongo.MongoAlertEventStore;
import com.gogidix.rapidassist.alerting.service.infrastructure.persistence.mongo.MongoAlertRuleStore;
import com.gogidix.rapidassist.alerting.service.infrastructure.persistence.noop.NoOpAlertEventStore;
import com.gogidix.rapidassist.alerting.service.infrastructure.persistence.noop.NoOpAlertRuleStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class AlertingPersistenceConfiguration {

    @Bean
    @ConditionalOnClass(MongoTemplate.class)
    @ConditionalOnProperty(prefix = "gogidix.alerting", name = "mongo-enabled", havingValue = "true", matchIfMissing = false)
    public AlertRuleStore mongoAlertRuleStore(MongoTemplate template) {
        return new MongoAlertRuleStore(template);
    }

    @Bean
    @ConditionalOnClass(MongoTemplate.class)
    @ConditionalOnProperty(prefix = "gogidix.alerting", name = "mongo-enabled", havingValue = "true", matchIfMissing = false)
    public AlertEventStore mongoAlertEventStore(MongoTemplate template) {
        return new MongoAlertEventStore(template);
    }

    @Bean
    @ConditionalOnMissingBean
    public AlertRuleStore noOpAlertRuleStore() {
        return new NoOpAlertRuleStore();
    }

    @Bean
    @ConditionalOnMissingBean
    public AlertEventStore noOpAlertEventStore() {
        return new NoOpAlertEventStore();
    }
}
