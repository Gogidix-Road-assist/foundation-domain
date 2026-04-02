package com.gogidix.rapidassist.ai.speech.recognition.application.mapper;

import com.gogidix.rapidassist.ai.speech.recognition.application.dto.AudioMetadataDto;
import com.gogidix.rapidassist.ai.speech.recognition.domain.model.AudioMetadata;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for AudioMetadata.
 */
@Mapper(componentModel = "spring")
public interface AudioMetadataMapper {

    AudioMetadataMapper INSTANCE = Mappers.getMapper(AudioMetadataMapper.class);

    AudioMetadataDto toDto(AudioMetadata model);

    AudioMetadata toModel(AudioMetadataDto dto);

    List<AudioMetadataDto> toDtoList(List<AudioMetadata> models);
    List<AudioMetadata> toModelList(List<AudioMetadataDto> dtos);
}
