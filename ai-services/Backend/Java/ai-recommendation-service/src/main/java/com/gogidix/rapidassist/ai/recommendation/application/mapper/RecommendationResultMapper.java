package com.gogidix.rapidassist.ai.recommendation.application.mapper;

import com.gogidix.rapidassist.ai.recommendation.application.dto.RecommendationResultDto;
import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationResult;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * MapStruct mapper for RecommendationResult.
 */
@Mapper(componentModel = "spring", uses = {RecommendedItemMapper.class})
public interface RecommendationResultMapper {

    RecommendationResultDto toDto(RecommendationResult domain);

    RecommendationResult toDomain(RecommendationResultDto dto);

    List<RecommendationResultDto> toDtoList(List<RecommendationResult> domains);
}
