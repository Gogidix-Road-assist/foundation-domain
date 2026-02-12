package com.gogidix.rapidassist.ai.matching.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.matching.domain.model.SimilarityScore;
import com.gogidix.rapidassist.ai.matching.infrastructure.persistence.entity.SimilarityScoreEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

/**
 * MapStruct Mapper for SimilarityScore.
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SimilarityScorePersistenceMapper {

    @Mapping(target = "id", ignore = true)
    SimilarityScoreEntity toEntity(SimilarityScore domain);

    @Mapping(source = "uuid", target = "id")
    SimilarityScore toDomain(SimilarityScoreEntity entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "uuid", ignore = true)
    void updateEntityFromDomain(SimilarityScore domain, @MappingTarget SimilarityScoreEntity entity);

    List<SimilarityScoreEntity> toEntityList(List<SimilarityScore> domains);

    List<SimilarityScore> toDomainList(List<SimilarityScoreEntity> entities);
}
