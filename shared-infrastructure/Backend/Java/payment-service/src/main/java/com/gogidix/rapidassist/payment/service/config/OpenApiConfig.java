package com.gogidix.rapidassist.payment.service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.media.IntegerSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Payment Service.
 * <p>
 * Configures Swagger UI and API documentation generation with comprehensive
 * information about the payment service endpoints, security schemes, and schemas.
 *
 * @author Gogidix
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "OAuth2";

    /**
     * Configures the main OpenAPI documentation.
     */
    @Bean
    public OpenAPI paymentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment Service API")
                        .description("""
                                ## Payment Service

                                The Payment Service provides payment processing capabilities for the Rapid Assist platform.
                                It supports payment intent creation, retrieval, and status tracking.

                                ### Features
                                - Create payment intents with Stripe integration
                                - Retrieve payment intent details
                                - Multi-tenant payment processing
                                - Secure payment processing with OAuth2

                                ### Authentication
                                All endpoints require OAuth2 Bearer token authentication. Include the token in the
                                Authorization header:
                                `Authorization: Bearer <your-jwt-token>`

                                ### Multi-Tenancy
                                The service extracts tenant information from the JWT context. Ensure your JWT includes
                                the tenantId claim.

                                ### Errors
                                The service uses RFC 7807 Problem Details for HTTP APIs for error responses.
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
                                .url("http://localhost:8326")
                                .description("Local development"),
                        new Server()
                                .url("https://api.staging.gogidix.com/payment-service")
                                .description("Staging environment"),
                        new Server()
                                .url("https://api.gogidix.com/payment-service")
                                .description("Production environment")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("OAuth2 JWT token from authentication service"))
                        .addSchemas("Money", new Schema<>()
                                .type("object")
                                .description("Monetary amount with currency")
                                .addProperty("amount", new IntegerSchema()
                                        .description("Amount in minor currency units (e.g., cents)")
                                        .example(1000))
                                .addProperty("currency", new StringSchema()
                                        .description("ISO 4217 currency code")
                                        .example("USD")
                                        .pattern("^[A-Z]{3}$")))
                );
    }

    /**
     * Configures the API grouping for payment endpoints.
     */
    @Bean
    public GroupedOpenApi paymentApi() {
        return GroupedOpenApi.builder()
                .group("payment-v1")
                .pathsToMatch("/api/v1/payments/**")
                .build();
    }

    /**
     * Configures the API grouping for health and status endpoints.
     */
    @Bean
    public GroupedOpenApi statusApi() {
        return GroupedOpenApi.builder()
                .group("status")
                .pathsToMatch("/actuator/**", "/status")
                .build();
    }
}
