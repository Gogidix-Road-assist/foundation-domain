package com.gogidix.rapidassist.service.registry.discovery.infrastructure.web;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Service Registry Discovery.
 */
@Configuration
public class OpenApiConfiguration {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI serviceRegistryOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("RapidAssist Service Registry Discovery")
                .description(
                    """
                    Service Registry for dynamic service discovery and registration.

                    ## Features

                    - **Service Registration**: Register service instances dynamically
                    - **Health Monitoring**: Track service health status
                    - **Load Balancing**: Support for weighted load balancing
                    - **Multi-Tenancy**: Built-in multi-tenant support
                    - **Service Discovery**: Query and discover services by tags
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
                new Server().url("http://localhost:8333").description("Development"),
                new Server().url("https://service-registry.rapidassist.com").description("Production")
            ))
            .components(new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME,
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")))
            .addSecurityItem(new io.swagger.v3.oas.models.security.SecurityRequirement()
                .addList(SECURITY_SCHEME_NAME));
    }
}
