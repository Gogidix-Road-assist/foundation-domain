package com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.riskassessment.domain.aggregate.RiskAssessment;
import com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.entity.RiskAssessmentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Map;

/**
 * MapStruct Mapper for RiskAssessment domain and persistence entity
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RiskAssessmentPersistenceMapper {

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "metadata", source = "metadata", qualifiedByName = "mapMetadataToObject")
    RiskAssessmentEntity toEntity(RiskAssessment domain);

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "riskFactors", ignore = true)
    @Mapping(target = "alerts", ignore = true)
    @Mapping(target = "mitigationRecommendations", ignore = true)
    @Mapping(target = "metadata", source = "metadata", qualifiedByName = "mapObjectToMetadata")
    RiskAssessment toDomain(RiskAssessmentEntity entity);

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "metadata", source = "metadata", qualifiedByName = "mapMetadataToObject")
    void updateEntityFromDomain(RiskAssessment domain, @MappingTarget RiskAssessmentEntity entity);

    List<RiskAssessmentEntity> toEntityList(List<RiskAssessment> domains);

    List<RiskAssessment> toDomainList(List<RiskAssessmentEntity> entities);

    /**
     * Custom mapping method for metadata: Map<String, Object> to Object
     */
    @org.mapstruct.Named("mapMetadataToObject")
    default Object mapMetadataToObject(Map<String, Object> metadata) {
        return metadata;
    }

    /**
     * Custom mapping method for metadata: Object to Map<String, Object>
     */
    @org.mapstruct.Named("mapObjectToMetadata")
    @SuppressWarnings("unchecked")
    default Map<String, Object> mapObjectToMetadata(Object metadata) {
        if (metadata instanceof Map) {
            return (Map<String, Object>) metadata;
        }
        return null;
    }
}
