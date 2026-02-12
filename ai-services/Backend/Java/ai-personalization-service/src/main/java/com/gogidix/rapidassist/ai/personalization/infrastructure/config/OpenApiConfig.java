package com.gogidix.rapidassist.ai.personalization.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for AI Personalization Service.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI personalizationServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Personalization Service API")
                        .description("AI-powered personalization service for Rapid Assist platform. " +
                                "Provides user behavior tracking, personalized recommendations, " +
                                "user segmentation, and preference learning.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gogidix")
                                .email("support@gogidix.com")
                                .url("https://gogidix.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://gogidix.com/license")));
    }

    @Bean
    public GroupedOpenApi personalizationApi() {
        return GroupedOpenApi.builder()
                .group("personalization")
                .pathsToMatch("/api/v1/personalization/**")
                .build();
    }
}
