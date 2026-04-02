package com.gogidix.rapidassist.session.token.service.infrastructure.provider;

import com.gogidix.rapidassist.session.token.service.domain.port.out.SessionTokenProvider;
import com.gogidix.rapidassist.session.token.service.infrastructure.provider.comprehensive.ComprehensiveSessionTokenProvider;
import com.gogidix.rapidassist.session.token.service.infrastructure.provider.noop.NoOpSessionTokenProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
@EnableConfigurationProperties(SessionTokenProperties.class)
public class SessionTokenProviderConfiguration {

    @Bean
    @ConditionalOnProperty(name = "gogidix.session-token.provider.type", havingValue = "noop", matchIfMissing = false)
    public SessionTokenProvider noOpSessionTokenProvider() {
        return new NoOpSessionTokenProvider();
    }

    @Bean
    @ConditionalOnClass(RedisTemplate.class)
    @ConditionalOnProperty(name = "gogidix.session-token.provider.type", havingValue = "jwt", matchIfMissing = true)
    public SessionTokenProvider comprehensiveSessionTokenProvider(RedisTemplate<String, String> redisTemplate,
                                                                SessionTokenProperties properties) {
        return new ComprehensiveSessionTokenProvider(redisTemplate, properties);
    }
}
