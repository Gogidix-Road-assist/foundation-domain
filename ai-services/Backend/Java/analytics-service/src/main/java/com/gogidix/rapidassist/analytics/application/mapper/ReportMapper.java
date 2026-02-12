package com.gogidix.rapidassist.analytics.application.mapper;

import com.gogidix.rapidassist.analytics.application.dto.ReportDto;
import com.gogidix.rapidassist.analytics.domain.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct mapper for Report.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface ReportMapper {

    ReportDto toDto(Report domain);

    Report toDomain(ReportDto dto);

    List<ReportDto> toDtoList(List<Report> domains);

    void updateDomainFromDto(ReportDto dto, @MappingTarget Report domain);
}
