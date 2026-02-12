package com.gogidix.rapidassist.ai.gateway.application.mapper;

import com.gogidix.rapidassist.ai.gateway.application.dto.ApiKeyDto;
import com.gogidix.rapidassist.ai.gateway.domain.model.ApiKey;
import com.gogidix.rapidassist.ai.gateway.domain.model.ApiKeyStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for ApiKey DTO.
 */
@Mapper(componentModel = "spring")
public interface ApiKeyMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "apiKeyStatusToString")
    ApiKeyDto toDto(ApiKey domain);

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToApiKeyStatus")
    ApiKey toDomain(ApiKeyDto dto);

    @Named("stringToApiKeyStatus")
    default ApiKeyStatus stringToApiKeyStatus(String status) {
        if (status == null) return null;
        try {
            return ApiKeyStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("apiKeyStatusToString")
    default String apiKeyStatusToString(ApiKeyStatus status) {
        return status != null ? status.name() : null;
    }
}
