package com.gogidix.rapidassist.access.control.service.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration: SecurityConfig
 *
 * Configures Spring Security for the access-control service.
 * Uses stateless session management for API-first architecture.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @SuppressWarnings("unused")
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @SuppressWarnings("unused")
    private final TenantInterceptor tenantInterceptor;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          TenantInterceptor tenantInterceptor) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.tenantInterceptor = tenantInterceptor;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                    .requestMatchers("/api/v1/status").permitAll()
                    .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()
                    .anyRequest().authenticated()
            );

        // Add JWT filter
        // http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
