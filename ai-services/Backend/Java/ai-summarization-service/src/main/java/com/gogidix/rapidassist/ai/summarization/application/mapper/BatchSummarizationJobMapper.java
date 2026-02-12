package com.gogidix.rapidassist.ai.summarization.application.mapper;

import com.gogidix.rapidassist.ai.summarization.application.dto.BatchSummarizationJobDto;
import com.gogidix.rapidassist.ai.summarization.domain.model.BatchSummarizationJob;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Mapper(componentModel = "spring")
@Component
public interface BatchSummarizationJobMapper {
    BatchSummarizationJobDto toDto(BatchSummarizationJob job);
    BatchSummarizationJob toDomain(BatchSummarizationJobDto dto);
    List<BatchSummarizationJobDto> toDtoList(List<BatchSummarizationJob> jobs);
}
