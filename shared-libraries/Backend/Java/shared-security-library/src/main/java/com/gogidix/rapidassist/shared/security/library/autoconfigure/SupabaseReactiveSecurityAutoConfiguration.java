package com.gogidix.rapidassist.shared.security.library.autoconfigure;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtDecoders;
import org.springframework.security.oauth2.jwt.JwtValidators;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Flux;

@AutoConfiguration
@ConditionalOnClass(Flux.class)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties(SupabaseSecurityProperties.class)
public class SupabaseReactiveSecurityAutoConfiguration {

    @Bean
    @org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean(SecurityWebFilterChain.class)
    public SecurityWebFilterChain supabaseReactiveSecurityFilterChain(
            ServerHttpSecurity http,
            SupabaseSecurityProperties props,
            ObjectProvider<JwtDecoder> jwtDecoderProvider
    ) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        String issuerUri = props.getIssuerUri();
        if (issuerUri == null || issuerUri.isBlank()) {
            http.authorizeExchange((authz) -> authz.anyExchange().permitAll());
            return http.build();
        }

        http.authorizeExchange((authz) -> authz.anyExchange().authenticated());

        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(new SupabaseJwtAuthoritiesConverter(props));

        http.oauth2ResourceServer((oauth2) -> oauth2.jwt((jwt) -> jwt
                .jwtAuthenticationConverter(new ReactiveJwtAuthenticationConverterAdapter(authenticationConverter))));

        return http.build();
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.security.oauth2.jwt.JwtDecoder")
    @ConditionalOnProperty(prefix = "gogidix.security.supabase", name = "issuer-uri")
    public JwtDecoder reactiveJwtDecoder(SupabaseSecurityProperties props) {
        String issuerUri = props.getIssuerUri();
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
