package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.gateway.domain.model.GatewayConfig;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.GatewayConfigEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for GatewayConfig.
 */
@Mapper(componentModel = "spring")
public interface GatewayConfigPersistenceMapper {

    @Mapping(target = "id", source = "uuid")
    GatewayConfig toDomain(GatewayConfigEntity entity);

    @Mapping(target = "uuid", source = "id")
    GatewayConfigEntity toEntity(GatewayConfig domain);

    @Mapping(target = "uuid", source = "id")
    void updateEntityFromDomain(GatewayConfig domain, @MappingTarget GatewayConfigEntity entity);
}
