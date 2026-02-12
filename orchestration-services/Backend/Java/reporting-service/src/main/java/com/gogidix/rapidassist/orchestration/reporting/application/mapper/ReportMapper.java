package com.gogidix.rapidassist.orchestration.reporting.application.mapper;

import com.gogidix.rapidassist.orchestration.reporting.application.dto.ReportDTO;
import com.gogidix.rapidassist.orchestration.reporting.domain.model.Report;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for Report entity and DTO
 */
@Mapper(componentModel = "spring")
public interface ReportMapper {

    ReportDTO toDTO(Report report);

    Report toEntity(ReportDTO dto);

    List<ReportDTO> toDTOList(List<Report> reports);

    @Mapping(target = "reportId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(ReportDTO dto, @MappingTarget Report entity);
}
