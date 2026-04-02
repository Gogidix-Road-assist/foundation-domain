package com.gogidix.rapidassist.ai.speech.recognition.application.mapper;

import com.gogidix.rapidassist.ai.speech.recognition.application.dto.WordTimestampDto;
import com.gogidix.rapidassist.ai.speech.recognition.domain.model.WordTimestamp;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * MapStruct mapper for WordTimestamp.
 */
@Mapper(componentModel = "spring")
public interface WordTimestampMapper {

    WordTimestampMapper INSTANCE = Mappers.getMapper(WordTimestampMapper.class);

    WordTimestampDto toDto(WordTimestamp model);

    WordTimestamp toModel(WordTimestampDto dto);

    List<WordTimestampDto> toDtoList(List<WordTimestamp> models);
    List<WordTimestamp> toModelList(List<WordTimestampDto> dtos);
}
