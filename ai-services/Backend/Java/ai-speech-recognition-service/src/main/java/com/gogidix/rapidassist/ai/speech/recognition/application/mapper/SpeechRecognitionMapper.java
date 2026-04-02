package com.gogidix.rapidassist.ai.speech.recognition.application.mapper;

import com.gogidix.rapidassist.ai.speech.recognition.application.dto.SpeechRecognitionDto;
import com.gogidix.rapidassist.ai.speech.recognition.domain.model.SpeechRecognition;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for SpeechRecognition.
 */
@Mapper(componentModel = "spring")
public interface SpeechRecognitionMapper {

    SpeechRecognitionMapper INSTANCE = Mappers.getMapper(SpeechRecognitionMapper.class);

    SpeechRecognitionDto toDto(SpeechRecognition model);

    SpeechRecognition toModel(SpeechRecognitionDto dto);

    List<SpeechRecognitionDto> toDtoList(List<SpeechRecognition> models);
    List<SpeechRecognition> toModelList(List<SpeechRecognitionDto> dtos);
}
