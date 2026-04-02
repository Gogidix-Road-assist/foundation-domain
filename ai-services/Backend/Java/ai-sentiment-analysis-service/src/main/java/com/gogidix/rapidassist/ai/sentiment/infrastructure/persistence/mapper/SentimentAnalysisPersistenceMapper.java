package com.gogidix.rapidassist.ai.sentiment.infrastructure.persistence.mapper;

import com.gogidix.rapidassist.ai.sentiment.domain.aggregate.SentimentAnalysis;
import com.gogidix.rapidassist.ai.sentiment.infrastructure.persistence.entity.SentimentAnalysisEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

/**
 * MapStruct mapper for converting between SentimentAnalysis aggregate and SentimentAnalysisEntity
 */
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
)
public interface SentimentAnalysisPersistenceMapper {

    /**
     * Convert aggregate to entity
     */
    SentimentAnalysisEntity toEntity(SentimentAnalysis sentimentAnalysis);

    /**
     * Convert entity to aggregate
     */
    SentimentAnalysis toAggregate(SentimentAnalysisEntity entity);

    /**
     * Update entity from aggregate
     */
    void updateEntityFromAggregate(SentimentAnalysis sentimentAnalysis, @MappingTarget SentimentAnalysisEntity entity);
}
