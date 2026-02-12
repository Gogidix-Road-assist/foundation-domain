package com.gogidix.rapidassist.orchestration.alerting_service.infrastructure.config;

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
 * OpenAPI configuration for API documentation
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8083}")
    private int serverPort;

    @Bean
    public OpenAPI alertingServiceOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:" + serverPort);
        server.setDescription("Development server");

        Contact contact = new Contact();
        contact.setEmail("support@gogidix.com");
        contact.setName("Gogidix Support");

        License license = new License()
            .name("Proprietary")
            .url("https://www.gogidix.com");

        Info info = new Info()
            .title("Alerting Service API")
            .version("1.0.0")
            .description("Alerting Service for Roadside Assistance Platform - " +
                "Manages emergency alerts, breakdown notifications, and escalation workflows")
            .contact(contact)
            .license(license);

        return new OpenAPI()
            .info(info)
            .servers(List.of(server));
    }
}
