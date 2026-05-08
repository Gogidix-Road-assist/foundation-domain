package com.gogidix.rapidassist.identity.access.service.infrastructure.web;

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
 * OpenAPI configuration for Identity and Access Service.
 */
@Configuration
public class OpenApiConfiguration {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI identityAccessOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("RapidAssist Identity and Access Service")
                .description(
                    """
                    Identity and Access Management service for user authentication,
                    authorization, roles, permissions, and organizational units.

                    ## Features

                    - **User Management**: Create, update, and manage user accounts
                    - **Role-Based Access Control**: Define roles and permissions
                    - **Organizational Units**: Manage organizational hierarchy
                    - **Memberships**: Assign users to organizational units
                    - **Multi-Tenancy**: Built-in tenant isolation
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
                new Server().url("http://localhost:8315").description("Development"),
                new Server().url("https://identity-access.rapidassist.com").description("Production")
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
