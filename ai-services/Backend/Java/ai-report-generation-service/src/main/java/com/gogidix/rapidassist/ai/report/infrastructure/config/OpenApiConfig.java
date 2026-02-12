package com.gogidix.rapidassist.ai.report.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI configuration for Report Generation Service.
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI reportGenerationServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Report Generation Service API")
                        .description("AI-powered report generation service for Rapid Assist platform. " +
                                "Provides automated report generation, template management, scheduled reports, " +
                                "and multiple output formats (PDF, Excel, CSV, HTML).")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gogidix Rapid Assist")
                                .email("support@gogidix.com")
                                .url("https://www.gogidix.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://www.gogidix.com/license")));
    }
}
