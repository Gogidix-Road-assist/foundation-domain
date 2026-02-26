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

    @Mapping(target = "matchId", ignore = true)
    SimilarityScoreDto toDto(SimilarityScore domain);

    @Mapping(target = "matchDate", ignore = true)
    SimilarityScore toDomain(SimilarityScoreDto dto);

    void updateDomainFromDto(SimilarityScoreDto dto, @MappingTarget SimilarityScore domain);

    List<SimilarityScoreDto> toDtoList(List<SimilarityScore> domains);

    List<SimilarityScore> toDomainList(List<SimilarityScoreDto> dtos);
}
