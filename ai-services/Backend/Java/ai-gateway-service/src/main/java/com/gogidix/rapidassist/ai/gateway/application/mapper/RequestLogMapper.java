package com.gogidix.rapidassist.ai.gateway.application.mapper;

import com.gogidix.rapidassist.ai.gateway.application.dto.RequestLogDto;
import com.gogidix.rapidassist.ai.gateway.domain.model.RequestLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for RequestLog DTO.
 */
@Mapper(componentModel = "spring")
public interface RequestLogMapper {

    RequestLogDto toDto(RequestLog domain);
    RequestLog toDomain(RequestLogDto dto);
}
