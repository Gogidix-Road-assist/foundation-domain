package com.gogidix.rapidassist.ai.imagerecognition.application.mapper;

import com.gogidix.rapidassist.ai.imagerecognition.application.dto.*;
import com.gogidix.rapidassist.ai.imagerecognition.domain.aggregate.ImageRecognition;
import com.gogidix.rapidassist.ai.imagerecognition.domain.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * MapStruct mapper for converting between domain models and DTOs.
 */
@Mapper(componentModel = "spring")
@Component
public interface ImageRecognitionMapper {

    // ImageRecognition mappings
    @Mappings({
            @Mapping(target = "recognizedObjects", ignore = true),
            @Mapping(target = "sceneLabels", ignore = true),
            @Mapping(target = "brandDetections", ignore = true),
            @Mapping(target = "imageFeatures", ignore = true)
    })
    ImageRecognitionDto toDto(ImageRecognition domain);

    @Mappings({
            @Mapping(target = "recognizedObjects", ignore = true),
            @Mapping(target = "sceneLabels", ignore = true),
            @Mapping(target = "brandDetections", ignore = true),
            @Mapping(target = "imageFeatures", ignore = true)
    })
    ImageRecognition toDomain(ImageRecognitionDto dto);

    List<ImageRecognitionDto> toDtoList(List<ImageRecognition> domains);

    // RecognizedObject mappings
    RecognizedObjectDto toDto(RecognizedObject domain);
    RecognizedObject toDomain(RecognizedObjectDto dto);
    List<RecognizedObjectDto> toRecognizedObjectDtoList(List<RecognizedObject> domains);

    // BoundingBox mappings
    BoundingBoxDto toDto(BoundingBox domain);
    BoundingBox toDomain(BoundingBoxDto dto);

    // SceneLabel mappings
    SceneLabelDto toDto(SceneLabel domain);
    SceneLabel toDomain(SceneLabelDto dto);
    List<SceneLabelDto> toSceneLabelDtoList(List<SceneLabel> domains);

    // BrandDetection mappings
    BrandDetectionDto toDto(BrandDetection domain);
    BrandDetection toDomain(BrandDetectionDto dto);
    List<BrandDetectionDto> toBrandDetectionDtoList(List<BrandDetection> domains);

    // ImageFeature mappings
    ImageFeatureDto toDto(ImageFeature domain);
    ImageFeature toDomain(ImageFeatureDto dto);
    List<ImageFeatureDto> toImageFeatureDtoList(List<ImageFeature> domains);
}
