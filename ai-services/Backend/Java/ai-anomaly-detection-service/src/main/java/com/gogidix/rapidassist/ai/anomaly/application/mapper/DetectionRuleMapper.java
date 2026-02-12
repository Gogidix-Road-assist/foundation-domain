package com.gogidix.rapidassist.ai.anomaly.application.mapper;

import com.gogidix.rapidassist.ai.anomaly.application.dto.DetectionRuleDto;
import com.gogidix.rapidassist.ai.anomaly.domain.model.DetectionRule;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct Mapper for DetectionRule
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface DetectionRuleMapper {

    DetectionRuleDto toDto(DetectionRule rule);

    DetectionRule toEntity(DetectionRuleDto dto);

    List<DetectionRuleDto> toDtoList(List<DetectionRule> rules);

    List<DetectionRule> toEntityList(List<DetectionRuleDto> dtos);

    void updateEntityFromDto(DetectionRuleDto dto, @MappingTarget DetectionRule rule);
}
