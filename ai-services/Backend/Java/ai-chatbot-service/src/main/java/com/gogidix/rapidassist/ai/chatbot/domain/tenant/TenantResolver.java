package com.gogidix.rapidassist.ai.chatbot.domain.tenant;

import com.gogidix.rapidassist.ai.chatbot.domain.exception.ChatbotException;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * Tenant resolution strategy interface and implementations.
 * Extracts tenant ID from various sources (JWT, headers, API key, etc.)
 */
public interface TenantResolver {

    /**
     * Resolve tenant ID from the provided context
     */
    String resolveTenant(TenantResolutionContext context);

    /**
     * Default implementation using headers
     */
    @Slf4j
    class HeaderTenantResolver implements TenantResolver {

        private static final String TENANT_HEADER = "X-Tenant-ID";
        private static final String DEFAULT_TENANT = "default";

        @Override
        public String resolveTenant(TenantResolutionContext context) {
            return context.getHeader(TENANT_HEADER)
                    .orElseGet(() -> {
                        log.info("No tenant header found, using default tenant");
                        return DEFAULT_TENANT;
                    });
        }
    }

    /**
     * JWT-based tenant resolver
     */
    @Slf4j
    class JwtTenantResolver implements TenantResolver {

        private static final String TENANT_CLAIM = "tenantId";

        @Override
        public String resolveTenant(TenantResolutionContext context) {
            return context.getClaim(TENANT_CLAIM)
                    .map(Object::toString)
                    .orElseThrow(() -> new ChatbotException("TENANT_NOT_FOUND", "Tenant ID not found in JWT token"));
        }
    }

    /**
     * API Key-based tenant resolver
     */
    @Slf4j
    class ApiKeyTenantResolver implements TenantResolver {

        @Override
        public String resolveTenant(TenantResolutionContext context) {
            return context.getApiKey()
                    .map(this::extractTenantFromApiKey)
                    .orElseThrow(() -> new ChatbotException("API_KEY_MISSING", "API key not provided"));
        }

        private String extractTenantFromApiKey(String apiKey) {
            // Extract tenant ID from API key (implementation depends on your API key format)
            String[] parts = apiKey.split("_");
            if (parts.length > 0) {
                return parts[0];
            }
            throw new ChatbotException("INVALID_API_KEY", "Invalid API key format");
        }
    }

    /**
     * Composite tenant resolver that tries multiple strategies
     */
    @Slf4j
    class CompositeTenantResolver implements TenantResolver {

        private final java.util.List<TenantResolver> resolvers;

        public CompositeTenantResolver(java.util.List<TenantResolver> resolvers) {
            this.resolvers = resolvers;
        }

        @Override
        public String resolveTenant(TenantResolutionContext context) {
            for (TenantResolver resolver : resolvers) {
                try {
                    String tenantId = resolver.resolveTenant(context);
                    if (tenantId != null && !tenantId.isEmpty()) {
                        log.info("Resolved tenant using {}: {}", resolver.getClass().getSimpleName(), tenantId);
                        return tenantId;
                    }
                } catch (Exception e) {
                    log.info("Failed to resolve tenant with {}: {}", resolver.getClass().getSimpleName(), e.getMessage());
                }
            }
            throw new ChatbotException("TENANT_RESOLUTION_FAILED", "Unable to resolve tenant from any strategy");
        }
    }

    /**
     * Context object for tenant resolution
     */
    @Slf4j
    class TenantResolutionContext {

        private final java.util.Map<String, String> headers;
        private final java.util.Map<String, Object> claims;
        private final String apiKey;

        public TenantResolutionContext(java.util.Map<String, String> headers,
                                       java.util.Map<String, Object> claims,
                                       String apiKey) {
            this.headers = headers;
            this.claims = claims;
            this.apiKey = apiKey;
        }

        public Optional<String> getHeader(String name) {
            return Optional.ofNullable(headers.get(name));
        }

        public Optional<Object> getClaim(String name) {
            return Optional.ofNullable(claims.get(name));
        }

        public Optional<String> getApiKey() {
            return Optional.ofNullable(apiKey);
        }

        public static TenantResolutionContext fromHeaders(java.util.Map<String, String> headers) {
            return new TenantResolutionContext(headers, java.util.Collections.emptyMap(), null);
        }

        public static TenantResolutionContext fromClaims(java.util.Map<String, Object> claims) {
            return new TenantResolutionContext(java.util.Collections.emptyMap(), claims, null);
        }

        public static TenantResolutionContext fromApiKey(String apiKey) {
            return new TenantResolutionContext(java.util.Collections.emptyMap(), java.util.Collections.emptyMap(), apiKey);
        }
    }
}
