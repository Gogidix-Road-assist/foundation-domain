package com.gogidix.rapidassist.event.audit.service.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI Configuration for Event Audit Service
 *
 * Provides API documentation with JWT Bearer authentication
 */
@Configuration
public class OpenApiConfiguration {

    public static final String BEARER_AUTH = "bearerAuth";

    @Bean
    public OpenAPI eventAuditServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Event Audit Service API")
                        .description("Event Audit Service - Provides comprehensive audit trail logging for all system events. Supports event appending, querying, and retention management.")
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
