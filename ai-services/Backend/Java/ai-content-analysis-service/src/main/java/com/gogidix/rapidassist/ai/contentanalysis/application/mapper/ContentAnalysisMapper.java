package com.gogidix.rapidassist.ai.contentanalysis.application.mapper;

import com.gogidix.rapidassist.ai.contentanalysis.application.dto.*;
import com.gogidix.rapidassist.ai.contentanalysis.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * MapStruct mapper for Content Analysis domain and DTOs
 */
@Mapper(componentModel = "spring")
public interface ContentAnalysisMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "statusToString")
    @Mapping(target = "topics", ignore = true)
    ContentAnalysisDto toDto(ContentAnalysis analysis);

    @Mapping(target = "status", source = "status", qualifiedByName = "stringToStatus")
    ContentAnalysis toEntity(ContentAnalysisDto dto);

    ContentMetricsDto toDto(ContentMetrics metrics);

    ContentMetrics toEntity(ContentMetricsDto dto);

    @Mapping(target = "sentimentType", source = "sentimentType", qualifiedByName = "sentimentTypeToString")
    SentimentAnalysisDto toDto(SentimentAnalysis sentiment);

    @Mapping(target = "sentimentType", source = "sentimentType", qualifiedByName = "stringToSentimentType")
    SentimentAnalysis toEntity(SentimentAnalysisDto dto);

    SEOAnalysisDto toDto(SEOAnalysis seo);

    SEOAnalysis toEntity(SEOAnalysisDto dto);

    ReadabilityAnalysisDto toDto(ReadabilityAnalysis readability);

    ReadabilityAnalysis toEntity(ReadabilityAnalysisDto dto);

    ContentTopicDto toDto(ContentTopic topic);

    ContentTopic toEntity(ContentTopicDto dto);

    List<ContentTopicDto> toTopicDtoList(List<ContentTopic> topics);

    List<ContentTopic> toTopicEntityList(List<ContentTopicDto> dtos);

    @Named("statusToString")
    static String statusToString(ContentAnalysis.AnalysisStatus status) {
        return status != null ? status.name() : null;
    }

    @Named("stringToStatus")
    static ContentAnalysis.AnalysisStatus stringToStatus(String status) {
        return status != null ? ContentAnalysis.AnalysisStatus.valueOf(status) : null;
    }

    @Named("sentimentTypeToString")
    static String sentimentTypeToString(SentimentAnalysis.SentimentType type) {
        return type != null ? type.name() : null;
    }

    @Named("stringToSentimentType")
    static SentimentAnalysis.SentimentType stringToSentimentType(String type) {
        return type != null ? SentimentAnalysis.SentimentType.valueOf(type) : null;
    }
}
