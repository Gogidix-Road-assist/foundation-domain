package com.gogidix.rapidassist.ai.report.application.mapper;

import com.gogidix.rapidassist.ai.report.application.dto.ReportDistributionDto;
import com.gogidix.rapidassist.ai.report.domain.model.ReportDistribution;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MapStruct mapper for ReportDistribution.
 */
@Mapper(componentModel = "spring")
@Component
public interface ReportDistributionMapper {

    @Mapping(target = "isSuccessful", ignore = true)
    ReportDistributionDto toDto(ReportDistribution distribution);

    ReportDistribution toDomain(ReportDistributionDto dto);

    List<ReportDistributionDto> toDtoList(List<ReportDistribution> distributions);
}
