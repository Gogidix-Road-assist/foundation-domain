package com.gogidix.rapidassist.dashboard.configuration.service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for the Dashboard Configuration Service.
 *
 * <p>This class configures the OpenAPI documentation for the service, including
 * API information, servers, security schemes, and endpoint groupings.</p>
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    /**
     * Configures the OpenAPI documentation for the entire service.
     *
     * @return the OpenAPI configuration
     */
    @Bean
    public OpenAPI dashboardConfigurationServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Dashboard Configuration Service API")
                .description("""
                    ## Dashboard Configuration Service

                    The Dashboard Configuration Service is responsible for managing dashboard configurations,
                    including layouts, widgets, themes, and permissions.

                    ### Features
                    - Dashboard CRUD operations
                    - Widget management
                    - Layout customization
                    - Theme configuration
                    - Permission management
                    - Dashboard cloning
                    - Multi-tenant support

                    ### Authentication
                    This API uses JWT bearer tokens for authentication. Include your token in the
                    Authorization header as a Bearer token.

                    ### Error Responses
                    All error responses follow a standard format:
                    ```json
                    {
                      "timestamp": "2024-01-01T00:00:00Z",
                      "status": 400,
                      "error": "Bad Request",
                      "message": "Error details here",
                      "path": "/api/dashboards",
                      "requestId": "uuid"
                    }
                    ```
                    """)
                .version("1.0.0")
                .contact(new Contact()
                    .name("Rapid Assist Team")
                    .email("support@gogidix.com")
                    .url("https://gogidix.com"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://gogidix.com/terms")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8200")
                    .description("Local development server"),
                new Server()
                    .url("https://staging-api.gogidix.com")
                    .description("Staging server"),
                new Server()
                    .url("https://api.gogidix.com")
                    .description("Production server")
            ))
            .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
            .components(new Components()
                .addSecuritySchemes(SECURITY_SCHEME_NAME,
                    new SecurityScheme()
                        .name(SECURITY_SCHEME_NAME)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .description("JWT token authentication. Provide the token without the 'Bearer' prefix.")
                )
                .addSchemas("ErrorResponse", new Schema<>()
                    .type("object")
                    .addProperty("timestamp", new Schema<>().type("string").format("date-time").description("Error timestamp"))
                    .addProperty("status", new Schema<>().type("integer").description("HTTP status code"))
                    .addProperty("error", new Schema<>().type("string").description("Error type"))
                    .addProperty("message", new Schema<>().type("string").description("Error message"))
                    .addProperty("path", new Schema<>().type("string").description("Request path"))
                    .addProperty("requestId", new Schema<>().type("string").description("Unique request ID"))
                )
            );
    }

    /**
     * Creates a grouped OpenAPI for dashboard management endpoints.
     *
     * @return the grouped OpenAPI configuration
     */
    @Bean
    public GroupedOpenApi dashboardManagementApi() {
        return GroupedOpenApi.builder()
            .group("dashboard-management")
            .pathsToMatch("/api/dashboards/**")
            .build();
    }

    /**
     * Creates a grouped OpenAPI for health check endpoints.
     *
     * @return the grouped OpenAPI configuration
     */
    @Bean
    public GroupedOpenApi healthApi() {
        return GroupedOpenApi.builder()
            .group("health")
            .pathsToMatch("/api/dashboards/health")
            .build();
    }
}
