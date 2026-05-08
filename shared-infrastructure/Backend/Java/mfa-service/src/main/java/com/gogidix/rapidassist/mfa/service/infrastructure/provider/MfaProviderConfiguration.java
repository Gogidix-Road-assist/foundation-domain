package com.gogidix.rapidassist.mfa.service.infrastructure.provider;

import com.gogidix.rapidassist.mfa.service.domain.port.out.MfaProvider;
import com.gogidix.rapidassist.mfa.service.infrastructure.provider.comprehensive.ComprehensiveMfaProvider;
import com.gogidix.rapidassist.mfa.service.infrastructure.provider.noop.NoOpMfaProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
@EnableConfigurationProperties(MfaProperties.class)
public class MfaProviderConfiguration {

    @Bean
    @ConditionalOnProperty(name = "gogidix.mfa.provider.type", havingValue = "noop", matchIfMissing = false)
    public MfaProvider noOpMfaProvider() {
        return new NoOpMfaProvider();
    }

    @Bean
    @ConditionalOnProperty(name = "gogidix.mfa.provider.type", havingValue = "comprehensive", matchIfMissing = true)
    public MfaProvider comprehensiveMfaProvider(MongoTemplate mongoTemplate,
                                                MfaProperties properties) {
        return new ComprehensiveMfaProvider(mongoTemplate, properties);
    }
}
