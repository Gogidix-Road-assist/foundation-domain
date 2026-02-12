package com.gogidix.rapidassist.ai.analytics.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for AI BI Analytics Service
 */
@Configuration
public class OpenApiConfig {

    @Value("${spring.application.name:AI BI Analytics Service}")
    private String applicationName;

    @Value("${spring.application.version:1.0.0}")
    private String applicationVersion;

    @Bean
    public OpenAPI analyticsOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(applicationName)
                        .description("AI-powered Business Intelligence and Analytics Service for Rapid Assist Platform")
                        .version(applicationVersion)
                        .contact(new Contact()
                                .name("Gogidix")
                                .email("support@gogidix.com")
                                .url("https://www.gogidix.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://www.gogidix.com/license")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.rapidassist.gogidix.com")
                                .description("Production Server")
                ));
    }
}
