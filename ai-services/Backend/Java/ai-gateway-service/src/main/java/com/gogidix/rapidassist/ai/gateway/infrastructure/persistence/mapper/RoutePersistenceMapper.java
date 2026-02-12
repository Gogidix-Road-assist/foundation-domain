package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.gateway.domain.model.Route;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.RouteEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for Route.
 */
@Mapper(componentModel = "spring")
public interface RoutePersistenceMapper {

    @Mapping(target = "id", source = "uuid")
    Route toDomain(RouteEntity entity);

    @Mapping(target = "uuid", source = "id")
    RouteEntity toEntity(Route domain);

    @Mapping(target = "uuid", source = "id")
    void updateEntityFromDomain(Route domain, @MappingTarget RouteEntity entity);
}
