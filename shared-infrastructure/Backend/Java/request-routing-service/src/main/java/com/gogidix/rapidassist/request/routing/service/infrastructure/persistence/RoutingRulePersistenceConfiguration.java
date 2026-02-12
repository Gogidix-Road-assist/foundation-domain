package com.gogidix.rapidassist.request.routing.service.infrastructure.persistence;

import com.gogidix.rapidassist.request.routing.service.domain.port.out.RoutingRuleStore;
import com.gogidix.rapidassist.request.routing.service.infrastructure.persistence.memory.InMemoryRoutingRuleStore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RoutingRulePersistenceConfiguration {

    @Bean
    @ConditionalOnMissingBean(RoutingRuleStore.class)
    public RoutingRuleStore inMemoryRoutingRuleStore() {
        return new InMemoryRoutingRuleStore();
    }
}
