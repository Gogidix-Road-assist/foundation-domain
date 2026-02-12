package com.gogidix.rapidassist.ai.riskassessment.application.mapper;

import com.gogidix.rapidassist.ai.riskassessment.application.dto.RiskThresholdDto;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskThreshold;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct Mapper for RiskThreshold domain to DTO
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RiskThresholdMapper {

    @Mapping(target = "valid", expression = "java(domain.isValid())")
    RiskThresholdDto toDto(RiskThreshold domain);

    List<RiskThresholdDto> toDtoList(List<RiskThreshold> domains);
}
