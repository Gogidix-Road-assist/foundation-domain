package com.gogidix.rapidassist.policy.engine.service.infrastructure.policy;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "gogidix.policy-engine")
public class PolicyEngineProperties {

    private Provider provider = new Provider();
    private Cache cache = new Cache();
    private Evaluation evaluation = new Evaluation();

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    // Helper methods
    public long getCacheTtlSeconds() {
        return cache.getTtlSeconds();
    }

    public int getMaxEvaluationDepth() {
        return evaluation.getMaxDepth();
    }

    public long getMaxEvaluationTimeMs() {
        return evaluation.getMaxTimeMs();
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

    public static class Cache {
        private boolean enabled = true;
        private int maxSize = 1000;
        private long ttlSeconds = 300; // 5 minutes

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getMaxSize() {
            return maxSize;
        }

        public void setMaxSize(int maxSize) {
            this.maxSize = maxSize;
        }

        public long getTtlSeconds() {
            return ttlSeconds;
        }

        public void setTtlSeconds(long ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }
    }

    public static class Evaluation {
        private int maxDepth = 10;
        private long maxTimeMs = 5000; // 5 seconds
        private boolean enableMetrics = true;
        private boolean enableTracing = true;

        public int getMaxDepth() {
            return maxDepth;
        }

        public void setMaxDepth(int maxDepth) {
            this.maxDepth = maxDepth;
        }

        public long getMaxTimeMs() {
            return maxTimeMs;
        }

        public void setMaxTimeMs(long maxTimeMs) {
            this.maxTimeMs = maxTimeMs;
        }

        public boolean isEnableMetrics() {
            return enableMetrics;
        }

        public void setEnableMetrics(boolean enableMetrics) {
            this.enableMetrics = enableMetrics;
        }

        public boolean isEnableTracing() {
            return enableTracing;
        }

        public void setEnableTracing(boolean enableTracing) {
            this.enableTracing = enableTracing;
        }
    }
}