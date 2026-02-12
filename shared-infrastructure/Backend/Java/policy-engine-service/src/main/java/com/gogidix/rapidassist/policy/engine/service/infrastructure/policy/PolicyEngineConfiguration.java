package com.gogidix.rapidassist.policy.engine.service.infrastructure.policy;

import com.gogidix.rapidassist.policy.engine.service.domain.port.out.PolicyEvaluator;
import com.gogidix.rapidassist.policy.engine.service.infrastructure.policy.comprehensive.ComprehensivePolicyEvaluator;
import com.gogidix.rapidassist.policy.engine.service.infrastructure.policy.noop.NoOpPolicyEvaluator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableConfigurationProperties(PolicyEngineProperties.class)
public class PolicyEngineConfiguration {

    @Bean
    @ConditionalOnProperty(name = "gogidix.policy-engine.provider.type", havingValue = "noop", matchIfMissing = false)
    public PolicyEvaluator noOpPolicyEvaluator() {
        return new NoOpPolicyEvaluator();
    }

    @Bean
    @ConditionalOnProperty(name = "gogidix.policy-engine.provider.type", havingValue = "comprehensive", matchIfMissing = true)
    public PolicyEvaluator comprehensivePolicyEvaluator(MongoTemplate mongoTemplate,
                                                       PolicyEngineProperties properties) {
        return new ComprehensivePolicyEvaluator(mongoTemplate, properties);
    }
}
