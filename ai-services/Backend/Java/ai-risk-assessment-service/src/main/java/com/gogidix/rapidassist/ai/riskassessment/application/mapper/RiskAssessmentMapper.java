package com.gogidix.rapidassist.ai.riskassessment.application.mapper;

import com.gogidix.rapidassist.ai.riskassessment.application.dto.RiskAlertDto;
import com.gogidix.rapidassist.ai.riskassessment.application.dto.RiskAssessmentDto;
import com.gogidix.rapidassist.ai.riskassessment.application.dto.RiskFactorDto;
import com.gogidix.rapidassist.ai.riskassessment.domain.aggregate.RiskAssessment;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskAlert;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskFactor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct Mapper for RiskAssessment domain to DTO
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {RiskFactorMapper.class, RiskAlertMapper.class}
)
public interface RiskAssessmentMapper {

    @Mapping(target = "riskFactorCount", expression = "java(domain.getRiskFactors() != null ? domain.getRiskFactors().size() : 0)")
    @Mapping(target = "highRiskFactorCount", expression = "java(domain.getHighRiskFactors() != null ? domain.getHighRiskFactors().size() : 0)")
    @Mapping(target = "criticalFactorCount", expression = "java(domain.getCriticalRiskFactors() != null ? domain.getCriticalRiskFactors().size() : 0)")
    @Mapping(target = "activeAlertCount", expression = "java(domain.getActiveAlerts() != null ? domain.getActiveAlerts().size() : 0)")
    @Mapping(target = "unresolvedAlertCount", expression = "java(domain.getUnresolvedAlerts() != null ? domain.getUnresolvedAlerts().size() : 0)")
    @Mapping(target = "durationDays", expression = "java(domain.getAssessmentDurationDays())")
    @Mapping(target = "hasCriticalRisks", expression = "java(domain.hasCriticalRisks())")
    @Mapping(target = "allAlertsResolved", expression = "java(domain.areAllAlertsResolved())")
    RiskAssessmentDto toDto(RiskAssessment domain);

    List<RiskAssessmentDto> toDtoList(List<RiskAssessment> domains);
}
