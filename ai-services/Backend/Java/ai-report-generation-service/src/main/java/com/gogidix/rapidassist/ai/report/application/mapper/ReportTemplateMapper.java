package com.gogidix.rapidassist.ai.report.application.mapper;

import com.gogidix.rapidassist.ai.report.application.dto.ReportTemplateDto;
import com.gogidix.rapidassist.ai.report.domain.model.ReportTemplate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

/**
 * MapStruct mapper for ReportTemplate.
 */
@Mapper(componentModel = "spring")
@Component
public interface ReportTemplateMapper {

    @Mapping(target = "isActive", ignore = true)
    ReportTemplateDto toDto(ReportTemplate template);

    ReportTemplate toDomain(ReportTemplateDto dto);
}
