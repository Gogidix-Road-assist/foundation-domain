package com.gogidix.rapidassist.currency.converter.service.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfiguration {

    @Bean
    public OpenAPI currencyConverterServiceOpenAPI() {
        final String securitySchemeName = "bearerAuth";

        return new OpenAPI()
                .info(new Info()
                        .title("Currency Converter Service API")
                        .description("Foundation Domain service for currency conversion and exchange rate management. Provides real-time currency conversion with support for multiple currencies and configurable exchange rates.")
                        .version("1.0.0")
                        .termsOfService("https://gogidix.com/terms")
                        .contact(new io.swagger.v3.oas.models.info.Contact()
                                .name("Gogidix Support")
                                .email("support@gogidix.com")
                                .url("https://gogidix.com"))
                        .license(new io.swagger.v3.oas.models.info.License()
                                .name("Apache 2.0")
                                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()
                                        .name(securitySchemeName)
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("JWT token for authentication. Include tenantId claim.")));
    }
}
