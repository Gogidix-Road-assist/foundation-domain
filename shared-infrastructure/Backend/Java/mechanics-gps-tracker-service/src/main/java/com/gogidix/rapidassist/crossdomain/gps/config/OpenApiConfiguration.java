package com.gogidix.rapidassist.crossdomain.gps.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for GPS Tracker Service.
 */
@Configuration
public class OpenApiConfiguration {

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("gps-tracker-service")
                .pathsToMatch("/api/bridge/gps-tracker/**")
                .build();
    }

    @Bean
    public OpenAPI gpsTrackerServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("GPS Tracker Service API")
                        .description("Cross-domain bridge between Mechanics and Central-Monitoring for real-time GPS tracking. " +
                                "Provides location updates, nearby mechanic search, and job tracking.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gogidix Technology")
                                .email("tech@gogidix.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Development"),
                        new Server().url("https://api.gogidix.com").description("Production")
                ))
                .addSecurityItem(new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .name("Authorization"));
    }
}
