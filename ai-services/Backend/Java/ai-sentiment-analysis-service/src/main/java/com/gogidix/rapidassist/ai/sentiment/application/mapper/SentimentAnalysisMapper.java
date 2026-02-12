package com.gogidix.rapidassist.ai.sentiment.application.mapper;

import com.gogidix.rapidassist.ai.sentiment.application.dto.SentimentAnalysisDto;
import com.gogidix.rapidassist.ai.sentiment.domain.aggregate.SentimentAnalysis;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for converting between SentimentAnalysis aggregate and DTO
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SentimentAnalysisMapper {

    /**
     * Convert aggregate to DTO
     */
    SentimentAnalysisDto toDto(SentimentAnalysis sentimentAnalysis);

    /**
     * Convert DTO to aggregate
     */
    SentimentAnalysis toAggregate(SentimentAnalysisDto dto);

    /**
     * Update DTO from aggregate
     */
    void updateDtoFromAggregate(SentimentAnalysis sentimentAnalysis, @MappingTarget SentimentAnalysisDto dto);
}
