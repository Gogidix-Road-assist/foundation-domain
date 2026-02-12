package com.gogidix.rapidassist.ai.speech.recognition.application.mapper;

import com.gogidix.rapidassist.ai.speech.recognition.application.dto.SpeakerDto;
import com.gogidix.rapidassist.ai.speech.recognition.domain.model.Speaker;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for Speaker.
 */
@Mapper(componentModel = "spring")
public interface SpeakerMapper {

    SpeakerMapper INSTANCE = Mappers.getMapper(SpeakerMapper.class);

    SpeakerDto toDto(Speaker model);

    Speaker toModel(SpeakerDto dto);

    List<SpeakerDto> toDtoList(List<Speaker> models);
    List<Speaker> toModelList(List<SpeakerDto> dtos);
}
