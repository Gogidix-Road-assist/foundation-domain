package com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.summarization.domain.model.SummaryQualityMetrics;
import com.gogidix.rapidassist.ai.summarization.infrastructure.persistence.entity.SummaryQualityMetricsEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface SummaryQualityMetricsPersistenceMapper {

    @Mapping(source = "uuid", target = "id")
    SummaryQualityMetrics toDomain(SummaryQualityMetricsEntity entity);

    @Mapping(source = "id", target = "uuid")
    SummaryQualityMetricsEntity toEntity(SummaryQualityMetrics domain);

    List<SummaryQualityMetrics> toDomainList(List<SummaryQualityMetricsEntity> entities);
}
