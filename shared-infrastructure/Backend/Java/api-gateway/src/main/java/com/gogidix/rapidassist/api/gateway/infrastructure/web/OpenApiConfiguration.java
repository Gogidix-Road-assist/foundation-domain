package com.gogidix.rapidassist.api.gateway.infrastructure.web;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for API Gateway.
 *
 * <p>This configuration generates API documentation using Springdoc OpenAPI.
 * The documentation is accessible at /swagger-ui.html</p>
 */
@Configuration
public class OpenApiConfiguration {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    /**
     * Configures the OpenAPI documentation.
     *
     * @return the OpenAPI configuration
     */
    @Bean
    public OpenAPI apiGatewayOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("RapidAssist API Gateway")
                .description(
                    """
                    API Gateway for the RapidAssist Foundation Domain.

                    This gateway provides centralized routing, rate limiting, security,
                    and observability for all microservices in the RapidAssist platform.

                    ## Key Features

                    - **Dynamic Route Management**: Create, update, and manage routes dynamically
                    - **Rate Limiting**: Configurable rate limiting per route
                    - **Circuit Breaker**: Resilience patterns for fault tolerance
                    - **Request Metrics**: Track request metrics and performance
                    - **Multi-Tenancy**: Built-in support for multi-tenant architecture
                    """
                )
                .version("1.0.0")
                .contact(new Contact()
                    .name("Gogidix Support")
                    .email("support@gogidix.com")
                    .url("https://gogidix.com"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://gogidix.com/terms")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Development"),
                new Server()
                    .url("https://api-gateway.rapidassist.com")
                    .description("Production")
            ))
            .components(new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token authentication using Supabase")
                )
            )
            .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME));
    }
}
