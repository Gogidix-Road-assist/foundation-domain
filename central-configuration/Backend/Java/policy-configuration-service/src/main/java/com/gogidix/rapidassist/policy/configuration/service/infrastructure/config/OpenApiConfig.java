package com.gogidix.rapidassist.policy.configuration.service.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * OpenAPI/Swagger configuration for API documentation
 * Generates interactive API documentation accessible at /swagger-ui.html
 *
 * @author Rapid Assist Team
 * @since 1.0.0
 */
@Configuration
public class OpenApiConfig {

    @Value("${server.port:8103}")
    private int serverPort;

    @Value("${spring.application.name:policy-configuration-service}")
    private String applicationName;

    @Value("${info.app.version:1.0.0}")
    private String applicationVersion;

    /**
     * Configure OpenAPI documentation
     *
     * @return OpenAPI configuration
     */
    @Bean
    public OpenAPI policyConfigurationServiceOpenAPI() {
        Server server = new Server();
        server.setUrl("http://localhost:" + serverPort);
        server.setDescription("Development Server");

        Server prodServer = new Server();
        prodServer.setUrl("https://api.gogidix.com");
        prodServer.setDescription("Production Server");

        Contact contact = new Contact();
        contact.setEmail("support@gogidix.com");
        contact.setName("Rapid Assist Team");
        contact.setUrl("https://gogidix.com");

        License license = new License()
                .name("MIT License")
                .url("https://choosealicense.com/licenses/mit/");

        Info info = new Info()
                .title("Policy Configuration Service API")
                .version(applicationVersion)
                .description("""
                        Policy Configuration Service provides RESTful APIs for managing organizational policies.

                        **Key Features:**
                        * Create, update, delete, and query policies
                        * Support for multiple policy types (security, privacy, business rules, etc.)
                        * Multi-tenant architecture with tenant isolation
                        * Policy versioning and audit trail
                        * Redis caching for high performance
                        * MongoDB persistence

                        **Authentication:**
                        All endpoints (except public health check) require OAuth2 JWT authentication.

                        **Rate Limiting:**
                        API calls are rate-limited per user and IP address.
                        """)
                .contact(contact)
                .license(license);

        return new OpenAPI()
                .info(info)
                .servers(List.of(server, prodServer));
    }
}
