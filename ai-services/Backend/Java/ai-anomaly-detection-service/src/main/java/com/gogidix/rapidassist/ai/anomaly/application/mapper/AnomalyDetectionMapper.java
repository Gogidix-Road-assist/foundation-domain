package com.gogidix.rapidassist.ai.anomaly.application.mapper;

import com.gogidix.rapidassist.ai.anomaly.application.dto.AnomalyDetectionDto;
import com.gogidix.rapidassist.ai.anomaly.domain.model.AnomalyDetection;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct Mapper for AnomalyDetection
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface AnomalyDetectionMapper {

    AnomalyDetectionDto toDto(AnomalyDetection detection);

    AnomalyDetection toEntity(AnomalyDetectionDto dto);

    List<AnomalyDetectionDto> toDtoList(List<AnomalyDetection> detections);

    List<AnomalyDetection> toEntityList(List<AnomalyDetectionDto> dtos);

    void updateEntityFromDto(AnomalyDetectionDto dto, @MappingTarget AnomalyDetection detection);
}
