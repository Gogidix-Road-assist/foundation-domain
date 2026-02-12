package com.gogidix.rapidassist.ai.anomaly.application.mapper;

import com.gogidix.rapidassist.ai.anomaly.application.dto.AnomalyAlertDto;
import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalyAlert;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct Mapper for AnomalyAlert
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AnomalyAlertMapper {

    AnomalyAlertDto toDto(AnomalyAlert alert);

    AnomalyAlert toEntity(AnomalyAlertDto dto);

    List<AnomalyAlertDto> toDtoList(List<AnomalyAlert> alerts);

    List<AnomalyAlert> toEntityList(List<AnomalyAlertDto> dtos);

    void updateEntityFromDto(AnomalyAlertDto dto, @MappingTarget AnomalyAlert alert);
}
