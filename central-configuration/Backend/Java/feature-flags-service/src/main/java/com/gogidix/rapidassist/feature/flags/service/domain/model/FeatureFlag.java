package com.gogidix.rapidassist.feature.flags.service.domain.model;

import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Domain model representing a feature flag
 */
public class FeatureFlag {

    private final String id;
    private final String tenantId;
    private final String key;
    private final String name;
    private final String description;
    private final boolean enabled;
    private final FlagType type;
    private final RolloutStrategy rolloutStrategy;
    private final Set<String> allowedTenants;
    private final Set<String> allowedUsers;
    private final Set<String> allowedCountries;
    private final PercentageRollout percentageRollout;
    private final String environment;
    private final FlagStatus status;
    private final String createdBy;
    private final Instant createdAt;
    private final String updatedBy;
    private final Instant updatedAt;
    private final Integer version;
    private final Set<String> tags;
    private final Boolean requiresApproval;
    private final Instant expiresAt;

    private FeatureFlag(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.tenantId = builder.tenantId;
        this.key = builder.key;
        this.name = builder.name;
        this.description = builder.description;
        this.enabled = builder.enabled;
        this.type = builder.type;
        this.rolloutStrategy = builder.rolloutStrategy;
        this.allowedTenants = new HashSet<>(builder.allowedTenants);
        this.allowedUsers = new HashSet<>(builder.allowedUsers);
        this.allowedCountries = new HashSet<>(builder.allowedCountries);
        this.percentageRollout = builder.percentageRollout;
        this.environment = builder.environment;
        this.status = builder.status;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt != null ? builder.createdAt : Instant.now();
        this.updatedBy = builder.updatedBy;
        this.updatedAt = builder.updatedAt != null ? builder.updatedAt : Instant.now();
        this.version = builder.version != null ? builder.version : 1;
        this.tags = new HashSet<>(builder.tags);
        this.requiresApproval = builder.requiresApproval;
        this.expiresAt = builder.expiresAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static FeatureFlag create(String tenantId, String key, String name, String createdBy) {
        return builder()
            .tenantId(tenantId)
            .key(key)
            .name(name)
            .createdBy(createdBy)
            .build();
    }

    public FeatureFlag enable() {
        return toBuilder().enabled(true).status(FlagStatus.ACTIVE).build();
    }

    public FeatureFlag disable() {
        return toBuilder().enabled(false).build();
    }

    public FeatureFlag archive() {
        return toBuilder().status(FlagStatus.ARCHIVED).enabled(false).build();
    }

    public boolean isActive() {
        return status == FlagStatus.ACTIVE &&
               (expiresAt == null || expiresAt.isAfter(Instant.now()));
    }

    public boolean isEligible(String userId, String tenantId, String countryCode) {
        if (!enabled || !isActive()) {
            return false;
        }

        return switch (rolloutStrategy) {
            case ALL_USERS -> true;
            case SPECIFIC_TENANTS -> allowedTenants.contains(tenantId);
            case SPECIFIC_USERS -> allowedUsers.contains(userId);
            // COUNTRY_BASED, PERCENTAGE, GRADUAL handled by service
            case COUNTRY_BASED -> allowedCountries.contains(countryCode);
            case PERCENTAGE, GRADUAL -> false; // Evaluated by service
        };
    }

    public Builder toBuilder() {
        return new Builder(this);
    }

    // Getters
    public String id() { return id; }
    public String tenantId() { return tenantId; }
    public String key() { return key; }
    public String name() { return name; }
    public String description() { return description; }
    public boolean enabled() { return enabled; }
    public FlagType type() { return type; }
    public RolloutStrategy rolloutStrategy() { return rolloutStrategy; }
    public Set<String> allowedTenants() { return new HashSet<>(allowedTenants); }
    public Set<String> allowedUsers() { return new HashSet<>(allowedUsers); }
    public Set<String> allowedCountries() { return new HashSet<>(allowedCountries); }
    public PercentageRollout percentageRollout() { return percentageRollout; }
    public String environment() { return environment; }
    public FlagStatus status() { return status; }
    public String createdBy() { return createdBy; }
    public Instant createdAt() { return createdAt; }
    public String updatedBy() { return updatedBy; }
    public Instant updatedAt() { return updatedAt; }
    public Integer version() { return version; }
    public Set<String> tags() { return new HashSet<>(tags); }
    public Boolean requiresApproval() { return requiresApproval; }
    public Instant expiresAt() { return expiresAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FeatureFlag that = (FeatureFlag) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Enums and nested classes

    public enum FlagType {
        BOOLEAN, MULTIVARIATE, KILL_SWITCH
    }

    public enum RolloutStrategy {
        ALL_USERS,
        SPECIFIC_TENANTS,
        SPECIFIC_USERS,
        PERCENTAGE,
        COUNTRY_BASED,
        GRADUAL
    }

    public enum FlagStatus {
        DRAFT, ACTIVE, INACTIVE, ARCHIVED
    }

    public record PercentageRollout(int percentage, String bucketingKey) {
        public PercentageRollout {
            if (percentage < 0 || percentage > 100) {
                throw new IllegalArgumentException("Percentage must be between 0 and 100");
            }
        }
    }

    public static class Builder {
        private String id;
        private String tenantId;
        private String key;
        private String name;
        private String description;
        private boolean enabled = true;
        private FlagType type = FlagType.BOOLEAN;
        private RolloutStrategy rolloutStrategy = RolloutStrategy.ALL_USERS;
        private Set<String> allowedTenants = new HashSet<>();
        private Set<String> allowedUsers = new HashSet<>();
        private Set<String> allowedCountries = new HashSet<>();
        private PercentageRollout percentageRollout;
        private String environment = "production";
        private FlagStatus status = FlagStatus.DRAFT;
        private String createdBy;
        private Instant createdAt;
        private String updatedBy;
        private Instant updatedAt;
        private Integer version;
        private Set<String> tags = new HashSet<>();
        private Boolean requiresApproval = false;
        private Instant expiresAt;

        private Builder() {}

        private Builder(FeatureFlag flag) {
            this.id = flag.id;
            this.tenantId = flag.tenantId;
            this.key = flag.key;
            this.name = flag.name;
            this.description = flag.description;
            this.enabled = flag.enabled;
            this.type = flag.type;
            this.rolloutStrategy = flag.rolloutStrategy;
            this.allowedTenants = new HashSet<>(flag.allowedTenants);
            this.allowedUsers = new HashSet<>(flag.allowedUsers);
            this.allowedCountries = new HashSet<>(flag.allowedCountries);
            this.percentageRollout = flag.percentageRollout;
            this.environment = flag.environment;
            this.status = flag.status;
            this.createdBy = flag.createdBy;
            this.createdAt = flag.createdAt;
            this.updatedBy = flag.updatedBy;
            this.updatedAt = flag.updatedAt;
            this.version = flag.version;
            this.tags = new HashSet<>(flag.tags);
            this.requiresApproval = flag.requiresApproval;
            this.expiresAt = flag.expiresAt;
        }

        public Builder id(String id) { this.id = id; return this; }
        public Builder tenantId(String tenantId) { this.tenantId = tenantId; return this; }
        public Builder key(String key) { this.key = key; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }
        public Builder type(FlagType type) { this.type = type; return this; }
        public Builder rolloutStrategy(RolloutStrategy rolloutStrategy) { this.rolloutStrategy = rolloutStrategy; return this; }
        public Builder allowedTenants(Set<String> allowedTenants) { this.allowedTenants = allowedTenants; return this; }
        public Builder addAllowedTenant(String tenant) { this.allowedTenants.add(tenant); return this; }
        public Builder allowedUsers(Set<String> allowedUsers) { this.allowedUsers = allowedUsers; return this; }
        public Builder addAllowedUser(String user) { this.allowedUsers.add(user); return this; }
        public Builder allowedCountries(Set<String> allowedCountries) { this.allowedCountries = allowedCountries; return this; }
        public Builder addAllowedCountry(String country) { this.allowedCountries.add(country); return this; }
        public Builder percentageRollout(PercentageRollout percentageRollout) { this.percentageRollout = percentageRollout; return this; }
        public Builder environment(String environment) { this.environment = environment; return this; }
        public Builder status(FlagStatus status) { this.status = status; return this; }
        public Builder createdBy(String createdBy) { this.createdBy = createdBy; return this; }
        public Builder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedBy(String updatedBy) { this.updatedBy = updatedBy; return this; }
        public Builder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder version(Integer version) { this.version = version; return this; }
        public Builder tags(Set<String> tags) { this.tags = tags; return this; }
        public Builder addTag(String tag) { this.tags.add(tag); return this; }
        public Builder requiresApproval(Boolean requiresApproval) { this.requiresApproval = requiresApproval; return this; }
        public Builder expiresAt(Instant expiresAt) { this.expiresAt = expiresAt; return this; }

        public FeatureFlag build() {
            return new FeatureFlag(this);
        }
    }
}
