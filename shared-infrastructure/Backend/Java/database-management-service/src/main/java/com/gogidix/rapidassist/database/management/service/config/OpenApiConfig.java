package com.gogidix.rapidassist.database.management.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Database Management Service.
 * <p>
 * This configuration sets up the API documentation with service information,
 * license details, and server configurations.
 * </p>
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures the OpenAPI documentation metadata.
     *
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI databaseManagementServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Database Management Service API")
                .description("REST API for managing database connections, health checks, migrations, backups, and statistics. " +
                           "This service provides centralized database management capabilities for the Rapid-Assist platform.")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Gogidix Support")
                    .email("support@gogidix.com")
                    .url("https://gogidix.com"))
                .license(new License()
                    .name("Proprietary")
                    .url("https://gogidix.com/license")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8200")
                    .description("Development Server"),
                new Server()
                    .url("https://database-management.rapidassist.com")
                    .description("Production Server")
            ));
    }

    /**
     * Configures the API grouping for documentation.
     *
     * @return GroupedOpenApi configuration
     */
    @Bean
    public GroupedOpenApi databaseManagementApi() {
        return GroupedOpenApi.builder()
            .group("database-management")
            .pathsToMatch("/api/database/**")
            .packagesToScan("com.gogidix.rapidassist.database.management.service.adapters.in.web")
            .build();
    }
}
