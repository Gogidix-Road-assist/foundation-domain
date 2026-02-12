package com.gogidix.rapidassist.geo.location.service.config;

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
 * OpenAPI configuration for Geo Location Service.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI geoLocationServiceOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Geo Location Service API")
                .description("REST API for geolocation, geofencing, and geocoding operations")
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
                    .url("http://localhost:8313")
                    .description("Development Server"),
                new Server()
                    .url("https://geo-location.rapidassist.com")
                    .description("Production Server")
            ));
    }

    @Bean
    public GroupedOpenApi geoLocationApi() {
        return GroupedOpenApi.builder()
            .group("geo-location")
            .pathsToMatch("/**")
            .packagesToScan("com.gogidix.rapidassist.geo.location.service.adapters.in.web")
            .build();
    }
}
