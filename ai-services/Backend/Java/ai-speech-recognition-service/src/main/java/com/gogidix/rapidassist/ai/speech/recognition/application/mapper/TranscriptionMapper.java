package com.gogidix.rapidassist.ai.speech.recognition.application.mapper;

import com.gogidix.rapidassist.ai.speech.recognition.application.dto.TranscriptionDto;
import com.gogidix.rapidassist.ai.speech.recognition.domain.model.Transcription;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for Transcription.
 */
@Mapper(componentModel = "spring", uses = {WordTimestampMapper.class})
public interface TranscriptionMapper {

    TranscriptionMapper INSTANCE = Mappers.getMapper(TranscriptionMapper.class);

    TranscriptionDto toDto(Transcription model);

    Transcription toModel(TranscriptionDto dto);

    List<TranscriptionDto> toDtoList(List<Transcription> models);
    List<Transcription> toModelList(List<TranscriptionDto> dtos);
}
