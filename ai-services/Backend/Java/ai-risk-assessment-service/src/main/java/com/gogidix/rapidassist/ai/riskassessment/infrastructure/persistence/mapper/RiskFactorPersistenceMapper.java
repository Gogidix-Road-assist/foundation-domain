package com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskFactor;
import com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.entity.RiskFactorEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Map;

/**
 * MapStruct Mapper for RiskFactor domain and persistence entity
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RiskFactorPersistenceMapper {

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "metadata", source = "metadata")
    RiskFactorEntity toEntity(RiskFactor domain);

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "metadata", source = "metadata")
    RiskFactor toDomain(RiskFactorEntity entity);

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "metadata", source = "metadata")
    void updateEntityFromDomain(RiskFactor domain, @MappingTarget RiskFactorEntity entity);

    List<RiskFactorEntity> toEntityList(List<RiskFactor> domains);

    List<RiskFactor> toDomainList(List<RiskFactorEntity> entities);

    /**
     * Custom mapping method for metadata: Map<String, Object> to Object
     */
    default Object mapMetadata(Map<String, Object> metadata) {
        return metadata;
    }

    /**
     * Custom mapping method for metadata: Object to Map<String, Object>
     */
    @SuppressWarnings("unchecked")
    default Map<String, Object> mapMetadata(Object metadata) {
        if (metadata instanceof Map) {
            return (Map<String, Object>) metadata;
        }
        return null;
    }
}
