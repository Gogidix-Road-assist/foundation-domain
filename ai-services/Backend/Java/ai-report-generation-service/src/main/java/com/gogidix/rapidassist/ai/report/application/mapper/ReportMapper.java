package com.gogidix.rapidassist.ai.report.application.mapper;

import com.gogidix.rapidassist.ai.report.application.dto.ReportDto;
import com.gogidix.rapidassist.ai.report.domain.aggregate.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

/**
 * MapStruct mapper for Report DTOs.
 */
@Mapper(componentModel = "spring", uses = {ReportTemplateMapper.class, ReportDistributionMapper.class})
@Component
public interface ReportMapper {

    @Mapping(target = "durationSeconds", ignore = true)
    @Mapping(target = "readyForDownload", ignore = true)
    @Mapping(target = "successfulDistributions", ignore = true)
    @Mapping(target = "failedDistributions", ignore = true)
    @Mapping(target = "schedule.isActive", ignore = true)
    ReportDto toDto(Report report);

    Report toDomain(ReportDto dto);

    default ReportDto toDtoWithComputedFields(Report report) {
        ReportDto dto = toDto(report);
        dto.setDurationSeconds(report.getGenerationDurationSeconds());
        dto.setReadyForDownload(report.isReadyForDownload());
        dto.setSuccessfulDistributions(report.getSuccessfulDistributionCount());
        dto.setFailedDistributions(report.getFailedDistributionCount());
        return dto;
    }
}

