package com.gogidix.rapidassist.payments.adapter.service.config;

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
 * OpenAPI configuration for Payments Adapter Service.
 *
 * @author Gogidix
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI paymentsAdapterServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payments Adapter Service API")
                        .description("""
                                ## Payments Adapter Service

                                The Payments Adapter Service provides integration with external payment providers
                                for the Rapid Assist platform. It acts as an abstraction layer between the
                                payment service and third-party payment gateways.

                                ### Features
                                - Provider-agnostic payment interface
                                - Support for multiple payment providers
                                - Adapter pattern for easy provider integration

                                ### Supported Providers
                                - Stripe
                                - PayPal
                                - Square
                                - Custom providers

                                ### Usage
                                This service is typically called by the Payment Service and not directly by clients.
                                """)
                        .version("v1.0.0")
                        .contact(new Contact()
                                .name("Gogidix API Team")
                                .email("api-support@gogidix.com")
                                .url("https://gogidix.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://gogidix.com/terms")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8325")
                                .description("Local development"),
                        new Server()
                                .url("https://api.staging.gogidix.com/payments-adapter-service")
                                .description("Staging environment"),
                        new Server()
                                .url("https://api.gogidix.com/payments-adapter-service")
                                .description("Production environment")
                ));
    }

    @Bean
    public GroupedOpenApi adapterApi() {
        return GroupedOpenApi.builder()
                .group("adapter-v1")
                .pathsToMatch("/api/v1/adapters/**")
                .build();
    }

    @Bean
    public GroupedOpenApi statusApi() {
        return GroupedOpenApi.builder()
                .group("status")
                .pathsToMatch("/actuator/**", "/status")
                .build();
    }
}
