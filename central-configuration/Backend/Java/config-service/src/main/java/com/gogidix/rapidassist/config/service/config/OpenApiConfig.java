package com.gogidix.rapidassist.config.service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation.
 * Configures the OpenAPI specification and security schemes.
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:config-service}")
    private String applicationName;

    @Value("${spring.application.version:1.0.0-SNAPSHOT}")
    private String applicationVersion;

    @Value("${app.openapi.description:Configuration Management Service API}")
    private String apiDescription;

    @Value("${app.openapi.contact-email:support@gogidix.com}")
    private String contactEmail;

    @Value("${app.openapi.license:Proprietary}")
    private String licenseName;

    @Value("${app.openapi.server-url:http://localhost:8080}")
    private String serverUrl;

    @Value("${app.openapi.server-description:Development Server}")
    private String serverDescription;

    /**
     * Configure OpenAPI documentation metadata.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title(applicationName + " API")
                .version(applicationVersion)
                .description(apiDescription)
                .contact(new Contact()
                    .name("Gogidix Support")
                    .email(contactEmail))
                .license(new License()
                    .name(licenseName)))
            .servers(List.of(
                new Server()
                    .url(serverUrl)
                    .description(serverDescription)))
            .components(new Components()
                .addSecuritySchemes("bearer-jwt",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token authentication"))
                .addSecuritySchemes("basic-auth",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("basic")
                        .description("Basic authentication")))
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"))
            .addSecurityItem(new SecurityRequirement().addList("basic-auth"));
    }

    /**
     * Group API by controllers for better organization in Swagger UI.
     */
    @Bean
    public GroupedOpenApi configurationApi() {
        return GroupedOpenApi.builder()
            .group("configurations")
            .pathsToMatch("/api/v1/configurations/**")
            .build();
    }

    @Bean
    public GroupedOpenApi statusApi() {
        return GroupedOpenApi.builder()
            .group("status")
            .pathsToMatch("/api/v1/status/**")
            .build();
    }

    @Bean
    public GroupedOpenApi actuatorApi() {
        return GroupedOpenApi.builder()
            .group("actuator")
            .pathsToMatch("/actuator/**")
            .pathsToExclude("/actuator/health/**") // Exclude detailed health
            .build();
    }
}
