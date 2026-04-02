package com.gogidix.rapidassist.ai.nlp.processing.infrastructure.security;

import com.gogidix.rapidassist.shared.security.library.jwt.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Security configuration for NLP Processing Service.
 * Uses JWT-based stateless authentication.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtTokenUtil jwtTokenUtil;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Disable CSRF for stateless API
            .csrf(AbstractHttpConfigurer::disable)

            // Configure CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Configure session management - stateless
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Configure authorization rules
            .authorizeHttpRequests(auth -> auth
                // Permit actuator endpoints for health checks
                .requestMatchers("/actuator/**").permitAll()

                // Permit OpenAPI/Swagger endpoints
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs/**").permitAll()

                // Permit all other endpoints (tenant isolation handled by interceptor)
                .anyRequest().permitAll()
            )

            // Add JWT filter
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenUtil),
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration =
            new org.springframework.web.cors.CorsConfiguration();

        configuration.setAllowedOriginPatterns(java.util.List.of("*"));
        configuration.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.List.of(
            "Authorization",
            "Content-Type",
            "Accept",
            "X-Requested-With",
            "X-Tenant-ID",
            "X-Organization-ID",
            "X-Request-ID",
            "X-Correlation-ID"
        ));
        configuration.setExposedHeaders(java.util.List.of(
            "X-Total-Count",
            "X-Correlation-ID",
            "X-Tenant-ID"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
            new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
