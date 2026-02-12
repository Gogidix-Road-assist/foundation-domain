package com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskThreshold;
import com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.entity.RiskThresholdEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct Mapper for RiskThreshold domain and persistence entity
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RiskThresholdPersistenceMapper {

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "id", ignore = true)
    RiskThresholdEntity toEntity(RiskThreshold domain);

    @Mapping(target = "id", source = "uuid")
    RiskThreshold toDomain(RiskThresholdEntity entity);

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDomain(RiskThreshold domain, @MappingTarget RiskThresholdEntity entity);

    List<RiskThresholdEntity> toEntityList(List<RiskThreshold> domains);

    List<RiskThreshold> toDomainList(List<RiskThresholdEntity> entities);
}
