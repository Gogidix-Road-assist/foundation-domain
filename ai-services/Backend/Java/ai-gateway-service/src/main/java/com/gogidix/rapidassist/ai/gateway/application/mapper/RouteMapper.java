package com.gogidix.rapidassist.ai.gateway.application.mapper;

import com.gogidix.rapidassist.ai.gateway.application.dto.RouteDto;
import com.gogidix.rapidassist.ai.gateway.domain.model.Route;
import com.gogidix.rapidassist.ai.gateway.domain.model.RouteStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * MapStruct mapper for Route DTO.
 */
@Mapper(componentModel = "spring")
public interface RouteMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "routeStatusToString")
    RouteDto toDto(Route domain);

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToRouteStatus")
    Route toDomain(RouteDto dto);

    @Named("stringToRouteStatus")
    default RouteStatus stringToRouteStatus(String status) {
        if (status == null) return null;
        try {
            return RouteStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    @Named("routeStatusToString")
    default String routeStatusToString(RouteStatus status) {
        return status != null ? status.name() : null;
    }
}
