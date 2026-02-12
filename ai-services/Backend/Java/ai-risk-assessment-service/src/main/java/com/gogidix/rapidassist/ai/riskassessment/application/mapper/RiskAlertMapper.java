package com.gogidix.rapidassist.ai.riskassessment.application.mapper;

import com.gogidix.rapidassist.ai.riskassessment.application.dto.RiskAlertDto;
import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskAlert;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct Mapper for RiskAlert domain to DTO
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RiskAlertMapper {

    @Mapping(target = "active", expression = "java(domain.isActive())")
    @Mapping(target = "timeSinceCreationHours", expression = "java(domain.getTimeSinceCreationHours())")
    @Mapping(target = "overdue", ignore = true)
    RiskAlertDto toDto(RiskAlert domain);

    List<RiskAlertDto> toDtoList(List<RiskAlert> domains);
}
