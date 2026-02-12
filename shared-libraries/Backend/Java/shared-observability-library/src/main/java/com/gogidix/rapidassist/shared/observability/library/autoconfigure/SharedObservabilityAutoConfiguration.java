package com.gogidix.rapidassist.shared.observability.library.autoconfigure;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@AutoConfiguration
@ConditionalOnClass(MeterRegistry.class)
public class SharedObservabilityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public MeterRegistryCustomizer<MeterRegistry> gogidixCommonTagsMeterRegistryCustomizer(Environment environment) {
        String appName = environment.getProperty("spring.application.name", "unknown-service");
        return (registry) -> registry.config().commonTags("application", appName);
    }

    @Bean
    public MeterFilter denyMetricsFromEndpointsWebMvcIfPresent(ObjectProvider<MeterFilter> existing) {
        // no-op placeholder that avoids adding conflicting filters; keeps API extensible
        return MeterFilter.accept();
    }
}
