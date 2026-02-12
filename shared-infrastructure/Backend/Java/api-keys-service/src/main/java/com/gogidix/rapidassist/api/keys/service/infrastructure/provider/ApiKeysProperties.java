package com.gogidix.rapidassist.api.keys.service.infrastructure.provider;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "gogidix.api-keys")
public class ApiKeysProperties {

    private Provider provider = new Provider();
    private Security security = new Security();
    private int defaultExpiryDays = 365;
    private int maxUsagePerKey = 0; // 0 means unlimited
    private Map<String, Object> defaultMetadata = new HashMap<>();

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public int getDefaultExpiryDays() {
        return defaultExpiryDays;
    }

    public void setDefaultExpiryDays(int defaultExpiryDays) {
        this.defaultExpiryDays = defaultExpiryDays;
    }

    public int getMaxUsagePerKey() {
        return maxUsagePerKey;
    }

    public void setMaxUsagePerKey(int maxUsagePerKey) {
        this.maxUsagePerKey = maxUsagePerKey;
    }

    public Map<String, Object> getDefaultMetadata() {
        return defaultMetadata;
    }

    public void setDefaultMetadata(Map<String, Object> defaultMetadata) {
        this.defaultMetadata = defaultMetadata;
    }

    public static class Provider {
        private String type = "mongo"; // mongo or noop

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Security {
        private int keyLength = 32; // bytes
        private String keyPrefix = "gogidix_";
        private boolean hashKeys = true;
        private String hashAlgorithm = "SHA-256";

        public int getKeyLength() {
            return keyLength;
        }

        public void setKeyLength(int keyLength) {
            this.keyLength = keyLength;
        }

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }

        public boolean isHashKeys() {
            return hashKeys;
        }

        public void setHashKeys(boolean hashKeys) {
            this.hashKeys = hashKeys;
        }

        public String getHashAlgorithm() {
            return hashAlgorithm;
        }

        public void setHashAlgorithm(String hashAlgorithm) {
            this.hashAlgorithm = hashAlgorithm;
        }
    }
}