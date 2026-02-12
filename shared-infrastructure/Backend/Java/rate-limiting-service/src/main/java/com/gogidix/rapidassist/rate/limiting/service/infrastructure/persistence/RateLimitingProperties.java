package com.gogidix.rapidassist.rate.limiting.service.infrastructure.persistence;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gogidix.rate-limiting")
public class RateLimitingProperties {

    private Provider provider = new Provider();
    private Algorithm algorithm = new Algorithm();
    private String keyPrefix = "rapidassist";
    private long defaultLimit = 100;
    private long defaultWindowSizeMs = 60000; // 1 minute

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
    }

    public String getKeyPrefix() {
        return keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public long getDefaultLimit() {
        return defaultLimit;
    }

    public void setDefaultLimit(long defaultLimit) {
        this.defaultLimit = defaultLimit;
    }

    public long getDefaultWindowSizeMs() {
        return defaultWindowSizeMs;
    }

    public void setDefaultWindowSizeMs(long defaultWindowSizeMs) {
        this.defaultWindowSizeMs = defaultWindowSizeMs;
    }

    // Helper methods
    public String getAlgorithmType() {
        return algorithm.getType();
    }

    public String getAlgorithmTypeLower() {
        return algorithm.getType().toLowerCase();
    }

    public long getLimit() {
        return algorithm.getLimit();
    }

    public long getWindowSizeMs() {
        return algorithm.getWindowSizeMs();
    }

    public long getCapacity() {
        return algorithm.getCapacity();
    }

    public long getRefillRate() {
        return algorithm.getRefillRate();
    }

    public long getRefillIntervalMs() {
        return algorithm.getRefillIntervalMs();
    }

    public static class Provider {
        private String type = "comprehensive"; // comprehensive or noop

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

    public static class Algorithm {
        private String type = "fixed_window"; // fixed_window, sliding_window, token_bucket
        private long limit = 100;
        private long windowSizeMs = 60000; // 1 minute
        private long capacity = 100; // for token bucket
        private long refillRate = 10; // tokens per interval
        private long refillIntervalMs = 1000; // 1 second

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public long getLimit() {
            return limit;
        }

        public void setLimit(long limit) {
            this.limit = limit;
        }

        public long getWindowSizeMs() {
            return windowSizeMs;
        }

        public void setWindowSizeMs(long windowSizeMs) {
            this.windowSizeMs = windowSizeMs;
        }

        public long getCapacity() {
            return capacity;
        }

        public void setCapacity(long capacity) {
            this.capacity = capacity;
        }

        public long getRefillRate() {
            return refillRate;
        }

        public void setRefillRate(long refillRate) {
            this.refillRate = refillRate;
        }

        public long getRefillIntervalMs() {
            return refillIntervalMs;
        }

        public void setRefillIntervalMs(long refillIntervalMs) {
            this.refillIntervalMs = refillIntervalMs;
        }
    }
}