package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.matching.domain.model.MatchingResult;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity.MatchingResultEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct Mapper for converting between MatchingResult domain model
 * and MatchingResultEntity MongoDB document.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
    uses = {}
)
public interface MatchingResultPersistenceMapper {

    /**
     * Convert domain model to MongoDB entity.
     */
    @Mapping(target = "id", ignore = true)
    MatchingResultEntity toEntity(MatchingResult domain);

    /**
     * Convert MongoDB entity to domain model.
     */
    @Mapping(source = "uuid", target = "id")
    MatchingResult toDomain(MatchingResultEntity entity);

    /**
     * Update entity from domain model.
     */
    @Mapping(target = "id", ignore = true)
    void updateEntityFromDomain(MatchingResult domain, @MappingTarget MatchingResultEntity entity);

    /**
     * Convert domain list to entity list.
     */
    List<MatchingResultEntity> toEntityList(List<MatchingResult> domains);

    /**
     * Convert entity list to domain list.
     */
    List<MatchingResult> toDomainList(List<MatchingResultEntity> entities);
}
