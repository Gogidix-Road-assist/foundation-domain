package com.gogidix.rapidassist.policy.configuration.service.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Security configuration for Policy Configuration Service
 * Provides OAuth2 JWT resource server configuration with role-based access control
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final String[] ACTUATOR_ENDPOINTS = {
            "/actuator/health",
            "/actuator/info",
            "/actuator/prometheus"
    };

    private static final String[] SWAGGER_ENDPOINTS = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html"
    };

    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/policies/health",
            "/error"
    };

    /**
     * Configure security filter chain with OAuth2 JWT resource server
     *
     * @param http HttpSecurity configuration
     * @return SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless API
            .csrf(AbstractHttpConfigurer::disable)

            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers(ACTUATOR_ENDPOINTS).permitAll()
                .requestMatchers(SWAGGER_ENDPOINTS).permitAll()
                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()

                // Admin endpoints require ADMIN role
                .requestMatchers(HttpMethod.DELETE, "/api/policies/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/policies/**").hasAnyRole("ADMIN", "MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/policies/**").hasAnyRole("ADMIN", "MANAGER")

                // Read endpoints require authenticated user
                .requestMatchers(HttpMethod.GET, "/api/policies/**").authenticated()

                // All other requests require authentication
                .anyRequest().authenticated()
            )

            // Enable OAuth2 resource server with JWT
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(withDefaults())
            )

            // Configure CORS (see CorsConfig for more details)
            .cors(withDefaults());

        return http.build();
    }

    /**
     * Password encoder for hashing passwords
     * Uses BCrypt with default strength (10)
     *
     * @return PasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
