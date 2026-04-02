package com.gogidix.rapidassist.ai.forecasting.infrastructure.config;

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
 * OpenAPI configuration for API documentation.
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8082}")
    private String serverPort;

    @Bean
    public OpenAPI forecastingServiceOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:" + serverPort);
        server.setDescription("Development Server");

        Contact contact = new Contact();
        contact.setName("Gogidix Rapid Assist Team");
        contact.setEmail("support@gogidix.com");
        contact.setUrl("https://www.gogidix.com");

        License license = new License()
                .name("MIT License")
                .url("https://opensource.org/licenses/MIT");

        Info info = new Info()
                .title("AI Forecasting Service API")
                .version("1.0.0")
                .description("REST API for AI-powered forecasting service. Handles predictive analytics, " +
                           "time series forecasting, ML model management, and accuracy tracking with multi-tenancy support.")
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server));
    }
}
