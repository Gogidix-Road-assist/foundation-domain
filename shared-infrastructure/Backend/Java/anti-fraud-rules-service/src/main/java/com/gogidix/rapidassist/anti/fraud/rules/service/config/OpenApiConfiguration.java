package com.gogidix.rapidassist.anti.fraud.rules.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI antiFraudRulesServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Anti-Fraud Rules Service API")
                        .description("Service for managing anti-fraud detection rules within the Rapid Assist Foundation Domain shared infrastructure")
                        .version("1.0.0"));
    }
}
