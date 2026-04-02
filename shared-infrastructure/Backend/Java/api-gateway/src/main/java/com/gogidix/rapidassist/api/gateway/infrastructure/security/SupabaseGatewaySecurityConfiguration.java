package com.gogidix.rapidassist.api.gateway.infrastructure.security;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.util.StringUtils;

@Configuration
@EnableWebFluxSecurity
@EnableConfigurationProperties(SupabaseSecurityProperties.class)
public class SupabaseGatewaySecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(
            ServerHttpSecurity http,
            SupabaseSecurityProperties props,
            ObjectProvider<ReactiveJwtDecoder> jwtDecoderProvider
    ) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        String issuerUri = props.getIssuerUri();
        if (!StringUtils.hasText(issuerUri)) {
            http.authorizeExchange((ex) -> ex.anyExchange().permitAll());
            return http.build();
        }

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new SupabaseJwtAuthoritiesConverter(props));

        http.authorizeExchange((ex) -> ex
                .pathMatchers("/status", "/actuator/**").permitAll()
                .pathMatchers("/api/v1/management/**").hasRole("ADMIN")
                .pathMatchers("/api/v1/business/**").hasAuthority("PERM_API_ACCESS")
                .anyExchange().authenticated());
        http.oauth2ResourceServer((oauth2) -> oauth2
                .jwt((jwt) -> jwt
                        .jwtDecoder(jwtDecoderProvider.getObject())
                        .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter))));

        return http.build();
    }

    @Bean
    public ReactiveJwtDecoder reactiveJwtDecoder(SupabaseSecurityProperties props) {
        String issuerUri = props.getIssuerUri();
        NimbusReactiveJwtDecoder decoder = (NimbusReactiveJwtDecoder) NimbusReactiveJwtDecoder.withIssuerLocation(issuerUri).build();

        OAuth2TokenValidator<Jwt> withIssuer = JwtValidators.createDefaultWithIssuer(issuerUri);
        OAuth2TokenValidator<Jwt> withAudience = new AudienceValidator(props.getAudience());
        decoder.setJwtValidator(new DelegatingOAuth2TokenValidator<>(withIssuer, withAudience));

        return decoder;
    }
}
