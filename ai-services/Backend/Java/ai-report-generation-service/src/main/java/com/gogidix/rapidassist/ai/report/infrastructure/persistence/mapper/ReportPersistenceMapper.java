package com.gogidix.rapidassist.ai.report.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.report.domain.aggregate.Report;
import com.gogidix.rapidassist.ai.report.infrastructure.persistence.entity.ReportEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

/**
 * MapStruct mapper for Report entity persistence.
 */
@Mapper(componentModel = "spring")
@Component
public interface ReportPersistenceMapper {

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "generation", ignore = true)
    @Mapping(target = "template", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "distributions", ignore = true)
    Report toDomain(ReportEntity entity);

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "version", ignore = true)
    ReportEntity toEntity(Report domain);
}

