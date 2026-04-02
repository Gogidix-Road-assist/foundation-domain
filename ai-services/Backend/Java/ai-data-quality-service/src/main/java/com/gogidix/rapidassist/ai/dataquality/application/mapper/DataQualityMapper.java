package com.gogidix.rapidassist.ai.dataquality.application.mapper;

import com.gogidix.rapidassist.ai.dataquality.application.dto.*;
import com.gogidix.rapidassist.ai.dataquality.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for Data Quality domain
 */
@Mapper(componentModel = "spring")
public interface DataQualityMapper {

    // Domain to DTO mappings
    DataQualityRuleDto toDto(DataQualityRule rule);
    List<DataQualityRuleDto> toRuleDtoList(List<DataQualityRule> rules);

    DataQualityCheckDto toDto(DataQualityCheck check);
    List<DataQualityCheckDto> toCheckDtoList(List<DataQualityCheck> checks);

    DataQualityIssueDto toDto(DataQualityIssue issue);
    List<DataQualityIssueDto> toIssueDtoList(List<DataQualityIssue> issues);

    DataQualityReportDto toDto(DataQualityReport report);
    List<DataQualityReportDto> toReportDtoList(List<DataQualityReport> reports);

    DataQualityMetricDto toDto(DataQualityMetric metric);
    List<DataQualityMetricDto> toMetricDtoList(List<DataQualityMetric> metrics);

    // DTO to Domain mappings
    DataQualityRule toDomain(DataQualityRuleDto dto);
    void updateRuleFromDto(DataQualityRuleDto dto, @MappingTarget DataQualityRule rule);

    DataQualityCheck toDomain(DataQualityCheckDto dto);
    DataQualityIssue toDomain(DataQualityIssueDto dto);
    DataQualityReport toDomain(DataQualityReportDto dto);
    DataQualityMetric toDomain(DataQualityMetricDto dto);
}
