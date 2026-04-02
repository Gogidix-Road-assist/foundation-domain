package com.gogidix.rapidassist.integration.adapters.service.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI Configuration for Integration Adapters Service
 *
 * Provides API documentation with JWT Bearer authentication
 */
@Configuration
public class OpenApiConfiguration {

    public static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI integrationAdaptersServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Integration Adapters Service API")
                        .description("Integration Adapters Service - Manages third-party integrations including payment gateways, mapping services, SMS providers, and email services. Provides unified adapter layer for external service communication.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gogidix Support")
                                .email("support@gogidix.com")))
                .addSecurityItem(new SecurityRequirement().addList(BEARER_AUTH))
                .components(new Components()
                        .addSecuritySchemes(BEARER_AUTH,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token from Keycloak. Include in format: Bearer <token>")));
    }
}
