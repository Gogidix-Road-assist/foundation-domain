package com.gogidix.rapidassist.shared.cors.config.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.web.filter.CorsFilter;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for {@link SharedCorsAutoConfiguration}.
 */
class SharedCorsAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SharedCorsAutoConfiguration.class));

    @Test
    void shouldLoadCorsPropertiesWithDefaults() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(CorsProperties.class);
            assertThat(context).hasSingleBean(CorsFilter.class);

            CorsProperties properties = context.getBean(CorsProperties.class);
            assertThat(properties.getAllowedOrigins()).isEqualTo("http://localhost:3000,http://localhost:8080,https://rapidassist.gogidix.com");
            assertThat(properties.getAllowedMethods()).containsExactly("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");
            assertThat(properties.getAllowCredentials()).isTrue();
            assertThat(properties.getMaxAge()).isEqualTo(3600L);
            assertThat(properties.getEnabled()).isTrue();
        });
    }

    @Test
    void shouldCreateCorsFilterBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(CorsFilter.class);
            assertThat(context).hasBean("gogidixCorsFilter");
        });
    }

    @Test
    void shouldApplyCustomConfiguration() {
        contextRunner
                .withPropertyValues(
                    "gogidix.cors.allowed-origins=http://test.example.com,http://localhost:3000",
                    "gogidix.cors.allowed-methods=GET,POST,PUT",
                    "gogidix.cors.allow-credentials=true",
                    "gogidix.cors.max-age=7200"
                )
                .run(context -> {
                    CorsProperties properties = context.getBean(CorsProperties.class);
                    assertThat(properties.getAllowedOrigins()).isEqualTo("http://test.example.com,http://localhost:3000");
                    assertThat(properties.getAllowedMethods()).containsExactly("GET", "POST", "PUT");
                    assertThat(properties.getAllowCredentials()).isTrue();
                    assertThat(properties.getMaxAge()).isEqualTo(7200L);
                });
    }

    @Test
    void shouldDisableCorsWhenDisabled() {
        contextRunner
                .withPropertyValues("gogidix.cors.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(CorsFilter.class);
                });
    }
}
