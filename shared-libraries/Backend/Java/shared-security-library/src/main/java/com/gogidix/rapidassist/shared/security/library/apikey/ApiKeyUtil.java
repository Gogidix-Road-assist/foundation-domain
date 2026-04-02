package com.gogidix.rapidassist.shared.security.library.apikey;

import com.gogidix.rapidassist.shared.security.library.exception.InvalidTokenException;
import com.gogidix.rapidassist.shared.security.library.jwt.JwtTokenUtil;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for API key generation and validation.
 */
@Component
public class ApiKeyUtil {

    private final JwtTokenUtil jwtTokenUtil;
    private final Map<String, ApiKeyInfo> apiKeys;

    public ApiKeyUtil(JwtTokenUtil jwtTokenUtil) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.apiKeys = new ConcurrentHashMap<>();
    }

    /**
     * Generate a new API key
     * @param keyId unique identifier for the key
     * @param userId user ID who owns the key
     * @param scopes permissions/scopes for this key
     * @param apiKeyType type of API key (SERVICE, USER, PARTNER)
     * @return generated API key
     */
    public String generateApiKey(String keyId, String userId, Set<String> scopes, String apiKeyType) {
        Map<String, Object> claims = Map.of(
                "type", "api_key",
                "keyId", keyId,
                "userId", userId,
                "scopes", scopes,
                "apiKeyType", apiKeyType
        );

        String apiKey = jwtTokenUtil.generateToken(keyId, claims, 31536000000L); // 1 year

        ApiKeyInfo info = new ApiKeyInfo(keyId, userId, scopes, apiKeyType, new Date(System.currentTimeMillis() + 31536000000L));
        apiKeys.put(apiKey, info);

        return apiKey;
    }

    /**
     * Validate API key
     * @param apiKey API key to validate
     * @return true if key is valid
     */
    public Boolean validateApiKey(String apiKey) {
        if (apiKey == null || !apiKey.startsWith("gsk_")) {
            return false;
        }

        try {
            String keyId = jwtTokenUtil.extractClaim(apiKey, claims -> claims.get("keyId", String.class));
            return keyId != null && !isApiKeyRevoked(apiKey);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Get API key info
     * @param apiKey API key
     * @return ApiKeyInfo
     */
    public ApiKeyInfo getApiKeyInfo(String apiKey) {
        return apiKeys.get(apiKey);
    }

    /**
     * Check if API key has required scope
     * @param apiKey API key
     * @param requiredScope scope to check
     * @return true if key has the scope
     */
    public Boolean hasScope(String apiKey, String requiredScope) {
        ApiKeyInfo info = apiKeys.get(apiKey);
        return info != null && info.getScopes().contains(requiredScope);
    }

    /**
     * Check if API key has any of the required scopes
     * @param apiKey API key
     * @param requiredScopes set of scopes to check
     * @return true if key has any of the scopes
     */
    public Boolean hasAnyScope(String apiKey, Set<String> requiredScopes) {
        ApiKeyInfo info = apiKeys.get(apiKey);
        if (info == null) return false;
        return info.getScopes().stream().anyMatch(requiredScopes::contains);
    }

    /**
     * Revoke API key
     * @param apiKey API key to revoke
     */
    public void revokeApiKey(String apiKey) {
        ApiKeyInfo info = apiKeys.get(apiKey);
        if (info != null) {
            info.setRevoked(true);
            info.setRevokedAt(new Date());
        }
    }

    /**
     * Check if API key is revoked
     * @param apiKey API key
     * @return true if key is revoked
     */
    public Boolean isApiKeyRevoked(String apiKey) {
        ApiKeyInfo info = apiKeys.get(apiKey);
        return info != null && info.getRevoked();
    }

    /**
     * Extract user ID from API key
     * @param apiKey API key
     * @return user ID
     */
    public String extractUserId(String apiKey) {
        try {
            return jwtTokenUtil.extractClaim(apiKey, claims -> claims.get("userId", String.class));
        } catch (Exception e) {
            throw new InvalidTokenException("API_KEY", "Invalid API key", e);
        }
    }

    /**
     * Get all API keys for a user
     * @param userId user ID
     * @return set of API keys for the user
     */
    public Set<String> getApiKeysForUser(String userId) {
        return apiKeys.entrySet().stream()
                .filter(entry -> userId.equals(entry.getValue().getUserId()))
                .map(Map.Entry::getKey)
                .collect(java.util.stream.Collectors.toSet());
    }

    /**
     * Generate a readable API key (not the actual key, for display purposes)
     * @param keyId unique identifier
     * @return formatted key ID for display
     */
    public String getDisplayKey(String keyId) {
        return "gsk_" + keyId.substring(0, Math.min(8, keyId.length())) + "****************";
    }

    /**
     * Inner class to store API key information
     */
    public static class ApiKeyInfo {
        private final String keyId;
        private final String userId;
        private final Set<String> scopes;
        private final String apiKeyType;
        private final Date expiresAt;
        private Boolean revoked;
        private Date revokedAt;
        private Date lastUsedAt;
        private Integer usageCount;

        public ApiKeyInfo(String keyId, String userId, Set<String> scopes, String apiKeyType, Date expiresAt) {
            this.keyId = keyId;
            this.userId = userId;
            this.scopes = scopes;
            this.apiKeyType = apiKeyType;
            this.expiresAt = expiresAt;
            this.revoked = false;
            this.usageCount = 0;
        }

        public String getKeyId() { return keyId; }
        public String getUserId() { return userId; }
        public Set<String> getScopes() { return scopes; }
        public String getApiKeyType() { return apiKeyType; }
        public Date getExpiresAt() { return expiresAt; }
        public Boolean getRevoked() { return revoked; }
        public void setRevoked(Boolean revoked) { this.revoked = revoked; }
        public Date getRevokedAt() { return revokedAt; }
        public void setRevokedAt(Date revokedAt) { this.revokedAt = revokedAt; }
        public Date getLastUsedAt() { return lastUsedAt; }
        public void setLastUsedAt(Date lastUsedAt) { this.lastUsedAt = lastUsedAt; }
        public Integer getUsageCount() { return usageCount; }
        public void setUsageCount(Integer usageCount) { this.usageCount = usageCount; }
        public void incrementUsage() { this.usageCount++; this.lastUsedAt = new Date(); }

        public Boolean isExpired() {
            return expiresAt.before(new Date());
        }
    }
}
