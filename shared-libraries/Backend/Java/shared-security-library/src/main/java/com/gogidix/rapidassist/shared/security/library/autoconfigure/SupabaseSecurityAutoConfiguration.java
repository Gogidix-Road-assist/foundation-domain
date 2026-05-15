package com.gogidix.rapidassist.shared.security.library.autoconfigure;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@AutoConfiguration
@EnableConfigurationProperties(SupabaseSecurityProperties.class)
public class SupabaseSecurityAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain supabaseSecurityFilterChain(
            HttpSecurity http,
            SupabaseSecurityProperties props,
            ObjectProvider<JwtDecoder> jwtDecoderProvider
    ) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        String issuerUri = props.getIssuerUri();
        if (issuerUri == null || issuerUri.isBlank()) {
            http.authorizeHttpRequests((authz) -> authz.anyRequest().permitAll());
            return http.build();
        }

        http.authorizeHttpRequests((authz) -> authz
                .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
                .anyRequest().authenticated());
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(new SupabaseJwtAuthoritiesConverter(props));

        http.oauth2ResourceServer((oauth2) -> oauth2.jwt((jwt) -> jwt
                .decoder(jwtDecoderProvider.getObject())
                .jwtAuthenticationConverter(authenticationConverter)));

        return http.build();
    }

    @Bean
    @ConditionalOnProperty(prefix = "gogidix.security.supabase", name = "issuer-uri")
    public JwtDecoder jwtDecoder(SupabaseSecurityProperties props) {
        String issuerUri = props.getIssuerUri();
        if (issuerUri == null || issuerUri.isBlank()) {
            return token -> {
                throw new org.springframework.security.oauth2.jwt.JwtValidationException(
                        "Supabase issuer-uri not configured", java.util.List.of());
            };
        }
        JwtDecoder base = JwtDecoders.fromIssuerLocation(issuerUri);

        if (base instanceof NimbusJwtDecoder nimbus) {
            OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
            OAuth2TokenValidator<Jwt> withAudience = new AudienceValidator(props.getAudience());
            nimbus.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience));
            return nimbus;
        }

        return base;
    }
}
