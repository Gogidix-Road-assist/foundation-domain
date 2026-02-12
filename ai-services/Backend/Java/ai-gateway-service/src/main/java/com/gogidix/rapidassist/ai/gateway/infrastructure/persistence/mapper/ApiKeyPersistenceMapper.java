package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.gateway.domain.model.ApiKey;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.ApiKeyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for ApiKey.
 */
@Mapper(componentModel = "spring")
public interface ApiKeyPersistenceMapper {

    @Mapping(target = "id", source = "uuid")
    ApiKey toDomain(ApiKeyEntity entity);

    @Mapping(target = "uuid", source = "id")
    ApiKeyEntity toEntity(ApiKey domain);

    @Mapping(target = "uuid", source = "id")
    void updateEntityFromDomain(ApiKey domain, @MappingTarget ApiKeyEntity entity);
}
