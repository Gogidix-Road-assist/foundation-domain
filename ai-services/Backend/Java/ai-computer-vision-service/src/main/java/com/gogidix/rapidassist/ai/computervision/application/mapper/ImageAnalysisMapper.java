package com.gogidix.rapidassist.ai.computervision.application.mapper;

import com.gogidix.rapidassist.ai.computervision.application.dto.*;
import com.gogidix.rapidassist.ai.computervision.domain.aggregate.ImageAnalysisAggregate;
import com.gogidix.rapidassist.ai.computervision.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * MapStruct mapper for ImageAnalysis
 */
@Mapper(componentModel = "spring")
public interface ImageAnalysisMapper {

    ImageAnalysisDto toDto(ImageAnalysisAggregate aggregate);

    ImageAnalysisAggregate toAggregate(ImageAnalysisDto dto);

    @Mapping(target = "resolution", ignore = true)
    @Mapping(target = "aspectRatio", ignore = true)
    @Mapping(target = "landscape", ignore = true)
    @Mapping(target = "portrait", ignore = true)
    @Mapping(target = "confidenceScore", ignore = true)
    @Mapping(target = "metadata", ignore = true)
    ImageAnalysisDto toDto(ImageAnalysis model);

    @Mapping(target = "confidenceLevel", source = "confidenceScore", qualifiedByName = "confidenceToLevel")
    ObjectDetectionDto toDto(ObjectDetection model);

    @Mapping(target = "confidenceLevel", source = "confidenceScore", qualifiedByName = "confidenceToLevel")
    @Mapping(target = "ageGroup", source = "age", qualifiedByName = "ageToGroup")
    @Mapping(target = "smiling", source = "smileConfidence", qualifiedByName = "smileToBoolean")
    FaceDetectionDto toDto(FaceDetection model);

    @Mapping(target = "confidenceLevel", source = "confidenceScore", qualifiedByName = "confidenceToLevel")
    @Mapping(target = "longestLine", ignore = true)
    @Mapping(target = "averageLineHeight", ignore = true)
    TextRecognitionDto toDto(TextRecognition model);

    @Mapping(target = "primaryConfidenceLevel", source = "primaryConfidence", qualifiedByName = "confidenceToLevel")
    @Mapping(target = "predictions", source = "predictions", qualifiedByName = "predictionsWithLevels")
    ImageClassificationDto toDto(ImageClassification model);

    List<ObjectDetectionDto> toObjectDetectionDtoList(List<ObjectDetection> models);
    List<FaceDetectionDto> toFaceDetectionDtoList(List<FaceDetection> models);
    List<TextRecognitionDto> toTextRecognitionDtoList(List<TextRecognition> models);
    List<ImageClassificationDto> toImageClassificationDtoList(List<ImageClassification> models);

    @Named("confidenceToLevel")
    default String confidenceToLevel(Double confidence) {
        if (confidence == null) {
            return "VERY_LOW";
        }
        return ConfidenceLevel.fromScore(confidence).name();
    }

    @Named("ageToGroup")
    default String ageToGroup(Integer age) {
        if (age == null) {
            return "Unknown";
        }
        if (age < 13) return "Child";
        if (age < 20) return "Teenager";
        if (age < 60) return "Adult";
        return "Senior";
    }

    @Named("smileToBoolean")
    default Boolean smileToBoolean(Double smileConfidence) {
        return smileConfidence != null && smileConfidence >= 0.5;
    }

    @Named("predictionsWithLevels")
    default List<ImageClassificationDto.ClassPredictionDto> predictionsWithLevels(List<ImageClassification.ClassPrediction> predictions) {
        if (predictions == null) {
            return List.of();
        }
        return predictions.stream()
                .map(pred -> ImageClassificationDto.ClassPredictionDto.builder()
                        .className(pred.getClassName())
                        .confidence(pred.getConfidence())
                        .confidenceLevel(pred.getConfidenceLevel().name())
                        .rank(pred.getRank())
                        .build())
                .toList();
    }
}
