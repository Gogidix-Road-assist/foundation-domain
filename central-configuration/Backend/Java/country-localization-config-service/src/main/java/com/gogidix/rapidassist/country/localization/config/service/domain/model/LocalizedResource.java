package com.gogidix.rapidassist.country.localization.config.service.domain.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.Instant;
import java.util.*;

/**
 * Domain model representing localized content/resources
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class LocalizedResource {

    private final String id;
    private final String resourceKey;
    private final String countryCode;
    private final String resourceType;
    private final Map<String, String> translations; // locale -> translation
    private final String defaultValue;
    private final String context;
    private final boolean active;
    private final String createdBy;
    private final Instant createdAt;
    private final String updatedBy;
    private final Instant updatedAt;
    private final Integer version;

    private LocalizedResource(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.resourceKey = builder.resourceKey;
        this.countryCode = builder.countryCode;
        this.resourceType = builder.resourceType;
        this.translations = Map.copyOf(builder.translations);
        this.defaultValue = builder.defaultValue;
        this.context = builder.context;
        this.active = builder.active;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
        this.updatedBy = builder.updatedBy;
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : Instant.now();
        this.version = builder.version != null ? builder.version : 1;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getTranslation(String locale) {
        return translations.getOrDefault(locale, defaultValue);
    }

    public boolean hasTranslation(String locale) {
        return translations.containsKey(locale);
    }

    // Getters
    public String id() { return id; }
    public String resourceKey() { return resourceKey; }
    public String countryCode() { return countryCode; }
    public String resourceType() { return resourceType; }
    public Map<String, String> translations() { return new HashMap<>(translations); }
    public String defaultValue() { return defaultValue; }
    public String context() { return context; }
    public boolean active() { return active; }
    public String createdBy() { return createdBy; }
    public Instant createdAt() { return createdAt; }
    public String updatedBy() { return updatedBy; }
    public Instant updatedAt() { return updatedAt; }
    public Integer version() { return version; }

    public static class Builder {
        private String id;
        private String resourceKey;
        private String countryCode;
        private String resourceType = "TEXT";
        private Map<String, String> translations = new HashMap<>();
        private String defaultValue;
        private String context;
        private boolean active = true;
        private String createdBy;
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
        private Integer version;

        public Builder id(String id) { this.id = id; return this; }
        public Builder resourceKey(String resourceKey) { this.resourceKey = resourceKey; return this; }
        public Builder countryCode(String countryCode) { this.countryCode = countryCode; return this; }
        public Builder resourceType(String resourceType) { this.resourceType = resourceType; return this; }
        public Builder translations(Map<String, String> translations) { this.translations = translations; return this; }
        public Builder addTranslation(String locale, String value) {
            this.translations.put(locale, value);
            return this;
        }
        public Builder defaultValue(String defaultValue) { this.defaultValue = defaultValue; return this; }
        public Builder context(String context) { this.context = context; return this; }
        public Builder active(boolean active) { this.active = active; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder version(Integer version) { this.version = version; return this; }

        public LocalizedResource build() {
            return new LocalizedResource(this);
        }
    }
}
