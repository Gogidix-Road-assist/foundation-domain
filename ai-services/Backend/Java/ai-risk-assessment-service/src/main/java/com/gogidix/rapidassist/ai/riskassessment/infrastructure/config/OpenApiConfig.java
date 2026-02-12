package com.gogidix.rapidassist.ai.riskassessment.infrastructure.config;

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
 * OpenAPI/Swagger Configuration for AI Risk Assessment Service
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI riskAssessmentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("AI Risk Assessment Service API")
                        .description("AI-powered risk assessment service for Rapid Assist platform. " +
                                "Provides risk scoring, risk factor analysis, alert generation, and mitigation recommendations.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Gogidix")
                                .email("support@gogidix.com")
                                .url("https://www.gogidix.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://www.gogidix.com/license")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.rapidassist.gogidix.com")
                                .description("Production Server")
                ));
    }

    @Bean
    public GroupedOpenApi riskAssessmentApi() {
        return GroupedOpenApi.builder()
                .group("risk-assessment")
                .pathsToMatch("/api/v1/risk-assessments/**")
                .build();
    }

    @Bean
    public GroupedOpenApi riskAlertApi() {
        return GroupedOpenApi.builder()
                .group("risk-alerts")
                .pathsToMatch("/api/v1/risk-alerts/**")
                .build();
    }

    @Bean
    public GroupedOpenApi riskThresholdApi() {
        return GroupedOpenApi.builder()
                .group("risk-thresholds")
                .pathsToMatch("/api/v1/risk-thresholds/**")
                .build();
    }
}
