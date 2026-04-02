package com.gogidix.rapidassist.ai.summarization.application.mapper;

import com.gogidix.rapidassist.ai.summarization.application.dto.SummarizationRequestDto;
import com.gogidix.rapidassist.ai.summarization.domain.model.SummarizationRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface SummarizationRequestMapper {
    SummarizationRequestDto toDto(SummarizationRequest request);
    SummarizationRequest toDomain(SummarizationRequestDto dto);
    List<SummarizationRequestDto> toDtoList(List<SummarizationRequest> requests);
    void updateEntityFromDto(SummarizationRequestDto dto, @MappingTarget SummarizationRequest entity);
}
