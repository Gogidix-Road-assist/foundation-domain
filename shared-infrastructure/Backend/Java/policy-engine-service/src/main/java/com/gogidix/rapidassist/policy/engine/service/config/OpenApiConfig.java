package com.gogidix.rapidassist.policy.engine.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI configuration for Policy Engine Service.
 */
@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "OAuth2";

    @Bean
    public OpenAPI policyEngineServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Policy Engine Service API")
                        .description("""
                                ## Policy Engine Service

                                The Policy Engine Service evaluates business rules and policies
                                for the Rapid Assist platform. It provides a flexible, configurable
                                way to enforce business logic across multiple services.

                                ### Features
                                - Policy evaluation engine
                                - Multi-tenant policy support
                                - Dynamic policy configuration
                                - Decision audit trail

                                ### Usage
                                Clients submit policy evaluation requests with input parameters,
                                and the service returns allow/deny decisions with reasons and obligations.
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
                                .url("http://localhost:8327")
                                .description("Local development"),
                        new Server()
                                .url("https://api.staging.gogidix.com/policy-engine-service")
                                .description("Staging environment"),
                        new Server()
                                .url("https://api.gogidix.com/policy-engine-service")
                                .description("Production environment")
                ))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(
                        new io.swagger.v3.oas.models.Components()
                                .addSecuritySchemes(SECURITY_SCHEME_NAME,
                                        new SecurityScheme()
                                                .type(SecurityScheme.Type.HTTP)
                                                .scheme("bearer")
                                                .bearerFormat("JWT"))
                );
    }

    @Bean
    public GroupedOpenApi policyApi() {
        return GroupedOpenApi.builder()
                .group("policy-v1")
                .pathsToMatch("/api/v1/policies/**")
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
