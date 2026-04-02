package com.gogidix.rapidassist.ai.gateway.application.mapper;

import com.gogidix.rapidassist.ai.gateway.application.dto.GatewayConfigDto;
import com.gogidix.rapidassist.ai.gateway.domain.model.GatewayConfig;
import com.gogidix.rapidassist.ai.gateway.domain.model.GatewayStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for GatewayConfig DTO.
 */
@Mapper(componentModel = "spring")
public interface GatewayConfigMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "gatewayStatusToString")
    GatewayConfigDto toDto(GatewayConfig domain);

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToGatewayStatus")
    GatewayConfig toDomain(GatewayConfigDto dto);

    @Named("stringToGatewayStatus")
    default GatewayStatus stringToGatewayStatus(String status) {
        if (status == null) return null;
        try {
            return GatewayStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("gatewayStatusToString")
    default String gatewayStatusToString(GatewayStatus status) {
        return status != null ? status.name() : null;
    }
}
