package com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.gateway.domain.model.RequestLog;
import com.gogidix.rapidassist.ai.gateway.infrastructure.persistence.entity.RequestLogEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

/**
 * MapStruct mapper for RequestLog.
 */
@Mapper(componentModel = "spring")
public interface RequestLogPersistenceMapper {

    @Mapping(target = "id", source = "uuid")
    RequestLog toDomain(RequestLogEntity entity);

    @Mapping(target = "uuid", source = "id")
    RequestLogEntity toEntity(RequestLog domain);

    @Mapping(target = "uuid", source = "id")
    void updateEntityFromDomain(RequestLog domain, @MappingTarget RequestLogEntity entity);
}
