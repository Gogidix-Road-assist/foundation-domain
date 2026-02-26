package com.gogidix.rapidassist.ai.matching.application.mapper;

import com.gogidix.rapidassist.ai.matching.application.dto.SimilarityScoreDto;
import com.gogidix.rapidassist.ai.matching.domain.model.SimilarityScore;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct Mapper for SimilarityScore DTO.
 */
@Mapper(componentModel = "spring")
public interface SimilarityScoreMapper {

    @Mapping(target = "entity1Id", source = "sourceEntityId")
    @Mapping(target = "entity1Type", source = "sourceEntityType")
    @Mapping(target = "entity2Id", source = "targetEntityId")
    @Mapping(target = "entity2Type", source = "targetEntityType")
    @Mapping(target = "score", source = "overallSimilarity")
    @Mapping(target = "algorithm", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SimilarityScoreDto toDto(SimilarityScore domain);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "sourceEntityId", source = "entity1Id")
    @Mapping(target = "sourceEntityType", source = "entity1Type")
    @Mapping(target = "targetEntityId", source = "entity2Id")
    @Mapping(target = "targetEntityType", source = "entity2Type")
    @Mapping(target = "overallSimilarity", source = "score")
    @Mapping(target = "algorithmType", ignore = true)
    @Mapping(target = "fieldSimilarities", ignore = true)
    @Mapping(target = "similarityDetails", source = "scoreDetails")
    @Mapping(target = "computationTimeMs", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    SimilarityScore toDomain(SimilarityScoreDto dto);

    void updateDomainFromDto(SimilarityScoreDto dto, @MappingTarget SimilarityScore domain);

    List<SimilarityScoreDto> toDtoList(List<SimilarityScore> domains);

    List<SimilarityScore> toDomainList(List<SimilarityScoreDto> dtos);
}
