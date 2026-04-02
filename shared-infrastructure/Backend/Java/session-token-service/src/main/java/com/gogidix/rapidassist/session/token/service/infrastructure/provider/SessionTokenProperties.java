package com.gogidix.rapidassist.session.token.service.infrastructure.provider;

import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ConfigurationProperties(prefix = "gogidix.session-token")
public class SessionTokenProperties {

    private Provider provider = new Provider();
    private Jwt jwt = new Jwt();
    private String keyPrefix = "rapidassist:session";
    private boolean enableRefreshTokens = true;

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Jwt getJwt() {
        return jwt;
    }

    public void setJwt(Jwt jwt) {
        this.jwt = jwt;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public boolean isEnableRefreshTokens() {
        return enableRefreshTokens;
    }

    public void setEnableRefreshTokens(boolean enableRefreshTokens) {
        this.enableRefreshTokens = enableRefreshTokens;
    }

    // Helper methods
    public String getSecret() {
        return jwt.getSecret();
    }

    public String getIssuer() {
        return jwt.getIssuer();
    }

    public SignatureAlgorithm getAlgorithm() {
        return SignatureAlgorithm.forName(jwt.getAlgorithm());
    }

    public Duration getAccessTokenTtl() {
        return Duration.ofSeconds(jwt.getAccessTokenTtlSeconds());
    }

    public Duration getRefreshTokenTtl() {
        return Duration.ofSeconds(jwt.getRefreshTokenTtlSeconds());
    }

    public String getDefaultScopes() {
        return jwt.getDefaultScopes();
    }

    public List<String> getDefaultRoles() {
        return jwt.getDefaultRoles();
    }

    public static class Provider {
        private String type = "jwt"; // jwt or noop

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Jwt {
        private String secret;
        private String algorithm = "HS256";
        private String issuer = "Gogidix RapidAssist";
        private long accessTokenTtlSeconds = 3600; // 1 hour
        private long refreshTokenTtlSeconds = 2592000; // 30 days
        private String defaultScopes = "read write";
        private List<String> defaultRoles = new ArrayList<>();

        public String getSecret() {
            if (secret == null || secret.isEmpty()) {
                throw new IllegalArgumentException("JWT secret must be configured");
            }
            return secret;
        }

        public void setSecret(String secret) {
            this.secret = secret;
        }

        public String getAlgorithm() {
            return algorithm;
        }

        public void setAlgorithm(String algorithm) {
            this.algorithm = algorithm;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public long getAccessTokenTtlSeconds() {
            return accessTokenTtlSeconds;
        }

        public void setAccessTokenTtlSeconds(long accessTokenTtlSeconds) {
            this.accessTokenTtlSeconds = accessTokenTtlSeconds;
        }

        public long getRefreshTokenTtlSeconds() {
            return refreshTokenTtlSeconds;
        }

        public void setRefreshTokenTtlSeconds(long refreshTokenTtlSeconds) {
            this.refreshTokenTtlSeconds = refreshTokenTtlSeconds;
        }

        public String getDefaultScopes() {
            return defaultScopes;
        }

        public void setDefaultScopes(String defaultScopes) {
            this.defaultScopes = defaultScopes;
        }

        public List<String> getDefaultRoles() {
            return defaultRoles;
        }

        public void setDefaultRoles(List<String> defaultRoles) {
            this.defaultRoles = defaultRoles;
        }

        public void setDefaultRolesString(String roles) {
            if (roles != null && !roles.trim().isEmpty()) {
                this.defaultRoles = Arrays.asList(roles.split(","));
            }
        }
    }
}