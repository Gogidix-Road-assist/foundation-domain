package com.gogidix.rapidassist.config.service.adapters.in.web;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

/**
 * Test security configuration that disables security for tests.
 * This allows us to test controllers without dealing with full authentication setup.
 *
 * <p>Note: This configuration uses @Primary to override the main SecurityConfig during tests.
 * It does not use @EnableWebSecurity to avoid conflicts with the main security configuration.
 */
@TestConfiguration
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
