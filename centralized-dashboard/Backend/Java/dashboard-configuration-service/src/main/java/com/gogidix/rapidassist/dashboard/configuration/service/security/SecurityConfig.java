package com.gogidix.rapidassist.dashboard.configuration.service.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for the Dashboard Configuration Service.
 *
 * <p>This class configures Spring Security for JWT-based authentication and
 * role-based authorization. It sets up the security filter chain with JWT
 * authentication and specifies which endpoints require authentication.</p>
 *
 * <p>Note: This is a basic security configuration. For production use, consider:</p>
 * <ul>
 *   <li>Implementing proper JWT validation and user details loading</li>
 *   <li>Adding role-based access control (RBAC)</li>
 *   <li>Configuring rate limiting</li>
 *   <li>Adding security headers (Content-Security-Policy, X-Frame-Options, etc.)</li>
 *   <li>Integrating with a centralized identity provider</li>
 * </ul>
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] PUBLIC_ENDPOINTS = {
        "/api/dashboards/health",
        "/actuator/**",
        "/swagger-ui/**",
        "/v3/api-docs/**",
        "/swagger-resources/**",
        "/webjars/**"
    };

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Configures the security filter chain.
     *
     * <p>Public endpoints are accessible without authentication. All other endpoints
     * require JWT authentication. Sessions are stateless.</p>
     *
     * @param http the HttpSecurity to configure
     * @return the configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
