package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.gateway.domain.model.RateLimit;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.RateLimitEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for RateLimit.
 */
@Mapper(componentModel = "spring")
public interface RateLimitPersistenceMapper {

    @Mapping(target = "id", source = "uuid")
    RateLimit toDomain(RateLimitEntity entity);

    @Mapping(target = "uuid", source = "id")
    RateLimitEntity toEntity(RateLimit domain);

    @Mapping(target = "uuid", source = "id")
    void updateEntityFromDomain(RateLimit domain, @MappingTarget RateLimitEntity entity);
}
