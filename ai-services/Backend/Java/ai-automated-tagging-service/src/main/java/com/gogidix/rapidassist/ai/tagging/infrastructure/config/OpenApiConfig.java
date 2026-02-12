package com.gogidix.rapidassist.ai.tagging.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for AI Automated Tagging Service
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private int serverPort;

    @Bean
    public OpenAPI taggingServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Automated Tagging Service API")
                        .description("AI-powered automated content tagging and tag management service for Rapid Assist platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gogidix Rapid Assist")
                                .email("support@gogidix.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Development Server")
                ));
    }
}
