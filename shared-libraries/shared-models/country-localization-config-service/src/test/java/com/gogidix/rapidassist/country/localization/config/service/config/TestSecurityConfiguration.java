package com.gogidix.rapidassist.country.localization.config.service.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Test security configuration that disables authentication for actuator endpoints.
 * This allows smoke tests to access health and info endpoints without credentials.
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfiguration {

    @Bean
    public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/actuator/**").permitAll()
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
