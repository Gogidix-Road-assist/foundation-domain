package com.gogidix.rapidassist.config.service.infrastructure.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.core.userdetails.User.builder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri:}")
    private String issuerUri;

    @Value("${app.security.cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private List<String> allowedOrigins;

    @Value("${app.security.cors.allowed-methods:GET,POST,PUT,DELETE,OPTIONS}")
    private List<String> allowedMethods;

    @Value("${app.security.cors.allowed-headers:*}")
    private List<String> allowedHeaders;

    @Value("${app.security.cors.allow-credentials:true}")
    private Boolean allowCredentials;

    @Value("${app.security.cors.max-age:3600}")
    private Long corsMaxAge;

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(allowedMethods);
        configuration.setAllowedHeaders(allowedHeaders);
        configuration.setAllowCredentials(allowCredentials);
        configuration.setMaxAge(corsMaxAge);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(mvcMatcherBuilder.pattern("/actuator/health")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/actuator/info")).permitAll()
                .requestMatchers(mvcMatcherBuilder.pattern("/api/v1/configurations/**")).hasAnyRole("CONFIG_READER", "CONFIG_ADMIN", "CONFIG_APPROVER")
                .requestMatchers(mvcMatcherBuilder.pattern("/api/v1/status/**")).permitAll()
                .anyRequest().authenticated()
            );

        if (!issuerUri.isEmpty()) {
            http
                .oauth2ResourceServer(oauth2 -> oauth2
                    .jwt(jwt -> jwt
                        .decoder(jwtDecoder())
                        .jwtAuthenticationConverter(jwtAuthenticationConverter())
                    )
                );
        } else {
            http
                .httpBasic(withDefaults())
                .userDetailsService(userDetailsService());
        }

        return http.build();
    }

    @Value("${app.security.users.admin.username:config-admin}")
    private String adminUsername;

    @Value("${app.security.users.admin.password:#{null}}")
    private String adminPassword;

    @Value("${app.security.users.reader.username:config-reader}")
    private String readerUsername;

    @Value("${app.security.users.reader.password:#{null}}")
    private String readerPassword;

    @Value("${app.security.users.approver.username:config-approver}")
    private String approverUsername;

    @Value("${app.security.users.approver.password:#{null}}")
    private String approverPassword;

    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        // Only add users if passwords are configured (not null and not empty)
        if (adminPassword != null && !adminPassword.isBlank()) {
            manager.createUser(builder()
                .username(adminUsername)
                .password(passwordEncoder().encode(adminPassword))
                .roles("CONFIG_ADMIN")
                .build());
        }

        if (readerPassword != null && !readerPassword.isBlank()) {
            manager.createUser(builder()
                .username(readerUsername)
                .password(passwordEncoder().encode(readerPassword))
                .roles("CONFIG_READER")
                .build());
        }

        if (approverPassword != null && !approverPassword.isBlank()) {
            manager.createUser(builder()
                .username(approverUsername)
                .password(passwordEncoder().encode(approverPassword))
                .roles("CONFIG_APPROVER")
                .build());
        }

        return manager;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return issuerUri.isEmpty() ? null : JwtDecoders.fromIssuerLocation(issuerUri);
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("ROLE_");
        authoritiesConverter.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}