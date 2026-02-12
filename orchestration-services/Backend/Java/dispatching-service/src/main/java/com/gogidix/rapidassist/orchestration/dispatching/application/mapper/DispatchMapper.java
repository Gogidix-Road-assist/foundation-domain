package com.gogidix.rapidassist.orchestration.dispatching.application.mapper;

import com.gogidix.rapidassist.orchestration.dispatching.application.command.CreateDispatchCommand;
import com.gogidix.rapidassist.orchestration.dispatching.application.dto.request.CreateDispatchRequestDto;
import com.gogidix.rapidassist.orchestration.dispatching.application.dto.response.DispatchResponseDto;
import com.gogidix.rapidassist.orchestration.dispatching.domain.model.Dispatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * MapStruct mapper for Dispatch entity
 */
@Mapper(componentModel = "spring")
public interface DispatchMapper {

    DispatchResponseDto toResponseDto(Dispatch dispatch);

    List<DispatchResponseDto> toResponseDtoList(List<Dispatch> dispatches);

    @Mapping(target = "dispatchId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "priority", source = "priority", qualifiedByName = "mapCommandPriorityToEntityPriority")
    @Mapping(target = "assignmentMethod", source = "assignmentMethod", qualifiedByName = "mapCommandAssignmentMethodToEntityAssignmentMethod")
    @Mapping(target = "location", source = "location", qualifiedByName = "mapCommandLocationToEntityLocation")
    Dispatch toEntity(CreateDispatchCommand command);

    @Mapping(target = "priority", source = "priority", qualifiedByName = "mapStringToEntityPriority")
    @Mapping(target = "assignmentMethod", source = "assignmentMethod", qualifiedByName = "mapStringToEntityAssignmentMethod")
    @Mapping(target = "location", source = "location", qualifiedByName = "mapDtoToEntityLocation")
    CreateDispatchCommand toCommand(CreateDispatchRequestDto request);

    @Named("mapStringToEntityPriority")
    default Dispatch.DispatchPriority mapStringToEntityPriority(String priority) {
        return priority != null ? Dispatch.DispatchPriority.valueOf(priority.toUpperCase()) : Dispatch.DispatchPriority.MEDIUM;
    }

    @Named("mapStringToEntityAssignmentMethod")
    default Dispatch.AssignmentMethod mapStringToEntityAssignmentMethod(String method) {
        return method != null ? Dispatch.AssignmentMethod.valueOf(method.toUpperCase()) : Dispatch.AssignmentMethod.AUTOMATIC;
    }

    @Named("mapDtoToEntityLocation")
    default Dispatch.Location mapDtoToEntityLocation(CreateDispatchRequestDto.LocationDto dto) {
        if (dto == null) return null;
        return Dispatch.Location.builder()
            .latitude(dto.getLatitude())
            .longitude(dto.getLongitude())
            .address(dto.getAddress())
            .build();
    }

    @Named("mapCommandPriorityToEntityPriority")
    default Dispatch.DispatchPriority mapCommandPriorityToEntityPriority(CreateDispatchCommand.DispatchPriority priority) {
        if (priority == null) return Dispatch.DispatchPriority.MEDIUM;
        return Dispatch.DispatchPriority.valueOf(priority.name());
    }

    @Named("mapCommandAssignmentMethodToEntityAssignmentMethod")
    default Dispatch.AssignmentMethod mapCommandAssignmentMethodToEntityAssignmentMethod(CreateDispatchCommand.AssignmentMethod method) {
        if (method == null) return Dispatch.AssignmentMethod.AUTOMATIC;
        return Dispatch.AssignmentMethod.valueOf(method.name());
    }

    @Named("mapCommandLocationToEntityLocation")
    default Dispatch.Location mapCommandLocationToEntityLocation(CreateDispatchCommand.LocationCommand location) {
        if (location == null) return null;
        return Dispatch.Location.builder()
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .address(location.getAddress())
            .build();
    }

    default String mapStatusToString(Dispatch.DispatchStatus status) {
        return status != null ? status.name() : null;
    }

    default String mapPriorityToString(Dispatch.DispatchPriority priority) {
        return priority != null ? priority.name() : null;
    }

    default CreateDispatchCommand.DispatchPriority mapEntityPriorityToCommandPriority(Dispatch.DispatchPriority priority) {
        return priority != null ? CreateDispatchCommand.DispatchPriority.valueOf(priority.name()) : null;
    }

    default CreateDispatchCommand.AssignmentMethod mapEntityAssignmentMethodToCommandAssignmentMethod(Dispatch.AssignmentMethod method) {
        return method != null ? CreateDispatchCommand.AssignmentMethod.valueOf(method.name()) : null;
    }

    default CreateDispatchCommand.LocationCommand mapEntityLocationToCommandLocation(Dispatch.Location location) {
        if (location == null) return null;
        return CreateDispatchCommand.LocationCommand.builder()
            .latitude(location.getLatitude())
            .longitude(location.getLongitude())
            .address(location.getAddress())
            .build();
    }
}
