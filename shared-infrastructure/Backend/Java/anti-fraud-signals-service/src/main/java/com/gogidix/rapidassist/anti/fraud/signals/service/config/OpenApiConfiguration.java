package com.gogidix.rapidassist.anti.fraud.signals.service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI antiFraudSignalsServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Anti-Fraud Signals Service API")
                        .description("Service for processing and analyzing anti-fraud signals within the Rapid Assist Foundation Domain shared infrastructure")
                        .version("1.0.0"));
    }
}
