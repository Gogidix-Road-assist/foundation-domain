package com.gogidix.shared.infrastructure.config;

import com.gogidix.shared.infrastructure.security.JwtAuthenticationFilter;
import com.gogidix.shared.infrastructure.security.UnauthorizedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security Configuration for AI Services
 *
 * Features:
 * - JWT-based authentication
 * - State REST API (no sessions)
 * - Public health check endpoints
 * - Role-based access control (RBAC) support
 * - CORS configuration
 *
 * Usage: Copy this class to each service's infrastructure config package
 * and update the package declaration accordingly.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UnauthorizedHandler unauthorizedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                         UnauthorizedHandler unauthorizedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.unauthorizedHandler = unauthorizedHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for stateless API
                .csrf(AbstractHttpConfigurer::disable)

                // Configure CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // Configure session management - stateless
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // Configure authorization rules
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints - health checks and actuator
                        .requestMatchers(
                                "/actuator/health/**",
                                "/actuator/info",
                                "/status",
                                "/api/*/health",
                                "/api/*/status",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Public API documentation in dev/staging environments
                        .requestMatchers("/swagger**", "/v3/api-docs/**").permitAll()

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )

                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)

                // Configure exception handling
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(unauthorizedHandler)
                        .accessDeniedHandler(unauthorizedHandler)
                );

        return http.build();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();

        // Allow credentials
        configuration.setAllowCredentials(true);

        // Allowed origins from environment or default to localhost
        String allowedOriginsEnv = System.getenv().getOrDefault("ALLOWED_ORIGINS",
                "http://localhost:3000,http://localhost:8080,http://localhost:4200");

        configuration.setAllowedOrigins(java.util.Arrays.asList(allowedOriginsEnv.split(",")));

        // Allowed methods
        configuration.setAllowedMethods(java.util.Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // Allowed headers
        configuration.setAllowedHeaders(java.util.Arrays.asList("*"));

        // Exposed headers
        configuration.setExposedHeaders(java.util.Arrays.asList(
                "X-Total-Count", "X-Trace-Id", "Authorization"
        ));

        // Max age for preflight requests
        configuration.setMaxAge(3600L);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
