package com.gogidix.rapidassist.shared.cors.config.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration properties for shared CORS configuration.
 *
 * <p>Properties are prefixed with "gogidix.cors"</p>
 */
@ConfigurationProperties(prefix = "gogidix.cors")
public class CorsProperties {

    /**
     * Comma-separated list of allowed origins.
     * Defaults to common development and production origins.
     */
    private String allowedOrigins = "http://localhost:3000,http://localhost:8080,https://rapidassist.gogidix.com";

    /**
     * HTTP methods allowed for cross-origin requests.
     */
    private List<String> allowedMethods = List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");

    /**
     * Headers allowed for cross-origin requests.
     * Default is "*" (all headers).
     */
    private List<String> allowedHeaders = List.of("*");

    /**
     * Whether credentials (cookies, authorization headers) are allowed.
     */
    private Boolean allowCredentials = true;

    /**
     * Maximum age (in seconds) for CORS preflight responses.
     */
    private Long maxAge = 3600L;

    /**
     * Path patterns to apply CORS configuration to.
     * Default is all paths ("/**").
     */
    private List<String> pathPatterns = List.of("/**");

    /**
     * Whether CORS configuration is enabled.
     */
    private Boolean enabled = true;

    public String getAllowedOrigins() {
        return allowedOrigins;
    }

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    public List<String> getAllowedMethods() {
        return allowedMethods;
    }

    public void setAllowedMethods(List<String> allowedMethods) {
        this.allowedMethods = allowedMethods;
    }

    public List<String> getAllowedHeaders() {
        return allowedHeaders;
    }

    public void setAllowedHeaders(List<String> allowedHeaders) {
        this.allowedHeaders = allowedHeaders;
    }

    public Boolean getAllowCredentials() {
        return allowCredentials;
    }

    public void setAllowCredentials(Boolean allowCredentials) {
        this.allowCredentials = allowCredentials;
    }

    public Long getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Long maxAge) {
        this.maxAge = maxAge;
    }

    public List<String> getPathPatterns() {
        return pathPatterns;
    }

    public void setPathPatterns(List<String> pathPatterns) {
        this.pathPatterns = pathPatterns;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
