package com.gogidix.rapidassist.waf.policy.service.infrastructure.policy;

import com.gogidix.rapidassist.waf.policy.service.domain.port.out.WafPolicyEngine;
import com.gogidix.rapidassist.waf.policy.service.infrastructure.policy.noop.NoOpWafPolicyEngine;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WafPolicyConfiguration {

    @Bean
    @ConditionalOnMissingBean(WafPolicyEngine.class)
    public WafPolicyEngine noOpWafPolicyEngine() {
        return new NoOpWafPolicyEngine();
    }
}
