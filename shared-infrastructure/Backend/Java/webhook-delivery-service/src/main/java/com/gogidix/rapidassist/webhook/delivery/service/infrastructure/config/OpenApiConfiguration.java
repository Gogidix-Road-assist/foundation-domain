package com.gogidix.rapidassist.webhook.delivery.service.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI webhookDeliveryServiceOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Webhook Delivery Service API")
                        .description("Webhook delivery and management service for the Rapid Assist Foundation Domain shared infrastructure. Handles webhook event delivery, retry logic, and webhook endpoint management.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gogidix Support")
                                .email("support@gogidix.com")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token for authentication. Token must contain tenantId and correlationId in claims.")));
    }
}
