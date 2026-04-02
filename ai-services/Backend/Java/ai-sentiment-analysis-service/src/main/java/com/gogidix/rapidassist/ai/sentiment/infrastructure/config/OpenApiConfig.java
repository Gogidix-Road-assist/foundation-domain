package com.gogidix.rapidassist.ai.sentiment.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for sentiment analysis service
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8080}")
    private int serverPort;

    @Bean
    public OpenAPI sentimentAnalysisOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:" + serverPort);
        server.setDescription("Development Server");

        Contact contact = new Contact();
        contact.setEmail("support@gogidix.com");
        contact.setName("Gogidix Support");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("AI Sentiment Analysis Service API")
                .version("1.0.0")
                .description("AI-powered sentiment analysis service for Rapid Assist platform. " +
                        "Provides text sentiment analysis, emotion detection, and aspect-based sentiment extraction.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
