package com.gogidix.rapidassist.pricing.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "OAuth2";

    @Bean
    public OpenAPI pricingServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Pricing Service API")
                        .description("Pricing and billing plan management for Rapid Assist platform")
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Gogidix API Team")
                                .email("api-support@gogidix.com")
                                .url("https://gogidix.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://gogidix.com/terms")))
                .servers(List.of(
                        new Server().url("http://localhost:8328").description("Local development"),
                        new Server().url("https://api.staging.gogidix.com/pricing-service").description("Staging"),
                        new Server().url("https://api.gogidix.com/pricing-service").description("Production")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new io.swagger.v3.oas.models.Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")));
    }

    @Bean
    public GroupedOpenApi pricingApi() {
        return GroupedOpenApi.builder()
                .group("pricing-v1")
                .pathsToMatch("/api/v1/pricing/**")
                .build();
    }

    @Bean
    public GroupedOpenApi statusApi() {
        return GroupedOpenApi.builder()
                .group("status")
                .pathsToMatch("/actuator/**", "/status")
                .build();
    }
}
