package com.gogidix.rapidassist.ai.recommendation.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI/Swagger configuration for AI Recommendation Service.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI recommendationServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Recommendation Service API")
                        .description("AI-powered recommendation service for Rapid Assist platform. " +
                                "Provides collaborative filtering, content-based recommendations, " +
                                "hybrid recommendation engines, and user personalization.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gogidix")
                                .email("support@gogidix.com")
                                .url("https://gogidix.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://gogidix.com/license")));
    }
}
