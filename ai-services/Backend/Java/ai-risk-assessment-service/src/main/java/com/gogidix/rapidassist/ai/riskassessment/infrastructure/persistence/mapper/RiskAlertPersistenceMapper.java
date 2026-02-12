package com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.riskassessment.domain.model.RiskAlert;
import com.gogidix.rapidassist.ai.riskassessment.infrastructure.persistence.entity.RiskAlertEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;
import java.util.Map;

/**
 * MapStruct Mapper for RiskAlert domain and persistence entity
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface RiskAlertPersistenceMapper {

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "metadata", source = "metadata")
    RiskAlertEntity toEntity(RiskAlert domain);

    @Mapping(target = "id", source = "uuid")
    @Mapping(target = "metadata", source = "metadata")
    RiskAlert toDomain(RiskAlertEntity entity);

    @Mapping(target = "uuid", source = "id")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "metadata", source = "metadata")
    void updateEntityFromDomain(RiskAlert domain, @MappingTarget RiskAlertEntity entity);

    List<RiskAlertEntity> toEntityList(List<RiskAlert> domains);

    List<RiskAlert> toDomainList(List<RiskAlertEntity> entities);

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
