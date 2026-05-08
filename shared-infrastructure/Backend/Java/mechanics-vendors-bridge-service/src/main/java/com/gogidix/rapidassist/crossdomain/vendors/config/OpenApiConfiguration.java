package com.gogidix.rapidassist.crossdomain.vendors.config;

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
 * OpenAPI/Swagger configuration for Mechanics-Vendors Bridge Service.
 */
@Configuration
public class OpenApiConfiguration {

    @Bean
    public GroupedOpenApi groupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("mechanics-vendors-bridge")
                .pathsToMatch("/api/bridge/vendors/**")
                .build();
    }

    @Bean
    public OpenAPI mechanicsVendorsBridgeOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Mechanics-Vendors Bridge Service API")
                        .description("Cross-domain bridge between Mechanics and Vendors-Ecommerce. " +
                                "Handles parts ordering, catalog search, vendor comparison, and auto-reordering.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gogidix Technology")
                                .email("tech@gogidix.com"))
                        .license(new License()
                                .name("MIT")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server().url("http://localhost:8100").description("Development"),
                        new Server().url("https://api.gogidix.com").description("Production")
                ))
                .addSecurityItem(new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")
                        .name("Authorization"));
    }
}
