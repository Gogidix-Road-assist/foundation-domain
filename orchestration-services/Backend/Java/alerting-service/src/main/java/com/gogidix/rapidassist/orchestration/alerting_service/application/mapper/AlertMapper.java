package com.gogidix.rapidassist.orchestration.alerting_service.application.mapper;

import com.gogidix.rapidassist.orchestration.alerting_service.application.dto.AlertDTO;
import com.gogidix.rapidassist.orchestration.alerting_service.application.dto.AlertSummaryDTO;
import com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for Alert entity and DTOs
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AlertMapper {

    AlertDTO toDTO(Alert alert);

    AlertSummaryDTO toSummaryDTO(Alert alert);

    List<AlertDTO> toDTOList(List<Alert> alerts);

    List<AlertSummaryDTO> toSummaryDTOList(List<Alert> alerts);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", expression = "java(com.gogidix.rapidassist.orchestration.alerting_service.domain.model.Alert.AlertStatus.PENDING)")
    @Mapping(target = "escalationLevel", ignore = true)
    @Mapping(target = "escalationRequired", ignore = true)
    Alert toEntity(CreateAlertRequest request);
}
