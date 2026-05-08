package com.gogidix.shared.infrastructure.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT Configuration Properties
 *
 * Reads JWT configuration from application.yml
 *
 * Properties to configure in application.yml:
 * jwt:
 *   secret: ${JWT_SECRET:your-secret-key}
 *   expiration: ${JWT_EXPIRATION:86400000}
 *   issuer: ${JWT_ISSUER:gogidix-ai-services}
 */
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private String secret;
    private Long expiration = 86400000L; // 24 hours in milliseconds
    private String issuer = "gogidix-ai-services";

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }
}
