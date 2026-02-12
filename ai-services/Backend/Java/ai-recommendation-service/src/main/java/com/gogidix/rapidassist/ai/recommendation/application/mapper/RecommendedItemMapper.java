package com.gogidix.rapidassist.ai.recommendation.application.mapper;

import com.gogidix.rapidassist.ai.recommendation.application.dto.RecommendationResultDto.RecommendedItemDto;
import com.gogidix.rapidassist.ai.recommendation.domain.model.RecommendationResult.RecommendedItem;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * MapStruct mapper for RecommendedItem.
 */
@Mapper(componentModel = "spring")
public interface RecommendedItemMapper {

    RecommendedItemDto toDto(RecommendedItem domain);

    RecommendedItem toDomain(RecommendedItemDto dto);

    List<RecommendedItemDto> toDtoList(List<RecommendedItem> domains);
}
