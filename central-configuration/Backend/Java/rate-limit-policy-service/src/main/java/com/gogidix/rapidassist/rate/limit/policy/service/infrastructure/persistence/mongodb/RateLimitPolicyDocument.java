package com.gogidix.rapidassist.rate.limit.policy.service.infrastructure.persistence.mongodb;

import com.gogidix.rapidassist.rate.limit.policy.service.domain.model.RateLimitPolicy;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Document(collection = "rate_limit_policies")
public record RateLimitPolicyDocument(

    @Id
    String id,

    @Field("tenantId")
    String tenantId,

    @Field("policyKey")
    String policyKey,

    @Field("name")
    String name,

    @Field("description")
    String description,

    @Field("limitType")
    RateLimitPolicy.LimitType limitType,

    @Field("config")
    RateLimitPolicy.RateLimitConfig config,

    @Field("scope")
    RateLimitPolicy.Scope scope,

    @Field("enabled")
    boolean enabled,

    @Field("environment")
    String environment,

    @Field("createdBy")
    String createdBy,

    @Field("createdAt")
    Instant createdAt,

    @Field("updatedBy")
    String updatedBy,

    @Field("updatedAt")
    Instant updatedAt,

    @Field("version")
    Integer version

) {

    public static RateLimitPolicyDocument fromDomain(RateLimitPolicy policy) {
        return new RateLimitPolicyDocument(
            policy.id(),
            policy.tenantId(),
            policy.policyKey(),
            policy.name(),
            policy.description(),
            policy.limitType(),
            policy.config(),
            policy.scope(),
            policy.enabled(),
            policy.environment(),
            policy.createdBy(),
            policy.createdAt(),
            policy.updatedBy(),
            policy.updatedAt(),
            policy.version()
        );
    }

    public RateLimitPolicy toDomain() {
        return RateLimitPolicy.builder()
            .id(id)
            .tenantId(tenantId)
            .policyKey(policyKey)
            .name(name)
            .description(description)
            .limitType(limitType)
            .config(config)
            .scope(scope)
            .enabled(enabled)
            .environment(environment)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .updatedBy(updatedBy)
            .updatedAt(updatedAt)
            .version(version)
            .build();
    }
}
