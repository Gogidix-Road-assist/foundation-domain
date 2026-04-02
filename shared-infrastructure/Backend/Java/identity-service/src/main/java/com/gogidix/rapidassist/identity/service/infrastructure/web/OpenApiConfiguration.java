package com.gogidix.rapidassist.identity.service.infrastructure.web;

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
 * OpenAPI configuration for Identity Service.
 */
@Configuration
public class OpenApiConfiguration {

    private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

    @Bean
    public OpenAPI identityServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("RapidAssist Identity Service")
                .description("User identity and profile management service")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Gogidix Support")
                    .email("support@gogidix.com")
                    .url("https://gogidix.com"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://gogidix.com/terms")))
            .servers(List.of(
                new Server().url("http://localhost:8316").description("Development"),
                new Server().url("https://identity.rapidassist.com").description("Production")
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
