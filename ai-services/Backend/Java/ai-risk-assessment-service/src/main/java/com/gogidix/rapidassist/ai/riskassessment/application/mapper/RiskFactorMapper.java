package com.gogidix.rapidassist.ai.riskassessment.application.mapper;

import com.gogidix.rapidassist.ai.riskassessment.application.dto.RiskFactorDto;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskFactor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct Mapper for RiskFactor domain to DTO
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RiskFactorMapper {

    @Mapping(target = "riskLevel", expression = "java(domain.getRiskLevel())")
    @Mapping(target = "weightedScore", expression = "java(domain.calculateWeightedScore())")
    @Mapping(target = "highRisk", expression = "java(domain.isHighRisk())")
    @Mapping(target = "critical", expression = "java(domain.isCritical())")
    RiskFactorDto toDto(RiskFactor domain);

    List<RiskFactorDto> toDtoList(List<RiskFactor> domains);
}
