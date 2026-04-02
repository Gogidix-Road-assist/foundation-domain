package com.gogidix.rapidassist.ai.anomaly.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for API documentation.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI anomalyDetectionServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Anomaly Detection Service API")
                        .description("REST API for AI-powered anomaly detection service in Rapid Assist platform")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gogidix")
                                .email("support@gogidix.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://gogidix.com")));
    }
}
